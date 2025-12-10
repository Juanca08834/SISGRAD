package com.unicauca.anteproyecto.infrastructure.adapters.out.persistence;

import com.unicauca.anteproyecto.domain.model.Anteproyecto;
import com.unicauca.anteproyecto.domain.ports.out.AnteproyectoPersistencePort;
import com.unicauca.anteproyecto.infrastructure.adapters.out.persistence.entity.AnteproyectoEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador JPA para Anteproyecto
 * UBICACIÓN: infrastructure/adapters/out/persistence/AnteproyectoJpaAdapter.java
 */
@Component
public class AnteproyectoJpaAdapter implements AnteproyectoPersistencePort {

    private final AnteproyectoRepository repository;

    public AnteproyectoJpaAdapter(AnteproyectoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Anteproyecto guardar(Anteproyecto anteproyecto) {
        AnteproyectoEntity entity = toEntity(anteproyecto);
        AnteproyectoEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Anteproyecto> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Anteproyecto> buscarTodos() {
        return repository.findAllByOrderByFechaSubidaDesc().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Anteproyecto actualizar(Anteproyecto anteproyecto) {
        AnteproyectoEntity entity = toEntity(anteproyecto);
        AnteproyectoEntity updated = repository.save(entity);
        return toDomain(updated);
    }

    // Métodos de mapeo
    private AnteproyectoEntity toEntity(Anteproyecto anteproyecto) {
        AnteproyectoEntity entity = new AnteproyectoEntity();
        entity.setId(anteproyecto.getId());
        entity.setFormatoAId(anteproyecto.getFormatoAId());
        entity.setArchivoUrl(anteproyecto.getArchivoUrl());
        entity.setEstado(anteproyecto.getEstado());
        entity.setFechaSubida(anteproyecto.getFechaSubida());
        entity.setDocenteId(anteproyecto.getDocenteId());
        return entity;
    }

    private Anteproyecto toDomain(AnteproyectoEntity entity) {
        Anteproyecto anteproyecto = new Anteproyecto();
        anteproyecto.setId(entity.getId());
        anteproyecto.setFormatoAId(entity.getFormatoAId());
        anteproyecto.setArchivoUrl(entity.getArchivoUrl());
        anteproyecto.setEstado(entity.getEstado());
        anteproyecto.setFechaSubida(entity.getFechaSubida());
        anteproyecto.setDocenteId(entity.getDocenteId());
        return anteproyecto;
    }
}