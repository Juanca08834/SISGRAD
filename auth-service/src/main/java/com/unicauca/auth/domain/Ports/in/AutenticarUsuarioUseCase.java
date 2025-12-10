package com.unicauca.auth.domain.ports.in;

import com.unicauca.auth.domain.model.Usuario;

/**
 * Puerto de entrada (Use Case) para autenticación de usuarios
 */
public interface AutenticarUsuarioUseCase {
    /**
     * Autentica un usuario con email y contraseña
     * @param email Email del usuario
     * @param password Contraseña en texto plano
     * @return Token JWT generado
     * @throws RuntimeException si las credenciales son inválidas
     */
    String autenticar(String email, String password);
    
    /**
     * Obtiene un usuario por su email
     * @param email Email del usuario
     * @return Usuario encontrado
     * @throws RuntimeException si el usuario no existe
     */
    Usuario obtenerUsuarioPorEmail(String email);
}