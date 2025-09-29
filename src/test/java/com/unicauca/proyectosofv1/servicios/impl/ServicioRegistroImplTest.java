package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.excepciones.SISGRADException;
import com.unicauca.proyectosofv1.repositorio.RepositorioUsuario;
import com.unicauca.proyectosofv1.seguridad.EncriptadorContrasenia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioRegistroImplTest {

    private RepositorioUsuario repo;
    private EncriptadorContrasenia encriptador;
    private ServicioRegistroImpl servicio;

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(RepositorioUsuario.class);
        encriptador = Mockito.mock(EncriptadorContrasenia.class);
        // evitar NPE en la llamada a generarHash(...)
        when(encriptador.generarHash(any(char[].class))).thenReturn("HASHED");
        servicio = new ServicioRegistroImpl(repo, encriptador);
    }

    @Test
    void registrar_nombresVacio_lanzaSISGRAD() {
        SISGRADException ex = assertThrows(SISGRADException.class, () ->
                servicio.registrar("", "Perez", "3001234567", "Ingeniería de Sistemas",
                        "Estudiante", "user@unicauca.edu.co", "Aa1!aa")
        );
        assertEquals("Ingresa tus nombres.", ex.getMessage());
    }

    @Test
    void registrar_apellidosVacio_lanzaSISGRAD() {
        SISGRADException ex = assertThrows(SISGRADException.class, () ->
                servicio.registrar("Juan", "", "3001234567", "Ingeniería de Sistemas",
                        "Estudiante", "user@unicauca.edu.co", "Aa1!aa")
        );
        assertEquals("Ingresa tus apellidos.", ex.getMessage());
    }

    @Test
    void registrar_celularFormatoInvalido_lanzaSISGRAD() {
        SISGRADException ex = assertThrows(SISGRADException.class, () ->
                servicio.registrar("Juan", "Perez", "ABC123", "Ingeniería de Sistemas",
                        "Estudiante", "user@unicauca.edu.co", "Aa1!aa")
        );
        assertEquals("El celular debe tener solo dígitos (7 a 15).", ex.getMessage());
    }

    @Test
    void registrar_emailNoInstitucional_lanzaSISGRAD() {
        SISGRADException ex = assertThrows(SISGRADException.class, () ->
                servicio.registrar("Juan", "Perez", "3001234567", "Ingeniería de Sistemas",
                        "Estudiante", "juan@gmail.com", "Aa1!aa")
        );
        assertEquals("Usa tu correo institucional @unicauca.edu.co", ex.getMessage());
    }

    @Test
    void registrar_programaInvalido_lanzaSISGRAD() {
        SISGRADException ex = assertThrows(SISGRADException.class, () ->
                servicio.registrar("Juan", "Perez", "3001234567", "Programa Inexistente",
                        "Estudiante", "user@unicauca.edu.co", "Aa1!aa")
        );
        assertEquals("Selecciona un programa válido.", ex.getMessage());
    }

    @Test
    void registrar_rolInvalido_lanzaSISGRAD() {
        SISGRADException ex = assertThrows(SISGRADException.class, () ->
                servicio.registrar("Juan", "Perez", "3001234567", "Ingeniería de Sistemas",
                        "NoRol", "user@unicauca.edu.co", "Aa1!aa")
        );
        assertEquals("Seleccione un rol válido (Docente, Estudiante o Coordinador).", ex.getMessage());
    }

    @Test
    void registrar_usuarioExistente_convierteRuntimeEnSISGRAD() {
        // Simular violación unique/constraint desde el repositorio
        doThrow(new RuntimeException("UNIQUE constraint failed: usuario.email")).when(repo).guardar(any());
        SISGRADException ex = assertThrows(SISGRADException.class, () ->
                servicio.registrar("Juan", "Perez", "3001234567", "Ingeniería de Sistemas",
                        "Estudiante", "user@unicauca.edu.co", "Aa1!aa")
        );
        assertEquals("Ya existe un usuario con ese email.", ex.getMessage());
    }

    @Test
    void registrar_exito_invocaGuardarYEncripta() {
        // Contraseña que cumple la política (mín. 6, mayúscula, minúscula, dígito, caracter especial)
        String pwd = "Aa1!Aa1!";
        assertDoesNotThrow(() ->
                servicio.registrar("Juan", "Perez", "3001234567", "Ingeniería de Sistemas",
                        "Estudiante", "user@unicauca.edu.co", pwd)
        );

        verify(encriptador, times(1)).generarHash(any(char[].class));
        verify(repo, times(1)).guardar(any());
    }
}
