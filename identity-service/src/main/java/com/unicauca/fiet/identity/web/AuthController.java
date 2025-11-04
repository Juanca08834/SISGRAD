package com.unicauca.fiet.identity.web;

import com.unicauca.fiet.identity.domain.Program;
import com.unicauca.fiet.identity.domain.Teacher;
import com.unicauca.fiet.identity.service.TeacherService;
import com.unicauca.fiet.identity.web.dto.AuthResponse;
import com.unicauca.fiet.identity.web.dto.LoginRequest;
import com.unicauca.fiet.identity.web.dto.RegisterTeacherRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación")
public class AuthController {

    private final TeacherService teacherService;

    public AuthController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @Operation(summary = "Registro de docentes (requisito 1)")
    @PostMapping("/register")
    public ResponseEntity<Teacher> register(@Valid @RequestBody RegisterTeacherRequest dto) {
        Teacher t = teacherService.registerTeacher(
                dto.getNombres(), dto.getApellidos(), dto.getCelular(),
                dto.getPrograma() == null ? Program.INGENIERIA_SISTEMAS : dto.getPrograma(),
                dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(t);
    }

    @Operation(summary = "Login de docentes")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        String token = teacherService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Operation(summary = "Obtener perfil del usuario autenticado", security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/me")
    public ResponseEntity<Teacher> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        Teacher teacher = teacherService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        return ResponseEntity.ok(teacher);
    }

    // ✅ Cambiado de Long a UUID
    @Operation(summary = "Obtener docente por ID", security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/users/{id}")
    public ResponseEntity<Teacher> getUserById(@PathVariable UUID id) {
        Teacher teacher = teacherService.findById(id);
        return ResponseEntity.ok(teacher);
    }
}
