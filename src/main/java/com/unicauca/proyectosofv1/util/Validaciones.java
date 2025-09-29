package com.unicauca.proyectosofv1.util;

import java.util.regex.Pattern;



public final class Validaciones {
    // ≥6 chars, al menos 1 mayúscula, 1 dígito, 1 símbolo
    private static final Pattern POLITICA_CONTRASENA =
            Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{6,}$");

    // Email razonable (RFC relajado)
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validaciones() {}

    public static boolean esContrasenaValida(String pwd) {
        return pwd != null && POLITICA_CONTRASENA.matcher(pwd).matches();
    }

    public static boolean esEmailValido(String email) {
        return email != null && EMAIL.matcher(email).matches();
    }

    /** Si tienes dominio institucional fijo, descomenta y usa esto:
    public static boolean esEmailInstitucional(String email) {
        return esEmailValido(email) && email.toLowerCase().endsWith("@tu-dominio.edu.co");
    } */
}
