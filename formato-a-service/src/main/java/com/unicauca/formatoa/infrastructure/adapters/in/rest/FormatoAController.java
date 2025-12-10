package com.unicauca.formatoa.infrastructure.adapters.in.rest;

import com.unicauca.formatoa.application.dto.*;
import com.unicauca.formatoa.application.mapper.FormatoMapper;
import com.unicauca.formatoa.domain.model.Evaluacion;
import com.unicauca.formatoa.domain.model.FormatoA;
import com.unicauca.formatoa.domain.ports.in.ConsultarFormatoUseCase;
import com.unicauca.formatoa.domain.ports.in.CrearFormatoUseCase;
import com.unicauca.formatoa.domain.ports.in.EvaluarFormatoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de Formatos A
 */
@RestController
@RequestMapping("/api/formatos")
@Tag(name = "Formatos A", description = "API para gestión de Formatos A de trabajos de grado")
public class FormatoAController {

    private final CrearFormatoUseCase crearFormatoUseCase;
    private final EvaluarFormatoUseCase evaluarFormatoUseCase;
    private final ConsultarFormatoUseCase consultarFormatoUseCase;
    private final FormatoMapper mapper;

    public FormatoAController(CrearFormatoUseCase crearFormatoUseCase,
                             EvaluarFormatoUseCase evaluarFormatoUseCase,
                             ConsultarFormatoUseCase consultarFormatoUseCase,
                             FormatoMapper mapper) {
        this.crearFormatoUseCase = crearFormatoUseCase;
        this.evaluarFormatoUseCase = evaluarFormatoUseCase;
        this.consultarFormatoUseCase = consultarFormatoUseCase;
        this.mapper = mapper;
    }

    /**
     * Crea un nuevo Formato A
     */
    @PostMapping
    @Operation(
        summary = "Crear nuevo Formato A",
        description = "Crea un nuevo Formato A para inicio del proceso de trabajo de grado",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Formato A creado exitosamente",
                content = @Content(schema = @Schema(implementation = FormatoResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
        }
    )
    public ResponseEntity<FormatoResponse> crear(@Valid @RequestBody CrearFormatoRequest request) {
        FormatoA formato = mapper.toFormatoA(request);
        FormatoA formatoCreado = crearFormatoUseCase.crear(formato);
        FormatoResponse response = mapper.toFormatoResponse(formatoCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Crea una nueva versión de un Formato A (reintento)
     */
    @PostMapping("/{id}/nueva-version")
    @Operation(
        summary = "Crear nueva versión de Formato A",
        description = "Crea una nueva versión del Formato A cuando fue rechazado (máximo 3 intentos)",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Nueva versión creada exitosamente",
                content = @Content(schema = @Schema(implementation = FormatoResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "No se puede crear nueva versión"),
            @ApiResponse(responseCode = "404", description = "Formato no encontrado")
        }
    )
    public ResponseEntity<FormatoResponse> crearNuevaVersion(
            @PathVariable Long id,
            @Valid @RequestBody CrearFormatoRequest request) {
        
        FormatoA nuevoFormato = mapper.toFormatoA(request);
        FormatoA formatoActualizado = crearFormatoUseCase.crearNuevaVersion(id, nuevoFormato);
        FormatoResponse response = mapper.toFormatoResponse(formatoActualizado);
        return ResponseEntity.ok(response);
    }

    /**
     * Evalúa un Formato A
     */
    @PostMapping("/evaluaciones")
    @Operation(
        summary = "Evaluar Formato A",
        description = "Permite al coordinador evaluar un Formato A (aprobar/rechazar con observaciones)",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Evaluación registrada exitosamente",
                content = @Content(schema = @Schema(implementation = EvaluacionResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Formato no encontrado")
        }
    )
    public ResponseEntity<EvaluacionResponse> evaluar(
            @Valid @RequestBody EvaluarFormatoRequest request) {
        
        Evaluacion evaluacion = mapper.toEvaluacion(request);
        Evaluacion evaluacionCreada = evaluarFormatoUseCase.evaluar(evaluacion);
        EvaluacionResponse response = mapper.toEvaluacionResponse(evaluacionCreada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtiene un Formato A por ID
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener Formato A por ID",
        description = "Consulta los detalles de un Formato A específico",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Formato encontrado",
                content = @Content(schema = @Schema(implementation = FormatoResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Formato no encontrado")
        }
    )
    public ResponseEntity<FormatoResponse> obtenerPorId(@PathVariable Long id) {
        FormatoA formato = consultarFormatoUseCase.obtenerPorId(id);
        FormatoResponse response = mapper.toFormatoResponse(formato);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todos los Formatos A de un docente
     */
    @GetMapping("/docente/{docenteId}")
    @Operation(
        summary = "Obtener Formatos A de un docente",
        description = "Lista todos los Formatos A creados por un docente específico",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de formatos del docente"
            )
        }
    )
    public ResponseEntity<List<FormatoResponse>> obtenerPorDocente(
            @PathVariable Long docenteId) {
        
        List<FormatoA> formatos = consultarFormatoUseCase.obtenerPorDocente(docenteId);
        List<FormatoResponse> response = formatos.stream()
            .map(mapper::toFormatoResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todos los Formatos A pendientes de evaluación
     */
    @GetMapping("/pendientes")
    @Operation(
        summary = "Obtener Formatos A pendientes",
        description = "Lista todos los Formatos A que están pendientes de evaluación (para coordinadores)",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de formatos pendientes"
            )
        }
    )
    public ResponseEntity<List<FormatoResponse>> obtenerPendientes() {
        List<FormatoA> formatos = consultarFormatoUseCase.obtenerPendientes();
        List<FormatoResponse> response = formatos.stream()
            .map(mapper::toFormatoResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todos los Formatos A
     */
    @GetMapping
    @Operation(
        summary = "Obtener todos los Formatos A",
        description = "Lista todos los Formatos A del sistema",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de todos los formatos"
            )
        }
    )
    public ResponseEntity<List<FormatoResponse>> obtenerTodos() {
        List<FormatoA> formatos = consultarFormatoUseCase.obtenerTodos();
        List<FormatoResponse> response = formatos.stream()
            .map(mapper::toFormatoResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}