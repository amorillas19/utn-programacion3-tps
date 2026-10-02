package com.orm;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

        try {
            // 1) Datos de prueba (si la base ya tiene datos, no hace nada)
            Seeder.cargarDatos(em);

            // 2) Las 22 consultas, por nivel
            nivel1(em);
            nivel2(em);
            nivel3(em);
            nivel4(em);
            nivel5(em);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // ------------------------------------------------------------------
    // NIVEL 1
    // ------------------------------------------------------------------
    private static void nivel1(EntityManager em) {
        mostrar("1. Todas las facturas",
                "14 facturas",
                ConsultasJPQL.consulta01_todasLasFacturas(em),
                Main::factura);

        mostrar("2. Numero, fecha e importe",
                "14 filas [numero, fecha, importe]",
                ConsultasJPQL.consulta02_numeroFechaImporte(em),
                Main::fila);

        mostrar("3. Articulos del rubro 'Electrónica'",
                "3 articulos: ART001, ART004, ART005",
                ConsultasJPQL.consulta03_articulosPorRubro(em, "Electrónica"),
                Main::articulo);

        mostrar("4. Facturas de marzo 2026 (01/03 00:00 a 31/03 23:59:59)",
                "5 facturas: 3, 4, 5, 6, 7",
                ConsultasJPQL.consulta04_facturasEntreFechas(em,
                        LocalDateTime.of(2026, 3, 1, 0, 0, 0),
                        LocalDateTime.of(2026, 3, 31, 23, 59, 59)),
                Main::factura);
    }

    // ------------------------------------------------------------------
    // NIVEL 2
    // ------------------------------------------------------------------
    private static void nivel2(EntityManager em) {
        mostrar("5. EMITIDAS, mas de $10.000 y no anuladas",
                "4 facturas: 4, 5, 7, 8",
                ConsultasJPQL.consulta05_facturasEmitidasNoAnuladas(em, "EMITIDA", 10000.0),
                Main::factura);

        mostrar("6. Clientes con 'andina' en el nombre o CUIT que empiece con '20-'",
                "4 clientes (todos menos Cooperativa del Sur)",
                ConsultasJPQL.consulta06_clientesPorTextoOCuit(em, "andina", "20-"),
                Main::cliente);

        mostrar("7. Estados distintos ordenados",
                "ANULADA, BORRADOR, EMITIDA, PAGADA",
                ConsultasJPQL.consulta07_estadosDistintos(em),
                String::valueOf);

        mostrar("8. Cantidad, suma y promedio",
                "[14, 322000.0, 23000.0]",
                Collections.<Object[]>singletonList(ConsultasJPQL.consulta08_conteoSumaPromedio(em)),
                Main::fila);

        mostrar("9. Puntos de venta numero 1, 2 y 5",
                "3 puntos de venta (el 3 no entra)",
                ConsultasJPQL.consulta09_puntosVentaPorNumeros(em, Arrays.asList(1, 2, 5)),
                Main::puntoVenta);
    }

    // ------------------------------------------------------------------
    // NIVEL 3
    // ------------------------------------------------------------------
    private static void nivel3(EntityManager em) {
        mostrar("10. Facturas cargadas por 'maria'",
                "5 facturas: 8, 9, 10, 11, 12",
                ConsultasJPQL.consulta10_facturasPorUsuario(em, "maria"),
                Main::factura);

        mostrar("11. Detalles de facturas del punto de venta numero 2",
                "7 detalles (facturas 5, 6, 7 y 11)",
                ConsultasJPQL.consulta11_detallesPorPuntoVenta(em, 2),
                d -> "factura " + d.getFactura().getNumero() + " | " + d.getDescripcion()
                        + " | cantidad " + d.getCantidad());

        mostrar("12. Articulos con su marca (LEFT JOIN)",
                "5 filas; Cable HDMI con marca null",
                ConsultasJPQL.consulta12_articulosConMarca(em),
                Main::fila);

        mostrar("13a. Facturas con articulos Samsung",
                "10 facturas: 3, 4, 5, 6, 7, 8, 9, 10, 12, 13",
                ConsultasJPQL.consulta13_facturasPorMarca(em, "Samsung"),
                Main::factura);

        mostrar("13b. Facturas con articulos Logitech",
                "8 facturas: 1, 2, 5, 8, 9, 11, 13, 14",
                ConsultasJPQL.consulta13_facturasPorMarca(em, "Logitech"),
                Main::factura);

        mostrar("14. Facturas con importe mayor al promedio",
                "5 facturas: 5, 6, 7, 8, 10",
                ConsultasJPQL.consulta14_facturasSobrePromedio(em),
                Main::factura);

        mostrar("15. Facturas del cliente 20-12345678-9",
                "4 facturas: 1, 3, 6, 10",
                ConsultasJPQL.consulta15_facturasPorCliente(em, "20-12345678-9"),
                Main::factura);
    }

    // ------------------------------------------------------------------
    // NIVEL 4
    // ------------------------------------------------------------------
    private static void nivel4(EntityManager em) {
        mostrar("16. Resumen por punto de venta",
                "Casa Central 7 / 93000; Sucursal Norte 4 / 127000; Sucursal Sur 3 / 102000",
                ConsultasJPQL.consulta16_resumenPorPuntoVenta(em),
                Main::fila);

        mostrar("17. Usuarios con mas de 5 facturas",
                "solo admin con 7",
                ConsultasJPQL.consulta17_usuariosConMasDeNFacturas(em, 5L),
                Main::fila);

        mostrar("18. Ventas por marca",
                "Samsung 28 / 280000; Logitech 24 / 39000",
                ConsultasJPQL.consulta18_ventasPorMarca(em),
                Main::fila);

        mostrar("19. Total por condicion de IVA",
                "Resp. Inscripto 220500; Monotributo 80000; Consumidor Final 21500",
                ConsultasJPQL.consulta19_totalPorCondicionIva(em),
                Main::fila);
    }

    // ------------------------------------------------------------------
    // NIVEL 5
    // ------------------------------------------------------------------
    private static void nivel5(EntityManager em) {
        mostrar("20. Marcas con articulos facturados",
                "Samsung y Logitech",
                ConsultasJPQL.consulta20_marcasConArticulosFacturados(em),
                m -> m.getDenominacion());

        mostrar("21. Articulos nunca facturados",
                "Auriculares (ART004)",
                ConsultasJPQL.consulta21_articulosNuncaFacturados(em),
                Main::articulo);

        mostrar("22. Categoria por importe (mayor a menor)",
                "14 filas: 2 ALTO, 8 MEDIO, 4 BAJO",
                ConsultasJPQL.consulta22_categoriaPorImporte(em),
                Main::fila);
    }

    // ------------------------------------------------------------------
    // Ayudas para imprimir
    // ------------------------------------------------------------------

    /** Imprime el titulo, lo esperado, y lo que devolvio la consulta. */
    private static <T> void mostrar(String titulo, String esperado, List<T> filas, Function<T, String> formato) {
        System.out.println();
        System.out.println("=== " + titulo + " ===");
        System.out.println("Esperado: " + esperado);
        System.out.println("Obtenido: " + filas.size() + " fila(s)");
        for (T fila : filas) {
            System.out.println("   " + formato.apply(fila));
        }
    }

    private static String fila(Object[] columnas) {
        return Arrays.toString(columnas);
    }

    private static String factura(FacturaVenta f) {
        return "Factura " + f.getNumero() + " | " + f.getEstado() + " | $" + f.getImporteTotal()
                + " | " + f.getFechaEmision();
    }

    private static String articulo(Articulo a) {
        return a.getCodigo() + " | " + a.getDenominacion();
    }

    private static String cliente(Cliente c) {
        return c.getCuitCuil() + " | " + c.getDenominacion();
    }

    private static String puntoVenta(PuntoVenta p) {
        return "Numero " + p.getNumero() + " | " + p.getDescripcion();
    }
}
