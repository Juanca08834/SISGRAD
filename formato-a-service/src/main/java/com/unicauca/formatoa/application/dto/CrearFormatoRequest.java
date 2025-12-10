package com.unicauca.formatoa.application.dto;

import com.unicauca.formatoa.domain.model.Modalidad;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * DTO para crear un Formato A
 */
public class CrearFormatoRequest {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 10, max = 500, message = "El título debe tener entre 10 y 500 caracteres")
    private String titulo;
    
    @NotNull(message = "La modalidad es obligatoria")
    private Modalidad modalidad;
    
    @NotNull(message = "La fecha de presentación es obligatoria")
    private LocalDateTime fechaPresentacion;
    
    @NotBlank(message = "El director es obligatorio")
    private String director;
    
    private String codirector; // Opcional
    
    @NotBlank(message = "El objetivo general es obligatorio")
    @Size(min = 50, max = 1000, message = "El objetivo general debe tener entre 50 y 1000 caracteres")
    private String objetivoGeneral;
    
    @NotBlank(message = "Los objetivos específicos son obligatorios")
    @Size(min = 100, max = 2000, message = "Los objetivos específicos deben tener entre 100 y 2000 caracteres")
    private String objetivosEspecificos;
    
    @NotBlank(message = "La URL del archivo PDF es obligatoria")
    private String archivoUrl;
    
    @NotNull(message = "El ID del docente es obligatorio")
    private Long docenteId;

    // Constructores
    public CrearFormatoRequest() {}

    // Getters y Setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public void setModalidad(Modalidad modalidad) {
        this.modalidad = modalidad;
    }

    public LocalDateTime getFechaPresentacion() {
        return fechaPresentacion;
    }

    public void setFechaPresentacion(LocalDateTime fechaPresentacion) {
        this.fechaPresentacion = fechaPresentacion;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCodirector() {
        return codirector;
    }

    public void setCodirector(String codirector) {
        this.codirector = codirector;
    }

    public String getObjetivoGeneral() {
        return objetivoGeneral;
    }

    public void setObjetivoGeneral(String objetivoGeneral) {
        this.objetivoGeneral = objetivoGeneral;
    }

    public String getObjetivosEspecificos() {
        return objetivosEspecificos;
    }

    public void setObjetivosEspecificos(String objetivosEspecificos) {
        this.objetivosEspecificos = objetivosEspecificos;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }
}