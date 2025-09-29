package com.unicauca.proyectosofv1;

import com.unicauca.proyectosofv1.excepciones.SISGRADException;
import com.unicauca.proyectosofv1.infraestructura.sqlite.DBInit;
import com.unicauca.proyectosofv1.infraestructura.sqlite.FabricaConexionSQLite;
import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioUsuario;
import com.unicauca.proyectosofv1.repositorio.sqlite.RepositorioUsuarioSQLite;
import com.unicauca.proyectosofv1.seguridad.EncriptadorContrasenia;
import com.unicauca.proyectosofv1.servicios.ServicioAuth;
import com.unicauca.proyectosofv1.servicios.ServicioRegistro;
import com.unicauca.proyectosofv1.servicios.impl.ServicioAuthImpl;
import com.unicauca.proyectosofv1.servicios.impl.ServicioRegistroImpl;
import com.unicauca.proyectosofv1.ui.*;

import com.unicauca.proyectosofv1.servicios.ServicioFormatoA;
import com.unicauca.proyectosofv1.servicios.impl.ServicioFormatoAImpl;

import javax.swing.*;
import java.sql.*;

public class Principal {

    // Servicios estáticos para reabrir login sin cerrar la app
    private static ServicioAuth servicioAuth;
    private static ServicioRegistro servicioRegistro;
    private static ServicioFormatoA servicioFormatoA;

