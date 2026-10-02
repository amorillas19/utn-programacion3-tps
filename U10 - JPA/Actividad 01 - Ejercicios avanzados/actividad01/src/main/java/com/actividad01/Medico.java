package com.actividad01;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Medico extends Base {
    
    @Column
    private String nombre;
    
    @Column
    private String apellido;
    
    @Column
    private int edad;
    
    @Column
    private String especialidad;
    
    @Column
    private String matricula;
    
    @OneToMany(mappedBy = "medico")
    private Set<Consulta> consultas;
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public int getEdad() {
        return edad;
    }
    
    public void setEdad(int edad) {
        this.edad = edad;
    }
    
    public String getEspecialidad() {
        return especialidad;
    }
    
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    
    public String getMatricula() {
        return matricula;
    }
    
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    
    public Set<Consulta> getConsultas() {
        return consultas;
    }
    
    public void setConsultas(Set<Consulta> consultas) {
        this.consultas = consultas;
    }
}
