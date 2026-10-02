package com.actividad01;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class Base {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    @Column
    protected Boolean eliminado;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Boolean getEliminado() {
        return eliminado;
    }
    
    public void setEliminado(Boolean eliminado) {
        this.eliminado = eliminado;
    }
}
