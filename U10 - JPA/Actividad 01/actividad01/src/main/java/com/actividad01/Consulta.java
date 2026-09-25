package com.actividad01;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Consulta extends Base {
    
    @Column
    private LocalDate fecha;
    
    @Column
    private String diagnostico;
    
    @ManyToOne
    private Paciente paciente;
    
    @ManyToOne
    private Medico medico;
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public String getDiagnostico() {
        return diagnostico;
    }
    
    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }
    
    public Paciente getPaciente() {
        return paciente;
    }
    
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
    
    public Medico getMedico() {
        return medico;
    }
    
    public void setMedico(Medico medico) {
        this.medico = medico;
    }
}
