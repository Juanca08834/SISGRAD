package com.unicauca.fiet.identity.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Respuesta de autenticación con el JWT emitido. */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
}
