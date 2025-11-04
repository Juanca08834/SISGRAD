package com.unicauca.fiet.identity.service.password;

/** Regla: al menos un caracter especial. */
public class SpecialCharPolicy implements PasswordPolicy {
    public void validate(String rawPassword) {
        if (rawPassword == null || !rawPassword.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un caracter especial.");
        }
    }
}
