package com.unicauca.fiet.project.service;

import com.unicauca.fiet.project.domain.Modality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Comando para enviar/reenviar el Formato A (req. 2 y 4). */
public record SubmitFormatoARequest(
        UUID projectId,
        @NotBlank String teacherEmail,
        @NotBlank String title,
        @NotNull Modality modality,
        @NotBlank String director,
        String codirector,
        @NotBlank String objectiveGeneral,
        @NotBlank String objectivesEspecificos,
        @NotBlank String pdfUrl,
        String acceptanceLetterUrl,
        @NotBlank String coordinatorEmail
) {}
