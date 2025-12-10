package com.unicauca.auth.domain.model;

/**
 * Enum que representa los programas académicos disponibles
 * en la Universidad del Cauca
 */
public enum Programa {
    INGENIERIA_SISTEMAS("Ingeniería de Sistemas"),
    INGENIERIA_ELECTRONICA("Ingeniería Electrónica y Telecomunicaciones"),
    AUTOMATICA_INDUSTRIAL("Automática Industrial"),
    TECNOLOGIA_TELEMATICA("Tecnología en Telemática");

    private final String nombre;

    Programa(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}