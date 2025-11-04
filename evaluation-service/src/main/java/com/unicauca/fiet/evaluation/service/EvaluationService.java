package com.unicauca.fiet.evaluation.service;

import com.unicauca.fiet.common.events.FormatoAEvaluatedEvent;
import com.unicauca.fiet.common.messaging.EventPublisher;
import com.unicauca.fiet.evaluation.domain.Evaluation;
import com.unicauca.fiet.evaluation.repo.EvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Servicio de aplicación para persistir la evaluación y publicar eventos.
 */
@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final EventPublisher eventPublisher;
    private final EvaluationTemplate template = new FormatAEvaluationProcess();

    public EvaluationService(EvaluationRepository evaluationRepository, EventPublisher eventPublisher) {
        this.evaluationRepository = evaluationRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Ejecuta la evaluación de un Formato A.
     * @param ctx contexto con la decisión del coordinador
     * @return entidad Evaluation persistida
     */
    @Transactional
    public Evaluation evaluate(EvaluationContext ctx) {
        Evaluation.Result result = template.evaluate(ctx);
        Evaluation eval = Evaluation.builder()
                .projectId(ctx.projectId())
                .attemptNumber(ctx.attempt())
                .result(result)
                .comments(ctx.comments())
                .evaluatedAt(Instant.now())
                .build();
        eval = evaluationRepository.save(eval);

        // Publica evento para notificaciones y actualización de estado
        eventPublisher.publish(FormatoAEvaluatedEvent.builder()
                .projectId(ctx.projectId())
                .attemptNumber(ctx.attempt())
                .decision(switch (result) {
                    case APPROVED -> FormatoAEvaluatedEvent.Decision.APPROVED;
                    case REJECTED -> FormatoAEvaluatedEvent.Decision.REJECTED;
                    case REJECTED_FINAL -> FormatoAEvaluatedEvent.Decision.REJECTED_FINAL;
                })
                .comments(ctx.comments())
                .notifyEmails(ctx.notifyEmails())
                .occurredOn(Instant.now())
                .build());

        return eval;
    }
}
