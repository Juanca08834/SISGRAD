package com.unicauca.proyectosofv1.repositorio;

import com.unicauca.proyectosofv1.modelo.Usuario;

public interface RepositorioUsuario {
    Usuario buscarPorEmail(String email);
    void guardar(Usuario usuario);
    void actualizar(Usuario usuario);
}
