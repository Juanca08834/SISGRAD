package com.unicauca.proyectosofv1.servicios;

import com.unicauca.proyectosofv1.excepciones.SISGRADException;

public interface ServicioRegistro {
    void registrar(String nombres, String apellidos, String celular,
                   String programa, String rol, String email, String contraseniaPlano)
            throws SISGRADException;
}
