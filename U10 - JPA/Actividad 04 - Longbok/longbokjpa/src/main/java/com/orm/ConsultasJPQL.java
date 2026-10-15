package com.orm;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Las 22 consultas JPQL del TP, una por metodo.
 *
 * Como leer una consulta JPQL (ejemplo):
 *
 *   SELECT f FROM FacturaVenta f WHERE f.estado = :estado
 *
 *   - "FacturaVenta" es el nombre de la CLASE Java (no el de la tabla).
 *   - "f" es un alias: un apodo para referirse a cada factura.
 *   - "f.estado" es el ATRIBUTO Java (no el de la columna).
 *   - ":estado" es un parametro con nombre. Se completa con setParameter(),
 *     nunca concatenando texto (eso evita la inyeccion SQL).
 *
 * Tipo de retorno segun lo que se pide en el SELECT:
 *   - SELECT f ...            -> entidades completas  -> List<FacturaVenta>
 *   - SELECT f.a, f.b ...     -> varias columnas      -> List<Object[]>
 *   - SELECT DISTINCT f.a ... -> una sola columna     -> List<String>
 */
public class ConsultasJPQL {

    // =====================================================================
    // NIVEL 1: consultas basicas y proyecciones
    // =====================================================================

    /** 1. Todas las facturas. */
    public static List<FacturaVenta> consulta01_todasLasFacturas(EntityManager em) {
        String jpql = "SELECT f FROM FacturaVenta f";
        return em.createQuery(jpql, FacturaVenta.class).getResultList();
    }

