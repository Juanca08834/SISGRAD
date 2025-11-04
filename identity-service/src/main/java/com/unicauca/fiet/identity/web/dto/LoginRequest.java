package com.unicauca.fiet.identity.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** DTO de entrada para autenticación de docentes. */
@Data
public class LoginRequest {
    @Email @NotBlank private String email;
    @NotBlank private String password;
}
