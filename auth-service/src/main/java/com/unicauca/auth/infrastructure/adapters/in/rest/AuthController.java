package com.unicauca.auth.infrastructure.adapters.in.rest;


import com.unicauca.auth.application.dto.AuthResponse;
import com.unicauca.auth.application.dto.LoginRequest;
import com.unicauca.auth.application.dto.RegistroRequest;
import com.unicauca.auth.application.mapper.UsuarioMapper;
import com.unicauca.auth.domain.model.Usuario;
import com.unicauca.auth.domain.ports.in.AutenticarUsuarioUseCase;
import com.unicauca.auth.domain.ports.in.RegistrarUsuarioUseCase;
import com.unicauca.auth.domain.ports.in.ValidarTokenUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "API para registro, login y validación de usuarios")
public class AuthController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final ValidarTokenUseCase validarTokenUseCase;
    private final UsuarioMapper usuarioMapper;

    public AuthController(RegistrarUsuarioUseCase registrarUsuarioUseCase,
                         AutenticarUsuarioUseCase autenticarUsuarioUseCase,
                         ValidarTokenUseCase validarTokenUseCase,
                         UsuarioMapper usuarioMapper) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.validarTokenUseCase = validarTokenUseCase;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping("/registro")
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Registra un nuevo usuario (docente, coordinador, jefe de departamento)",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Usuario registrado exitosamente",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
        }
    )
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        Usuario usuario = usuarioMapper.toUsuario(request);
        Usuario usuarioGuardado = registrarUsuarioUseCase.registrar(usuario);
        String token = autenticarUsuarioUseCase.autenticar(
            usuarioGuardado.getEmail(), 
            request.getPassword()
        );
        AuthResponse response = usuarioMapper.toAuthResponse(token, usuarioGuardado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario y devuelve un token JWT",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Login exitoso",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
        }
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = autenticarUsuarioUseCase.autenticar(
            request.getEmail(), 
            request.getPassword()
        );
        Usuario usuario = autenticarUsuarioUseCase.obtenerUsuarioPorEmail(request.getEmail());
        AuthResponse response = usuarioMapper.toAuthResponse(token, usuario);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validar")
    @Operation(
        summary = "Validar token JWT",
        description = "Valida un token JWT y retorna información del usuario",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token válido"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
        }
    )
    public ResponseEntity<Map<String, Object>> validarToken(
            @RequestHeader("Authorization") String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        boolean esValido = validarTokenUseCase.validarToken(token);

        if (!esValido) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("valido", true);
        response.put("email", validarTokenUseCase.extraerEmail(token));
        response.put("rol", validarTokenUseCase.extraerRol(token));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(
        summary = "Obtener usuario actual",
        description = "Obtiene la información del usuario autenticado"
    )
    public ResponseEntity<Usuario> obtenerUsuarioActual(
            @RequestHeader("Authorization") String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        String email = validarTokenUseCase.extraerEmail(token);
        Usuario usuario = autenticarUsuarioUseCase.obtenerUsuarioPorEmail(email);

        return ResponseEntity.ok(usuario);
    }
}