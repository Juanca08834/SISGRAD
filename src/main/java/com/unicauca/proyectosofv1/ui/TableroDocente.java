package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.Principal;
import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.modelo.EstadoFormatoA;
import com.unicauca.proyectosofv1.modelo.FormatoAVersion;
import com.unicauca.proyectosofv1.servicios.ServicioFormatoA;

import javax.swing.*;
import java.awt.*;

public class TableroDocente extends JFrame {

    private final Usuario usuario;
    private final ServicioFormatoA servicioFormatoA;

    private long trabajoIdActual = 0L; // 0 = aún no existe

    private JLabel lblEstado;
    private final JButton btnSubirFormatoA = new JButton("Subir Formato A");
    private JButton btnVerHistorial;
    private JButton btnCerrarSesion;

    private JButton btnEvaluarAnteproyectos;
    private JButton btnEvaluarMonografias;
    private JLabel lblBienvenida;

    private final JButton btnAbrirUltimoPdf = new JButton("Abrir último PDF");

    public TableroDocente(Usuario docente, ServicioFormatoA servicioFormatoA) {
        super("Docente - Formato A");
        this.usuario = docente;
        this.servicioFormatoA = servicioFormatoA;

        initComponents();

        // Detectar TG actual (si existe)
        try {
            Integer tg = servicioFormatoA.buscarTrabajoIdPorDocenteEmail(usuario.getEmail());
            if (tg != null) {
                this.trabajoIdActual = tg;
            }
        } catch (Exception ignore) {
        }

        // Listeners
        btnSubirFormatoA.addActionListener(e -> abrirDialogoSubir());
        btnVerHistorial.addActionListener(e -> abrirHistorial());
        btnCerrarSesion.addActionListener(e -> {
            dispose();
            Principal.volverALogin();
        });

        // Ventana
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        refrescarEstado();

        // Cuando recupere foco, refresca (útil tras cerrar diálogos)
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                refrescarEstado();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        lblBienvenida = new JLabel();
        lblEstado = new JLabel("Estado: —   |   Intentos: 0/3");
        btnEvaluarAnteproyectos = new JButton();
        btnEvaluarMonografias = new JButton();
        btnVerHistorial = new JButton("Ver historial");
        btnCerrarSesion = new JButton("Cerrar sesión");

        setTitle("Tablero - Docente");

        lblBienvenida.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblBienvenida.setText("Hola, " + usuario.getNombres());

        btnEvaluarAnteproyectos.setText("Evaluar anteproyectos");
        btnEvaluarAnteproyectos.addActionListener(e -> onEvaluarAnteproyectos());

        btnEvaluarMonografias.setText("Evaluar monografías");
        btnEvaluarMonografias.addActionListener(e -> onEvaluarMonografias());

        var layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(lblBienvenida, GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                                        .addComponent(lblEstado, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnSubirFormatoA, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnVerHistorial, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnAbrirUltimoPdf, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnEvaluarAnteproyectos, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnEvaluarMonografias, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                                .addComponent(btnSubirFormatoA, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(btnVerHistorial, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(btnAbrirUltimoPdf, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(btnEvaluarAnteproyectos, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(btnEvaluarMonografias, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(btnCerrarSesion)
                                .addContainerGap(24, Short.MAX_VALUE))
        );

        btnAbrirUltimoPdf.addActionListener(e -> {
            try {
                if (trabajoIdActual <= 0) {
                    Integer tg = servicioFormatoA.buscarTrabajoIdPorDocenteEmail(usuario.getEmail());
                    if (tg != null) {
                        trabajoIdActual = tg;
                    }
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
        });

        pack();
    }

    private void abrirDialogoSubir() {
        new DialogoSubirFormatoA(
                this,
                servicioFormatoA,
                tgId -> {
                    this.trabajoIdActual = tgId;
                    refrescarEstado();
                },
                (trabajoIdActual > 0 ? (int) trabajoIdActual : null) // si existe TG, pre-llenar y bloquear lo fijo
        ).setVisible(true);
    }

    private void refrescarEstado() {
        try {
            if (trabajoIdActual <= 0) {
                lblEstado.setText("Estado: (sin proyecto)   |   Intentos: 0/3");
                btnSubirFormatoA.setEnabled(true);
                return;
            }

            var est = servicioFormatoA.estadoActual((int) trabajoIdActual);
            int intentos = servicioFormatoA.contarIntentos((int) trabajoIdActual);

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

            btnSubirFormatoA.setEnabled(puedeSubir);
        } catch (Exception ex) {
            lblEstado.setText("Estado: (no disponible)");
            btnSubirFormatoA.setEnabled(true); // deja que el backend valide
        }
    }

    private void abrirHistorial() {
        if (trabajoIdActual <= 0) {
            Integer tg = servicioFormatoA.buscarTrabajoIdPorDocenteEmail(usuario.getEmail());
            if (tg != null) {
                this.trabajoIdActual = tg;
            }
        }
        if (trabajoIdActual <= 0) {
            JOptionPane.showMessageDialog(this, "Aún no tienes un Trabajo de Grado asociado como director/codirector.");
            return;
        }
        new DialogoHistorialFormatoA(this, servicioFormatoA, (int) trabajoIdActual).setVisible(true);
    }

    private void onEvaluarAnteproyectos() {
        JOptionPane.showMessageDialog(this, "Pendiente: listado de anteproyectos a evaluar");
    }

    private void onEvaluarMonografias() {
        JOptionPane.showMessageDialog(this, "Pendiente: listado de monografías a evaluar");
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

}
