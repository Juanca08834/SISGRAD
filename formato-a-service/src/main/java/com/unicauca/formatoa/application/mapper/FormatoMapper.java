package com.unicauca.formatoa.application.mapper;

import com.unicauca.formatoa.application.dto.CrearFormatoRequest;
import com.unicauca.formatoa.application.dto.EvaluacionResponse;
import com.unicauca.formatoa.application.dto.EvaluarFormatoRequest;
import com.unicauca.formatoa.application.dto.FormatoResponse;
import com.unicauca.formatoa.domain.model.Evaluacion;
import com.unicauca.formatoa.domain.model.FormatoA;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre DTOs y entidades de dominio
 */
@Component
public class FormatoMapper {

    /**
     * Convierte CrearFormatoRequest a FormatoA (dominio)
     */
    public FormatoA toFormatoA(CrearFormatoRequest request) {
        FormatoA formato = new FormatoA();
        formato.setTitulo(request.getTitulo());
        formato.setModalidad(request.getModalidad());
        formato.setFechaPresentacion(request.getFechaPresentacion());
        formato.setDirector(request.getDirector());
        formato.setCodirector(request.getCodirector());
        formato.setObjetivoGeneral(request.getObjetivoGeneral());
        formato.setObjetivosEspecificos(request.getObjetivosEspecificos());
        formato.setArchivoUrl(request.getArchivoUrl());
        formato.setDocenteId(request.getDocenteId());
        return formato;
    }

    /**
     * Convierte FormatoA (dominio) a FormatoResponse
     */
    public FormatoResponse toFormatoResponse(FormatoA formato) {
        return new FormatoResponse(
            formato.getId(),
            formato.getTitulo(),
            formato.getModalidad(),
            formato.getFechaPresentacion(),
            formato.getDirector(),
            formato.getCodirector(),
            formato.getObjetivoGeneral(),
            formato.getObjetivosEspecificos(),
            formato.getArchivoUrl(),
            formato.getEstado(),
            formato.getNumeroIntento(),
            formato.getDocenteId(),
            formato.getFechaCreacion(),
            formato.getFechaActualizacion()
        );
    }

    /**
     * Convierte EvaluarFormatoRequest a Evaluacion (dominio)
     */
    public Evaluacion toEvaluacion(EvaluarFormatoRequest request) {
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setFormatoAId(request.getFormatoAId());
        evaluacion.setCoordinadorId(request.getCoordinadorId());
        evaluacion.setResultado(request.getResultado());
        evaluacion.setObservaciones(request.getObservaciones());
        return evaluacion;
    }

    /**
     * Convierte Evaluacion (dominio) a EvaluacionResponse
     */
    public EvaluacionResponse toEvaluacionResponse(Evaluacion evaluacion) {
        return new EvaluacionResponse(
            evaluacion.getId(),
            evaluacion.getFormatoAId(),
            evaluacion.getCoordinadorId(),
            evaluacion.getResultado(),
            evaluacion.getObservaciones(),
            evaluacion.getFechaEvaluacion()
        );
    }
}