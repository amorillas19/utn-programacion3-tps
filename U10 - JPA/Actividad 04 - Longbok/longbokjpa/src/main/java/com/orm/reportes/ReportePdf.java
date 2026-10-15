package com.orm.reportes;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.orm.dto.FacturaReporteDTO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public final class ReportePdf {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat MONEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"));

    private static final Font TITULO = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static final Font ENCABEZADO = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    private static final Font CELDA = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

    private static final String[] COLUMNAS = {
            "N° Factura", "Fecha", "Cliente", "Condición IVA",
            "Punto de venta", "Importe total", "Ítems"
    };

    private ReportePdf() {
    }

    public static void generar(List<FacturaReporteDTO> filas, Path destino) throws IOException, DocumentException {
        Files.createDirectories(destino.toAbsolutePath().getParent());

        Document documento = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(destino.toFile()));
            documento.open();

            documento.add(new Paragraph("Reporte ejecutivo de ventas", TITULO));

            PdfPTable tabla = new PdfPTable(COLUMNAS.length);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1.3f, 1.6f, 3.2f, 3.2f, 2.8f, 2.2f, 1.1f});
            tabla.setSpacingBefore(12);
            tabla.setHeaderRows(1); // el encabezado se repite en cada pagina

            for (String columna : COLUMNAS) {
                PdfPCell celda = new PdfPCell(new Phrase(columna, ENCABEZADO));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                celda.setPadding(5);
                tabla.addCell(celda);
            }

            double total = 0;
            for (FacturaReporteDTO f : filas) {
                tabla.addCell(celda(String.valueOf(f.getNumeroFactura()), Element.ALIGN_LEFT));
                tabla.addCell(celda(f.getFechaEmision().format(FECHA), Element.ALIGN_LEFT));
                tabla.addCell(celda(f.getClienteDenominacion(), Element.ALIGN_LEFT));
                tabla.addCell(celda(f.getCondicionIva(), Element.ALIGN_LEFT));
                tabla.addCell(celda(f.getPuntoVentaDescripcion(), Element.ALIGN_LEFT));
                tabla.addCell(celda(MONEDA.format(f.getImporteTotal()), Element.ALIGN_RIGHT));
                tabla.addCell(celda(String.valueOf(f.getCantidadItems()), Element.ALIGN_RIGHT));
                total += f.getImporteTotal();
            }
            documento.add(tabla);

            Paragraph resumen = new Paragraph(
                    "Facturas: " + filas.size() + "   |   Total facturado: " + MONEDA.format(total), ENCABEZADO);
            resumen.setSpacingBefore(10);
            documento.add(resumen);
        } finally {
            if (documento.isOpen()) {
                documento.close(); // tambien cierra el archivo
            }
        }
    }

    private static PdfPCell celda(String texto, int alineacion) {
        PdfPCell celda = new PdfPCell(new Phrase(texto == null ? "" : texto, CELDA));
        celda.setHorizontalAlignment(alineacion);
        celda.setPadding(4);
        return celda;
    }
}
