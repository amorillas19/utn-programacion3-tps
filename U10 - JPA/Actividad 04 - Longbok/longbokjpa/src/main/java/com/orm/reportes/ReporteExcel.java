package com.orm.reportes;

import com.orm.dto.FacturaReporteDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public final class ReporteExcel {

    private static final String[] COLUMNAS = {
            "N° Factura", "Fecha", "Cliente", "Condición IVA",
            "Punto de venta", "Importe total", "Ítems"
    };
    // ancho de cada columna, en caracteres aproximados
    private static final int[] ANCHOS = {12, 14, 32, 30, 24, 18, 8};

    private ReporteExcel() {
    }

    public static void generar(List<FacturaReporteDTO> filas, Path destino) throws IOException {
        Files.createDirectories(destino.toAbsolutePath().getParent());

        try (Workbook libro = new XSSFWorkbook();
             OutputStream salida = Files.newOutputStream(destino)) {

            Sheet hoja = libro.createSheet("Facturas");
            DataFormat formato = libro.createDataFormat();

            Font negrita = libro.createFont();
            negrita.setBold(true);

            CellStyle estiloEncabezado = libro.createCellStyle();
            estiloEncabezado.setFont(negrita);
            estiloEncabezado.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle estiloFecha = libro.createCellStyle();
            estiloFecha.setDataFormat(formato.getFormat("dd/mm/yyyy"));

            CellStyle estiloImporte = libro.createCellStyle();
            estiloImporte.setDataFormat(formato.getFormat("#,##0.00"));

            CellStyle estiloTotal = libro.createCellStyle();
            estiloTotal.setFont(negrita);
            estiloTotal.setDataFormat(formato.getFormat("#,##0.00"));

            // Encabezado
            Row encabezado = hoja.createRow(0);
            for (int i = 0; i < COLUMNAS.length; i++) {
                Cell celda = encabezado.createCell(i);
                celda.setCellValue(COLUMNAS[i]);
                celda.setCellStyle(estiloEncabezado);
                hoja.setColumnWidth(i, ANCHOS[i] * 256);
            }
            hoja.createFreezePane(0, 1);

            // Datos
            int numeroFila = 1;
            double total = 0;
            for (FacturaReporteDTO f : filas) {
                Row fila = hoja.createRow(numeroFila++);
                fila.createCell(0).setCellValue(f.getNumeroFactura());

                Cell fecha = fila.createCell(1);
                fecha.setCellValue(Date.from(f.getFechaEmision().atZone(ZoneId.systemDefault()).toInstant()));
                fecha.setCellStyle(estiloFecha);

                fila.createCell(2).setCellValue(f.getClienteDenominacion());
                fila.createCell(3).setCellValue(f.getCondicionIva());
                fila.createCell(4).setCellValue(f.getPuntoVentaDescripcion());

                Cell importe = fila.createCell(5);
                importe.setCellValue(f.getImporteTotal());
                importe.setCellStyle(estiloImporte);

                fila.createCell(6).setCellValue(f.getCantidadItems());
                total += f.getImporteTotal();
            }

            // Fila de total
            Row filaTotal = hoja.createRow(numeroFila);
            Cell etiqueta = filaTotal.createCell(4);
            etiqueta.setCellValue("TOTAL");
            etiqueta.setCellStyle(estiloTotal);
            Cell celdaTotal = filaTotal.createCell(5);
            celdaTotal.setCellValue(total);
            celdaTotal.setCellStyle(estiloTotal);

            libro.write(salida);
        }
    }
}
