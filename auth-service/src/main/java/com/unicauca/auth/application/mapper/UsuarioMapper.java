package com.unicauca.auth.application.mapper;

import com.unicauca.auth.application.dto.AuthResponse;
import com.unicauca.auth.application.dto.RegistroRequest;
import com.unicauca.auth.domain.model.Usuario;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre DTOs y entidades de dominio
 * Separa la capa de presentación de la capa de dominio
 */
@Component
public class UsuarioMapper {

    /**
     * Convierte un RegistroRequest en una entidad Usuario de dominio
     * @param request DTO con los datos de registro
     * @return Usuario de dominio
     */
    public Usuario toUsuario(RegistroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setCelular(request.getCelular());
        usuario.setPrograma(request.getPrograma());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());
        usuario.setRol(request.getRol());
        return usuario;
    }

    /**
     * Convierte un Usuario de dominio y un token en un AuthResponse
     * @param token Token JWT generado
     * @param usuario Usuario autenticado
     * @return DTO con la respuesta de autenticación
     */
    public AuthResponse toAuthResponse(String token, Usuario usuario) {
        return new AuthResponse(
            token,
            usuario.getId(),
            usuario.getNombres(),
            usuario.getApellidos(),
            usuario.getEmail(),
            usuario.getRol(),
            usuario.getPrograma()
        );
    }
}