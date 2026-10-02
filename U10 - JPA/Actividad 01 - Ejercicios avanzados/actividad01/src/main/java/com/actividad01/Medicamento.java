package com.actividad01;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Medicamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String nombre;
    
    @Column
    private String droga;
    
    @Column
    private int pesoEnGramos;
    
    @ManyToMany(mappedBy = "medicamentos")
    private Set<Paciente> pacientes;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDroga() {
        return droga;
    }
    
    public void setDroga(String droga) {
        this.droga = droga;
    }
    
    public int getPesoEnGramos() {
        return pesoEnGramos;
    }
    
    public void setPesoEnGramos(int pesoEnGramos) {
        this.pesoEnGramos = pesoEnGramos;
    }
    
    public Set<Paciente> getPacientes() {
        return pacientes;
    }
    
    public void setPacientes(Set<Paciente> pacientes) {
        this.pacientes = pacientes;
    }
}
