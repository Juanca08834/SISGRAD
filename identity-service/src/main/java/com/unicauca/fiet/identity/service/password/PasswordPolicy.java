package com.unicauca.fiet.identity.service.password;

/**
 * Estrategia (Strategy) de validación de contraseñas.
 * Cada implementación valida una regla diferente.
 */
public interface PasswordPolicy {
    /**
     * Lanza IllegalArgumentException si la contraseña no cumple la regla.
     */
    void validate(String rawPassword);
}
