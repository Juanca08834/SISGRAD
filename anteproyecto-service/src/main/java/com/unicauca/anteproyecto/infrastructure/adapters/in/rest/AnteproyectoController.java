package com.unicauca.anteproyecto.infrastructure.adapters.in.rest;

import com.unicauca.anteproyecto.application.dto.*;
import com.unicauca.anteproyecto.application.mapper.AnteproyectoMapper;
import com.unicauca.anteproyecto.domain.model.Anteproyecto;
import com.unicauca.anteproyecto.domain.model.EvaluadorAsignado;
import com.unicauca.anteproyecto.domain.ports.in.AsignarEvaluadoresUseCase;
import com.unicauca.anteproyecto.domain.ports.in.ConsultarAnteproyectoUseCase;
import com.unicauca.anteproyecto.domain.ports.in.CrearAnteproyectoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de Anteproyectos
 * UBICACIÓN: infrastructure/adapters/in/rest/AnteproyectoController.java
 */
@RestController
@RequestMapping("/api/anteproyectos")
@Tag(name = "Anteproyectos", description = "API para gestión de Anteproyectos y asignación de evaluadores")
public class AnteproyectoController {

    private final CrearAnteproyectoUseCase crearUseCase;
    private final AsignarEvaluadoresUseCase asignarUseCase;
    private final ConsultarAnteproyectoUseCase consultarUseCase;
    private final AnteproyectoMapper mapper;

    public AnteproyectoController(CrearAnteproyectoUseCase crearUseCase,
                                 AsignarEvaluadoresUseCase asignarUseCase,
                                 ConsultarAnteproyectoUseCase consultarUseCase,
                                 AnteproyectoMapper mapper) {
        this.crearUseCase = crearUseCase;
        this.asignarUseCase = asignarUseCase;
        this.consultarUseCase = consultarUseCase;
        this.mapper = mapper;
    }

    /**
     * Crea un nuevo anteproyecto (REQUISITO 6)
     */
    @PostMapping
    @Operation(
        summary = "Subir anteproyecto (REQUISITO 6)",
        description = "Docente sube el anteproyecto una vez aprobado el Formato A. " +
                     "El sistema envía una notificación asíncrona al jefe de departamento."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Anteproyecto creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnteproyectoResponse.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inválido o expirado", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<AnteproyectoResponse> crear(
            @Valid @RequestBody CrearAnteproyectoRequest request) {
        
        Anteproyecto anteproyecto = mapper.toAnteproyecto(request);
        Anteproyecto creado = crearUseCase.crear(anteproyecto);
        AnteproyectoResponse response = mapper.toResponse(creado);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Asigna 2 evaluadores a un anteproyecto (REQUISITO 8) ⭐
     */
    @PostMapping("/{id}/asignar-evaluadores")
    @Operation(
        summary = "Asignar evaluadores (REQUISITO 8)",
        description = "Jefe de departamento asigna 2 docentes del departamento para evaluar el anteproyecto. " +
                     "Se notifica a los evaluadores mediante email (simulado con logger)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Evaluadores asignados exitosamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = EvaluadorDto.class))
            )
        ),
        @ApiResponse(responseCode = "400", description = "Debe asignar exactamente 2 evaluadores", content = @Content),
        @ApiResponse(responseCode = "404", description = "Anteproyecto no encontrado", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inválido o expirado", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<EvaluadorDto>> asignarEvaluadores(
            @Parameter(description = "ID del anteproyecto", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AsignarEvaluadoresRequest request) {
        
        // Convertir DTOs a entidades de dominio
        List<EvaluadorAsignado> evaluadores = request.getEvaluadores().stream()
            .map(dto -> mapper.toEvaluadorAsignado(dto, id))
            .collect(Collectors.toList());
        
        // Asignar evaluadores (lógica de negocio)
        List<EvaluadorAsignado> asignados = asignarUseCase.asignarEvaluadores(id, evaluadores);
        
        // Convertir a DTOs de respuesta
        List<EvaluadorDto> response = asignados.stream()
            .map(mapper::toEvaluadorDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un anteproyecto por ID
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener anteproyecto por ID",
        description = "Consulta los detalles de un anteproyecto específico incluyendo sus evaluadores asignados"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Anteproyecto encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AnteproyectoResponse.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Anteproyecto no encontrado", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inválido o expirado", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<AnteproyectoResponse> obtenerPorId(
            @Parameter(description = "ID del anteproyecto", required = true, example = "1")
            @PathVariable Long id) {
        Anteproyecto anteproyecto = consultarUseCase.obtenerPorId(id);
        List<EvaluadorAsignado> evaluadores = consultarUseCase.obtenerEvaluadores(id);
        AnteproyectoResponse response = mapper.toResponseConEvaluadores(anteproyecto, evaluadores);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todos los anteproyectos (REQUISITO 7)
     */
    @GetMapping
    @Operation(
        summary = "Listar anteproyectos (REQUISITO 7)",
        description = "Jefe de departamento ve los anteproyectos subidos por docentes para luego asignar evaluadores"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de anteproyectos",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = AnteproyectoResponse.class))
            )
        ),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inválido o expirado", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<AnteproyectoResponse>> obtenerTodos() {
        List<Anteproyecto> anteproyectos = consultarUseCase.obtenerTodos();
        List<AnteproyectoResponse> response = anteproyectos.stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene los evaluadores asignados a un anteproyecto
     */
    @GetMapping("/{id}/evaluadores")
    @Operation(
        summary = "Obtener evaluadores de un anteproyecto",
        description = "Lista los 2 evaluadores asignados al anteproyecto"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de evaluadores asignados",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = EvaluadorDto.class))
            )
        ),
        @ApiResponse(responseCode = "404", description = "Anteproyecto no encontrado", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT inválido o expirado", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<EvaluadorDto>> obtenerEvaluadores(
            @Parameter(description = "ID del anteproyecto", required = true, example = "1")
            @PathVariable Long id) {
        List<EvaluadorAsignado> evaluadores = consultarUseCase.obtenerEvaluadores(id);
        List<EvaluadorDto> response = evaluadores.stream()
            .map(mapper::toEvaluadorDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}