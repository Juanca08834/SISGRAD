package com.unicauca.proyectosofv1.repositorio.sqlite;

import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioUsuario;

import java.sql.*;
import java.time.LocalDateTime;

public class RepositorioUsuarioSQLite implements RepositorioUsuario {
    private final Connection conn;

    public RepositorioUsuarioSQLite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        final String sql =
            "SELECT u.id, u.nombres, u.apellidos, u.email, u.password_hash, u.rol, u.celular, u.creado_en, " +
            "       COALESCE(pe.nombre, pd.nombre) AS programa " +
            "FROM usuario u " +
            "LEFT JOIN estudiante e ON e.id = u.id " +
            "LEFT JOIN docente d   ON d.id = u.id " +
            "LEFT JOIN programa pe ON pe.id = e.programa_id " +
            "LEFT JOIN programa pd ON pd.id = d.programa_id " +
            "WHERE u.email = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String programa = rs.getString("programa");
                if (programa == null) programa = ""; // coordinador puede no tener programa
                String creado = rs.getString("creado_en");
                LocalDateTime ts = LocalDateTime.now();
                if (creado != null && !creado.isBlank()) {
                    try { ts = LocalDateTime.parse(creado.replace(' ', 'T')); } catch (Exception ignore) {}
                }
                return new Usuario(
                    rs.getString("email"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("celular") == null ? "" : rs.getString("celular"),
                    programa,
                    rs.getString("rol"),
                    rs.getString("password_hash"),
                    ts
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando usuario por email", e);
        }
    }

    @Override
    public void guardar(Usuario u) {
        // Inserta en `usuario` y en `estudiante`/`docente` con su `programa_id`
        final String insUsuario =
            "INSERT INTO usuario(nombres, apellidos, email, password_hash, rol, celular, creado_en) " +
            "VALUES (?,?,?,?,?,?, datetime('now','localtime'))";
        final String selPrograma = "SELECT id FROM programa WHERE nombre = ?";
        final String insPrograma = "INSERT INTO programa(nombre) VALUES (?)";
        final String insEst = "INSERT OR IGNORE INTO estudiante(id, programa_id) VALUES (?,?)";
        final String insDoc = "INSERT OR IGNORE INTO docente(id, programa_id) VALUES (?,?)";

        try {
            conn.setAutoCommit(false);

            // 1) usuario
            int nuevoId;
            try (PreparedStatement ps = conn.prepareStatement(insUsuario, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, u.getNombres());
                ps.setString(2, u.getApellidos());
                ps.setString(3, u.getEmail());
                ps.setString(4, u.getPasswordHash());
                ps.setString(5, u.getRol().toLowerCase());
                ps.setString(6, u.getCelular() == null ? "" : u.getCelular());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No se obtuvo ID de usuario");
                    nuevoId = keys.getInt(1);
                }
            }

            // 2) programa -> id
            Integer programaId = null;
            if (u.getPrograma() != null && !u.getPrograma().isBlank()) {
                try (PreparedStatement ps = conn.prepareStatement(selPrograma)) {
                    ps.setString(1, u.getPrograma());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) programaId = rs.getInt(1);
                    }
                }
                if (programaId == null) {
                    try (PreparedStatement ps = conn.prepareStatement(insPrograma, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, u.getPrograma());
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) programaId = keys.getInt(1);
                        }
                    }
                }
            }

            // 3) rol específico
            if ("estudiante".equalsIgnoreCase(u.getRol()) && programaId != null) {
                try (PreparedStatement ps = conn.prepareStatement(insEst)) {
                    ps.setInt(1, nuevoId);
                    ps.setInt(2, programaId);
                    ps.executeUpdate();
                }
            } else if ("docente".equalsIgnoreCase(u.getRol()) && programaId != null) {
                try (PreparedStatement ps = conn.prepareStatement(insDoc)) {
                    ps.setInt(1, nuevoId);
                    ps.setInt(2, programaId);
                    ps.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            String msg = (e.getMessage() == null ? "" : e.getMessage().toLowerCase());
            if (msg.contains("unique") || msg.contains("constraint") || msg.contains("duplicate")) {
                throw new RuntimeException("Ya existe un usuario con ese email.");
            }
            throw new RuntimeException("Error guardando usuario", e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
        }
    }

    @Override
    public void actualizar(Usuario usuario) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
