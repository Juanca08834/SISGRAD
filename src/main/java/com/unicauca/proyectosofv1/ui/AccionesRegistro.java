package com.unicauca.proyectosofv1.ui;

@FunctionalInterface
public interface AccionesRegistro {
    void alEnviar(DatosRegistro datos);
    default void alCancelar() { /* no-op */ }
}