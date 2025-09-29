package com.unicauca.proyectosofv1.ui;

@FunctionalInterface
public interface AccionesRecuperacion {
    void alEnviarSolicitud(DatosRecuperacion datos);
    default void alCancelar() { /* no-op */ }
}