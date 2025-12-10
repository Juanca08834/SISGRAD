package com.unicauca.anteproyecto.domain.model;

import java.time.LocalDateTime;

/**
 * Entidad de dominio Anteproyecto
 * Representa un anteproyecto de trabajo de grado
 */
public class Anteproyecto {
    private Long id;
    private Long formatoAId;
    private String archivoUrl;
    private EstadoAnteproyecto estado;
    private LocalDateTime fechaSubida;
    private Long docenteId;

    // Constructor vacío
    public Anteproyecto() {
        this.estado = EstadoAnteproyecto.PENDIENTE_ASIGNACION;
        this.fechaSubida = LocalDateTime.now();
    }

    // Constructor completo
    public Anteproyecto(Long formatoAId, String archivoUrl, Long docenteId) {
        this();
        this.formatoAId = formatoAId;
        this.archivoUrl = archivoUrl;
        this.docenteId = docenteId;
    }

    // ==================== MÉTODOS DE NEGOCIO ====================

    /**
     * Verifica si el anteproyecto puede ser asignado
     */
    public boolean puedeAsignarEvaluadores() {
        return this.estado == EstadoAnteproyecto.PENDIENTE_ASIGNACION;
    }

    /**
     * Marca el anteproyecto como en evaluación
     */
    public void iniciarEvaluacion() {
        if (!puedeAsignarEvaluadores()) {
            throw new RuntimeException("No se puede iniciar la evaluación porque este anteproyecto ya tiene evaluadores asignados.");
        }
        this.estado = EstadoAnteproyecto.EN_EVALUACION;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFormatoAId() {
        return formatoAId;
    }

    public void setFormatoAId(Long formatoAId) {
        this.formatoAId = formatoAId;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public EstadoAnteproyecto getEstado() {
        return estado;
    }

    public void setEstado(EstadoAnteproyecto estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }
}