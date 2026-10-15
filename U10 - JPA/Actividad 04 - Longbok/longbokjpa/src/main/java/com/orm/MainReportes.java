package com.orm;

import com.orm.dto.FacturaReporteDTO;
import com.orm.reportes.FacturaReporteQueries;
import com.orm.reportes.ReporteExcel;
import com.orm.reportes.ReportePdf;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.nio.file.Path;
import java.util.List;

public class MainReportes {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

        try {
            Seeder.cargarDatos(em);

            List<FacturaReporteDTO> reporte = FacturaReporteQueries.listarReporte(em);
            System.out.println("Filas del reporte: " + reporte.size());
            reporte.forEach(f -> System.out.println("   " + f.getNumeroFactura() + " | "
                    + f.getClienteDenominacion() + " | $" + f.getImporteTotal()
                    + " | items: " + f.getCantidadItems()));

            Path carpeta = Path.of("reportes");
            ReportePdf.generar(reporte, carpeta.resolve("reporte_facturas.pdf"));
            ReporteExcel.generar(reporte, carpeta.resolve("reporte_facturas.xlsx"));

            System.out.println("Archivos generados en: " + carpeta.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
