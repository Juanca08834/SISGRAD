package com.unicauca.fiet.identity.service.password;

/** Regla: al menos un dígito. */
public class DigitPolicy implements PasswordPolicy {
    public void validate(String rawPassword) {
        if (rawPassword == null || !rawPassword.chars().anyMatch(Character::isDigit)) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un dígito.");
        }
    }
}
