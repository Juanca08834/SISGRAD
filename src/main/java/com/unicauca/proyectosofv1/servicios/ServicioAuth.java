package com.unicauca.proyectosofv1.servicios;

import com.unicauca.proyectosofv1.modelo.Usuario;

public interface ServicioAuth {
    boolean login(String email, String contraseniaPlano);
    Usuario loginYObtener(String email, String contraseniaPlano);
}
