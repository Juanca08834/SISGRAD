package com.unicauca.proyectosofv1.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase Usuario")
class UsuarioTest {

    private static final String EMAIL_TEST = "juan.perez@unicauca.edu.co";
    private static final String NOMBRES_TEST = "Juan Carlos";
    private static final String APELLIDOS_TEST = "Pérez Gómez";
    private static final String CELULAR_TEST = "3001234567";
    private static final String PROGRAMA_TEST = "Ingeniería de Sistemas";
    private static final String ROL_TEST = "Estudiante";
    private static final String PASSWORD_HASH_TEST = "hashedPassword123";
    private static final LocalDateTime FECHA_TEST = LocalDateTime.of(2024, 1, 15, 10, 30, 45);

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(
                EMAIL_TEST,
                NOMBRES_TEST,
                APELLIDOS_TEST,
                CELULAR_TEST,
                PROGRAMA_TEST,
                ROL_TEST,
                PASSWORD_HASH_TEST,
                FECHA_TEST
        );
    }

    @Nested
    @DisplayName("Pruebas de Getters")
    class PruebasGetters {

        @Test
        @DisplayName("getEmail() debe retornar el email correcto")
        void getEmailDebeRetornarEmailCorrecto() {
            assertEquals(EMAIL_TEST, usuario.getEmail());
            assertNotNull(usuario.getEmail());
        }

        @Test
        @DisplayName("getNombres() debe retornar los nombres correctos")
        void getNombresDebeRetornarNombresCorrectos() {
            assertEquals(NOMBRES_TEST, usuario.getNombres());
            assertNotNull(usuario.getNombres());
        }

        @Test
        @DisplayName("getApellidos() debe retornar los apellidos correctos")
        void getApellidosDebeRetornarApellidosCorrectos() {
            assertEquals(APELLIDOS_TEST, usuario.getApellidos());
            assertNotNull(usuario.getApellidos());
        }

        @Test
        @DisplayName("getCelular() debe retornar el celular correcto")
        void getCelularDebeRetornarCelularCorrecto() {
            assertEquals(CELULAR_TEST, usuario.getCelular());
            assertNotNull(usuario.getCelular());
        }

        @Test
        @DisplayName("getPrograma() debe retornar el programa correcto")
        void getProgramaDebeRetornarProgramaCorrecto() {
            assertEquals(PROGRAMA_TEST, usuario.getPrograma());
            assertNotNull(usuario.getPrograma());
        }

        @Test
        @DisplayName("getRol() debe retornar el rol correcto")
        void getRolDebeRetornarRolCorrecto() {
            assertEquals(ROL_TEST, usuario.getRol());
            assertNotNull(usuario.getRol());
        }

        @Test
        @DisplayName("getPasswordHash() debe retornar el hash correcto")
        void getPasswordHashDebeRetornarHashCorrecto() {
            assertEquals(PASSWORD_HASH_TEST, usuario.getPasswordHash());
            assertNotNull(usuario.getPasswordHash());
        }

        @Test
        @DisplayName("getCreadoEn() debe retornar la fecha correcta")
        void getCreadoEnDebeRetornarFechaCorrecta() {
            assertEquals(FECHA_TEST, usuario.getCreadoEn());
            assertNotNull(usuario.getCreadoEn());
            assertEquals(2024, usuario.getCreadoEn().getYear());
            assertEquals(1, usuario.getCreadoEn().getMonthValue());
            assertEquals(15, usuario.getCreadoEn().getDayOfMonth());
            assertEquals(10, usuario.getCreadoEn().getHour());
            assertEquals(30, usuario.getCreadoEn().getMinute());
            assertEquals(45, usuario.getCreadoEn().getSecond());
        }
    }
}