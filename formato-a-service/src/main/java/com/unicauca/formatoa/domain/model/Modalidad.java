package com.unicauca.formatoa.domain.model;

/**
 * Modalidades de trabajo de grado
 */
public enum Modalidad {
    INVESTIGACION("Investigación"),
    PRACTICA_PROFESIONAL("Práctica Profesional");

    private final String descripcion;

    Modalidad(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}