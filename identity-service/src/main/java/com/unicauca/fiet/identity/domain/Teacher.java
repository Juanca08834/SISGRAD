package com.unicauca.fiet.identity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/** Entidad Docente del sistema. */
@Entity
@Table(name = "teachers", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Nombres del docente. */
    private String nombres;

    /** Apellidos del docente. */
    private String apellidos;

    /** Celular (opcional). */
    private String celular;

    /** Programa académico al que pertenece el docente. */
    @Enumerated(EnumType.STRING)
    private Program programa;

    /** Email institucional único. */
    private String email;

    /** Contraseña hash (BCrypt). */
    private String passwordHash;

    /** Roles (por simplicidad, una cadena CSV, ej: "DOCENTE"). */
    private String roles;
}
