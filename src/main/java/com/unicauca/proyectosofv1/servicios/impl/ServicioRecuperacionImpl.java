package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioUsuario;
import com.unicauca.proyectosofv1.seguridad.EncriptadorContrasenia;

public class ServicioRecuperacionImpl {
    private final RepositorioUsuario repo;
    private final EncriptadorContrasenia enc;

    public ServicioRecuperacionImpl(RepositorioUsuario repo, EncriptadorContrasenia enc) {
        this.repo = repo; this.enc = enc;
    }

    public boolean resetearPassword(String email, String nuevaContraseniaPlano) {
        Usuario u = repo.buscarPorEmail(email);
        if (u == null) return false;
        String nuevoHash = enc.generarHash(nuevaContraseniaPlano.toCharArray());
        Usuario actualizado = new Usuario(
                u.getEmail(), u.getNombres(), u.getApellidos(), u.getCelular(),
                u.getPrograma(), u.getRol(), nuevoHash, u.getCreadoEn()
        );
        repo.actualizar(actualizado);
        return true;
    }
}
