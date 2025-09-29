package com.unicauca.proyectosofv1.infraestructura.sqlite;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class DBInit {

    public static void init(Connection conn) throws Exception {
        // Asegura FK
        try (Statement s = conn.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON;");
        }
        // Ejecuta scripts desde resources
        ejecutarScript(conn, "/db/schema.sql");
        ejecutarScript(conn, "/db/seed.sql");
    }

    private static void ejecutarScript(Connection conn, String resourcePath) throws Exception {
        // Carga desde el classpath (src/main/resources termina empaquetado dentro del jar)
        try (InputStream in = DBInit.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                // No hay recurso; salir silenciosamente
                return;
            }

            boolean prevAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                 Statement st = conn.createStatement()) {

                StringBuilder sb = new StringBuilder();
                String line;
                boolean inBlockComment = false;

                while ((line = br.readLine()) != null) {
                    String l = line;

                    // Manejo de comentarios de bloque /* ... */
                    if (inBlockComment) {
                        int end = l.indexOf("*/");
                        if (end >= 0) {
                            l = l.substring(end + 2); // resto de la línea tras cerrar el comentario
                            inBlockComment = false;
                        } else {
                            continue; // seguimos dentro del comentario
                        }
                    }
                    int start = l.indexOf("/*");
                    if (start >= 0) {
                        int end = l.indexOf("*/", start + 2);
                        if (end >= 0) {
                            // comentario de bloque en una sola línea: recortamos la parte comentada
                            l = l.substring(0, start) + l.substring(end + 2);
                        } else {
                            // inicia comentario de bloque y continúa en líneas siguientes
                            inBlockComment = true;
                            l = l.substring(0, start);
                        }
                    }

                    // Comentario de línea "-- ..."
                    l = l.replaceFirst("--.*$", "");

                    // Limpieza
                    l = l.trim();
                    if (l.isEmpty()) continue;

                    // Construimos la sentencia
                    sb.append(l).append('\n');

                    // ¿Termina en ';'? -> ejecutar
                    if (l.endsWith(";")) {
                        String sql = sb.toString().trim();
                        if (!sql.isEmpty()) {
                            st.execute(sql);
                        }
                        sb.setLength(0);
                    }
                }

                // Por si quedó algo sin ';' final
                String rem = sb.toString().trim();
                if (!rem.isEmpty()) {
                    st.execute(rem);
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(prevAutoCommit);
            }
        }
    }
}
