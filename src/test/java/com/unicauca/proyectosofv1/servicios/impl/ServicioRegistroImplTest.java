package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.excepciones.SISGRADException;
import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioUsuario;
import com.unicauca.proyectosofv1.seguridad.EncriptadorContrasenia;
import com.unicauca.proyectosofv1.servicios.ServicioRegistro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests Simples para ServicioRegistroImpl")
class ServicioRegistroImplTest {

    private ServicioRegistro servicio;
    private RepositorioUsuarioStub repositorio;
    private EncriptadorContraseniaStub encriptador;

    private static final String NOMBRES_VALIDOS = "Juan Carlos";
    private static final String APELLIDOS_VALIDOS = "Pérez Gómez";
    private static final String CELULAR_VALIDO = "3001234567";
    private static final String PROGRAMA_VALIDO = "Ingeniería de Sistemas";
    private static final String ROL_VALIDO = "Estudiante";
    private static final String EMAIL_VALIDO = "juan.perez@unicauca.edu.co";
    private static final String CONTRASENIA_VALIDA = "MiPass123!";

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioUsuarioStub();
        encriptador = new EncriptadorContraseniaStub();
        servicio = new ServicioRegistroImpl(repositorio, encriptador);
    }

    @Test
    @DisplayName("Debe registrar usuario exitosamente con datos válidos")
    void debeRegistrarUsuarioExitosamente() throws SISGRADException {
        assertDoesNotThrow(() -> {
            servicio.registrar(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CELULAR_VALIDO,
                    PROGRAMA_VALIDO, ROL_VALIDO, EMAIL_VALIDO, CONTRASENIA_VALIDA);
        });

        assertTrue(repositorio.fueGuardadoUsuario());
        assertTrue(encriptador.fueGeneradoHash());
    }

    @Test
    @DisplayName("Debe fallar cuando nombres es null")
    void debeFallarCuandoNombresEsNull() {
        SISGRADException excepcion = assertThrows(SISGRADException.class, () -> {
            servicio.registrar(null, APELLIDOS_VALIDOS, CELULAR_VALIDO,
                    PROGRAMA_VALIDO, ROL_VALIDO, EMAIL_VALIDO, CONTRASENIA_VALIDA);
        });

        assertEquals("Ingresa tus nombres.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe fallar cuando apellidos es vacío")
    void debeFallarCuandoApellidosEsVacio() {
        SISGRADException excepcion = assertThrows(SISGRADException.class, () -> {
            servicio.registrar(NOMBRES_VALIDOS, "", CELULAR_VALIDO,
                    PROGRAMA_VALIDO, ROL_VALIDO, EMAIL_VALIDO, CONTRASENIA_VALIDA);
        });

        assertEquals("Ingresa tus apellidos.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe fallar con email no institucional")
    void debeFallarConEmailNoInstitucional() {
        SISGRADException excepcion = assertThrows(SISGRADException.class, () -> {
            servicio.registrar(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CELULAR_VALIDO,
                    PROGRAMA_VALIDO, ROL_VALIDO, "juan@gmail.com", CONTRASENIA_VALIDA);
        });

        assertEquals("Usa tu correo institucional @unicauca.edu.co", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe fallar con contraseña muy corta")
    void debeFallarConContraseniaMuyCorta() {
        SISGRADException excepcion = assertThrows(SISGRADException.class, () -> {
            servicio.registrar(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CELULAR_VALIDO,
                    PROGRAMA_VALIDO, ROL_VALIDO, EMAIL_VALIDO, "12345");
        });

        assertEquals("La contraseña debe tener al menos 6 caracteres.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe fallar cuando ya existe usuario con mismo email")
    void debeFallarCuandoYaExisteUsuario() {
        repositorio.simularUsuarioExistente(EMAIL_VALIDO);

        SISGRADException excepcion = assertThrows(SISGRADException.class, () -> {
            servicio.registrar(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, CELULAR_VALIDO,
                    PROGRAMA_VALIDO, ROL_VALIDO, EMAIL_VALIDO, CONTRASENIA_VALIDA);
        });

        assertEquals("Ya existe un usuario con ese email.", excepcion.getMessage());
    }

    @Test
    @DisplayName("Debe manejar celular null correctamente")
    void debeManejarCelularNullCorrectamente() throws SISGRADException {
        assertDoesNotThrow(() -> {
            servicio.registrar(NOMBRES_VALIDOS, APELLIDOS_VALIDOS, null,
                    PROGRAMA_VALIDO, ROL_VALIDO, EMAIL_VALIDO, CONTRASENIA_VALIDA);
        });

        assertTrue(repositorio.fueGuardadoUsuario());
        assertEquals("", repositorio.getUltimoUsuario().getCelular());
    }

    private static class RepositorioUsuarioStub implements RepositorioUsuario {
        private boolean guardadoUsuario = false;
        private boolean actualizadoUsuario = false;
        private Usuario usuarioExistente = null;
        private Usuario ultimoUsuario = null;

        @Override
        public Usuario buscarPorEmail(String email) {
            return usuarioExistente;
        }

        @Override
        public void guardar(Usuario usuario) {
            this.guardadoUsuario = true;
            this.ultimoUsuario = usuario;
        }

        @Override
        public void actualizar(Usuario usuario) {
            this.actualizadoUsuario = true;
            this.ultimoUsuario = usuario;
        }

        public boolean fueGuardadoUsuario() {
            return guardadoUsuario;
        }

        public boolean fueActualizadoUsuario() {
            return actualizadoUsuario;
        }

        public void simularUsuarioExistente(String email) {
            usuarioExistente = new Usuario(email, "Test", "Test", "",
                    "Ingeniería de Sistemas", "Estudiante", "hash",
                    java.time.LocalDateTime.now());
        }

        public Usuario getUltimoUsuario() {
            return ultimoUsuario;
        }
    }

    private static class EncriptadorContraseniaStub extends EncriptadorContrasenia {
        private boolean generadoHash = false;

        @Override
        public String generarHash(char[] contrasenia) {
            this.generadoHash = true;
            return "hashedPassword123";
        }

        public boolean fueGeneradoHash() {
            return generadoHash;
        }
    }
}