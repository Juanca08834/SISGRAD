package com.unicauca.fiet.identity.service.password;

/** Regla: al menos una mayúscula. */
public class UppercasePolicy implements PasswordPolicy {
    public void validate(String rawPassword) {
        if (rawPassword == null || !rawPassword.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una mayúscula.");
        }
    }
}
