package com.unicauca.fiet.identity.service;

import com.unicauca.fiet.identity.domain.Program;
import com.unicauca.fiet.identity.domain.Teacher;
import com.unicauca.fiet.identity.repo.TeacherRepository;
import com.unicauca.fiet.identity.security.JwtService;
import com.unicauca.fiet.identity.service.password.PasswordValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de aplicación para operaciones con Docentes (registro y login).
 */
@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final PasswordValidator passwordValidator = PasswordValidator.defaultValidator();
    private final JwtService jwtService;

    public TeacherService(TeacherRepository teacherRepository, JwtService jwtService) {
        this.teacherRepository = teacherRepository;
        this.jwtService = jwtService;
    }

    /**
     * Registra un nuevo docente validando la política de contraseña.
     * @param nombres nombres
     * @param apellidos apellidos
     * @param celular celular (opcional, puede ser null)
     * @param programa programa académico
     * @param email email institucional
     * @param rawPassword contraseña sin cifrar
     * @return entidad persistida
     */
    @Transactional
    public Teacher registerTeacher(String nombres, String apellidos, String celular,
                                   Program programa, String email, String rawPassword) {
        if (teacherRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        passwordValidator.validate(rawPassword);
        String hash = passwordEncoder.encode(rawPassword);
        Teacher teacher = new TeacherBuilder()
                .nombres(nombres)
                .apellidos(apellidos)
                .celular(celular)
                .programa(programa)
                .email(email)
                .passwordHash(hash)
                .build();
        return teacherRepository.save(teacher);
    }

    /**
     * Autentica a un docente y retorna un JWT si las credenciales son válidas.
     * @param email email institucional
     * @param rawPassword contraseña en texto claro
     * @return token JWT firmado
     */
    public String login(String email, String rawPassword) {
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas."));
        if (!new BCryptPasswordEncoder().matches(rawPassword, teacher.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas.");
        }
        return jwtService.generate(email, Map.of("roles", teacher.getRoles()));
    }

    public Optional<Teacher> findByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

    public Teacher findById(UUID id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado con ID: " + id));
    }


}
