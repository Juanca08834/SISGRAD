package com.unicauca.fiet.evaluation.service;

import java.util.List;
import java.util.UUID;

/** Contexto de evaluación con datos de entrada. */
public record EvaluationContext(
        UUID projectId,
        int attempt,
        String decision, // APPROVED|REJECTED
        String comments,
        List<String> notifyEmails
) {}
