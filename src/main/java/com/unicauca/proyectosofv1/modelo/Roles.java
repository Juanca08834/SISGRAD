package com.unicauca.proyectosofv1.modelo;

import java.text.Normalizer;
import java.util.Set;

public final class Roles {
    public static final String DOCENTE     = "docente";
    public static final String ESTUDIANTE  = "estudiante";
    public static final String COORDINADOR = "coordinador";

    private static final Set<String> CANONICOS = Set.of(DOCENTE, ESTUDIANTE, COORDINADOR);

    private Roles() {}

    /**
     * Devuelve el rol canónico (docente|estudiante|coordinador),
     * o null si no reconoce/está vacío.
     */
    public static String canonico(String raw) {
        if (raw == null) return null;

        // Normaliza tildes y espacios raros
        String s = Normalizer.normalize(raw, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")  // quita marcas diacríticas
                .toLowerCase()
                .trim();

        if (s.isEmpty() || s.startsWith("seleccione")) return null;

        // Acepta singular/plural y variaciones comunes
        if (s.matches("^docente(s)?$"))      return DOCENTE;
        if (s.matches("^estudiante(s)?$"))   return ESTUDIANTE;

        // Coord.* (coordinacion/coordinador/coordinadora/coords/etc.)
        if (s.matches("^coord.*"))           return COORDINADOR;

        return null;
    }

    /** ¿Es exactamente uno de los roles canónicos? */
    public static boolean esCanonico(String s) {
        return CANONICOS.contains(s);
    }

    /**
     * Devuelve el rol canónico o lanza IllegalArgumentException con mensaje claro.
     * Útil en servicios para fallar temprano si llega un rol inválido.
     */
    public static String requireCanonico(String raw) {
        String c = canonico(raw);
        if (c == null) {
            throw new IllegalArgumentException("Rol inválido: " + raw +
                    ". Use: " + CANONICOS);
        }
        return c;
    }
}
