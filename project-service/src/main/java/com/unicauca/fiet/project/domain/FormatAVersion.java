package com.unicauca.fiet.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/** Versión del Formato A asociada a un proyecto. */
@Entity
@Table(name="formatoa_versions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FormatAVersion {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID projectId;
    private int versionNumber;

    private String title;
    @Enumerated(EnumType.STRING)
    private Modality modality;
    private Instant date;
    private String director;
    private String codirector;
    private String objectiveGeneral;
    private String objectivesEspecificos;
    private String pdfUrl;
    private String acceptanceLetterUrl; // obligatorio si PRACTICA_PROFESIONAL
}
