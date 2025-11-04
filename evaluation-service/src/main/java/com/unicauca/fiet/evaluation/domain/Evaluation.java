package com.unicauca.fiet.evaluation.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/** Entidad de Evaluación de Formato A realizada por el coordinador. */
@Entity
@Table(name="evaluations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Evaluation {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID projectId;
    private int attemptNumber;

    @Enumerated(EnumType.STRING)
    private Result result;

    private String comments;
    private Instant evaluatedAt;

    public enum Result { APPROVED, REJECTED, REJECTED_FINAL }
}
