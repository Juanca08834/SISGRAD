package com.unicauca.formatoa.infrastructure.adapters.out.persistence;

import com.unicauca.formatoa.domain.model.EstadoFormato;
import com.unicauca.formatoa.domain.model.FormatoA;
import com.unicauca.formatoa.domain.ports.out.FormatoPersistencePort;
import com.unicauca.formatoa.infrastructure.adapters.out.persistence.entity.FormatoAEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador JPA que implementa el puerto de persistencia
 */
@Component
public class FormatoJpaAdapter implements FormatoPersistencePort {

    private final FormatoARepository repository;

    public FormatoJpaAdapter(FormatoARepository repository) {
        this.repository = repository;
    }

    @Override
    public FormatoA guardar(FormatoA formatoA) {
        FormatoAEntity entity = toEntity(formatoA);
        FormatoAEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<FormatoA> buscarPorId(Long id) {
        return repository.findById(id)
            .map(this::toDomain);
    }

    @Override
    public List<FormatoA> buscarPorDocente(Long docenteId) {
        return repository.findByDocenteId(docenteId).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<FormatoA> buscarPorEstado(EstadoFormato estado) {
        return repository.findByEstado(estado).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<FormatoA> buscarTodos() {
        return repository.findAllByOrderByFechaCreacionDesc().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public FormatoA actualizar(FormatoA formatoA) {
        FormatoAEntity entity = toEntity(formatoA);
        FormatoAEntity updatedEntity = repository.save(entity);
        return toDomain(updatedEntity);
    }

    // ==================== MÉTODOS DE MAPEO ====================

    private FormatoAEntity toEntity(FormatoA formato) {
        FormatoAEntity entity = new FormatoAEntity();
        entity.setId(formato.getId());
        entity.setTitulo(formato.getTitulo());
        entity.setModalidad(formato.getModalidad());
        entity.setFechaPresentacion(formato.getFechaPresentacion());
        entity.setDirector(formato.getDirector());
        entity.setCodirector(formato.getCodirector());
        entity.setObjetivoGeneral(formato.getObjetivoGeneral());
        entity.setObjetivosEspecificos(formato.getObjetivosEspecificos());
        entity.setArchivoUrl(formato.getArchivoUrl());
        entity.setEstado(formato.getEstado());
        entity.setNumeroIntento(formato.getNumeroIntento());
        entity.setDocenteId(formato.getDocenteId());
        entity.setFechaCreacion(formato.getFechaCreacion());
        entity.setFechaActualizacion(formato.getFechaActualizacion());
        return entity;
    }

    private FormatoA toDomain(FormatoAEntity entity) {
        FormatoA formato = new FormatoA();
        formato.setId(entity.getId());
        formato.setTitulo(entity.getTitulo());
        formato.setModalidad(entity.getModalidad());
        formato.setFechaPresentacion(entity.getFechaPresentacion());
        formato.setDirector(entity.getDirector());
        formato.setCodirector(entity.getCodirector());
        formato.setObjetivoGeneral(entity.getObjetivoGeneral());
        formato.setObjetivosEspecificos(entity.getObjetivosEspecificos());
        formato.setArchivoUrl(entity.getArchivoUrl());
        formato.setEstado(entity.getEstado());
        formato.setNumeroIntento(entity.getNumeroIntento());
        formato.setDocenteId(entity.getDocenteId());
        formato.setFechaCreacion(entity.getFechaCreacion());
        formato.setFechaActualizacion(entity.getFechaActualizacion());
        return formato;
    }
}