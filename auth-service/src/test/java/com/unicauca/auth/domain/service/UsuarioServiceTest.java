package com.unicauca.auth.domain.service;

import com.unicauca.auth.domain.model.Programa;
import com.unicauca.auth.domain.model.Rol;
import com.unicauca.auth.domain.model.Usuario;
import com.unicauca.auth.domain.ports.out.UsuarioPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio de usuarios
 * Prueba las reglas de negocio de registro y autenticación
 */
@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioPersistencePort persistencePort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = new Usuario();
        usuarioValido.setId(1L);
        usuarioValido.setNombres("Juan");
        usuarioValido.setApellidos("Pérez");
        usuarioValido.setEmail("juan.perez@unicauca.edu.co");
        usuarioValido.setPassword("Password1!");
        usuarioValido.setPrograma(Programa.INGENIERIA_SISTEMAS);
        usuarioValido.setRol(Rol.DOCENTE);
        usuarioValido.setActivo(true);
    }

    @Test
    @DisplayName("Registrar usuario válido debería guardar y retornar usuario")
    void registrar_usuarioValido_deberiaGuardarYRetornar() {
        // Arrange
        when(persistencePort.existePorEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(persistencePort.guardar(any(Usuario.class))).thenReturn(usuarioValido);

        // Act
        Usuario resultado = usuarioService.registrar(usuarioValido);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombres());
        verify(persistencePort).guardar(any(Usuario.class));
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    @DisplayName("Registrar con email duplicado debería lanzar excepción")
    void registrar_emailDuplicado_deberiaLanzarExcepcion() {
        // Arrange
        when(persistencePort.existePorEmail(anyString())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.registrar(usuarioValido));
        assertTrue(exception.getMessage().contains("ya se encuentra registrado"));
        verify(persistencePort, never()).guardar(any());
    }

    @Test
    @DisplayName("Registrar con email no institucional debería lanzar excepción")
    void registrar_emailNoInstitucional_deberiaLanzarExcepcion() {
        // Arrange
        usuarioValido.setEmail("juan@gmail.com");
        when(persistencePort.existePorEmail(anyString())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.registrar(usuarioValido));
        assertTrue(exception.getMessage().contains("correo institucional"));
    }

    @Test
    @DisplayName("Registrar con contraseña inválida debería lanzar excepción")
    void registrar_passwordInvalida_deberiaLanzarExcepcion() {
        // Arrange
        usuarioValido.setPassword("12345"); // Muy corta y sin requisitos
        when(persistencePort.existePorEmail(anyString())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.registrar(usuarioValido));
        assertTrue(exception.getMessage().contains("contraseña"));
    }

    @Test
    @DisplayName("Autenticar con credenciales válidas debería retornar token")
    void autenticar_credencialesValidas_deberiaRetornarToken() {
        // Arrange
        String email = "juan.perez@unicauca.edu.co";
        String password = "Password1!";
        String expectedToken = "jwt-token-123";

        when(persistencePort.buscarPorEmail(email)).thenReturn(Optional.of(usuarioValido));
        when(passwordEncoder.matches(password, usuarioValido.getPassword())).thenReturn(true);
        when(jwtService.generarToken(usuarioValido)).thenReturn(expectedToken);

        // Act
        String token = usuarioService.autenticar(email, password);

        // Assert
        assertEquals(expectedToken, token);
        verify(persistencePort).actualizar(any(Usuario.class));
    }

    @Test
    @DisplayName("Autenticar con email inexistente debería lanzar excepción")
    void autenticar_emailInexistente_deberiaLanzarExcepcion() {
        // Arrange
        when(persistencePort.buscarPorEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.autenticar("noexiste@unicauca.edu.co", "password"));
        assertTrue(exception.getMessage().contains("correo electrónico o la contraseña son incorrectos"));
    }

    @Test
    @DisplayName("Autenticar con contraseña incorrecta debería lanzar excepción")
    void autenticar_passwordIncorrecta_deberiaLanzarExcepcion() {
        // Arrange
        when(persistencePort.buscarPorEmail(anyString())).thenReturn(Optional.of(usuarioValido));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.autenticar("juan.perez@unicauca.edu.co", "wrongpassword"));
        assertTrue(exception.getMessage().contains("correo electrónico o la contraseña son incorrectos"));
    }

    @Test
    @DisplayName("Autenticar usuario inactivo debería lanzar excepción")
    void autenticar_usuarioInactivo_deberiaLanzarExcepcion() {
        // Arrange
        usuarioValido.setActivo(false);
        when(persistencePort.buscarPorEmail(anyString())).thenReturn(Optional.of(usuarioValido));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.autenticar("juan.perez@unicauca.edu.co", "Password1!"));
        assertTrue(exception.getMessage().contains("cuenta se encuentra inactiva"));
    }

    @Test
    @DisplayName("Obtener usuario por email existente debería retornar usuario")
    void obtenerUsuarioPorEmail_emailExistente_deberiaRetornarUsuario() {
        // Arrange
        when(persistencePort.buscarPorEmail("juan.perez@unicauca.edu.co"))
                .thenReturn(Optional.of(usuarioValido));

        // Act
        Usuario resultado = usuarioService.obtenerUsuarioPorEmail("juan.perez@unicauca.edu.co");

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombres());
    }

    @Test
    @DisplayName("Obtener usuario por email inexistente debería lanzar excepción")
    void obtenerUsuarioPorEmail_emailInexistente_deberiaLanzarExcepcion() {
        // Arrange
        when(persistencePort.buscarPorEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> usuarioService.obtenerUsuarioPorEmail("noexiste@unicauca.edu.co"));
    }
}

