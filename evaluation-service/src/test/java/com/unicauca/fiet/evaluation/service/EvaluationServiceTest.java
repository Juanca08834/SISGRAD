package com.unicauca.fiet.evaluation.service;

import com.unicauca.fiet.common.events.FormatoAEvaluatedEvent;
import com.unicauca.fiet.common.messaging.EventPublisher;
import com.unicauca.fiet.evaluation.domain.Evaluation;
import com.unicauca.fiet.evaluation.repo.EvaluationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas del servicio de Evaluación (EvaluationService)")
public class EvaluationServiceTest {

    private EvaluationRepository evaluationRepository;
    private EventPublisher eventPublisher;
    private EvaluationService service;

    @BeforeEach
    void setup() {
        evaluationRepository = Mockito.mock(EvaluationRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        service = new EvaluationService(evaluationRepository, eventPublisher);
    }

    @Test
    @DisplayName("Debe guardar evaluación aprobada y publicar evento")
    void evaluate_approved_savesAndPublishesEvent() {
        when(evaluationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UUID projectId = UUID.randomUUID();
        EvaluationContext ctx = new EvaluationContext(
                projectId,
                1,
                "APPROVED",
                "Excelente propuesta",
                List.of("docente@uni.edu", "estudiante@uni.edu")
        );

        Evaluation result = service.evaluate(ctx);

        assertNotNull(result);
        assertEquals(Evaluation.Result.APPROVED, result.getResult());
        assertEquals(projectId, result.getProjectId());
        assertEquals(1, result.getAttemptNumber());
        assertEquals("Excelente propuesta", result.getComments());
        assertNotNull(result.getEvaluatedAt());

        verify(evaluationRepository, times(1)).save(any(Evaluation.class));

        ArgumentCaptor<FormatoAEvaluatedEvent> captor = ArgumentCaptor.forClass(FormatoAEvaluatedEvent.class);
        verify(eventPublisher, times(1)).publish(captor.capture());

        FormatoAEvaluatedEvent event = captor.getValue();
        assertEquals(FormatoAEvaluatedEvent.Decision.APPROVED, event.getDecision());
        assertEquals(projectId, event.getProjectId());
        assertEquals(2, event.getNotifyEmails().size());
    }

    @Test
    @DisplayName("Debe guardar evaluación rechazada y publicar evento")
    void evaluate_rejected_savesAndPublishesEvent() {
        when(evaluationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UUID projectId = UUID.randomUUID();
        EvaluationContext ctx = new EvaluationContext(
                projectId,
                1,
                "REJECTED",
                "Falta claridad en los objetivos",
                List.of("docente@uni.edu")
        );

        Evaluation result = service.evaluate(ctx);

        assertEquals(Evaluation.Result.REJECTED, result.getResult());
        assertEquals("Falta claridad en los objetivos", result.getComments());

        ArgumentCaptor<FormatoAEvaluatedEvent> captor = ArgumentCaptor.forClass(FormatoAEvaluatedEvent.class);
        verify(eventPublisher, times(1)).publish(captor.capture());

        FormatoAEvaluatedEvent event = captor.getValue();
        assertEquals(FormatoAEvaluatedEvent.Decision.REJECTED, event.getDecision());
        assertEquals("Falta claridad en los objetivos", event.getComments());
    }

    @Test
    @DisplayName("Debe marcar rechazo final en tercer intento rechazado")
    void evaluate_thirdAttemptRejected_marksFinalRejection() {
        when(evaluationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UUID projectId = UUID.randomUUID();
        EvaluationContext ctx = new EvaluationContext(
                projectId,
                3,
                "REJECTED",
                "No cumple con los estándares requeridos",
                List.of("docente@uni.edu", "estudiante@uni.edu")
        );

        Evaluation result = service.evaluate(ctx);

        assertEquals(Evaluation.Result.REJECTED_FINAL, result.getResult());
        assertEquals(3, result.getAttemptNumber());

        ArgumentCaptor<FormatoAEvaluatedEvent> captor = ArgumentCaptor.forClass(FormatoAEvaluatedEvent.class);
        verify(eventPublisher, times(1)).publish(captor.capture());

        FormatoAEvaluatedEvent event = captor.getValue();
        assertEquals(FormatoAEvaluatedEvent.Decision.REJECTED_FINAL, event.getDecision());
    }

    @Test
    @DisplayName("Debe guardar evaluación con lista vacía de emails")
    void evaluate_emptyEmailList_savesSuccessfully() {
        when(evaluationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UUID projectId = UUID.randomUUID();
        EvaluationContext ctx = new EvaluationContext(
                projectId,
                1,
                "APPROVED",
                "Aprobado",
                List.of()
        );

        Evaluation result = service.evaluate(ctx);

        assertNotNull(result);
        verify(evaluationRepository, times(1)).save(any(Evaluation.class));
        verify(eventPublisher, times(1)).publish(any(FormatoAEvaluatedEvent.class));
    }

    @Test
    @DisplayName("Debe incluir comentarios en el evento publicado")
    void evaluate_withComments_includesInEvent() {
        when(evaluationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String detailedComments = "El formato A presenta inconsistencias en la metodología propuesta. "
                + "Se recomienda revisar la sección de objetivos específicos.";

        EvaluationContext ctx = new EvaluationContext(
                UUID.randomUUID(),
                2,
                "REJECTED",
                detailedComments,
                List.of("docente@uni.edu")
        );

        service.evaluate(ctx);

        ArgumentCaptor<FormatoAEvaluatedEvent> captor = ArgumentCaptor.forClass(FormatoAEvaluatedEvent.class);
        verify(eventPublisher).publish(captor.capture());

        assertEquals(detailedComments, captor.getValue().getComments());
    }

    @Test
    @DisplayName("Debe manejar segundo intento aprobado correctamente")
    void evaluate_secondAttemptApproved_success() {
        when(evaluationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EvaluationContext ctx = new EvaluationContext(
                UUID.randomUUID(),
                2,
                "APPROVED",
                "Las correcciones fueron satisfactorias",
                List.of("docente@uni.edu", "estudiante@uni.edu")
        );

        Evaluation result = service.evaluate(ctx);

        assertEquals(Evaluation.Result.APPROVED, result.getResult());
        assertEquals(2, result.getAttemptNumber());

        verify(evaluationRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publish(any());
    }
}

