package com.actividad01;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
public class Paciente extends Base {
    
    @Column
    private String nombre;
    
    @Column
    private String apellido;
    
    @Column
    private int edad;
    
    @Column
    private String dni;
    
    @Column
    private String obraSocial;
    
    @Column
    private LocalDate fechaNacimiento;
    
    @Column
    private char sexo;
    
    @OneToMany(mappedBy = "paciente")
    private Set<Consulta> consultas;
    
    @OneToOne
    private HistoriaClinica historiaClinica;
    
    @ManyToMany
    private Set<Medicamento> medicamentos;
    
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
    
    public String getDni() {
        return dni;
    }
    
    public void setDni(String dni) {
        this.dni = dni;
    }
    
    public String getObraSocial() {
        return obraSocial;
    }
    
    public void setObraSocial(String obraSocial) {
        this.obraSocial = obraSocial;
    }
    
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public char getSexo() {
        return sexo;
    }
    
    public void setSexo(char sexo) {
        this.sexo = sexo;
    }
    
    public Set<Consulta> getConsultas() {
        return consultas;
    }
    
    public void setConsultas(Set<Consulta> consultas) {
        this.consultas = consultas;
    }
    
    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }
    
    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }
    
    public Set<Medicamento> getMedicamentos() {
        return medicamentos;
    }
    
    public void setMedicamentos(Set<Medicamento> medicamentos) {
        this.medicamentos = medicamentos;
    }
}
