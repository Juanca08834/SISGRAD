package com.unicauca.fiet.identity.service;

import com.unicauca.fiet.identity.domain.Program;
import com.unicauca.fiet.identity.domain.Teacher;

/**
 * Builder (GoF) para construir docentes de forma fluida y legible.
 * Facilita la creación a partir de DTOs y aplica defaults coherentes.
 */
public class TeacherBuilder {
    private String nombres;
    private String apellidos;
    private String celular;
    private Program programa;
    private String email;
    private String passwordHash;

    public TeacherBuilder nombres(String n) { this.nombres = n; return this; }
    public TeacherBuilder apellidos(String a) { this.apellidos = a; return this; }
    public TeacherBuilder celular(String c) { this.celular = c; return this; }
    public TeacherBuilder programa(Program p) { this.programa = p; return this; }
    public TeacherBuilder email(String e) { this.email = e; return this; }
    public TeacherBuilder passwordHash(String ph) { this.passwordHash = ph; return this; }

    /** Construye la entidad Teacher con rol DOCENTE por defecto. */
    public Teacher build() {
        return Teacher.builder()
                .nombres(nombres)
                .apellidos(apellidos)
                .celular(celular)
                .programa(programa)
                .email(email)
                .passwordHash(passwordHash)
                .roles("DOCENTE")
                .build();
    }
}