    public static void main(String[] args) {
        // 1) Look & Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // 2) Conexión viva
        final Connection conn;
        try {
            conn = FabricaConexionSQLite.obtenerConexion();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "No fue posible abrir la base de datos:\n" + e.getMessage(),
                    "SISGRAD - Error crítico",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Cierre elegante
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (Exception ignored) {}
        }));

        try {
            // 3) Esquema/semillas
            DBInit.init(conn);

            // 4) Migración de tabla legacy `usuarios` → modelo normalizado
            migrateUsuariosCompat(conn);

            // 5) Repos y servicios (asignados a campos estáticos)
            RepositorioUsuario repo = new RepositorioUsuarioSQLite(conn);
            EncriptadorContrasenia encriptador = new EncriptadorContrasenia();
            servicioRegistro = new ServicioRegistroImpl(repo, encriptador);
            servicioAuth     = new ServicioAuthImpl(repo, encriptador);
            servicioFormatoA = new ServicioFormatoAImpl(conn);

            // 6) UI: abrir login
            SwingUtilities.invokeLater(() -> abrirLogin(servicioAuth, servicioRegistro, servicioFormatoA));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Error al iniciar la aplicación:\n" + e.getMessage(),
                    "SISGRAD",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Reabrir login desde cualquier tablero (Cerrar sesión)
    public static void volverALogin() {
        SwingUtilities.invokeLater(() -> abrirLogin(servicioAuth, servicioRegistro, servicioFormatoA));
    }

    private static void abrirLogin(ServicioAuth servicioAuth,
                                   ServicioRegistro servicioRegistro,
                                   ServicioFormatoA servicioFormatoA) {
        final VentanaLogin[] loginRef = new VentanaLogin[1];

        AccionesLogin accionesLogin = new AccionesLogin() {
            @Override
            public void alIniciarSesion(String correo, char[] contrasenia) {
                try {
                    Usuario u = servicioAuth.loginYObtener(correo, new String(contrasenia));
                    if (u == null) {
                        JOptionPane.showMessageDialog(loginRef[0],
                                "Correo o contraseña incorrectos.",
                                "Error de inicio de sesión",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if ("docente".equalsIgnoreCase(u.getRol())) {
                        new TableroDocente(u, servicioFormatoA).setVisible(true);
                    } else if ("estudiante".equalsIgnoreCase(u.getRol())) {
                        new TableroEstudiante(u, servicioFormatoA).setVisible(true);
                    } else if ("coordinador".equalsIgnoreCase(u.getRol())) {
                        new TableroCoordinador(servicioFormatoA).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(loginRef[0],
                                "Rol no soportado por la UI: " + u.getRol(),
                                "Información",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    if (loginRef[0] != null) loginRef[0].dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(loginRef[0],
                            "Error iniciando sesión:\n" + ex.getMessage(),
                            "SISGRAD",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    if (contrasenia != null) java.util.Arrays.fill(contrasenia, '\0');
                }
            }

            @Override
            public void alSolicitarRegistro() {
                final DialogoRegistro[] dlgRef = new DialogoRegistro[1];

                AccionesRegistro acciones = new AccionesRegistro() {
                    @Override
                    public void alEnviar(DatosRegistro datos) {
                        try {
                            servicioRegistro.registrar(
                                    datos.nombres(), datos.apellidos(), datos.celular(),
                                    datos.programa(), datos.rol(), datos.email(), datos.password()
                            );
                            JOptionPane.showMessageDialog(dlgRef[0],
                                    "Registro exitoso. Ya puedes iniciar sesión.",
                                    "Registro",
                                    JOptionPane.INFORMATION_MESSAGE);
                            dlgRef[0].dispose();
                        } catch (SISGRADException ex) {
                            JOptionPane.showMessageDialog(dlgRef[0],
                                    ex.getMessage(), "Error de registro",
                                    JOptionPane.ERROR_MESSAGE);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(dlgRef[0],
                                    "Error inesperado en registro:\n" + ex.getMessage(),
                                    "Registro",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    @Override public void alCancelar() { dlgRef[0].dispose(); }
                };

                dlgRef[0] = new DialogoRegistro(loginRef[0], acciones);
                dlgRef[0].setLocationRelativeTo(loginRef[0]);
                dlgRef[0].setVisible(true);
            }

            @Override
            public void alOlvideContrasenia() {
                final DialogoRecuperarContrasenia[] dlgRef = new DialogoRecuperarContrasenia[1];

                AccionesRecuperacion acciones = new AccionesRecuperacion() {
                    @Override
                    public void alEnviarSolicitud(DatosRecuperacion datos) {
                        JOptionPane.showMessageDialog(dlgRef[0],
                                "Si el correo existe, se enviarán instrucciones (simulado).",
                                "Recuperación",
                                JOptionPane.INFORMATION_MESSAGE);
                        dlgRef[0].dispose();
                    }
                    @Override public void alCancelar() { dlgRef[0].dispose(); }
                };

                dlgRef[0] = new DialogoRecuperarContrasenia(loginRef[0], acciones);
                dlgRef[0].setLocationRelativeTo(loginRef[0]);
                dlgRef[0].setVisible(true);
            }
        };

        loginRef[0] = new VentanaLogin(accionesLogin);
        loginRef[0].setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        loginRef[0].setLocationRelativeTo(null);
        loginRef[0].setVisible(true);
    }

    /* =======================  Utilidades de migración  ======================= */

    private static boolean tableExists(Connection c, String name) throws SQLException {
        try (ResultSet rs = c.getMetaData().getTables(null, null, name, null)) {
            while (rs.next()) {
                String t = rs.getString("TABLE_NAME");
                if (t != null && t.equalsIgnoreCase(name)) return true;
            }
            return false;
        }
    }

    private static boolean columnExists(Connection c, String table, String col) throws SQLException {
        try (ResultSet rs = c.getMetaData().getColumns(null, null, table, col)) {
            return rs.next();
        }
    }

    /**
     * Migra datos desde la tabla legacy `usuarios` (si existe) al esquema
     * normalizado (`usuario`, `programa`, `estudiante`, `docente`).
     * Es idempotente.
     */
    private static void migrateUsuariosCompat(Connection conn) {
        try {
            if (!tableExists(conn, "usuarios")) return; // no hay legacy
            if (!tableExists(conn, "usuario"))  return; // esquema aún no listo

            // 1) Asegurar programas
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(
                    "INSERT OR IGNORE INTO programa(nombre) " +
                    "SELECT DISTINCT TRIM(programa) FROM usuarios " +
                    "WHERE programa IS NOT NULL AND TRIM(programa) <> ''"
                );
            }

            // 2) Insertar en usuario según columnas disponibles
            boolean hasCel = columnExists(conn, "usuario", "celular");
            boolean hasCre = columnExists(conn, "usuario", "creado_en");

            StringBuilder cols = new StringBuilder("nombres, apellidos, email, password_hash, rol");
            StringBuilder sel  = new StringBuilder(
                "SELECT u.nombres, u.apellidos, u.email, u.password_hash, " +
                "LOWER(CASE WHEN LOWER(u.rol)='coordinacion' THEN 'coordinador' ELSE u.rol END)"
            );
            if (hasCel) { cols.append(", celular");   sel.append(", COALESCE(u.celular,'')"); }
            if (hasCre) { cols.append(", creado_en"); sel.append(", datetime('now','localtime')"); }

            String sqlInsUsuario =
                "INSERT INTO usuario(" + cols + ") " + sel +
                " FROM usuarios u " +
                "WHERE NOT EXISTS (SELECT 1 FROM usuario n WHERE n.email = u.email)";

            try (Statement st = conn.createStatement()) {
                st.executeUpdate(sqlInsUsuario);
            }

            // 3) Vincular estudiante/docente
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(
                    "INSERT OR IGNORE INTO estudiante(id, programa_id) " +
                    "SELECT n.id, p.id " +
                    "FROM usuario n " +
                    "JOIN usuarios u ON u.email = n.email AND LOWER(u.rol)='estudiante' " +
                    "JOIN programa p ON p.nombre = u.programa"
                );
                st.executeUpdate(
                    "INSERT OR IGNORE INTO docente(id, programa_id) " +
                    "SELECT n.id, p.id " +
                    "FROM usuario n " +
                    "JOIN usuarios u ON u.email = n.email AND LOWER(u.rol)='docente' " +
                    "JOIN programa p ON p.nombre = u.programa"
                );
            }

            // 4) Vista de compatibilidad (opcional)
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(
                    "CREATE VIEW IF NOT EXISTS usuarios_view AS " +
                    "SELECT n.email, n.nombres, n.apellidos, n.celular, " +
                    "       COALESCE(pe.nombre, pd.nombre) AS programa, n.rol, n.password_hash " +
                    "FROM usuario n " +
                    "LEFT JOIN estudiante e ON e.id=n.id " +
                    "LEFT JOIN docente d   ON d.id=n.id " +
                    "LEFT JOIN programa pe ON pe.id=e.programa_id " +
                    "LEFT JOIN programa pd ON pd.id=d.programa_id"
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error migrando usuarios legacy", e);
        }
    }
}
