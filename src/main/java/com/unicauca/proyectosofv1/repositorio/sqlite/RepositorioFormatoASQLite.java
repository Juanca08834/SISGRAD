// RepositorioFormatoASQLite.java
package com.unicauca.proyectosofv1.repositorio.sqlite;

import com.unicauca.proyectosofv1.infraestructura.sqlite.FabricaConexionSQLite;
import com.unicauca.proyectosofv1.modelo.*;
import com.unicauca.proyectosofv1.repositorio.RepositorioFormatoA;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class RepositorioFormatoASQLite implements RepositorioFormatoA {
    @Override
    public long guardar(FormatoA f) {
        String sql = "INSERT INTO formato_a(proyecto_id, titulo, modalidad, fecha, director, codirector," +
                     " objetivo_general, objetivos_especificos, pdf_path, carta_path) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = FabricaConexionSQLite.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, f.proyectoId);
            ps.setString(2, f.titulo);
            ps.setString(3, f.modalidad.name());
            ps.setString(4, f.fecha.toString());
            ps.setString(5, f.director);
            ps.setString(6, f.codirector);
            ps.setString(7, f.objetivoGeneral);
            ps.setString(8, String.join("|", f.objetivosEspecificos));
            ps.setString(9, f.pdfPath);
            ps.setString(10, f.cartaAceptacionPath);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            throw new SQLException("No se obtuvo id");
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public FormatoA buscarUltimoPorProyecto(long proyectoId) {
        String sql = "SELECT * FROM formato_a WHERE proyecto_id=? ORDER BY id DESC LIMIT 1";
        try (Connection c = FabricaConexionSQLite.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<FormatoA> listarPorProyecto(long proyectoId) {
        String sql = "SELECT * FROM formato_a WHERE proyecto_id=? ORDER BY id DESC";
        List<FormatoA> out = new ArrayList<>();
        try (Connection c = FabricaConexionSQLite.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, proyectoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private FormatoA map(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        long pid = rs.getLong("proyecto_id");
        String titulo = rs.getString("titulo");
        Modalidad mod = Modalidad.valueOf(rs.getString("modalidad"));
        LocalDate fecha = LocalDate.parse(rs.getString("fecha"));
        String director = rs.getString("director");
        String codirector = rs.getString("codirector");
        String og = rs.getString("objetivo_general");
        List<String> oes = Arrays.asList(rs.getString("objetivos_especificos").split("\\|"));
        String pdf = rs.getString("pdf_path");
        String carta = rs.getString("carta_path");
        return new FormatoA(id, pid, titulo, mod, fecha, director, codirector, og, oes, pdf, carta);
    }
}
