package com.unicauca.proyectosofv1.repositorio;

import com.unicauca.proyectosofv1.plugins.core.TrabajoGradoDTO;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrabajoGradoDAO {

    private final Connection conn;

    public TrabajoGradoDAO(Connection conn) {
        this.conn = conn;
    }

    public List<TrabajoGradoDTO> listar() {
        String sql = """
            SELECT tg.id, tg.titulo, tg.fecha_formato_a,
                   u1.nombres || ' ' || u1.apellidos AS est1,
                   CASE WHEN u2.id IS NULL THEN NULL ELSE (u2.nombres || ' ' || u2.apellidos) END AS est2,
                   ud.nombres || ' ' || ud.apellidos AS director,
                   tg.modalidad, p.nombre AS programa
            FROM trabajo_grado tg
            JOIN estudiante e1 ON e1.id = tg.estudiante1_id
            LEFT JOIN estudiante e2 ON e2.id = tg.estudiante2_id
            JOIN usuario u1 ON u1.id = e1.id
            LEFT JOIN usuario u2 ON u2.id = e2.id
            JOIN docente d ON d.id = tg.director_id
            JOIN usuario ud ON ud.id = d.id
            JOIN programa p ON p.id = tg.programa_id
            ORDER BY tg.id
        """;
        List<TrabajoGradoDTO> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = String.valueOf(rs.getObject("id"));
                String titulo = rs.getString("titulo");

                String f = rs.getString("fecha_formato_a");
                java.time.LocalDate fechaA = (f == null || f.isBlank())
                        ? null
                        : java.time.LocalDate.parse(f);

                String e1 = rs.getString("est1");
                String e2 = rs.getString("est2");
                String director = rs.getString("director");
                String modalidad = rs.getString("modalidad");
                String programa = rs.getString("programa");

                out.add(new TrabajoGradoDTO(id, titulo, fechaA, e1, e2, director, modalidad, programa));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error consultando trabajos de grado", ex);
        }
        return out;
    }

    // ========== Helpers de normalización ==========
    private static String norm(String s) {
        if (s == null) {
            return "";
        }
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase().trim();
    }

// ========== 0) Crear Trabajo de Grado cuando trabajoId == 0 ==========
    /**
     * Crea un Trabajo de Grado y devuelve su ID (SQLite AUTOINCREMENT).
     */
    public long crearTrabajoGrado(String titulo, String modalidad,
            int directorId, Integer codirectorId,
            int estudiante1Id, Integer estudiante2Id,
            int programaId) throws Exception {
        // Si tu esquema usa valores específicos de modalidad en CHECK, mapea aquí:
        String modCanon = norm(modalidad).startsWith("practica") ? "Práctica Profesional" : "Investigación";

        String sql = """
        INSERT INTO trabajo_grado
          (titulo, modalidad, director_id, codirector_id, estudiante1_id, estudiante2_id, programa_id)
        VALUES (?,?,?,?,?,?,?)
    """;

        try (var ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, titulo);
            ps.setString(2, modCanon);
            ps.setInt(3, directorId);
            if (codirectorId == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, codirectorId);
            }
            ps.setInt(5, estudiante1Id);
            if (estudiante2Id == null) {
                ps.setNull(6, java.sql.Types.INTEGER);
            } else {
                ps.setInt(6, estudiante2Id);
            }
            ps.setInt(7, programaId);

            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new IllegalStateException("No se pudo crear Trabajo de Grado");
    }

    /**
     * Verifica existencia de un trabajo por id.
     */
    public boolean existeTrabajo(long trabajoId) throws Exception {
        try (var ps = conn.prepareStatement("SELECT 1 FROM trabajo_grado WHERE id = ?")) {
            ps.setLong(1, trabajoId);
            try (var rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

// ========== 1) Intentos ==========
    /**
     * Devuelve el mayor 'intento' registrado para el trabajo (0 si no hay
     * versiones).
     */
    public int maxIntentoPorTrabajo(long trabajoId) throws Exception {
        String sql = "SELECT COALESCE(MAX(intento),0) FROM formato_a_version WHERE trabajo_grado_id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, trabajoId);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * ¿La última versión fue RECHAZADA?
     */
    public boolean ultimaVersionRechazada(long trabajoId) throws Exception {
        String sql = """
        WITH ult AS (
          SELECT id
          FROM formato_a_version
          WHERE trabajo_grado_id = ?
          ORDER BY intento DESC
          LIMIT 1
        )
        SELECT EXISTS(
           SELECT 1 FROM evaluacion_formato_a e
           JOIN ult ON e.formato_a_version_id = ult.id
           WHERE LOWER(e.estado) = 'rechazado'
        )
    """;
        try (var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, trabajoId);
            try (var rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        }
    }

// ========== 2) Insertar versión ==========
    /**
     * Inserta una versión de Formato A y devuelve su ID.
     */
    public long insertarFormatoAVersion(long trabajoId, int intento,
            String objetivoGeneral, String objetivosEspecificos,
            String pdfPath, String cartaAceptacionPath,
            Integer codirectorId) throws Exception {
        String sql = """
        INSERT INTO formato_a_version
        (trabajo_grado_id, intento, objetivo_general, objetivos_especificos,
         pdf_path, carta_aceptacion_path, codirector_id)
        VALUES (?,?,?,?,?,?,?)
    """;
        try (var ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, trabajoId);
            ps.setInt(2, intento);
            ps.setString(3, objetivoGeneral);
            ps.setString(4, objetivosEspecificos);
            ps.setString(5, pdfPath);
            if (cartaAceptacionPath == null) {
                ps.setNull(6, java.sql.Types.VARCHAR);
            } else {
                ps.setString(6, cartaAceptacionPath);
            }
            if (codirectorId == null) {
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setInt(7, codirectorId);
            }

            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new IllegalStateException("No se obtuvo ID de la versión insertada");
    }

// ========== 3) Pendientes de evaluación ==========
    /**
     * Devuelve IDs de formato_a_version (últimas por TG) sin evaluación.
     */
    public java.util.List<Long> listarVersionesPendientesEvaluacion() throws Exception {
        String sql = """
        SELECT v.id
        FROM formato_a_version v
        JOIN (
          SELECT trabajo_grado_id, MAX(intento) AS max_intento
          FROM formato_a_version
          GROUP BY trabajo_grado_id
        ) m ON m.trabajo_grado_id = v.trabajo_grado_id AND m.max_intento = v.intento
        LEFT JOIN evaluacion_formato_a e
               ON e.formato_a_version_id = v.id
        WHERE e.id IS NULL
        ORDER BY v.trabajo_grado_id
    """;
        var out = new java.util.ArrayList<Long>();
        try (var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(rs.getLong(1));
            }
        }
        return out;
    }

}
