package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.excepciones.SISGRADException;
import com.unicauca.proyectosofv1.infraestructura.archivos.ArchivoStorage;
import com.unicauca.proyectosofv1.modelo.EstadoFormatoA;
import com.unicauca.proyectosofv1.modelo.FormatoAVersion;
import com.unicauca.proyectosofv1.notificacion.Notificador;
import com.unicauca.proyectosofv1.notificacion.NotificadorLogger;
import com.unicauca.proyectosofv1.servicios.ServicioFormatoA;

import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioFormatoAImpl implements ServicioFormatoA {

    private final Connection conn;
    private final Notificador notificador;
    private final ArchivoStorage storage;

    /* =======================  Constructores  ======================= */
    public ServicioFormatoAImpl(Connection conn) {
        this(conn, new NotificadorLogger(), ArchivoStorage.porDefecto());
    }

    public ServicioFormatoAImpl(Connection conn, Notificador notificador) {
        this(conn, notificador, ArchivoStorage.porDefecto());
    }

    public ServicioFormatoAImpl(Connection conn, Notificador notificador, ArchivoStorage storage) {
        this.conn = conn;
        this.notificador = notificador;
        this.storage = storage;
    }

    /* =======================  Utilidades locales  ======================= */
    private static String norm(String s) {
        if (s == null) {
            return "";
        }
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase().trim();
    }

    private static boolean esPdf(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return false;
        }
        String name = new java.io.File(ruta).getName();
        return norm(name).endsWith(".pdf");
    }

    private Integer obtenerUltimaVersionId(int trabajoGradoId) throws SQLException {
        String sql = "SELECT id FROM formato_a_version WHERE trabajo_grado_id=? ORDER BY intento DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trabajoGradoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    private boolean tieneEvaluacion(int formatoAVersionId) throws SQLException {
        String sql = "SELECT 1 FROM evaluacion_formato_a WHERE formato_a_version_id=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, formatoAVersionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean esAprobada(int formatoAVersionId) throws SQLException {
        String sql = "SELECT 1 FROM evaluacion_formato_a WHERE formato_a_version_id=? AND LOWER(estado)='aprobado' LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, formatoAVersionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean esRechazada(int formatoAVersionId) throws SQLException {
        String sql = "SELECT 1 FROM evaluacion_formato_a WHERE formato_a_version_id=? AND LOWER(estado)='rechazado' LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, formatoAVersionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int obtenerTrabajoIdPorVersion(int formatoAVersionId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT trabajo_grado_id FROM formato_a_version WHERE id=?")) {
            ps.setInt(1, formatoAVersionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No existe formato_a_version id=" + formatoAVersionId);
    }

    private List<String> correosInvolucradosTG(int tgId) throws SQLException {
        ArrayList<String> emails = new ArrayList<>();
        String q
                = "SELECT u.email FROM trabajo_grado tg JOIN usuario u ON u.id=tg.director_id WHERE tg.id=? "
                + "UNION "
                + "SELECT u.email FROM trabajo_grado tg JOIN usuario u ON u.id=tg.codirector_id WHERE tg.id=? "
                + "UNION "
                + "SELECT u.email FROM trabajo_grado tg JOIN usuario u ON u.id=tg.estudiante1_id WHERE tg.id=? "
                + "UNION "
                + "SELECT u.email FROM trabajo_grado tg JOIN usuario u ON u.id=tg.estudiante2_id WHERE tg.id=? ";
        try (PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setInt(1, tgId);
            ps.setInt(2, tgId);
            ps.setInt(3, tgId);
            ps.setInt(4, tgId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String e = rs.getString(1);
                    if (e != null && !e.isBlank()) {
                        emails.add(e);
                    }
                }
            }
        }
        if (emails.isEmpty()) {
            emails.add("notificaciones@ejemplo"); // fallback
        }
        return emails;
    }

    private boolean hasTable(String name) throws SQLException {
        DatabaseMetaData md = conn.getMetaData();
        try (ResultSet rs = md.getTables(null, null, name, null)) {
            while (rs.next()) {
                String t = rs.getString("TABLE_NAME");
                if (t != null && t.equalsIgnoreCase(name)) {
                    return true;
                }
            }
            return false;
        }
    }

    private Integer getUsuarioIdPorEmail(String email) throws SQLException {
        String sql = "SELECT id FROM usuario WHERE email=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    /* =======================  Implementación ServicioFormatoA  ======================= */
    @Override
    public FormatoAVersion subirFormatoA(int trabajoGradoId, String titulo, String modalidad,
            int directorId, Integer codirectorId,
            int estudiante1Id, Integer estudiante2Id, int programaId,
            String objetivoGeneral, String objetivosEspecificos,
            String pdfPath, String cartaAceptacionPath) throws SISGRADException {
        boolean prevAuto = true;
        try {
            // ---- Validaciones de entrada ----
            if (titulo == null || titulo.isBlank()) {
                throw new SISGRADException("Título es obligatorio.");
            }
            if (objetivoGeneral == null || objetivoGeneral.isBlank()) {
                throw new SISGRADException("Objetivo general es obligatorio.");
            }
            if (!esPdf(pdfPath)) {
                throw new SISGRADException("Adjunte el Formato A en PDF válido.");
            }

            boolean esPractica = norm(modalidad).startsWith("practica");
            if (esPractica && !esPdf(cartaAceptacionPath)) {
                throw new SISGRADException("Para Práctica Profesional, adjunte la carta de aceptación en PDF.");
            }
            final String modalidadCanon = esPractica ? "Práctica Profesional" : "Investigación";

            // ---- Validaciones de FKs y reglas de identidad/rol ----
            asegurarUsuarioRol(directorId, "docente", "Director");
            if (codirectorId != null) {
                asegurarUsuarioRol(codirectorId, "docente", "Codirector");
                if (codirectorId.equals(directorId)) {
                    throw new SISGRADException("Director y Codirector no pueden ser la misma persona.");
                }
            }

            asegurarUsuarioRol(estudiante1Id, "estudiante", "Estudiante 1");
            if (estudiante2Id != null) {
                asegurarUsuarioRol(estudiante2Id, "estudiante", "Estudiante 2");
                if (estudiante2Id.equals(estudiante1Id)) {
                    throw new SISGRADException("Estudiante 1 y Estudiante 2 no pueden ser el mismo usuario.");
                }
            }

            asegurarProgramaExiste(programaId);

            // ---- Transacción ----
            prevAuto = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // ---- Crear/actualizar cabecera TG ----
            if (trabajoGradoId <= 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO trabajo_grado(titulo,fecha_formato_a,estudiante1_id,estudiante2_id,"
                        + "director_id,codirector_id,modalidad,programa_id) VALUES (?,?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, titulo);
                    ps.setString(2, java.time.LocalDate.now().toString());
                    ps.setInt(3, estudiante1Id);
                    if (estudiante2Id == null) {
                        ps.setNull(4, Types.INTEGER);
                    } else {
                        ps.setInt(4, estudiante2Id);
                    }
                    ps.setInt(5, directorId);
                    if (codirectorId == null) {
                        ps.setNull(6, Types.INTEGER);
                    } else {
                        ps.setInt(6, codirectorId);
                    }
                    ps.setString(7, modalidadCanon);
                    ps.setInt(8, programaId);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            trabajoGradoId = keys.getInt(1);
                        }
                    }
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE trabajo_grado SET titulo=?, modalidad=?, codirector_id=?, fecha_formato_a=? WHERE id=?")) {
                    ps.setString(1, titulo);
                    ps.setString(2, modalidadCanon);
                    if (codirectorId == null) {
                        ps.setNull(3, Types.INTEGER);
                    } else {
                        ps.setInt(3, codirectorId);
                    }
                    ps.setString(4, java.time.LocalDate.now().toString());
                    ps.setInt(5, trabajoGradoId);
                    ps.executeUpdate();
                }
            }

            // ---- Reglas de reintento ----
            Integer ultimaVersionId = obtenerUltimaVersionId(trabajoGradoId);
            if (ultimaVersionId != null && !tieneEvaluacion(ultimaVersionId)) {
                throw new SISGRADException("Ya existe una versión pendiente de evaluación. Espere la decisión antes de subir otra.");
            }
            if (ultimaVersionId != null && esAprobada(ultimaVersionId)) {
                throw new SISGRADException("El Formato A ya fue aceptado. No es posible subir nuevas versiones.");
            }

            int intento = 1;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COALESCE(MAX(intento),0) FROM formato_a_version WHERE trabajo_grado_id=?")) {
                ps.setInt(1, trabajoGradoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        intento = rs.getInt(1) + 1;
                    }
                }
            }
            if (intento > 3 && ultimaVersionId != null && esRechazada(ultimaVersionId)) {
                throw new SISGRADException("Proyecto rechazado definitivamente tras 3 intentos.");
            }

            // ---- Guardar archivos en disco con nombres determinísticos ----
            String subcarpeta = "TG_" + trabajoGradoId;
            String nombreBase = "tg" + trabajoGradoId + "_i" + intento;
            String pdfGuardado = storage.guardarPdf(subcarpeta, Path.of(pdfPath), nombreBase + "_formatoa");
            String cartaGuardada = null;
            if (esPractica && cartaAceptacionPath != null && !cartaAceptacionPath.isBlank()) {
                cartaGuardada = storage.guardarPdf(subcarpeta, Path.of(cartaAceptacionPath), nombreBase + "_carta");
            }

            // ---- Insertar versión ----
            int versionId = -1;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO formato_a_version(trabajo_grado_id,intento,objetivo_general,objetivos_especificos,"
                    + "pdf_path,carta_aceptacion_path,codirector_id) VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, trabajoGradoId);
                ps.setInt(2, intento);
                ps.setString(3, objetivoGeneral);
                ps.setString(4, objetivosEspecificos);
                ps.setString(5, pdfGuardado);
                if (cartaGuardada == null) {
                    ps.setNull(6, Types.VARCHAR);
                } else {
                    ps.setString(6, cartaGuardada);
                }
                if (codirectorId == null) {
                    ps.setNull(7, Types.INTEGER);
                } else {
                    ps.setInt(7, codirectorId);
                }
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        versionId = rsGetIntSafe(keys, 1);
                    }
                }
            }

            conn.commit();

            return new FormatoAVersion(
                    versionId,
                    trabajoGradoId,
                    intento,
                    objetivoGeneral,
                    objetivosEspecificos,
                    pdfGuardado,
                    cartaGuardada,
                    codirectorId
            );

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignore) {
            }
            throw new SISGRADException("Error al subir Formato A: " + e.getMessage(), e);
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ignore) {
            }
            throw new SISGRADException("No fue posible guardar archivos: " + e.getMessage(), e);
        } finally {
            try {
                conn.setAutoCommit(prevAuto);
            } catch (SQLException ignore) {
            }
        }
    }

    private int rsGetIntSafe(ResultSet rs, int idx) throws SQLException {
        int v = rs.getInt(idx);
        // getInt retorna 0 si es NULL; si quieres distinguir, podrías usar wasNull()
        return v;
    }

    @Override
    public void evaluarFormatoA(int formatoAVersionId, String estado, String observaciones, int evaluadorUsuarioId) throws SISGRADException {
        if (!"aprobado".equalsIgnoreCase(estado) && !"rechazado".equalsIgnoreCase(estado)) {
            throw new SISGRADException("Estado inválido: " + estado);
        }
        try {

            try (PreparedStatement chk = conn.prepareStatement(
                    "SELECT 1 FROM evaluacion_formato_a WHERE formato_a_version_id=? LIMIT 1")) {
                chk.setInt(1, formatoAVersionId);
                try (ResultSet r = chk.executeQuery()) {
                    if (r.next()) {
                        throw new SISGRADException("Esta versión ya fue evaluada.");
                    }
                }
            }

            // Inserta evaluación (única por versión)
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO evaluacion_formato_a(formato_a_version_id,estado,observaciones,evaluador_id) VALUES (?,?,?,?)")) {
                ps.setInt(1, formatoAVersionId);
                ps.setString(2, estado.toLowerCase());
                ps.setString(3, observaciones == null || observaciones.isBlank() ? "(sin observaciones)" : observaciones);
                ps.setInt(4, evaluadorUsuarioId);
                ps.executeUpdate();
            }

            // Notificar a involucrados del TG
            int tgId = obtenerTrabajoIdPorVersion(formatoAVersionId);
            List<String> destinatarios = correosInvolucradosTG(tgId);
            notificador.enviar(
                    "Evaluación Formato A",
                    "La versión " + formatoAVersionId + " fue " + estado + ".\nObservaciones: " + observaciones,
                    destinatarios
            );

        } catch (SQLException e) {
            throw new SISGRADException("Error al evaluar Formato A: " + e.getMessage(), e);
        }
    }

    @Override
    public EstadoFormatoA estadoActual(int trabajoGradoId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT fav.intento, efa.estado "
                + "FROM formato_a_version fav "
                + "LEFT JOIN evaluacion_formato_a efa ON efa.formato_a_version_id=fav.id "
                + "WHERE fav.trabajo_grado_id=? "
                + "ORDER BY fav.intento ASC")) {
            ps.setInt(1, trabajoGradoId);
            try (ResultSet rs = ps.executeQuery()) {
                int ultimoIntento = 0;
                String ultimoEstado = null;
                while (rs.next()) {
                    ultimoIntento = rs.getInt(1);
                    ultimoEstado = rs.getString(2);
                }
                if (ultimoIntento == 0) {
                    return EstadoFormatoA.EN_PRIMERA_EVALUACION;
                }
                if (ultimoEstado == null) {
                    return switch (ultimoIntento) {
                        case 1 ->
                            EstadoFormatoA.EN_PRIMERA_EVALUACION;
                        case 2 ->
                            EstadoFormatoA.EN_SEGUNDA_EVALUACION;
                        default ->
                            EstadoFormatoA.EN_TERCERA_EVALUACION;
                    };
                } else if ("aprobado".equalsIgnoreCase(ultimoEstado)) {
                    return EstadoFormatoA.ACEPTADO_FORMATO_A;
                } else {
                    if (ultimoIntento >= 3) {
                        return EstadoFormatoA.RECHAZADO_FORMATO_A;
                    }
                    return switch (ultimoIntento) {
                        case 1 ->
                            EstadoFormatoA.EN_SEGUNDA_EVALUACION;
                        case 2 ->
                            EstadoFormatoA.EN_TERCERA_EVALUACION;
                        default ->
                            EstadoFormatoA.RECHAZADO_FORMATO_A;
                    };
                }
            }
        } catch (SQLException e) {
            return EstadoFormatoA.EN_PRIMERA_EVALUACION;
        }
    }

    @Override
    public List<FormatoAVersion> listarPendientes() {
        List<FormatoAVersion> list = new ArrayList<>();
        // Solo la ÚLTIMA versión de cada TG que aún no tenga evaluación
        String sql
                = "SELECT v.* "
                + "FROM formato_a_version v "
                + "JOIN (SELECT trabajo_grado_id, MAX(intento) AS max_intento "
                + "      FROM formato_a_version GROUP BY trabajo_grado_id) m "
                + "  ON m.trabajo_grado_id = v.trabajo_grado_id AND m.max_intento = v.intento "
                + "LEFT JOIN evaluacion_formato_a e ON e.formato_a_version_id = v.id "
                + "WHERE e.id IS NULL "
                + "ORDER BY v.trabajo_grado_id";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new FormatoAVersion(
                        rs.getInt("id"),
                        rs.getInt("trabajo_grado_id"),
                        rs.getInt("intento"),
                        rs.getString("objetivo_general"),
                        rs.getString("objetivos_especificos"),
                        rs.getString("pdf_path"),
                        rs.getString("carta_aceptacion_path"),
                        (Integer) rs.getObject("codirector_id")
                ));
            }
        } catch (SQLException e) {
            // log si quieres
        }
        return list;
    }

    @Override
    public int contarIntentos(int trabajoGradoId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM formato_a_version WHERE trabajo_grado_id=?")) {
            ps.setInt(1, trabajoGradoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public Integer buscarTrabajoIdPorDocenteEmail(String email) {
        try {
            if (!hasTable("trabajo_grado")) {
                return null;
            }
            if (!hasTable("usuario")) {
                return null;
            }
            Integer uid = getUsuarioIdPorEmail(email);
            if (uid == null) {
                return null;
            }

            String sql = "SELECT id FROM trabajo_grado WHERE director_id=? OR codirector_id=? "
                    + "ORDER BY id DESC LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, uid);
                ps.setInt(2, uid);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : null;
                }
            }
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Integer buscarTrabajoIdPorEstudianteEmail(String email) {
        try {
            if (!hasTable("trabajo_grado")) {
                return null;
            }
            if (!hasTable("usuario")) {
                return null;
            }
            Integer uid = getUsuarioIdPorEmail(email);
            if (uid == null) {
                return null;
            }

            String sql = "SELECT id FROM trabajo_grado WHERE estudiante1_id=? OR estudiante2_id=? "
                    + "ORDER BY id DESC LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, uid);
                ps.setInt(2, uid);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : null;
                }
            }
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public List<String[]> historialSimple(int trabajoGradoId) {
        List<String[]> out = new ArrayList<>();
        String sql = "SELECT fav.intento, "
                + "       COALESCE(efa.estado,'pendiente') AS estado, "
                + "       COALESCE(efa.observaciones,'') AS obs, "
                + "       fav.pdf_path, COALESCE(fav.carta_aceptacion_path,'') AS carta "
                + "FROM formato_a_version fav "
                + "LEFT JOIN evaluacion_formato_a efa ON efa.formato_a_version_id=fav.id "
                + "WHERE fav.trabajo_grado_id=? "
                + "ORDER BY fav.intento ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trabajoGradoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new String[]{
                        String.valueOf(rs.getInt("intento")),
                        rs.getString("estado"),
                        rs.getString("obs"),
                        rs.getString("pdf_path"),
                        rs.getString("carta")
                    });
                }
            }
        } catch (SQLException ignore) {
        }
        return out;
    }

    @Override
    public DatosFormatoA cargarDatosParaReintento(int trabajoGradoId) {
        String titulo = null, modalidad = null;
        Integer director = null, codirector = null, est1 = null, est2 = null, programa = null;
        String objG = null, objE = null;

        // 1) Cabecera del TG
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT titulo, modalidad, director_id, codirector_id, estudiante1_id, estudiante2_id, programa_id "
                + "FROM trabajo_grado WHERE id=?")) {
            ps.setInt(1, trabajoGradoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    titulo = rs.getString("titulo");
                    modalidad = rs.getString("modalidad");
                    director = rs.getInt("director_id");
                    codirector = (Integer) rs.getObject("codirector_id");
                    est1 = rs.getInt("estudiante1_id");
                    est2 = (Integer) rs.getObject("estudiante2_id");
                    programa = rs.getInt("programa_id");
                }
            }
        } catch (SQLException ignore) {
        }

        // 2) Última versión (para prellenar objetivos)
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT objetivo_general, objetivos_especificos "
                + "FROM formato_a_version WHERE trabajo_grado_id=? "
                + "ORDER BY intento DESC LIMIT 1")) {
            ps.setInt(1, trabajoGradoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    objG = rs.getString(1);
                    objE = rs.getString(2);
                }
            }
        } catch (SQLException ignore) {
        }

        // Si falta lo esencial, no hay nada que prellenar
        if (titulo == null || modalidad == null || director == null || est1 == null || programa == null) {
            return null;
        }

        return new DatosFormatoA(
                titulo, modalidad, director, codirector, est1, est2, programa, objG, objE
        );
    }

    // --- Helpers de validación de FKs/roles (pegar dentro de ServicioFormatoAImpl) ---
    private void asegurarUsuarioRol(int id, String rolEsperado, String etiquetaCampo)
            throws SISGRADException, SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT rol FROM usuario WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SISGRADException(etiquetaCampo + " (id=" + id + ") no existe.");
                }
                String rol = rs.getString(1);
                if (rol == null || !rol.equalsIgnoreCase(rolEsperado)) {
                    throw new SISGRADException(etiquetaCampo + " (id=" + id + ") no es " + rolEsperado + " (es " + rol + ").");
                }
            }
        }
    }

    private void asegurarProgramaExiste(int id) throws SISGRADException, SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM programa WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SISGRADException("Programa (id=" + id + ") no existe.");
                }
            }
        }
    }

}
