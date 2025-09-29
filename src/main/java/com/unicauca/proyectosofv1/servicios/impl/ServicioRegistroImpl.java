package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.excepciones.SISGRADException;
import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.modelo.Roles;
import com.unicauca.proyectosofv1.repositorio.RepositorioUsuario;
import com.unicauca.proyectosofv1.seguridad.EncriptadorContrasenia;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServicioRegistroImpl implements com.unicauca.proyectosofv1.servicios.ServicioRegistro {

    private static final Map<String, String> PROGRAMAS_CANON;

    static {
        Map<String, String> m = new HashMap<>();
        m.put("ingenieria de sistemas", "Ingeniería de Sistemas");
        m.put("ingenieria electronica y telecomunicaciones", "Ingeniería Electrónica y Telecomunicaciones");
        m.put("automatica industrial", "Automática industrial");
        m.put("tecnologia en telematica", "Tecnología en Telemática");
        PROGRAMAS_CANON = Collections.unmodifiableMap(m);
    }

    private final RepositorioUsuario repositorio;
    private final EncriptadorContrasenia encriptador;

    // ServicioRegistroImpl
    private static final java.util.regex.Pattern POLITICA
            = java.util.regex.Pattern.compile("^(?=\\S{6,})(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$");

    public ServicioRegistroImpl(RepositorioUsuario repositorio, EncriptadorContrasenia encriptador) {
        this.repositorio = repositorio;
        this.encriptador = encriptador;
    }

    private static String normalizeAsciiLower(String s) {
        if (s == null) {
            return null;
        }
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase().trim();
    }

    private static String canonPrograma(String programa) throws SISGRADException {
        String key = normalizeAsciiLower(programa);
        String canon = PROGRAMAS_CANON.get(key);
        if (canon == null) {
            throw new SISGRADException("Selecciona un programa válido.");
        }
        return canon;
    }

    private static void validarPassword(String contrasenia) throws SISGRADException {
        if (contrasenia == null || !POLITICA.matcher(contrasenia).matches()) {
            throw new SISGRADException(
                    "La contraseña debe tener mínimo 6 caracteres, y al menos 1 mayúscula, 1 minúscula, 1 dígito y 1 símbolo."
            );
        }
    }

    @Override
    public void registrar(String nombres, String apellidos, String celular,
            String programa, String rol, String email, String contraseniaPlano)
            throws SISGRADException {

        // Normalizar/trim de entradas
        nombres = (nombres == null) ? "" : nombres.trim();
        apellidos = (apellidos == null) ? "" : apellidos.trim();
        celular = (celular == null) ? "" : celular.trim();
        programa = (programa == null) ? "" : programa.trim();
        rol = (rol == null) ? "" : rol.trim();
        email = (email == null) ? "" : email.trim().toLowerCase(); // email en minúsculas

        // Validaciones básicas
        if (nombres.isEmpty()) {
            throw new SISGRADException("Ingresa tus nombres.");
        }
        if (apellidos.isEmpty()) {
            throw new SISGRADException("Ingresa tus apellidos.");
        }

        if (!celular.isEmpty() && !celular.matches("^\\d{7,15}$")) {
            throw new SISGRADException("El celular debe tener solo dígitos (7 a 15).");
        }

        if (!email.matches("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$")) {
            throw new SISGRADException("Usa tu correo institucional @unicauca.edu.co");
        }

        // Canonizar programa y rol
        String programaCanon = canonPrograma(programa);
        String rolCanon = Roles.canonico(rol);
        if (rolCanon == null) {
            throw new SISGRADException("Seleccione un rol válido (Docente, Estudiante o Coordinador).");
        }

        // Política de contraseña (debe coincidir con la UI)
        validarPassword(contraseniaPlano);

        // Unicidad de email
        if (repositorio.buscarPorEmail(email) != null) {
            throw new SISGRADException("Ya existe un usuario con ese email.");
        }

        // Hash de contraseña con limpieza de memoria
        char[] pwd = (contraseniaPlano != null) ? contraseniaPlano.toCharArray() : null;
        String hash;
        try {
            hash = encriptador.generarHash(pwd);
        } finally {
            if (pwd != null) {
                java.util.Arrays.fill(pwd, '\0');
            }
        }

        // Construcción y persistencia del usuario
        Usuario usuario = new Usuario(
                email,
                nombres,
                apellidos,
                celular,
                programaCanon,
                rolCanon,
                hash,
                java.time.LocalDateTime.now()
        );

        try {
            repositorio.guardar(usuario);
        } catch (RuntimeException ex) {
            String msg = String.valueOf(ex.getMessage()).toLowerCase();
            if (msg.contains("unique") || msg.contains("constraint") || msg.contains("duplicate")) {
                throw new SISGRADException("Ya existe un usuario con ese email.");
            }
            throw ex;
        }
    }

}
