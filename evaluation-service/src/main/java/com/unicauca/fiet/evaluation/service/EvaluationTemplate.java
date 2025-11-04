package com.unicauca.fiet.evaluation.service;

import com.unicauca.fiet.evaluation.domain.Evaluation;

/**
 * Template Method (GoF) para ejecutar el proceso de evaluación.
 * Permite aplicar pasos comunes y personalizar la decisión final.
 */
public abstract class EvaluationTemplate {

    /**
     * Ejecuta el flujo de evaluación completo (no sobrescribible).
     * @param ctx contexto con los datos necesarios
     * @return resultado de evaluación
     */
    public final Evaluation.Result evaluate(EvaluationContext ctx) {
        preCheck(ctx);
        Evaluation.Result result = doEvaluate(ctx);
        postActions(ctx, result);
        return result;
    }

    /** Validaciones previas (por defecto, ninguna). */
    protected void preCheck(EvaluationContext ctx) { }

    /** Paso específico que toma la decisión. */
    protected abstract Evaluation.Result doEvaluate(EvaluationContext ctx);

    /** Acciones posteriores como side-effects (por defecto, ninguna). */
    protected void postActions(EvaluationContext ctx, Evaluation.Result result) { }
}
