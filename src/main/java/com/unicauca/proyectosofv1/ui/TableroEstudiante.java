package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.Principal;
import com.unicauca.proyectosofv1.modelo.EstadoFormatoA;
import com.unicauca.proyectosofv1.modelo.FormatoAVersion;
import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.servicios.ServicioFormatoA;

import javax.swing.*;
import java.awt.*;

public class TableroEstudiante extends JFrame {

    private final Usuario usuario;
    private final ServicioFormatoA servicioFormatoA;

    private long trabajoIdActual = 0L; // 0 => aún no existe

    private JLabel lblBienvenida;
    private JLabel lblEstado;
    private JButton btnVerEstado;
    private JButton btnIniciarNuevo;
    private JButton btnVerHistorial;
    private JButton btnCerrarSesion;

    private JButton btnAbrirUltimoPdf = new JButton("Abrir último PDF");

    public TableroEstudiante(Usuario estudiante, ServicioFormatoA servicioFormatoA) {
        super("Estudiante - Estado Formato A");
        this.usuario = estudiante;
        this.servicioFormatoA = servicioFormatoA;

        initComponents();

        // Intento de autodetección del TG por email (si ya existe)
        try {
            Integer tg = servicioFormatoA.buscarTrabajoIdPorEstudianteEmail(usuario.getEmail());
            if (tg != null) {
                this.trabajoIdActual = tg;
            }
        } catch (Exception ignore) {
        }

        // Refrescar automáticamente cuando la ventana recobra foco
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                refrescarEstado();
            }
        });

        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        refrescarEstado();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        lblBienvenida = new JLabel();
        lblEstado = new JLabel("Estado: —   |   Intentos: 0/3");
        btnVerEstado = new JButton();
        btnIniciarNuevo = new JButton();
        btnVerHistorial = new JButton("Ver historial");
        btnCerrarSesion = new JButton("Cerrar sesión");

        setTitle("Tablero - Estudiante");

        lblBienvenida.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblBienvenida.setText("Hola, " + usuario.getNombres());

        btnVerEstado.setText("Ver estado de mi Trabajo de Grado");
        btnVerEstado.addActionListener(e -> onVerEstado());

        btnIniciarNuevo.setText("Iniciar / Reintentar Formato A");
        btnIniciarNuevo.addActionListener(e -> onIniciarNuevo());

        btnAbrirUltimoPdf = new javax.swing.JButton("Abrir último PDF");
        btnAbrirUltimoPdf.addActionListener(e -> abrirUltimoPdf());

        btnVerHistorial.addActionListener(e -> abrirHistorial());

        btnCerrarSesion.addActionListener(e -> {
            dispose();
            Principal.volverALogin();
        });

        var layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(lblBienvenida, GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                                        .addComponent(lblEstado, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnVerEstado, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnIniciarNuevo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnAbrirUltimoPdf, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnVerHistorial, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnCerrarSesion, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(lblBienvenida)
                                .addGap(8, 8, 8)
                                .addComponent(lblEstado)
                                .addGap(12, 12, 12)
                                .addComponent(btnVerEstado, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(btnIniciarNuevo, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnAbrirUltimoPdf, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addGap(12, 12, 12)
                                .addComponent(btnVerHistorial, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(btnCerrarSesion)
                                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }

    /**
     * Muestra el estado actual en un diálogo y refresca el label
     */
    private void onVerEstado() {
        refrescarEstado();
        JOptionPane.showMessageDialog(this, lblEstado.getText(),
                "Estado actual", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Abre el diálogo para subir/reintentar Formato A; al éxito, actualiza TG y
     * estado
     */
    private void onIniciarNuevo() {
        new DialogoSubirFormatoA(
                this,
                servicioFormatoA,
                tgId -> {
                    this.trabajoIdActual = tgId;
                    refrescarEstado();
                },
                (trabajoIdActual > 0 ? (int) trabajoIdActual : null) // si ya existe TG, pre-llenar y bloquear lo fijo
        ).setVisible(true);
    }

    /**
     * Actualiza label y habilita/inhabilita "Iniciar / Reintentar" según
     * estado/pendientes/intentos
     */
    private void refrescarEstado() {
        try {
            if (trabajoIdActual <= 0) {
                // detección tardía por si se creó desde otro rol/sesión
                Integer tg = servicioFormatoA.buscarTrabajoIdPorEstudianteEmail(usuario.getEmail());
                if (tg != null) {
                    this.trabajoIdActual = tg;
                }
            }

            if (trabajoIdActual <= 0) {
                lblEstado.setText("Estado: (sin proyecto)   |   Intentos: 0/3");
                btnIniciarNuevo.setEnabled(true);
                return;
            }

            var est = servicioFormatoA.estadoActual((int) trabajoIdActual);
            int intentos = servicioFormatoA.contarIntentos((int) trabajoIdActual);

            // ¿hay una versión de este TG pendiente de evaluación?
            boolean hayPendiente = false;
            for (FormatoAVersion v : servicioFormatoA.listarPendientes()) {
                if (v.trabajoGradoId == (int) trabajoIdActual) {
                    hayPendiente = true;
                    break;
                }
            }

            lblEstado.setText("Estado: " + est + "   |   Intentos: " + intentos + "/3");

            boolean puedeSubir
                    = !hayPendiente
                    && intentos < 3
                    && est != EstadoFormatoA.ACEPTADO_FORMATO_A
                    && est != EstadoFormatoA.RECHAZADO_FORMATO_A;

            btnIniciarNuevo.setEnabled(puedeSubir);
        } catch (Exception ex) {
            lblEstado.setText("Estado: (no disponible)");
            btnIniciarNuevo.setEnabled(true); // deja que el backend bloquee si corresponde
        }
    }

    /**
     * Abre historial; si no hay TG aún, intenta detectarlo y refresca
     */
    private void abrirHistorial() {
        if (trabajoIdActual <= 0) {
            Integer tg = servicioFormatoA.buscarTrabajoIdPorEstudianteEmail(usuario.getEmail());
            if (tg != null) {
                this.trabajoIdActual = tg;
            }
        }
        if (trabajoIdActual <= 0) {
            JOptionPane.showMessageDialog(this, "Aún no tienes un Trabajo de Grado registrado.");
            return;
        }
        new DialogoHistorialFormatoA(this, servicioFormatoA, (int) trabajoIdActual).setVisible(true);
    }

    private void abrirArchivo(String ruta) {
        try {
            if (ruta == null || ruta.isBlank()) {
                JOptionPane.showMessageDialog(this, "No hay archivo asociado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!java.awt.Desktop.isDesktopSupported()) {
                JOptionPane.showMessageDialog(this, "No se puede abrir el archivo en este sistema.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            java.awt.Desktop.getDesktop().open(new java.io.File(ruta));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirUltimoPdf() {
    try {
        if (trabajoIdActual <= 0) {
            Integer tg = servicioFormatoA.buscarTrabajoIdPorEstudianteEmail(usuario.getEmail());
            if (tg != null) trabajoIdActual = tg;
        }
        if (trabajoIdActual <= 0) {
            JOptionPane.showMessageDialog(this, "No hay Trabajo de Grado asociado.");
            return;
        }

        var hist = servicioFormatoA.historialSimple((int) trabajoIdActual);
        if (hist == null || hist.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sin historial aún.");
            return;
        }

        String[] ultimo = hist.get(hist.size() - 1);
        String pdf = (ultimo.length >= 4 ? ultimo[3] : null); // índice 3 = pdf_path
        if (pdf == null || pdf.isBlank()) {
            JOptionPane.showMessageDialog(this, "El último intento no tiene PDF registrado.");
            return;
        }

        abrirArchivo(pdf);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    

}
