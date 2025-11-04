package com.unicauca.fiet.identity.service;

import com.unicauca.fiet.identity.domain.Program;
import com.unicauca.fiet.identity.domain.Teacher;
import com.unicauca.fiet.identity.repo.TeacherRepository;
import com.unicauca.fiet.identity.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas del servicio de Docentes (TeacherService)")
class TeacherServiceTest {

    private TeacherRepository repo;
    private TeacherService service;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(TeacherRepository.class);
        JwtService jwt = new JwtService("secretsecretsecretsecretsecret123456", 60000);
        service = new TeacherService(repo, jwt);
    }

    @Test
    @DisplayName("Debe registrar un docente exitosamente con datos válidos")
    void registerTeacher_success() {
        when(repo.existsByEmail("docente@unicauca.edu.co")).thenReturn(false);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Teacher t = service.registerTeacher("Juan", "Pérez", "3001234567",
                Program.INGENIERIA_SISTEMAS, "docente@unicauca.edu.co", "Abcdef1!");

        assertEquals("docente@unicauca.edu.co", t.getEmail());
        assertEquals("Juan", t.getNombres());
        assertEquals("Pérez", t.getApellidos());
        assertEquals(Program.INGENIERIA_SISTEMAS, t.getPrograma());
        assertNotNull(t.getPasswordHash());
        verify(repo, times(1)).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Debe registrar un docente sin celular (campo opcional)")
    void registerTeacher_withoutCelular_success() {
        when(repo.existsByEmail("docente@unicauca.edu.co")).thenReturn(false);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Teacher t = service.registerTeacher("María", "González", null,
                Program.INGENIERIA_ELECTRONICA_Y_TELECOMUNICACIONES, "docente@unicauca.edu.co", "Secure123!");

        assertNull(t.getCelular());
        assertEquals("María", t.getNombres());
        verify(repo, times(1)).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email ya está registrado")
    void registerTeacher_duplicateEmail_throwsException() {
        when(repo.existsByEmail("docente@unicauca.edu.co")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.registerTeacher("Juan", "Pérez", null,
                Program.INGENIERIA_SISTEMAS, "docente@unicauca.edu.co", "Abcdef1!"));

        assertEquals("El email ya está registrado.", exception.getMessage());
        verify(repo, never()).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la contraseña no cumple los requisitos mínimos")
    void registerTeacher_invalidPassword_throwsException() {
        when(repo.existsByEmail("docente@unicauca.edu.co")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> service.registerTeacher("Juan", "Pérez", null,
                Program.INGENIERIA_SISTEMAS, "docente@unicauca.edu.co", "abc"));

        verify(repo, never()).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la contraseña no tiene mayúscula")
    void registerTeacher_passwordWithoutUppercase_throwsException() {
        when(repo.existsByEmail("docente@unicauca.edu.co")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> service.registerTeacher("Juan", "Pérez", null,
                Program.INGENIERIA_SISTEMAS, "docente@unicauca.edu.co", "abcdef1!"));

        verify(repo, never()).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la contraseña no tiene dígito")
    void registerTeacher_passwordWithoutDigit_throwsException() {
        when(repo.existsByEmail("docente@unicauca.edu.co")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> service.registerTeacher("Juan", "Pérez", null,
                Program.INGENIERIA_SISTEMAS, "docente@unicauca.edu.co", "Abcdefg!"));

        verify(repo, never()).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la contraseña no tiene carácter especial")
    void registerTeacher_passwordWithoutSpecialChar_throwsException() {
        when(repo.existsByEmail("docente@unicauca.edu.co")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> service.registerTeacher("Juan", "Pérez", null,
                Program.INGENIERIA_SISTEMAS, "docente@unicauca.edu.co", "Abcdef12"));

        verify(repo, never()).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Debe autenticar correctamente y retornar JWT con credenciales válidas")
    void login_success() {
        Teacher t = Teacher.builder()
                .email("docente@unicauca.edu.co")
                .passwordHash(new BCryptPasswordEncoder().encode("Abcdef1!"))
                .roles("DOCENTE")
                .build();
        when(repo.findByEmail("docente@unicauca.edu.co")).thenReturn(Optional.of(t));

        String token = service.login("docente@unicauca.edu.co", "Abcdef1!");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email no existe en login")
    void login_invalidEmail_throwsException() {
        when(repo.findByEmail("noexiste@unicauca.edu.co")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.login("noexiste@unicauca.edu.co", "Abcdef1!"));

        assertEquals("Credenciales inválidas.", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la contraseña es incorrecta en login")
    void login_invalidPassword_throwsException() {
        Teacher t = Teacher.builder()
                .email("docente@unicauca.edu.co")
                .passwordHash(new BCryptPasswordEncoder().encode("Abcdef1!"))
                .roles("DOCENTE")
                .build();
        when(repo.findByEmail("docente@unicauca.edu.co")).thenReturn(Optional.of(t));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.login("docente@unicauca.edu.co", "WrongPass1!"));

        assertEquals("Credenciales inválidas.", exception.getMessage());
    }
}
