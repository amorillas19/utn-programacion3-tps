package com.orm.reportes;

import com.orm.dto.FacturaReporteDTO;
import jakarta.persistence.EntityManager;

import java.util.List;

public final class FacturaReporteQueries {

    private FacturaReporteQueries() {
    }

    // LEFT JOIN en detalles: una factura sin items igual aparece (con cantidadItems = 0).
    // LEFT JOIN en cliente: si no hay cliente, COALESCE devuelve 'Consumidor Final'.
    private static final String JPQL =
            "SELECT new com.orm.dto.FacturaReporteDTO(" +
            "  f.numero, " +
            "  f.fechaEmision, " +
            "  COALESCE(c.denominacion, 'Consumidor Final'), " +
            "  ci.denominacion, " +
            "  pv.descripcion, " +
            "  f.importeTotal, " +
            "  COUNT(d)" +
            ") " +
            "FROM FacturaVenta f " +
            "LEFT JOIN f.cliente c " +
            "JOIN f.condicionIva ci " +
            "JOIN f.puntoVenta pv " +
            "LEFT JOIN f.detalles d " +
            "GROUP BY f.id, f.numero, f.fechaEmision, c.denominacion, " +
            "         ci.denominacion, pv.descripcion, f.importeTotal " +
            "ORDER BY f.numero";

    public static List<FacturaReporteDTO> listarReporte(EntityManager em) {
        return em.createQuery(JPQL, FacturaReporteDTO.class).getResultList();
    }
}
