package com.unicauca.formatoa.infrastructure.adapters.out.persistence;

import com.unicauca.formatoa.domain.model.Evaluacion;
import com.unicauca.formatoa.domain.ports.out.EvaluacionPersistencePort;
import com.unicauca.formatoa.infrastructure.adapters.out.persistence.entity.EvaluacionEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador JPA para Evaluaciones
 */
@Component
public class EvaluacionJpaAdapter implements EvaluacionPersistencePort {

    private final EvaluacionRepository repository;

    public EvaluacionJpaAdapter(EvaluacionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Evaluacion guardar(Evaluacion evaluacion) {
        EvaluacionEntity entity = toEntity(evaluacion);
        EvaluacionEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<Evaluacion> buscarPorFormatoA(Long formatoAId) {
        return repository.findByFormatoAId(formatoAId).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Evaluacion> buscarPorId(Long id) {
        return repository.findById(id)
            .map(this::toDomain);
    }

    // ==================== MÉTODOS DE MAPEO ====================

    private EvaluacionEntity toEntity(Evaluacion evaluacion) {
        EvaluacionEntity entity = new EvaluacionEntity();
        entity.setId(evaluacion.getId());
        entity.setFormatoAId(evaluacion.getFormatoAId());
        entity.setCoordinadorId(evaluacion.getCoordinadorId());
        entity.setResultado(evaluacion.getResultado());
        entity.setObservaciones(evaluacion.getObservaciones());
        entity.setFechaEvaluacion(evaluacion.getFechaEvaluacion());
        return entity;
    }

    private Evaluacion toDomain(EvaluacionEntity entity) {
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setId(entity.getId());
        evaluacion.setFormatoAId(entity.getFormatoAId());
        evaluacion.setCoordinadorId(entity.getCoordinadorId());
        evaluacion.setResultado(entity.getResultado());
        evaluacion.setObservaciones(entity.getObservaciones());
        evaluacion.setFechaEvaluacion(entity.getFechaEvaluacion());
        return evaluacion;
    }
}