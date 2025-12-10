package com.unicauca.anteproyecto.application.mapper;

import com.unicauca.anteproyecto.application.dto.*;
import com.unicauca.anteproyecto.domain.model.Anteproyecto;
import com.unicauca.anteproyecto.domain.model.EvaluadorAsignado;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversión entre DTOs y entidades de dominio
 * UBICACIÓN: application/mapper/AnteproyectoMapper.java
 */
@Component
public class AnteproyectoMapper {

    public Anteproyecto toAnteproyecto(CrearAnteproyectoRequest request) {
        return new Anteproyecto(
            request.getFormatoAId(),
            request.getArchivoUrl(),
            request.getDocenteId()
        );
    }

    public AnteproyectoResponse toResponse(Anteproyecto anteproyecto) {
        AnteproyectoResponse response = new AnteproyectoResponse();
        response.setId(anteproyecto.getId());
        response.setFormatoAId(anteproyecto.getFormatoAId());
        response.setArchivoUrl(anteproyecto.getArchivoUrl());
        response.setEstado(anteproyecto.getEstado());
        response.setFechaSubida(anteproyecto.getFechaSubida());
        response.setDocenteId(anteproyecto.getDocenteId());
        return response;
    }

    public AnteproyectoResponse toResponseConEvaluadores(Anteproyecto anteproyecto, 
                                                         List<EvaluadorAsignado> evaluadores) {
        AnteproyectoResponse response = toResponse(anteproyecto);
        response.setEvaluadores(
            evaluadores.stream()
                .map(this::toEvaluadorDto)
                .collect(Collectors.toList())
        );
        return response;
    }

    public EvaluadorAsignado toEvaluadorAsignado(EvaluadorDto dto, Long anteproyectoId) {
        return new EvaluadorAsignado(
            anteproyectoId,
            dto.getDocenteEvaluadorId(),
            dto.getNombreEvaluador(),
            dto.getEmailEvaluador()
        );
    }

    public EvaluadorDto toEvaluadorDto(EvaluadorAsignado evaluador) {
        EvaluadorDto dto = new EvaluadorDto();
        dto.setDocenteEvaluadorId(evaluador.getDocenteEvaluadorId());
        dto.setNombreEvaluador(evaluador.getNombreEvaluador());
        dto.setEmailEvaluador(evaluador.getEmailEvaluador());
        return dto;
    }
}