package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioUsuario;
import com.unicauca.proyectosofv1.seguridad.EncriptadorContrasenia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ServicioRecuperacionImplTest {

    private ServicioRecuperacionImpl servicioRecuperacion;
    private RepositorioUsuarioTest repositorioUsuario;
    private EncriptadorContraseniaTest encriptadorContrasenia;

    @BeforeEach
    void setUp() {
        repositorioUsuario = new RepositorioUsuarioTest();
        encriptadorContrasenia = new EncriptadorContraseniaTest();
        servicioRecuperacion = new ServicioRecuperacionImpl(repositorioUsuario, encriptadorContrasenia);
    }

    @Test
    @DisplayName("Resetear la contraseña debe retornar true y actulizar el usuario cuando este existe")
    void resetearPassword_DebeRetornarTrueYActualizarUsuario_CuandoUsuarioExiste() {
        String email = "usuario@test.com";
        String nuevaContrasenia = "nuevaPassword123";

        Usuario usuarioExistente = new Usuario(
                email, "Juan", "Pérez", "1234567890",
                "Ingeniería", "ESTUDIANTE", "hashAnterior", LocalDateTime.now()
        );

        repositorioUsuario.agregarUsuario(usuarioExistente);

        boolean resultado = servicioRecuperacion.resetearPassword(email, nuevaContrasenia);

        assertTrue(resultado);
        assertTrue(repositorioUsuario.fueConsultadoPorEmail(email));
        assertTrue(encriptadorContrasenia.fueGeneradoHash(nuevaContrasenia));

        Usuario usuarioActualizado = repositorioUsuario.getUsuarioActualizado();
        assertNotNull(usuarioActualizado);
        assertEquals(email, usuarioActualizado.getEmail());
        assertEquals("hash_" + nuevaContrasenia, usuarioActualizado.getPasswordHash()); // Hash simulado
    }

    @Test
    @DisplayName("Resetear contraseña debe retornar false cuando el usuario no existe")
    void resetearPassword_DebeRetornarFalse_CuandoUsuarioNoExiste() {
        String email = "noexiste@tesft.com";
        String nuevaContrasenia = "nuevaPassword123";

        boolean resultado = servicioRecuperacion.resetearPassword(email, nuevaContrasenia);

        assertFalse(resultado);
        assertTrue(repositorioUsuario.fueConsultadoPorEmail(email));
        assertFalse(encriptadorContrasenia.fueGeneradoHash(nuevaContrasenia));
        assertNull(repositorioUsuario.getUsuarioActualizado());
    }

    private static class RepositorioUsuarioTest implements RepositorioUsuario {
        private Usuario usuario;
        private String emailConsultado;
        private Usuario usuarioActualizado;

        public void agregarUsuario(Usuario usuario) {
            this.usuario = usuario;
        }

        @Override
        public Usuario buscarPorEmail(String email) {
            this.emailConsultado = email;
            return usuario != null && usuario.getEmail().equals(email) ? usuario : null;
        }

        @Override
        public void actualizar(Usuario usuario) {
            this.usuarioActualizado = usuario;
        }

        @Override
        public void guardar(Usuario usuario) {
        }

        public boolean fueConsultadoPorEmail(String email) {
            return email.equals(emailConsultado);
        }

        public Usuario getUsuarioActualizado() {
            return usuarioActualizado;
        }

    }

    private static class EncriptadorContraseniaTest extends EncriptadorContrasenia {
        private String contraseniaHasheada;

        @Override
        public String generarHash(char[] contrasenia) {
            this.contraseniaHasheada = new String(contrasenia);
            return "hash_" + contraseniaHasheada; // Hash simulado
        }

        public boolean fueGeneradoHash(String contrasenia) {
            return contrasenia.equals(contraseniaHasheada);
        }
    }
}