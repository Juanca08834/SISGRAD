package com.unicauca.auth.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la entidad Usuario
 * Verifica las reglas de negocio del dominio
 */
class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setNombres("Juan");
        usuario.setApellidos("Pérez");
        usuario.setEmail("juan.perez@unicauca.edu.co");
    }

    // ============= Tests de validación de email institucional =============

    @Test
    @DisplayName("Email institucional válido con dominio @unicauca.edu.co")
    void isEmailInstitucional_emailValido_deberiaRetornarTrue() {
        usuario.setEmail("juan.perez@unicauca.edu.co");
        assertTrue(usuario.isEmailInstitucional());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "usuario@gmail.com",
        "usuario@hotmail.com",
        "usuario@yahoo.com",
        "usuario@unicauca.com",
        "usuario@edu.co"
    })
    @DisplayName("Email no institucional debería retornar false")
    void isEmailInstitucional_emailNoInstitucional_deberiaRetornarFalse(String email) {
        usuario.setEmail(email);
        assertFalse(usuario.isEmailInstitucional());
    }

    @Test
    @DisplayName("Email null debería retornar false")
    void isEmailInstitucional_emailNull_deberiaRetornarFalse() {
        usuario.setEmail(null);
        assertFalse(usuario.isEmailInstitucional());
    }

    // ============= Tests de validación de contraseña =============

    @Test
    @DisplayName("Contraseña válida con todos los requisitos")
    void isPasswordValid_passwordValida_deberiaRetornarTrue() {
        // Tiene 6+ chars, dígito, mayúscula y especial
        assertTrue(usuario.isPasswordValid("Password1!"));
        assertTrue(usuario.isPasswordValid("Abc123@test"));
        assertTrue(usuario.isPasswordValid("MiClave$99"));
    }

    @Test
    @DisplayName("Contraseña muy corta (menos de 6 caracteres)")
    void isPasswordValid_passwordMuyCorta_deberiaRetornarFalse() {
        assertFalse(usuario.isPasswordValid("Ab1!"));
        assertFalse(usuario.isPasswordValid("12345"));
    }

    @Test
    @DisplayName("Contraseña sin dígito")
    void isPasswordValid_passwordSinDigito_deberiaRetornarFalse() {
        assertFalse(usuario.isPasswordValid("Password!"));
    }

    @Test
    @DisplayName("Contraseña sin mayúscula")
    void isPasswordValid_passwordSinMayuscula_deberiaRetornarFalse() {
        assertFalse(usuario.isPasswordValid("password1!"));
    }

    @Test
    @DisplayName("Contraseña sin carácter especial")
    void isPasswordValid_passwordSinEspecial_deberiaRetornarFalse() {
        assertFalse(usuario.isPasswordValid("Password1"));
    }

    @Test
    @DisplayName("Contraseña null")
    void isPasswordValid_passwordNull_deberiaRetornarFalse() {
        assertFalse(usuario.isPasswordValid(null));
    }

    // ============= Tests del nombre completo =============

    @Test
    @DisplayName("Nombre completo concatena nombres y apellidos")
    void getNombreCompleto_deberiaRetornarConcatenacion() {
        usuario.setNombres("Juan Carlos");
        usuario.setApellidos("Pérez García");
        assertEquals("Juan Carlos Pérez García", usuario.getNombreCompleto());
    }

    // ============= Tests de estado activo =============

    @Test
    @DisplayName("Usuario nuevo debería estar activo por defecto")
    void constructor_usuarioNuevo_deberiaEstarActivo() {
        Usuario nuevoUsuario = new Usuario();
        assertTrue(nuevoUsuario.isActivo());
    }

    @Test
    @DisplayName("Usuario nuevo debería tener fecha de creación")
    void constructor_usuarioNuevo_deberiaTenerFechaCreacion() {
        Usuario nuevoUsuario = new Usuario();
        assertNotNull(nuevoUsuario.getFechaCreacion());
    }

    // ============= Tests de actualizar último acceso =============

    @Test
    @DisplayName("Actualizar último acceso debería modificar la fecha")
    void actualizarUltimoAcceso_deberiaActualizarFecha() {
        Usuario nuevoUsuario = new Usuario();
        assertNull(nuevoUsuario.getUltimoAcceso());

        nuevoUsuario.actualizarUltimoAcceso();

        assertNotNull(nuevoUsuario.getUltimoAcceso());
    }

    // ============= Tests de roles =============

    @Test
    @DisplayName("Todos los roles del enum deberían existir")
    void roles_todosLosRoles_deberianExistir() {
        assertNotNull(Rol.DOCENTE);
        assertNotNull(Rol.COORDINADOR);
        assertNotNull(Rol.JEFE_DEPARTAMENTO);
        assertNotNull(Rol.ESTUDIANTE);
    }

    // ============= Tests de programas =============

    @Test
    @DisplayName("Todos los programas del enum deberían existir")
    void programas_todosLosProgramas_deberianExistir() {
        assertNotNull(Programa.INGENIERIA_SISTEMAS);
        assertNotNull(Programa.INGENIERIA_ELECTRONICA);
        assertNotNull(Programa.AUTOMATICA_INDUSTRIAL);
        assertNotNull(Programa.TECNOLOGIA_TELEMATICA);
    }
}

