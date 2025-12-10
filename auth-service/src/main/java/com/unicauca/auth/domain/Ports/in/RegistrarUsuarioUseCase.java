package com.unicauca.auth.domain.ports.in;

import com.unicauca.auth.domain.model.Usuario;

/**
 * Puerto de entrada (Use Case) para registrar un nuevo usuario
 * Define QUÉ se puede hacer, no CÓMO se hace
 */
public interface RegistrarUsuarioUseCase {
    /**
     * Registra un nuevo usuario en el sistema
     * @param usuario Usuario a registrar
     * @return Usuario registrado con su ID asignado
     * @throws RuntimeException si el email ya existe o los datos son inválidos
     */
    Usuario registrar(Usuario usuario);
}