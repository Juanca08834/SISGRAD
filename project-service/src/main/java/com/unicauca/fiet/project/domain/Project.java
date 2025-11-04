package com.unicauca.fiet.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/** Entidad Projecto de Grado. */
@Entity
@Table(name = "projects")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Id del docente propietario. */
    private String teacherEmail;

    /** Título actual del proyecto. */
    private String title;

    /** Número de intento/versión del Formato A en curso. */
    private int attemptNumber;

    /** Estado del proyecto para consultas de estudiantes. */
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private Instant createdAt;
}
