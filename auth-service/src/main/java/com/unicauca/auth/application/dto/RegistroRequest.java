package com.unicauca.auth.application.dto;

import com.unicauca.auth.domain.model.Programa;
import com.unicauca.auth.domain.model.Rol;
import jakarta.validation.constraints.*;

/**
 * DTO para la solicitud de registro de un nuevo usuario
 * Incluye validaciones de Bean Validation
 */
public class RegistroRequest {
    
    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;
    
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;
    
    private String celular; // Opcional
    
    @NotNull(message = "El programa es obligatorio")
    private Programa programa;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Pattern(regexp = ".*@unicauca\\.edu\\.co$", 
             message = "Debe usar un email institucional (@unicauca.edu.co)")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
    private String password;
    
    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    // Constructores
    public RegistroRequest() {}

    public RegistroRequest(String nombres, String apellidos, String celular,
                          Programa programa, String email, String password, Rol rol) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
        this.programa = programa;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters
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

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}