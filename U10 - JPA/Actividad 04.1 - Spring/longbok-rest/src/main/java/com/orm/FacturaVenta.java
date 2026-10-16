package com.orm;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "factura_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FacturaVenta extends AuditoriaApp {
    private Long numero;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @ManyToOne
    @JoinColumn(nullable = true)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(nullable = false)
    private CondicionIva condicionIva;

    @ManyToOne
    @JoinColumn(nullable = false)
    private TipoMoneda tipoMoneda;

    @ManyToOne
    @JoinColumn(nullable = false)
    private PuntoVenta puntoVenta;

    private double importeCobrado;
    private double importeSaldo;

    @Column(nullable = false)
    private double importeTotal;

    private String cae;
    private LocalDateTime caeFechaVencimiento;
    private String resultadoAfip;
    private String motivoRechazo;

    @Column(nullable = false)
    private String estado;

    private LocalDateTime fechaAnulacion;
    private String observaciones;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<FacturaVentaDetalle> detalles;
}
