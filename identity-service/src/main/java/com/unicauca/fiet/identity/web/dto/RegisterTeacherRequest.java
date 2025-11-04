package com.unicauca.fiet.identity.web.dto;

import com.unicauca.fiet.identity.domain.Program;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** DTO de entrada para registrar un Docente. */
@Data
public class RegisterTeacherRequest {
    @NotBlank private String nombres;
    @NotBlank private String apellidos;
    private String celular;
    private Program programa;
    @Email @NotBlank private String email;
    @NotBlank private String password;
}
