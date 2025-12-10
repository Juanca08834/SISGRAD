package com.unicauca.formatoa.domain.model;

import java.time.LocalDateTime;

/**
 * Entidad de dominio Evaluacion
 * Representa una evaluación realizada a un Formato A
 */
public class Evaluacion {
    private Long id;
    private Long formatoAId;
    private Long coordinadorId;
    private ResultadoEvaluacion resultado;
    private String observaciones;
    private LocalDateTime fechaEvaluacion;

    // Constructor vacío
    public Evaluacion() {
        this.fechaEvaluacion = LocalDateTime.now();
        this.resultado = ResultadoEvaluacion.PENDIENTE;
    }

    // Constructor completo
    public Evaluacion(Long formatoAId, Long coordinadorId, ResultadoEvaluacion resultado,
                     String observaciones) {
        this();
        this.formatoAId = formatoAId;
        this.coordinadorId = coordinadorId;
        this.resultado = resultado;
        this.observaciones = observaciones;
    }

    // ==================== MÉTODOS DE NEGOCIO ====================

    /**
     * Verifica si la evaluación fue aprobada
     */
    public boolean esAprobada() {
        return this.resultado == ResultadoEvaluacion.APROBADO;
    }

    /**
     * Verifica si la evaluación fue rechazada
     */
    public boolean esRechazada() {
        return this.resultado == ResultadoEvaluacion.RECHAZADO;
    }

    /**
     * Verifica si tiene observaciones
     */
    public boolean tieneObservaciones() {
        return this.observaciones != null && !this.observaciones.trim().isEmpty();
    }

    // ==================== GETTERS Y SETTERS ====================

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

    public Long getCoordinadorId() {
        return coordinadorId;
    }

    public void setCoordinadorId(Long coordinadorId) {
        this.coordinadorId = coordinadorId;
    }

    public ResultadoEvaluacion getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoEvaluacion resultado) {
        this.resultado = resultado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }
}