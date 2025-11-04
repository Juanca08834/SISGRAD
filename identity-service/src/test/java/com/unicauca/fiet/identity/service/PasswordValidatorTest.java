
package com.unicauca.fiet.identity.service;

import com.unicauca.fiet.identity.service.password.PasswordValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del validador de contraseñas (PasswordValidator)")
class PasswordValidatorTest {

    @Test
    @DisplayName("Debe aceptar una contraseña válida con todos los requisitos")
    void validPassword_passes() {
        PasswordValidator v = PasswordValidator.defaultValidator();
        assertDoesNotThrow(() -> v.validate("Abcdef1!"));
    }

    @Test
    @DisplayName("Debe rechazar contraseña sin longitud mínima")
    void invalidPassword_tooShort_throws() {
        PasswordValidator v = PasswordValidator.defaultValidator();
        assertThrows(IllegalArgumentException.class, () -> v.validate("Abc1!"));
    }

    @Test
    @DisplayName("Debe rechazar contraseña sin mayúscula")
    void invalidPassword_noUppercase_throws() {
        PasswordValidator v = PasswordValidator.defaultValidator();
        assertThrows(IllegalArgumentException.class, () -> v.validate("abcdef1!"));
    }

    @Test
    @DisplayName("Debe rechazar contraseña sin dígito")
    void invalidPassword_noDigit_throws() {
        PasswordValidator v = PasswordValidator.defaultValidator();
        assertThrows(IllegalArgumentException.class, () -> v.validate("Abcdefg!"));
    }

    @Test
    @DisplayName("Debe rechazar contraseña sin carácter especial")
    void invalidPassword_noSpecialChar_throws() {
        PasswordValidator v = PasswordValidator.defaultValidator();
        assertThrows(IllegalArgumentException.class, () -> v.validate("Abcdef12"));
    }

    @Test
    @DisplayName("Debe rechazar contraseña muy simple")
    void invalidPassword_tooSimple_throws() {
        PasswordValidator v = PasswordValidator.defaultValidator();
        assertThrows(IllegalArgumentException.class, () -> v.validate("abc"));
    }

    @Test
    @DisplayName("Debe aceptar contraseña con múltiples caracteres especiales")
    void validPassword_multipleSpecialChars_passes() {
        PasswordValidator v = PasswordValidator.defaultValidator();
        assertDoesNotThrow(() -> v.validate("Abc123!@#"));
    }
}
