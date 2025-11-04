package com.unicauca.fiet.document.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/** Entidad Anteproyecto. */
@Entity
@Table(name="anteproyectos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Anteproyecto {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID projectId;
    private String docenteEmail;
    private String pdfUrl;
    private Instant fechaEnvio;
}
