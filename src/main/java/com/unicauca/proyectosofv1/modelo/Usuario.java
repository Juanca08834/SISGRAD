package com.unicauca.proyectosofv1.modelo;

import java.time.LocalDateTime;

public class Usuario {
    private final String email;
    private final String nombres;
    private final String apellidos;
    private final String celular;
    private final String programa;
    private final String rol;
    private final String passwordHash;
    private final LocalDateTime creadoEn;

    public Usuario(String email, String nombres, String apellidos, String celular,
                   String programa, String rol, String passwordHash, LocalDateTime creadoEn) {
        this.email = email;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
        this.programa = programa;
        this.rol = rol;
        this.passwordHash = passwordHash;
        this.creadoEn = creadoEn;
    }

    public String getEmail() { return email; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getCelular() { return celular; }
    public String getPrograma() { return programa; }
    public String getRol() { return rol; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}
