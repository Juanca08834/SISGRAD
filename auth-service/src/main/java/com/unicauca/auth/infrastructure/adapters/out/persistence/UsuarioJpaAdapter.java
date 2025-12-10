package com.unicauca.auth.infrastructure.adapters.out.persistence;

import com.unicauca.auth.domain.model.Usuario;
import com.unicauca.auth.domain.ports.out.UsuarioPersistencePort;
import com.unicauca.auth.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * Adaptador JPA que implementa el puerto de persistencia
 * ARQUITECTURA HEXAGONAL: Esta clase adapta JPA al dominio
 * Convierte entre Usuario (dominio) y UsuarioEntity (JPA)
 */
@Component
public class UsuarioJpaAdapter implements UsuarioPersistencePort {

    private final UsuarioRepository repository;

    public UsuarioJpaAdapter(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = toEntity(usuario);
        UsuarioEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email)
            .map(this::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id)
            .map(this::toDomain);
    }

    @Override
    public boolean existePorEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public void actualizar(Usuario usuario) {
        UsuarioEntity entity = toEntity(usuario);
        repository.save(entity);
    }

    // ==================== MÉTODOS DE MAPEO ====================
    
    /**
     * Convierte de Usuario (dominio) a UsuarioEntity (JPA)
     */
    private UsuarioEntity toEntity(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.getId());
        entity.setNombres(usuario.getNombres());
        entity.setApellidos(usuario.getApellidos());
        entity.setCelular(usuario.getCelular());
        entity.setPrograma(usuario.getPrograma());
        entity.setEmail(usuario.getEmail());
        entity.setPassword(usuario.getPassword());
        entity.setRol(usuario.getRol());
        entity.setActivo(usuario.isActivo());
        entity.setFechaCreacion(usuario.getFechaCreacion());
        entity.setUltimoAcceso(usuario.getUltimoAcceso());
        return entity;
    }

    /**
     * Convierte de UsuarioEntity (JPA) a Usuario (dominio)
     */
    private Usuario toDomain(UsuarioEntity entity) {
        Usuario usuario = new Usuario();
        usuario.setId(entity.getId());
        usuario.setNombres(entity.getNombres());
        usuario.setApellidos(entity.getApellidos());
        usuario.setCelular(entity.getCelular());
        usuario.setPrograma(entity.getPrograma());
        usuario.setEmail(entity.getEmail());
        usuario.setPassword(entity.getPassword());
        usuario.setRol(entity.getRol());
        usuario.setActivo(entity.isActivo());
        usuario.setFechaCreacion(entity.getFechaCreacion());
        usuario.setUltimoAcceso(entity.getUltimoAcceso());
        return usuario;
    }
}