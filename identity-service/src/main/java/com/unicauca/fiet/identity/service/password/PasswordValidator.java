package com.unicauca.fiet.identity.service.password;

import java.util.List;

/**
 * Valida una contraseña aplicando una lista de estrategias (Strategy pattern).
 */
public class PasswordValidator {

    private final List<PasswordPolicy> policies;

    public PasswordValidator(List<PasswordPolicy> policies) {
        this.policies = policies;
    }

    /**
     * Ejecuta todas las reglas configuradas. Si alguna falla, lanza IllegalArgumentException.
     * @param rawPassword contraseña en texto claro
     */
    public void validate(String rawPassword) {
        for (PasswordPolicy p : policies) {
            p.validate(rawPassword);
        }
    }

    /**
     * Creador de validador por defecto (Factory Method pattern).
     * @return validador con reglas: longitud>=6, dígito, especial, mayúscula.
     */
    public static PasswordValidator defaultValidator() {
        return new PasswordValidator(List.of(
                new LengthPolicy(6),
                new DigitPolicy(),
                new SpecialCharPolicy(),
                new UppercasePolicy()
        ));
    }
}
