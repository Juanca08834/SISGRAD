package com.unicauca.anteproyecto.domain.model;

/**
 * Estados del anteproyecto
 */
public enum EstadoAnteproyecto {
    PENDIENTE_ASIGNACION("Pendiente de asignación de evaluadores"),
    EN_EVALUACION("En evaluación"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado");

    private final String descripcion;

    EstadoAnteproyecto(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}