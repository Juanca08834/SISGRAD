package com.unicauca.fiet.project.web;

import com.unicauca.fiet.project.domain.FormatAVersion;
import com.unicauca.fiet.project.service.ProjectService;
import com.unicauca.fiet.project.service.SubmitFormatoARequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Endpoints para gestión del Formato A. */
@RestController
@RequestMapping("/api/formatoa")
@Tag(name = "Formato A")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /** Envía el Formato A (nuevo o nueva versión). */
    @Operation(summary = "Envío/Formato A (req. 2 y 4)")
    @PostMapping("/submit")
    public ResponseEntity<FormatAVersion> submit(@Valid @RequestBody SubmitFormatoARequest req) {
        return ResponseEntity.ok(projectService.submitFormatoA(req));
    }
}
