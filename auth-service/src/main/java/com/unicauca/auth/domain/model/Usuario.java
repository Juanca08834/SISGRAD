package com.unicauca.auth.domain.model;

import java.time.LocalDateTime;

/**
 * Entidad de dominio Usuario
 * Representa un usuario del sistema (docente, estudiante, coordinador, jefe)
 * Esta clase contiene la lógica de negocio pura, sin dependencias de frameworks
 */
public class Usuario {
    private Long id;
    private String nombres;
    private String apellidos;
    private String celular;
    private Programa programa;
    private String email;
    private String password;
    private Rol rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;

    // Constructor vacío
    public Usuario() {
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor completo
    public Usuario(Long id, String nombres, String apellidos, String celular,
                   Programa programa, String email, String password, Rol rol) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
        this.programa = programa;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    // ==================== MÉTODOS DE NEGOCIO ====================
    
    /**
     * Valida que la contraseña cumpla con los requisitos de seguridad:
     * - Mínimo 6 caracteres
     * - Al menos un dígito
     * - Al menos una mayúscula
     * - Al menos un carácter especial
     */
    public boolean isPasswordValid(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        boolean tieneDigito = password.matches(".*\\d.*");
        boolean tieneMayuscula = password.matches(".*[A-Z].*");
        boolean tieneEspecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        
        return tieneDigito && tieneMayuscula && tieneEspecial;
    }

    /**
     * Verifica que el email sea institucional (@unicauca.edu.co)
     */
    public boolean isEmailInstitucional() {
        return email != null && email.endsWith("@unicauca.edu.co");
    }

    /**
     * Retorna el nombre completo del usuario
     */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    /**
     * Actualiza la fecha del último acceso al sistema
     */
    public void actualizarUltimoAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
    }

    // ==================== GETTERS Y SETTERS ====================

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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }
}