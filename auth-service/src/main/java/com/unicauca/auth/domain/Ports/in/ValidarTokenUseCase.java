package com.unicauca.auth.domain.ports.in;

/**
 * Puerto de entrada (Use Case) para validación de tokens JWT
 */
public interface ValidarTokenUseCase {
    /**
     * Valida si un token JWT es válido
     * @param token Token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    boolean validarToken(String token);
    
    /**
     * Extrae el email del usuario desde el token JWT
     * @param token Token JWT
     * @return Email del usuario
     */
    String extraerEmail(String token);
    
    /**
     * Extrae el rol del usuario desde el token JWT
     * @param token Token JWT
     * @return Rol del usuario
     */
    String extraerRol(String token);
}