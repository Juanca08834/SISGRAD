package com.unicauca.fiet.evaluation.service;

import com.unicauca.fiet.evaluation.domain.Evaluation;

/**
 * Proceso de evaluación del Formato A. 
 * Aplica la regla de rechazo definitivo al tercer intento.
 */
public class FormatAEvaluationProcess extends EvaluationTemplate {
    @Override
    protected Evaluation.Result doEvaluate(EvaluationContext ctx) {
        if ("APPROVED".equalsIgnoreCase(ctx.decision())) {
            return Evaluation.Result.APPROVED;
        }
        // si es intento 3 y la decisión es REJECTED -> REJECTED_FINAL
        if (ctx.attempt() >= 3) {
            return Evaluation.Result.REJECTED_FINAL;
        }
        return Evaluation.Result.REJECTED;
    }
}
