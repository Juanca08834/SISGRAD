package com.unicauca.formatoa.domain.model;

/**
 * Estados posibles de un Formato A
 */
public enum EstadoFormato {
    EN_PRIMERA_EVALUACION("En primera evaluación"),
    EN_SEGUNDA_EVALUACION("En segunda evaluación"),
    EN_TERCERA_EVALUACION("En tercera evaluación"),
    RECHAZADO_PRIMERA_EVALUACION("Rechazado en primera evaluación - Requiere nueva versión"),
    RECHAZADO_SEGUNDA_EVALUACION("Rechazado en segunda evaluación - Requiere nueva versión"),
    ACEPTADO("Aceptado"),
    RECHAZADO("Rechazado definitivamente");

    private final String descripcion;

    EstadoFormato(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}