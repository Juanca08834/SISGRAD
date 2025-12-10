package com.unicauca.formatoa.application.dto;

import com.unicauca.formatoa.domain.model.EstadoFormato;
import com.unicauca.formatoa.domain.model.Modalidad;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para Formato A
 */
public class FormatoResponse {
    private Long id;
    private String titulo;
    private Modalidad modalidad;
    private LocalDateTime fechaPresentacion;
    private String director;
    private String codirector;
    private String objetivoGeneral;
    private String objetivosEspecificos;
    private String archivoUrl;
    private EstadoFormato estado;
    private Integer numeroIntento;
    private Long docenteId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Constructor vacío
    public FormatoResponse() {}

    // Constructor completo
    public FormatoResponse(Long id, String titulo, Modalidad modalidad,
                          LocalDateTime fechaPresentacion, String director, String codirector,
                          String objetivoGeneral, String objetivosEspecificos,
                          String archivoUrl, EstadoFormato estado, Integer numeroIntento,
                          Long docenteId, LocalDateTime fechaCreacion,
                          LocalDateTime fechaActualizacion) {
        this.id = id;
        this.titulo = titulo;
        this.modalidad = modalidad;
        this.fechaPresentacion = fechaPresentacion;
        this.director = director;
        this.codirector = codirector;
        this.objetivoGeneral = objetivoGeneral;
        this.objetivosEspecificos = objetivosEspecificos;
        this.archivoUrl = archivoUrl;
        this.estado = estado;
        this.numeroIntento = numeroIntento;
        this.docenteId = docenteId;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public EstadoFormato getEstado() {
        return estado;
    }

    public void setEstado(EstadoFormato estado) {
        this.estado = estado;
    }

    public Integer getNumeroIntento() {
        return numeroIntento;
    }

    public void setNumeroIntento(Integer numeroIntento) {
        this.numeroIntento = numeroIntento;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}