    /**
     * 2. Solo numero, fecha de emision e importe total.
     * Al pedir varias columnas (no la entidad entera), cada fila viene como
     * un arreglo: fila[0] = numero, fila[1] = fechaEmision, fila[2] = importeTotal.
     */
    public static List<Object[]> consulta02_numeroFechaImporte(EntityManager em) {
        String jpql = "SELECT f.numero, f.fechaEmision, f.importeTotal FROM FacturaVenta f";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    /**
     * 3. Articulos de un rubro por su denominacion.
     * "a.rubro.denominacion" navega de Articulo a Rubro sin escribir el JOIN
     * (JPQL lo arma solo). Se llama "navegacion por path".
     */
    public static List<Articulo> consulta03_articulosPorRubro(EntityManager em, String denominacionRubro) {
        String jpql = "SELECT a FROM Articulo a WHERE a.rubro.denominacion = :denominacion";
        return em.createQuery(jpql, Articulo.class)
                .setParameter("denominacion", denominacionRubro)
                .getResultList();
    }

    /**
     * 4. Facturas emitidas dentro de un rango de fechas.
     * BETWEEN incluye los dos extremos (desde y hasta).
     */
    public static List<FacturaVenta> consulta04_facturasEntreFechas(EntityManager em,
            LocalDateTime desde, LocalDateTime hasta) {
        String jpql = "SELECT f FROM FacturaVenta f WHERE f.fechaEmision BETWEEN :desde AND :hasta";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }

    // =====================================================================
    // NIVEL 2: condiciones combinadas, texto y agregaciones basicas
    // =====================================================================

    /**
     * 5. Facturas con estado dado, importe mayor a un minimo y NO anuladas.
     * "IS NULL" es la forma correcta de preguntar si algo esta vacio:
     * "= NULL" nunca da verdadero.
     */
    public static List<FacturaVenta> consulta05_facturasEmitidasNoAnuladas(EntityManager em,
            String estado, double importeMinimo) {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.estado = :estado "
                + "AND f.importeTotal > :minimo "
                + "AND f.fechaAnulacion IS NULL";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("estado", estado)
                .setParameter("minimo", importeMinimo)
                .getResultList();
    }

    /**
     * 6. Clientes cuya denominacion contenga un texto (sin importar mayusculas)
     * O cuyo CUIT/CUIL empiece con un prefijo.
     *
     * LOWER() pasa a minusculas la columna, y el texto buscado tambien lo pasamos
     * a minusculas en Java: asi "ANDINA" y "andina" coinciden.
     * El simbolo % de LIKE significa "cualquier cosa": "%andina%" = contiene "andina";
     * "20-%" = empieza con "20-".
     */
    public static List<Cliente> consulta06_clientesPorTextoOCuit(EntityManager em,
            String textoParcial, String prefijoCuit) {
        String jpql = "SELECT c FROM Cliente c "
                + "WHERE LOWER(c.denominacion) LIKE :texto "
                + "OR c.cuitCuil LIKE :prefijo";
        return em.createQuery(jpql, Cliente.class)
                .setParameter("texto", "%" + textoParcial.toLowerCase() + "%")
                .setParameter("prefijo", prefijoCuit + "%")
                .getResultList();
    }

    /** 7. Estados distintos de las facturas, ordenados de la A a la Z. */
    public static List<String> consulta07_estadosDistintos(EntityManager em) {
        String jpql = "SELECT DISTINCT f.estado FROM FacturaVenta f ORDER BY f.estado ASC";
        return em.createQuery(jpql, String.class).getResultList();
    }

    /**
     * 8. Cantidad, suma y promedio en una sola consulta.
     * Devuelve UNA sola fila (un arreglo de 3 posiciones):
     * [0] = cantidad (Long), [1] = suma (Double), [2] = promedio (Double).
     */
    public static Object[] consulta08_conteoSumaPromedio(EntityManager em) {
        String jpql = "SELECT COUNT(f), SUM(f.importeTotal), AVG(f.importeTotal) FROM FacturaVenta f";
        return em.createQuery(jpql, Object[].class).getSingleResult();
    }

    /**
     * 9. Puntos de venta cuyo numero este en una lista.
     * El parametro es una lista completa: setParameter("numeros", List.of(1, 2, 5)).
     */
    public static List<PuntoVenta> consulta09_puntosVentaPorNumeros(EntityManager em, List<Integer> numeros) {
        String jpql = "SELECT p FROM PuntoVenta p WHERE p.numero IN (:numeros)";
        return em.createQuery(jpql, PuntoVenta.class)
                .setParameter("numeros", numeros)
                .getResultList();
    }

    // =====================================================================
    // NIVEL 3: navegacion, JOINs y subconsultas simples
    // =====================================================================

    /** 10. Facturas creadas por un usuario, navegando f.usuarioCarga.usuario. */
    public static List<FacturaVenta> consulta10_facturasPorUsuario(EntityManager em, String nombreUsuario) {
        String jpql = "SELECT f FROM FacturaVenta f WHERE f.usuarioCarga.usuario = :usuario";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("usuario", nombreUsuario)
                .getResultList();
    }

    /**
     * 11. Detalles de factura que pertenezcan a facturas de un punto de venta.
     * INNER JOIN: une cada detalle con su factura, y cada factura con su punto
     * de venta. Solo quedan las filas que tienen pareja en los dos lados.
     */
    public static List<FacturaVentaDetalle> consulta11_detallesPorPuntoVenta(EntityManager em,
            int numeroPuntoVenta) {
        String jpql = "SELECT d FROM FacturaVentaDetalle d "
                + "JOIN d.factura f "
                + "JOIN f.puntoVenta pv "
                + "WHERE pv.numero = :numero";
        return em.createQuery(jpql, FacturaVentaDetalle.class)
                .setParameter("numero", numeroPuntoVenta)
                .getResultList();
    }

    /**
     * 12. Denominacion de cada articulo junto a la de su marca, incluso si no tiene marca.
     * LEFT JOIN: trae TODOS los articulos; si no tienen marca, la marca viene null.
     * Con un JOIN comun, los articulos sin marca desaparecerian.
     */
    public static List<Object[]> consulta12_articulosConMarca(EntityManager em) {
        String jpql = "SELECT a.denominacion, m.denominacion FROM Articulo a LEFT JOIN a.marca m";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    /**
     * 13. Facturas con al menos un detalle de un articulo de una marca.
     * Camino: Factura -> detalles -> ListaPrecioArticulo -> Articulo -> Marca.
     * DISTINCT evita que una factura salga repetida si tiene varios detalles
     * de la misma marca.
     */
    public static List<FacturaVenta> consulta13_facturasPorMarca(EntityManager em, String denominacionMarca) {
        String jpql = "SELECT DISTINCT f FROM FacturaVenta f "
                + "JOIN f.detalles d "
                + "JOIN d.listaPrecioArticulo lpa "
                + "JOIN lpa.articulo a "
                + "JOIN a.marca m "
                + "WHERE m.denominacion = :marca";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("marca", denominacionMarca)
                .getResultList();
    }

    /**
     * 14. Facturas con importe mayor al promedio de todas.
     * La subconsulta (entre parentesis) calcula el promedio primero, y la
     * consulta de afuera lo usa para comparar. Usamos el alias f2 adentro para
     * no confundirlo con el f de afuera.
     */
    public static List<FacturaVenta> consulta14_facturasSobrePromedio(EntityManager em) {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.importeTotal > (SELECT AVG(f2.importeTotal) FROM FacturaVenta f2)";
        return em.createQuery(jpql, FacturaVenta.class).getResultList();
    }

    /** 15. Facturas de un cliente, buscado por su CUIT/CUIL. */
    public static List<FacturaVenta> consulta15_facturasPorCliente(EntityManager em, String cuitCuil) {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "JOIN f.cliente c "
                + "WHERE c.cuitCuil = :cuit";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("cuit", cuitCuil)
                .getResultList();
    }

    // =====================================================================
    // NIVEL 4: GROUP BY y HAVING
    // =====================================================================

    /**
     * 16. Por punto de venta: descripcion, cantidad de facturas y suma facturada.
     * GROUP BY junta las facturas en "grupos" (uno por punto de venta) y las
     * funciones COUNT y SUM se calculan dentro de cada grupo.
     * Agrupamos por id Y descripcion: si dos puntos de venta tuvieran el mismo
     * nombre, agrupando solo por descripcion se mezclarian.
     */
    public static List<Object[]> consulta16_resumenPorPuntoVenta(EntityManager em) {
        String jpql = "SELECT pv.descripcion, COUNT(f), SUM(f.importeTotal) "
                + "FROM FacturaVenta f "
                + "JOIN f.puntoVenta pv "
                + "GROUP BY pv.id, pv.descripcion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    /**
     * 17. Usuarios de carga con mas de N facturas.
     * WHERE filtra filas ANTES de agrupar; HAVING filtra grupos DESPUES de
     * agrupar. Como "mas de 5" depende del conteo del grupo, va en HAVING.
     */
    public static List<Object[]> consulta17_usuariosConMasDeNFacturas(EntityManager em, long minimo) {
        String jpql = "SELECT u.usuario, COUNT(f) "
                + "FROM FacturaVenta f "
                + "JOIN f.usuarioCarga u "
                + "GROUP BY u.id, u.usuario "
                + "HAVING COUNT(f) > :minimo";
        return em.createQuery(jpql, Object[].class)
                .setParameter("minimo", minimo)
                .getResultList();
    }

    /**
     * 18. Por marca: unidades vendidas y subtotal acumulado.
     * Se parte de los detalles (ahi estan la cantidad y el subtotal) y se sube
     * hasta la marca: Detalle -> ListaPrecioArticulo -> Articulo -> Marca.
     */
    public static List<Object[]> consulta18_ventasPorMarca(EntityManager em) {
        String jpql = "SELECT m.denominacion, SUM(d.cantidad), SUM(d.importeSubtotal) "
                + "FROM FacturaVentaDetalle d "
                + "JOIN d.listaPrecioArticulo lpa "
                + "JOIN lpa.articulo a "
                + "JOIN a.marca m "
                + "GROUP BY m.id, m.denominacion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    /** 19. Por condicion de IVA: denominacion y total facturado. */
    public static List<Object[]> consulta19_totalPorCondicionIva(EntityManager em) {
        String jpql = "SELECT ci.denominacion, SUM(f.importeTotal) "
                + "FROM FacturaVenta f "
                + "JOIN f.condicionIva ci "
                + "GROUP BY ci.id, ci.denominacion";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

    // =====================================================================
    // NIVEL 5: EXISTS, NOT EXISTS y CASE WHEN
    // =====================================================================

    /**
     * 20. Marcas que tienen al menos un articulo facturado.
     * EXISTS pregunta: "para ESTA marca, ¿hay al menos un resultado en la
     * subconsulta?". Se llama correlacionada porque la subconsulta usa la
     * marca "m" de la consulta de afuera (a.marca = m).
     * Hay dos EXISTS anidados: marca -> articulo -> detalle de factura.
     */
    public static List<Marca> consulta20_marcasConArticulosFacturados(EntityManager em) {
        String jpql = "SELECT m FROM Marca m "
                + "WHERE EXISTS ("
                + "    SELECT a.id FROM Articulo a "
                + "    WHERE a.marca = m "
                + "    AND EXISTS ("
                + "        SELECT d.id FROM FacturaVentaDetalle d "
                + "        JOIN d.listaPrecioArticulo lpa "
                + "        WHERE lpa.articulo = a"
                + "    )"
                + ")";
        return em.createQuery(jpql, Marca.class).getResultList();
    }

    /**
     * 21. Articulos que nunca fueron facturados.
     * Es lo contrario de EXISTS: NOT EXISTS se cumple cuando la subconsulta
     * NO devuelve ninguna fila para ese articulo.
     */
    public static List<Articulo> consulta21_articulosNuncaFacturados(EntityManager em) {
        String jpql = "SELECT a FROM Articulo a "
                + "WHERE NOT EXISTS ("
                + "    SELECT d.id FROM FacturaVentaDetalle d "
                + "    JOIN d.listaPrecioArticulo lpa "
                + "    WHERE lpa.articulo = a"
                + ")";
        return em.createQuery(jpql, Articulo.class).getResultList();
    }

    /**
     * 22. Numero, importe total y categoria calculada, de mayor a menor importe.
     * CASE WHEN es el "if / else if / else" de SQL. Se evalua en orden y se
     * queda con el primero que se cumple.
     * Decision sobre los bordes (la consigna no los define):
     *   > 50000            -> ALTO VALOR
     *   entre 10000 y 50000 (incluidos) -> MEDIO VALOR
     *   < 10000            -> BAJO VALOR
     */
    public static List<Object[]> consulta22_categoriaPorImporte(EntityManager em) {
        String jpql = "SELECT f.numero, f.importeTotal, "
                + "CASE "
                + "    WHEN f.importeTotal > 50000 THEN 'ALTO VALOR' "
                + "    WHEN f.importeTotal >= 10000 THEN 'MEDIO VALOR' "
                + "    ELSE 'BAJO VALOR' "
                + "END AS categoria "
                + "FROM FacturaVenta f "
                + "ORDER BY f.importeTotal DESC";
        return em.createQuery(jpql, Object[].class).getResultList();
    }
}
