package com.unicauca.auth.domain.service;

import com.unicauca.auth.domain.model.Usuario;
import com.unicauca.auth.domain.ports.in.AutenticarUsuarioUseCase;
import com.unicauca.auth.domain.ports.in.RegistrarUsuarioUseCase;
import com.unicauca.auth.domain.ports.out.UsuarioPersistencePort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de dominio que implementa la lógica de negocio para usuarios
 * ARQUITECTURA HEXAGONAL: Este servicio NO depende de frameworks externos,
 * solo de abstracciones (puertos/interfaces)
 */
@Service
public class UsuarioService implements RegistrarUsuarioUseCase, AutenticarUsuarioUseCase {

    private final UsuarioPersistencePort persistencePort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Constructor con inyección de dependencias
     * Spring inyecta automáticamente las implementaciones
     */
    public UsuarioService(UsuarioPersistencePort persistencePort,
                         PasswordEncoder passwordEncoder,
                         JwtService jwtService) {
        this.persistencePort = persistencePort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public Usuario registrar(Usuario usuario) {
        // REGLA DE NEGOCIO 1: Validar email único
        if (persistencePort.existePorEmail(usuario.getEmail())) {
            throw new RuntimeException("Este correo electrónico ya se encuentra registrado en el sistema. Por favor, utilice otro correo o inicie sesión.");
        }

        // REGLA DE NEGOCIO 2: Validar email institucional
        if (!usuario.isEmailInstitucional()) {
            throw new RuntimeException("Por favor, utilice su correo institucional de la Universidad del Cauca (@unicauca.edu.co)");
        }

        // REGLA DE NEGOCIO 3: Validar contraseña
        if (!usuario.isPasswordValid(usuario.getPassword())) {
            throw new RuntimeException(
                "La contraseña no cumple con los requisitos de seguridad. Debe tener:\n" +
                "- Mínimo 6 caracteres\n" +
                "- Al menos un número\n" +
                "- Al menos una letra mayúscula\n" +
                "- Al menos un carácter especial (!@#$%^&*)"
            );
        }

        // Encriptar contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Guardar en la base de datos
        return persistencePort.guardar(usuario);
    }

    @Override
    public String autenticar(String email, String password) {
        // Buscar usuario por email
        Usuario usuario = persistencePort.buscarPorEmail(email)
            .orElseThrow(() -> new RuntimeException("El correo electrónico o la contraseña son incorrectos. Por favor, verifique sus datos e intente nuevamente."));

        // REGLA DE NEGOCIO: Verificar que el usuario esté activo
        if (!usuario.isActivo()) {
            throw new RuntimeException("Su cuenta se encuentra inactiva. Por favor, contacte al administrador del sistema.");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("El correo electrónico o la contraseña son incorrectos. Por favor, verifique sus datos e intente nuevamente.");
        }

        // Actualizar último acceso
        usuario.actualizarUltimoAcceso();
        persistencePort.actualizar(usuario);

        // Generar y retornar token JWT
        return jwtService.generarToken(usuario);
    }

    @Override
    public Usuario obtenerUsuarioPorEmail(String email) {
        return persistencePort.buscarPorEmail(email)
            .orElseThrow(() -> new RuntimeException("No se encontró ningún usuario con el correo electrónico proporcionado."));
    }
}