package com.unicauca.anteproyecto.infrastructure.adapters.out.persistence;

import com.unicauca.anteproyecto.domain.model.EvaluadorAsignado;
import com.unicauca.anteproyecto.domain.ports.out.EvaluadorPersistencePort;
import com.unicauca.anteproyecto.infrastructure.adapters.out.persistence.entity.EvaluadorAsignadoEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador JPA para Evaluadores
 * UBICACIÓN: infrastructure/adapters/out/persistence/EvaluadorJpaAdapter.java
 */
@Component
public class EvaluadorJpaAdapter implements EvaluadorPersistencePort {

    private final EvaluadorAsignadoRepository repository;

    public EvaluadorJpaAdapter(EvaluadorAsignadoRepository repository) {
        this.repository = repository;
    }

    @Override
    public EvaluadorAsignado guardar(EvaluadorAsignado evaluador) {
        EvaluadorAsignadoEntity entity = toEntity(evaluador);
        EvaluadorAsignadoEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<EvaluadorAsignado> guardarTodos(List<EvaluadorAsignado> evaluadores) {
        List<EvaluadorAsignadoEntity> entities = evaluadores.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
        
        List<EvaluadorAsignadoEntity> saved = repository.saveAll(entities);
        
        return saved.stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<EvaluadorAsignado> buscarPorAnteproyecto(Long anteproyectoId) {
        return repository.findByAnteproyectoId(anteproyectoId).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    // Métodos de mapeo
    private EvaluadorAsignadoEntity toEntity(EvaluadorAsignado evaluador) {
        EvaluadorAsignadoEntity entity = new EvaluadorAsignadoEntity();
        entity.setId(evaluador.getId());
        entity.setAnteproyectoId(evaluador.getAnteproyectoId());
        entity.setDocenteEvaluadorId(evaluador.getDocenteEvaluadorId());
        entity.setNombreEvaluador(evaluador.getNombreEvaluador());
        entity.setEmailEvaluador(evaluador.getEmailEvaluador());
        entity.setFechaAsignacion(evaluador.getFechaAsignacion());
        entity.setNotificado(evaluador.isNotificado());
        return entity;
    }

    private EvaluadorAsignado toDomain(EvaluadorAsignadoEntity entity) {
        EvaluadorAsignado evaluador = new EvaluadorAsignado();
        evaluador.setId(entity.getId());
        evaluador.setAnteproyectoId(entity.getAnteproyectoId());
        evaluador.setDocenteEvaluadorId(entity.getDocenteEvaluadorId());
        evaluador.setNombreEvaluador(entity.getNombreEvaluador());
        evaluador.setEmailEvaluador(entity.getEmailEvaluador());
        evaluador.setFechaAsignacion(entity.getFechaAsignacion());
        evaluador.setNotificado(entity.isNotificado());
        return evaluador;
    }
}