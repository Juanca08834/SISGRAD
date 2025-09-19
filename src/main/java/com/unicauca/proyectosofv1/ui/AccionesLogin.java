package com.unicauca.proyectosofv1.ui;

/** Puerto de la vista de Login: la UI solo notifica eventos y no conoce la lógica. */
public interface AccionesLogin {
    void alIniciarSesion(String correo, char[] contrasenia);
    void alSolicitarRegistro();
    void alOlvideContrasenia();
}
