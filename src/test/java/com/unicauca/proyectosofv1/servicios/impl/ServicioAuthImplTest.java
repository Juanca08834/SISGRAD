package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioUsuario;
import com.unicauca.proyectosofv1.seguridad.EncriptadorContrasenia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioAuthImplTest {

    @Mock
    RepositorioUsuario repo;

    @Mock
    EncriptadorContrasenia encriptador;

    private ServicioAuthImpl servicio;

    @BeforeEach
    void setUp() {
        servicio = new ServicioAuthImpl(repo, encriptador);
    }

    @Test
    void loginRetornaTrueConCredencialesCorrectas() {
        Usuario usuario = new Usuario("a@b.com","Juan","Pérez","3001234","Ing","estudiante","HASH", LocalDateTime.now());
        when(repo.buscarPorEmail("a@b.com")).thenReturn(usuario);
        when(encriptador.verificar(any(char[].class), eq(usuario.getPasswordHash()))).thenReturn(true);

        assertTrue(servicio.login("a@b.com", "miPass"));
    }

    @Test
    void loginRetornaFalseSiUsuarioNoExiste() {
        when(repo.buscarPorEmail("no@existe.com")).thenReturn(null);
        assertFalse(servicio.login("no@existe.com", "x"));
    }

    @Test
    void loginRetornaFalseSiPasswordIncorrecta() {
        Usuario usuario = new Usuario("a@b.com","Juan","Pérez","3001234","Ing","estudiante","HASH", LocalDateTime.now());
        when(repo.buscarPorEmail("a@b.com")).thenReturn(usuario);
        when(encriptador.verificar(any(char[].class), eq(usuario.getPasswordHash()))).thenReturn(false);

        assertFalse(servicio.login("a@b.com", "otra"));
    }

    @Test
    void loginYObtenerDevuelveUsuarioSiCredencialesCorrectas() {
        Usuario usuario = new Usuario("u@x.com","Ana","Gómez","311","Mat","docente","H", LocalDateTime.now());
        when(repo.buscarPorEmail("u@x.com")).thenReturn(usuario);
        when(encriptador.verificar(any(char[].class), eq(usuario.getPasswordHash()))).thenReturn(true);

        Usuario res = servicio.loginYObtener("u@x.com", "miPass");
        assertNotNull(res);
        assertSame(usuario, res);
    }

    @Test
    void loginYObtenerDevuelveNullSiNoExisteOContraseñaIncorrecta() {
        when(repo.buscarPorEmail("no@x.com")).thenReturn(null);
        assertNull(servicio.loginYObtener("no@x.com", "p"));

        Usuario usuario = new Usuario("t@t.com","X","Y","","","estudiante","H", LocalDateTime.now());
        when(repo.buscarPorEmail("t@t.com")).thenReturn(usuario);
        when(encriptador.verificar(any(char[].class), eq(usuario.getPasswordHash()))).thenReturn(false);

        assertNull(servicio.loginYObtener("t@t.com", "wrong"));
    }
}
