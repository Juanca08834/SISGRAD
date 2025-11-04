package com.unicauca.fiet.identity.service.password;

/** Regla: longitud mínima. */
public class LengthPolicy implements PasswordPolicy {
    private final int min;

    public LengthPolicy(int min) { this.min = min; }

    public void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < min) {
            throw new IllegalArgumentException("La contraseña debe tener al menos " + min + " caracteres.");
        }
    }
}
