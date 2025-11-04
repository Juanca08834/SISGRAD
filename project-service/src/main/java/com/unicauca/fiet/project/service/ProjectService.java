package com.unicauca.fiet.project.service;

import com.unicauca.fiet.common.events.FormatoASubmittedEvent;
import com.unicauca.fiet.common.messaging.EventPublisher;
import com.unicauca.fiet.project.domain.*;
import com.unicauca.fiet.project.repo.FormatAVersionRepository;
import com.unicauca.fiet.project.repo.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de aplicación para manejar el Formato A y estado del proyecto.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final FormatAVersionRepository formatARepo;
    private final EventPublisher eventPublisher;

    public ProjectService(ProjectRepository projectRepository, FormatAVersionRepository formatARepo, EventPublisher eventPublisher) {
        this.projectRepository = projectRepository;
        this.formatARepo = formatARepo;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Envía el Formato A (nueva versión o versión inicial) aplicando reglas de negocio.
     * <ul>
     *   <li>Si modalidad es PRACTICA_PROFESIONAL, requiere carta de aceptación.</li>
     *   <li>Incrementa attemptNumber y actualiza el ProjectStatus según el intento.</li>
     *   <li>Publica un evento asíncrono para notificar al coordinador (req. 2).</li>
     * </ul>
     * @param req datos del Formato A
     * @return versión persistida
     */
    @Transactional
    public FormatAVersion submitFormatoA(SubmitFormatoARequest req) {
        if (req.modality() == Modality.PRACTICA_PROFESIONAL && (req.acceptanceLetterUrl() == null || req.acceptanceLetterUrl().isBlank())) {
            throw new IllegalArgumentException("La carta de aceptación es obligatoria para Práctica Profesional.");
        }

        Project project;
        int nextAttempt;
        if (req.projectId() == null) {
            project = Project.builder()
                    .teacherEmail(req.teacherEmail())
                    .title(req.title())
                    .attemptNumber(1)
                    .status(ProjectStatus.EN_PRIMERA_EVALUACION_FORMATO_A)
                    .createdAt(Instant.now())
                    .build();
            project = projectRepository.save(project);
            nextAttempt = 1;
        } else {
            project = projectRepository.findById(req.projectId())
                    .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
            // incremento de intento (se asume que el coordinador rechazó previamente)
            nextAttempt = project.getAttemptNumber() + 1;
            if (nextAttempt > 3) {
                project.setStatus(ProjectStatus.RECHAZADO_FORMATO_A);
                projectRepository.save(project);
                throw new IllegalStateException("Se alcanzó el máximo de 3 intentos. El proyecto queda RECHAZADO.");
            }
            project.setAttemptNumber(nextAttempt);
            switch (nextAttempt) {
                case 2 -> project.setStatus(ProjectStatus.EN_SEGUNDA_EVALUACION_FORMATO_A);
                case 3 -> project.setStatus(ProjectStatus.EN_TERCERA_EVALUACION_FORMATO_A);
                default -> project.setStatus(ProjectStatus.EN_PRIMERA_EVALUACION_FORMATO_A);
            }
            project.setTitle(req.title());
            projectRepository.save(project);
        }

        FormatAVersion version = FormatAVersion.builder()
                .projectId(project.getId())
                .versionNumber(nextAttempt)
                .title(req.title())
                .modality(req.modality())
                .date(Instant.now())
                .director(req.director())
                .codirector(req.codirector())
                .objectiveGeneral(req.objectiveGeneral())
                .objectivesEspecificos(req.objectivesEspecificos())
                .pdfUrl(req.pdfUrl())
                .acceptanceLetterUrl(req.acceptanceLetterUrl())
                .build();
        version = formatARepo.save(version);

        // Evento para notificar al coordinador
        eventPublisher.publish(FormatoASubmittedEvent.builder()
                .projectId(project.getId())
                .attemptNumber(nextAttempt)
                .title(req.title())
                .modality(req.modality().name())
                .coordinatorEmail(req.coordinatorEmail())
                .occurredOn(Instant.now())
                .build());

        return version;
    }
}
