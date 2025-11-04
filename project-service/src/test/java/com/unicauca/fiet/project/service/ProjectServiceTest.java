
package com.unicauca.fiet.project.service;

import com.unicauca.fiet.common.events.FormatoASubmittedEvent;
import com.unicauca.fiet.common.messaging.EventPublisher;
import com.unicauca.fiet.project.domain.*;
import com.unicauca.fiet.project.repo.FormatAVersionRepository;
import com.unicauca.fiet.project.repo.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas del servicio de Proyectos (ProjectService)")
public class ProjectServiceTest {

    private ProjectRepository projectRepo;
    private FormatAVersionRepository versionRepo;
    private EventPublisher publisher;
    private ProjectService service;

    @BeforeEach
    void setup() {
        projectRepo = Mockito.mock(ProjectRepository.class);
        versionRepo = Mockito.mock(FormatAVersionRepository.class);
        publisher = Mockito.mock(EventPublisher.class);
        service = new ProjectService(projectRepo, versionRepo, publisher);
    }

    @Test
    @DisplayName("Debe crear un nuevo proyecto y publicar evento al enviar Formato A inicial")
    void submitFormatoA_createsProjectAndPublishesEvent() {
        when(projectRepo.save(any())).thenAnswer(inv -> {
            Project p = inv.getArgument(0);
            try {
                var idField = Project.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(p, UUID.randomUUID());
            } catch (Exception ignored) {}
            return p;
        });
        when(versionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SubmitFormatoARequest req = new SubmitFormatoARequest(
                null, "docente@uni.edu", "Sistema de Gestión", Modality.INVESTIGACION,
                "Dr. Juan Pérez", "Dra. María López", "Objetivo General del Proyecto",
                "Objetivos Específicos", "http://pdf.com/formato.pdf", null, "coord@uni.edu"
        );

        FormatAVersion v = service.submitFormatoA(req);

        assertEquals(1, v.getVersionNumber());
        assertEquals("Sistema de Gestión", v.getTitle());
        assertEquals(Modality.INVESTIGACION, v.getModality());
        assertEquals("Dr. Juan Pérez", v.getDirector());

        ArgumentCaptor<FormatoASubmittedEvent> cap = ArgumentCaptor.forClass(FormatoASubmittedEvent.class);
        verify(publisher, times(1)).publish(cap.capture());

        FormatoASubmittedEvent event = cap.getValue();
        assertNotNull(event);
        assertEquals(1, event.getAttemptNumber());
        assertEquals("coord@uni.edu", event.getCoordinatorEmail());
    }

    @Test
    @DisplayName("Debe rechazar Práctica Profesional sin carta de aceptación")
    void submitFormatoA_practicaProfesional_requiresLetter() {
        SubmitFormatoARequest req = new SubmitFormatoARequest(
                null, "docente@uni.edu", "Proyecto en Empresa", Modality.PRACTICA_PROFESIONAL,
                "Director", null, "Objetivo General", "Objetivos Específicos",
                "http://pdf.com/formato.pdf", null, "coord@uni.edu"
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.submitFormatoA(req));

        assertEquals("La carta de aceptación es obligatoria para Práctica Profesional.",
            exception.getMessage());
        verify(projectRepo, never()).save(any());
        verify(publisher, never()).publish(any());
    }

    @Test
    @DisplayName("Debe aceptar Práctica Profesional con carta de aceptación")
    void submitFormatoA_practicaProfesional_withLetter_success() {
        when(projectRepo.save(any())).thenAnswer(inv -> {
            Project p = inv.getArgument(0);
            try {
                var idField = Project.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(p, UUID.randomUUID());
            } catch (Exception ignored) {}
            return p;
        });
        when(versionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SubmitFormatoARequest req = new SubmitFormatoARequest(
                null, "docente@uni.edu", "Proyecto en Empresa", Modality.PRACTICA_PROFESIONAL,
                "Director", null, "Objetivo General", "Objetivos Específicos",
                "http://pdf.com/formato.pdf", "http://pdf.com/carta.pdf", "coord@uni.edu"
        );

        FormatAVersion v = service.submitFormatoA(req);

        assertEquals(1, v.getVersionNumber());
        assertEquals("http://pdf.com/carta.pdf", v.getAcceptanceLetterUrl());
        verify(projectRepo, times(1)).save(any());
    }

    @Test
    @DisplayName("Debe incrementar el contador de intento en segundo envío tras rechazo")
    void submitFormatoA_secondAttempt_incrementsCounter() {
        UUID projectId = UUID.randomUUID();
        Project existingProject = Project.builder()
                .id(projectId)
                .teacherEmail("docente@uni.edu")
                .title("Título Anterior")
                .attemptNumber(1)
                .status(ProjectStatus.EN_PRIMERA_EVALUACION_FORMATO_A)
                .build();

        when(projectRepo.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(versionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SubmitFormatoARequest req = new SubmitFormatoARequest(
                projectId, "docente@uni.edu", "Título Mejorado", Modality.INVESTIGACION,
                "Director", null, "Objetivo Mejorado", "Objetivos",
                "http://pdf.com/formato_v2.pdf", null, "coord@uni.edu"
        );

        FormatAVersion v = service.submitFormatoA(req);

        assertEquals(2, v.getVersionNumber());
        assertEquals(2, existingProject.getAttemptNumber());
        assertEquals(ProjectStatus.EN_SEGUNDA_EVALUACION_FORMATO_A, existingProject.getStatus());

        verify(publisher, times(1)).publish(any(FormatoASubmittedEvent.class));
    }

    @Test
    @DisplayName("Debe marcar estado correcto en tercer intento")
    void submitFormatoA_thirdAttempt_setsCorrectStatus() {
        UUID projectId = UUID.randomUUID();
        Project existingProject = Project.builder()
                .id(projectId)
                .teacherEmail("docente@uni.edu")
                .title("Título")
                .attemptNumber(2)
                .status(ProjectStatus.EN_SEGUNDA_EVALUACION_FORMATO_A)
                .build();

        when(projectRepo.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(versionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SubmitFormatoARequest req = new SubmitFormatoARequest(
                projectId, "docente@uni.edu", "Título Final", Modality.INVESTIGACION,
                "Director", null, "Objetivo", "Objetivos",
                "http://pdf.com/formato_v3.pdf", null, "coord@uni.edu"
        );

        FormatAVersion v = service.submitFormatoA(req);

        assertEquals(3, v.getVersionNumber());
        assertEquals(ProjectStatus.EN_TERCERA_EVALUACION_FORMATO_A, existingProject.getStatus());
    }

    @Test
    @DisplayName("Debe rechazar definitivamente después del tercer intento")
    void submitFormatoA_fourthAttempt_rejectsDefinitively() {
        UUID projectId = UUID.randomUUID();
        Project existingProject = Project.builder()
                .id(projectId)
                .teacherEmail("docente@uni.edu")
                .title("Título")
                .attemptNumber(3)
                .status(ProjectStatus.EN_TERCERA_EVALUACION_FORMATO_A)
                .build();

        when(projectRepo.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SubmitFormatoARequest req = new SubmitFormatoARequest(
                projectId, "docente@uni.edu", "Título Cuarto Intento", Modality.INVESTIGACION,
                "Director", null, "Objetivo", "Objetivos",
                "http://pdf.com/formato_v4.pdf", null, "coord@uni.edu"
        );

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> service.submitFormatoA(req));

        assertTrue(exception.getMessage().contains("máximo de 3 intentos"));
        assertEquals(ProjectStatus.RECHAZADO_FORMATO_A, existingProject.getStatus());
        verify(versionRepo, never()).save(any());
        verify(publisher, never()).publish(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el proyecto no existe al reenviar")
    void submitFormatoA_projectNotFound_throwsException() {
        UUID nonExistentId = UUID.randomUUID();
        when(projectRepo.findById(nonExistentId)).thenReturn(Optional.empty());

        SubmitFormatoARequest req = new SubmitFormatoARequest(
                nonExistentId, "docente@uni.edu", "Título", Modality.INVESTIGACION,
                "Director", null, "Objetivo", "Objetivos",
                "http://pdf.com/formato.pdf", null, "coord@uni.edu"
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.submitFormatoA(req));

        assertEquals("Proyecto no encontrado", exception.getMessage());
    }
}
