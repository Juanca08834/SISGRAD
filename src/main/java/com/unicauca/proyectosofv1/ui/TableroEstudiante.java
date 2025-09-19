package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.modelo.EstadoProyecto;
import com.unicauca.proyectosofv1.modelo.ProyectoGrado;
import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioProyecto;
import com.unicauca.proyectosofv1.repositorio.sqlite.RepositorioProyectoSQLite;
import com.unicauca.proyectosofv1.servicios.ServicioConsultaEstado;
import com.unicauca.proyectosofv1.servicios.impl.ServicioConsultaEstadoImpl;

import javax.swing.*;

/**
 * Tablero del Estudiante.
 * Permite consultar el estado del proyecto de grado por id de proyecto.
 * Nota: segun los requisitos, el flujo lo inicia el docente subiendo Formato A.
 */
public class TableroEstudiante extends javax.swing.JFrame {
    private final Usuario usuario;

    // servicios/repos necesarios para la consulta
    private final RepositorioProyecto repoProyecto = new RepositorioProyectoSQLite();
    private final ServicioConsultaEstado srvEstado = new ServicioConsultaEstadoImpl(repoProyecto);

    // UI
    private javax.swing.JButton btnIniciarNuevo;
    private javax.swing.JButton btnVerEstado;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JLabel lblEstado;

    public TableroEstudiante(Usuario usuario) {
        this.usuario = usuario;
        initComponents();
        setLocationRelativeTo(null);
        lblBienvenida.setText("Bienvenido, " + usuario.getNombres() + " (Estudiante)");
        lblEstado.setText("Estado: -");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        lblBienvenida = new javax.swing.JLabel();
        btnVerEstado = new javax.swing.JButton();
        btnIniciarNuevo = new javax.swing.JButton();
        lblEstado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tablero - Estudiante");

        lblBienvenida.setFont(new java.awt.Font("Segoe UI", 1, 16));

        btnVerEstado.setText("Ver estado de mi Trabajo de Grado");
        btnVerEstado.addActionListener(e -> onVerEstado());

        btnIniciarNuevo.setText("Iniciar nuevo Trabajo de Grado");
        btnIniciarNuevo.addActionListener(e -> onIniciarNuevo());

        lblEstado.setFont(new java.awt.Font("Segoe UI", 0, 14));

        var layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblBienvenida, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                        .addComponent(btnVerEstado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnIniciarNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblEstado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(24, 24, 24)
                    .addComponent(lblBienvenida)
                    .addGap(18, 18, 18)
                    .addComponent(btnVerEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addComponent(btnIniciarNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(lblEstado)
                    .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }

    private void onVerEstado() {
        String input = JOptionPane.showInputDialog(
                this,
                "Ingresa el id del proyecto:",
                "Consultar estado",
                JOptionPane.QUESTION_MESSAGE
        );
        if (input == null) return; // cancelado
        input = input.trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes ingresar un id de proyecto", "Validacion",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            long proyectoId = Long.parseLong(input);

            // obtener estado via servicio
            EstadoProyecto estado = srvEstado.consultar(proyectoId);
            lblEstado.setText("Estado: " + estado.name());

            // opcional: mostrar intentos actuales leyendo el modelo completo
            ProyectoGrado p = repoProyecto.buscarPorId(proyectoId);
            if (p != null) {
                JOptionPane.showMessageDialog(this,
                        "Proyecto " + proyectoId + "\nEstado: " + estado.name() +
                                "\nIntentos Formato A: " + p.getIntentosFormatoA(),
                        "Detalle",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "El id de proyecto debe ser numerico", "Validacion",
                    JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, iae.getMessage(), "Validacion",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar estado: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onIniciarNuevo() {
        // Segun requisitos, el flujo lo inicia el docente subiendo el Formato A.
        JOptionPane.showMessageDialog(this,
                "Este flujo lo inicia el docente cargando el Formato A.\n" +
                "Solicita a tu docente crear el proyecto y podras ver el estado aqui.",
                "Informacion",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
