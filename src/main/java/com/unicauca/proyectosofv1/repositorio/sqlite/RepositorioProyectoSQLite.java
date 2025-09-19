// RepositorioProyectoSQLite.java
package com.unicauca.proyectosofv1.repositorio.sqlite;

import com.unicauca.proyectosofv1.infraestructura.sqlite.FabricaConexionSQLite;
import com.unicauca.proyectosofv1.modelo.*;
import com.unicauca.proyectosofv1.repositorio.RepositorioProyecto;

import java.sql.*;
import java.util.*;

public class RepositorioProyectoSQLite implements RepositorioProyecto {
    @Override
    public long crearProyecto(String docenteEmail) {
        String sql = "INSERT INTO proyecto(docente_email, estado, intentos) VALUES(?,?,?)";
        try (Connection c = FabricaConexionSQLite.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, docenteEmail);
            ps.setString(2, EstadoProyecto.PRIMERA_EVAL_A.name());
            ps.setInt(3, 1);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            throw new SQLException("No se obtuvo id");
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public ProyectoGrado buscarPorId(long id) {
        String sql = "SELECT docente_email, estado, intentos FROM proyecto WHERE id = ?";
        try (Connection c = FabricaConexionSQLite.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                ProyectoGrado p = new ProyectoGrado(id, rs.getString("docente_email"));
                // estado e intentos desde DB
                // setters indirectos via reflejo simple:
                // como los campos son privados, puedes exponer setEstado/setIntentos o reconstruir via CTOR
                // para mantener tu estilo, agrega setters controlados:
                return hydrate(p, EstadoProyecto.valueOf(rs.getString("estado")), rs.getInt("intentos"));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private ProyectoGrado hydrate(ProyectoGrado p, EstadoProyecto e, int intentos) {
        try {
            var f1 = ProyectoGrado.class.getDeclaredField("estado");
            var f2 = ProyectoGrado.class.getDeclaredField("intentosFormatoA");
            f1.setAccessible(true); f2.setAccessible(true);
            f1.set(p, e); f2.set(p, intentos);
            return p;
        } catch (Exception ex) { throw new RuntimeException(ex); }
    }

    @Override
    public void actualizarProyecto(ProyectoGrado p) {
        String sql = "UPDATE proyecto SET estado=?, intentos=? WHERE id=?";
        try (Connection c = FabricaConexionSQLite.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getEstado().name());
            ps.setInt(2, p.getIntentosFormatoA());
            ps.setLong(3, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<ProyectoGrado> listarTodos() {
        String sql = "SELECT id FROM proyecto";
        List<ProyectoGrado> out = new ArrayList<>();
        try (Connection c = FabricaConexionSQLite.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(buscarPorId(rs.getLong("id")));
            return out;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<ProyectoGrado> listarPorEstado(EstadoProyecto e) {
        String sql = "SELECT id FROM proyecto WHERE estado = ?";
        List<ProyectoGrado> out = new ArrayList<>();
        try (Connection c = FabricaConexionSQLite.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(buscarPorId(rs.getLong("id")));
            }
            return out;
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
}
