package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioUsuario;
import com.unicauca.proyectosofv1.seguridad.EncriptadorContrasenia;
import com.unicauca.proyectosofv1.servicios.ServicioAuth;

public class ServicioAuthImpl implements ServicioAuth {
    private final RepositorioUsuario repo;
    private final EncriptadorContrasenia encriptador;

    public ServicioAuthImpl(RepositorioUsuario repo, EncriptadorContrasenia encriptador) {
        this.repo = repo;
        this.encriptador = encriptador;
    }

    @Override
    public boolean login(String email, String contraseniaPlano) {
        Usuario u = repo.buscarPorEmail(email);
        if (u == null) return false;
        return encriptador.verificar(contraseniaPlano.toCharArray(), u.getPasswordHash());
    }

    @Override
    public Usuario loginYObtener(String email, String contraseniaPlano) {
        Usuario u = repo.buscarPorEmail(email);
        if (u == null) return null;
        return encriptador.verificar(contraseniaPlano.toCharArray(), u.getPasswordHash()) ? u : null;
    }
}
