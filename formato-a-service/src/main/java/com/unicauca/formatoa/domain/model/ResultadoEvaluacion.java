package com.unicauca.formatoa.domain.model;

/**
 * Resultado de una evaluación de Formato A
 */
public enum ResultadoEvaluacion {
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado"),
    PENDIENTE("Pendiente");

    private final String descripcion;

    ResultadoEvaluacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}