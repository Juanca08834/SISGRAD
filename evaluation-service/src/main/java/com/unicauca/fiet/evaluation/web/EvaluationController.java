package com.unicauca.fiet.evaluation.web;

import com.unicauca.fiet.evaluation.domain.Evaluation;
import com.unicauca.fiet.evaluation.service.EvaluationContext;
import com.unicauca.fiet.evaluation.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** Endpoints para que el coordinador evalúe el Formato A (req. 3). */
@RestController
@RequestMapping("/api/evaluations")
@Tag(name = "Evaluaciones")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    /** Crea una evaluación (APPROVED|REJECTED) y dispara notificaciones. */
    @Operation(summary = "Evaluar un Formato A (req. 3)")
    @PostMapping("/formato-a")
    public ResponseEntity<Evaluation> evaluateFormatoA(
            @RequestParam @NotNull UUID projectId,
            @RequestParam int attempt,
            @RequestParam @NotBlank String decision,
            @RequestParam(required = false) String comments,
            @RequestParam(required = false) List<String> notifyEmails
    ) {
        EvaluationContext ctx = new EvaluationContext(projectId, attempt, decision, comments, notifyEmails);
        return ResponseEntity.ok(evaluationService.evaluate(ctx));
    }
}
