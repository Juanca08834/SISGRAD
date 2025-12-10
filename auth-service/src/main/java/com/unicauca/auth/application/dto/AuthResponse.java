package com.unicauca.auth.application.dto;

import com.unicauca.auth.domain.model.Programa;
import com.unicauca.auth.domain.model.Rol;

/**
 * DTO para la respuesta de autenticación exitosa
 * Contiene el token JWT y la información básica del usuario
 */
public class AuthResponse {
    private String token;
    private String tipo;
    private Long id;
    private String nombres;
    private String apellidos;
    private String email;
    private Rol rol;
    private Programa programa;

    // Constructor vacío
    public AuthResponse() {
        this.tipo = "Bearer";
    }

    // Constructor completo
    public AuthResponse(String token, Long id, String nombres, String apellidos,
                       String email, Rol rol, Programa programa) {
        this.token = token;
        this.tipo = "Bearer";
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.rol = rol;
        this.programa = programa;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }
}