package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.excepciones.SISGRADException;
import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioUsuario;
import com.unicauca.proyectosofv1.seguridad.EncriptadorContrasenia;
import com.unicauca.proyectosofv1.servicios.ServicioRegistro;

import java.time.LocalDateTime;
import java.util.Set;

public class ServicioRegistroImpl implements ServicioRegistro {

    private static final Set<String> PROGRAMAS_VALIDOS = Set.of(
            "Ingeniería de Sistemas",
            "Ingeniería Electrónica y Telecomunicaciones",
            "Automática industrial",
            "Tecnología en Telemática"
    );
    private static final Set<String> ROLES_VALIDOS = Set.of("Estudiante", "Docente");

    private final RepositorioUsuario repositorio;
    private final EncriptadorContrasenia encriptador;

    public ServicioRegistroImpl(RepositorioUsuario repositorio, EncriptadorContrasenia encriptador) {
        this.repositorio = repositorio;
        this.encriptador = encriptador;
    }

    @Override
    public void registrar(String nombres, String apellidos, String celular,
            String programa, String rol, String email, String contraseniaPlano)
            throws SISGRADException {

        // Validaciones de dominio (duplicadas respecto a UI por seguridad)
        if (nombres == null || nombres.isBlank()) {
            throw new SISGRADException("Ingresa tus nombres.");
        }
        if (apellidos == null || apellidos.isBlank()) {
            throw new SISGRADException("Ingresa tus apellidos.");
        }

        if (celular != null && !celular.isBlank() && !celular.matches("^\\d{7,15}$")) {
            throw new SISGRADException("El celular debe tener solo dígitos (7 a 15).");
        }

        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$")) {
            throw new SISGRADException("Usa tu correo institucional @unicauca.edu.co");
        }

        programa = programa == null ? "" : programa.trim();

        rol = rol == null ? "" : rol.trim(); // quita espacios
        if (rol.equalsIgnoreCase("estudiante")) {
            rol = "Estudiante";
        }
        if (rol.equalsIgnoreCase("docente")) {
            rol = "Docente";
        }

        if (!ROLES_VALIDOS.contains(rol)) {
            throw new SISGRADException("Rol no válido.");
        }

        if (contraseniaPlano == null || contraseniaPlano.length() < 6) {
            throw new SISGRADException("La contraseña debe tener al menos 6 caracteres.");
        }
        if (!contraseniaPlano.matches(".*[A-Z].*")) {
            throw new SISGRADException("La contraseña debe tener al menos una mayúscula.");
        }
        if (!contraseniaPlano.matches(".*\\d.*")) {
            throw new SISGRADException("La contraseña debe tener al menos un dígito.");
        }
        if (!contraseniaPlano.matches(".*[^A-Za-z0-9].*")) {
            throw new SISGRADException("La contraseña debe tener al menos un caracter especial.");
        }

        if (repositorio.buscarPorEmail(email) != null) {
            throw new SISGRADException("Ya existe un usuario con ese email.");
        }

        // Hash y persistencia
        String hash = encriptador.generarHash(contraseniaPlano.toCharArray());
        Usuario usuario = new Usuario(
                email, nombres, apellidos,
                (celular == null ? "" : celular),
                programa, rol, hash, LocalDateTime.now()
        );
        repositorio.guardar(usuario);
    }

}
