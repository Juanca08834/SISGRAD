package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.infraestructura.sqlite.FabricaConexionSQLite;
import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioProyecto;
import com.unicauca.proyectosofv1.repositorio.RepositorioFormatoA;
import com.unicauca.proyectosofv1.repositorio.sqlite.RepositorioProyectoSQLite;
import com.unicauca.proyectosofv1.repositorio.sqlite.RepositorioFormatoASQLite;
import com.unicauca.proyectosofv1.servicios.ServicioFormatoA;
import com.unicauca.proyectosofv1.servicios.ServicioHistorialFormatoA;
import com.unicauca.proyectosofv1.servicios.impl.ServicioFormatoAImpl;
import com.unicauca.proyectosofv1.servicios.impl.ServicioHistorialFormatoAImpl;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Tablero del Docente.
 * - Bienvenida.
 * - Subir Formato A (abre DialogoFormatoA via ControladorFormatoA).
 * - Historial Formato A (abre DialogoHistorialFormatoA).
 * - Botones placeholder para evaluar (como en tu version original).
 *
 * Dos constructores:
 *  - TableroDocente(Usuario) -> crea servicios y proyectoId automaticamente (crea si no existe)
 *  - TableroDocente(Usuario, long, ServicioFormatoA) -> si inyectas desde Principal
 */
public class TableroDocente extends javax.swing.JFrame {

    private final Usuario usuario;
    private final long proyectoId;
    private final ServicioFormatoA servicioFormato;

    // --- UI ---
    private javax.swing.JButton btnEvaluarAnteproyectos;
    private javax.swing.JButton btnEvaluarMonografias;
    private javax.swing.JButton btnSubirFormatoA;
    private javax.swing.JButton btnHistorialFormatoA;
    private javax.swing.JLabel lblBienvenida;

    /** Constructor con DI completa */
    public TableroDocente(Usuario usuario, long proyectoId, ServicioFormatoA servicioFormato) {
        this.usuario = usuario;
        this.proyectoId = proyectoId;
        this.servicioFormato = servicioFormato;
        initComponents();
        setLocationRelativeTo(null);
        lblBienvenida.setText("Bienvenido, " + usuario.getNombres() + " (Docente)");
    }

    /** Constructor de conveniencia: crea repos/servicios y obtiene/crea el proyecto del docente */
    public TableroDocente(Usuario usuario) {
        this.usuario = usuario;

        // repos y servicio (puedes moverlo a Principal si prefieres)
        RepositorioProyecto repoProyecto = new RepositorioProyectoSQLite();
        RepositorioFormatoA repoFormato = new RepositorioFormatoASQLite();
        this.servicioFormato = new ServicioFormatoAImpl(repoProyecto, repoFormato);

        // obtener o crear un proyecto para este docente
        this.proyectoId = obtenerOCrearProyectoId(usuario.getEmail(), repoProyecto);

        initComponents();
        setLocationRelativeTo(null);
        lblBienvenida.setText("Bienvenido, " + usuario.getNombres() + " (Docente)");
    }

    // Busca un proyecto del docente; si no existe, crea uno y retorna el id
    private long obtenerOCrearProyectoId(String docenteEmail, RepositorioProyecto repoProyecto) {
        Long existente = buscarProyectoIdPorDocente(docenteEmail);
        if (existente != null) return existente;
        return repoProyecto.crearProyecto(docenteEmail);
    }

    // Consulta directa a SQLite para no ampliar la interfaz del repositorio
    private Long buscarProyectoIdPorDocente(String docenteEmail) {
        String sql = "SELECT id FROM proyecto WHERE docente_email=? ORDER BY id DESC LIMIT 1";
        try (Connection c = FabricaConexionSQLite.obtenerConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, docenteEmail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        } catch (SQLException e) {
            System.err.println("No se pudo consultar proyecto por docente: " + e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        lblBienvenida = new javax.swing.JLabel();
        btnSubirFormatoA = new javax.swing.JButton();
        btnHistorialFormatoA = new javax.swing.JButton();
        btnEvaluarAnteproyectos = new javax.swing.JButton();
        btnEvaluarMonografias = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tablero - Docente");

        lblBienvenida.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));

        btnSubirFormatoA.setText("Subir Formato A");
        btnSubirFormatoA.addActionListener(e -> onSubirFormatoA());

        btnHistorialFormatoA.setText("Historial Formato A");
        btnHistorialFormatoA.addActionListener(e -> onHistorialFormatoA());

        btnEvaluarAnteproyectos.setText("Evaluar anteproyectos");
        btnEvaluarAnteproyectos.addActionListener(e -> onEvaluarAnteproyectos());

        btnEvaluarMonografias.setText("Evaluar monografias");
        btnEvaluarMonografias.addActionListener(e -> onEvaluarMonografias());

        var layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblBienvenida, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                        .addComponent(btnSubirFormatoA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnHistorialFormatoA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEvaluarAnteproyectos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEvaluarMonografias, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addComponent(lblBienvenida)
                    .addGap(18, 18, 18)
                    .addComponent(btnSubirFormatoA, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addComponent(btnHistorialFormatoA, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addComponent(btnEvaluarAnteproyectos, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addComponent(btnEvaluarMonografias, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }

    private void onSubirFormatoA() {
        if (this.servicioFormato == null) {
            JOptionPane.showMessageDialog(this, "ServicioFormatoA no disponible", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        new ControladorFormatoA(this, this.proyectoId, this.servicioFormato).mostrarDialogo();
    }

    private void onHistorialFormatoA() {
        // Crea el servicio de historial y muestra el dialogo
        RepositorioFormatoA repoFormato = new RepositorioFormatoASQLite();
        ServicioHistorialFormatoA srvHist = new ServicioHistorialFormatoAImpl(repoFormato);
        new DialogoHistorialFormatoA(this, this.proyectoId, srvHist).setVisible(true);
    }

    private void onEvaluarAnteproyectos() {
        JOptionPane.showMessageDialog(this, "Pendiente: listado de anteproyectos a evaluar");
    }

    private void onEvaluarMonografias() {
        JOptionPane.showMessageDialog(this, "Pendiente: listado de monografias a evaluar");
    }
}
