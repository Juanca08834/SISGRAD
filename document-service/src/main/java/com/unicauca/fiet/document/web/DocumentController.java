package com.unicauca.fiet.document.web;

import com.unicauca.fiet.document.domain.Anteproyecto;
import com.unicauca.fiet.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** Endpoints de anteproyectos (envío y listado). */
@RestController
@RequestMapping("/api/anteproyectos")
@Tag(name = "Anteproyectos")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /** Enviar anteproyecto (req. 6). */
    @Operation(summary = "Enviar anteproyecto (req. 6)")
    @PostMapping("/submit")
    public ResponseEntity<Anteproyecto> submit(
            @RequestParam @NotNull UUID projectId,
            @RequestParam @Email String docenteEmail,
            @RequestParam @NotBlank String pdfUrl,
            @RequestParam @Email String deptHeadEmail) {
        return ResponseEntity.ok(documentService.submitAnteproyecto(projectId, docenteEmail, pdfUrl, deptHeadEmail));
    }

    /** Listar anteproyectos para Jefatura (req. 7 - listar). */
    @Operation(summary = "Listar anteproyectos (req. 7)")
    @GetMapping
    public ResponseEntity<List<Anteproyecto>> list() {
        return ResponseEntity.ok(documentService.listAnteproyectos());
    }
}
