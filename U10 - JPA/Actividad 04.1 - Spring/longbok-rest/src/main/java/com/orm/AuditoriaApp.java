package com.orm;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AuditoriaApp extends EntityId {
    @Column(nullable = false)
    private LocalDateTime fechaAlta;

    @Column
    private LocalDateTime fechaBaja;

    @Column(nullable = false)
    private LocalDateTime fechaModificacion;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Usuario usuarioCarga;

    @ManyToOne
    private Usuario usuarioBaja;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Usuario usuarioModificacion;
}
