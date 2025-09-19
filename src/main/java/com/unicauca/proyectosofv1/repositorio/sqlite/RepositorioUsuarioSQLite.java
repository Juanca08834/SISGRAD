package com.unicauca.proyectosofv1.repositorio.sqlite;

import com.unicauca.proyectosofv1.excepciones.SISGRADException;
import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioUsuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepositorioUsuarioSQLite implements RepositorioUsuario {

    private final Connection conn;

    public RepositorioUsuarioSQLite(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT email,nombres,apellidos,celular,programa,rol,password_hash,creado_en FROM usuarios WHERE email=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getString("email"),
                            rs.getString("nombres"),
                            rs.getString("apellidos"),
                            rs.getString("celular"),
                            rs.getString("programa"),
                            rs.getString("rol"),
                            rs.getString("password_hash"),
                            LocalDateTime.parse(rs.getString("creado_en"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nombres=?, apellidos=?, celular=?, programa=?, rol=?, password_hash=? WHERE email=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNombres());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getCelular());
            ps.setString(4, u.getPrograma());
            ps.setString(5, u.getRol());
            ps.setString(6, u.getPasswordHash());
            ps.setString(7, u.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando usuario", e);
        }
    }

    @Override
    public void guardar(Usuario u) {
        try ( var ps = conn.prepareStatement(
                "INSERT INTO usuarios (email,nombres,apellidos,celular,programa,rol,password_hash,creado_en) "
                + "VALUES (?,?,?,?,?,?,?,?)")) {
            ps.setString(1, u.getEmail());
            ps.setString(2, u.getNombres());
            ps.setString(3, u.getApellidos());
            if (u.getCelular() == null || u.getCelular().isBlank()) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, u.getCelular());
            }
            ps.setString(5, u.getPrograma());
            ps.setString(6, u.getRol());
            ps.setString(7, u.getPasswordHash());
            ps.setString(8, u.getCreadoEn().toString());
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            // LOG útil en consola
            e.printStackTrace();

            String msg = String.valueOf(e.getMessage());
            if (msg.contains("UNIQUE constraint failed") && msg.contains("usuarios.email")) {
                try {
                    throw new com.unicauca.proyectosofv1.excepciones.SISGRADException(
                            "El correo institucional ya está registrado."
                    );
                } catch (SISGRADException ex) {
                    Logger.getLogger(RepositorioUsuarioSQLite.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (msg.contains("CHECK constraint failed")) {
                try {
                    throw new com.unicauca.proyectosofv1.excepciones.SISGRADException(
                            "Datos inválidos según restricciones de la BD (verifica Rol y Programa)."
                    );
                } catch (SISGRADException ex) {
                    Logger.getLogger(RepositorioUsuarioSQLite.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (msg.contains("NOT NULL constraint failed")) {
                try {
                    throw new com.unicauca.proyectosofv1.excepciones.SISGRADException(
                            "Faltan campos obligatorios."
                    );
                } catch (SISGRADException ex) {
                    Logger.getLogger(RepositorioUsuarioSQLite.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                throw new com.unicauca.proyectosofv1.excepciones.SISGRADException(
                        "Error guardando usuario: " + msg, e
                );
            } catch (SISGRADException ex) {
                Logger.getLogger(RepositorioUsuarioSQLite.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
