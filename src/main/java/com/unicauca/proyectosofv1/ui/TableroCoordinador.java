package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.modelo.Decision;
import com.unicauca.proyectosofv1.modelo.EstadoProyecto;
import com.unicauca.proyectosofv1.modelo.ProyectoGrado;
import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioProyecto;
import com.unicauca.proyectosofv1.repositorio.sqlite.RepositorioProyectoSQLite;
import com.unicauca.proyectosofv1.servicios.ServicioEvaluacionFormatoA;
import com.unicauca.proyectosofv1.servicios.impl.ServicioEvaluacionFormatoAImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tablero para el coordinador de programa.
 * Permite listar proyectos y aprobar/rechazar Formato A con observaciones.
 */
public class TableroCoordinador extends JFrame {

    private final Usuario usuario; // por si quieres mostrar nombre/rol
    private final RepositorioProyecto repoProyecto = new RepositorioProyectoSQLite();
    private final ServicioEvaluacionFormatoA srvEval = new ServicioEvaluacionFormatoAImpl(repoProyecto);

    private final JTable tabla = new JTable();
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"id", "docente_email", "estado", "intentos"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTextArea txtObs = new JTextArea(3, 40);
    private final JButton btnAprobar = new JButton("Aprobar");
    private final JButton btnRechazar = new JButton("Rechazar");
    private final JButton btnRefrescar = new JButton("Refrescar");

    public TableroCoordinador(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Tablero - Coordinador");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        construirUI();
        setLocationRelativeTo(null);
        cargarTabla();
        pack();
    }

    private void construirUI() {
        var root = getContentPane();
        root.setLayout(new BorderLayout(8, 8));

        JLabel lbl = new JLabel("Coordinador: " + usuario.getNombres());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        root.add(lbl, BorderLayout.NORTH);

        tabla.setModel(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        root.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(8, 8));
        south.add(new JScrollPane(txtObs), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnRefrescar);
        acciones.add(btnRechazar);
        acciones.add(btnAprobar);
        south.add(acciones, BorderLayout.SOUTH);

        root.add(south, BorderLayout.SOUTH);

        btnRefrescar.addActionListener(e -> cargarTabla());
        btnAprobar.addActionListener(e -> onEvaluar(Decision.APROBADO));
        btnRechazar.addActionListener(e -> onEvaluar(Decision.RECHAZADO));
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<ProyectoGrado> proyectos = repoProyecto.listarTodos();
            for (ProyectoGrado p : proyectos) {
                modeloTabla.addRow(new Object[]{
                        p.getId(),
                        p.getDocenteEmail(),
                        p.getEstado().name(),
                        p.getIntentosFormatoA()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al listar proyectos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEvaluar(Decision decision) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un proyecto primero", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        long proyectoId = Long.parseLong(String.valueOf(modeloTabla.getValueAt(fila, 0)));
        String obs = txtObs.getText().trim();

        try {
            srvEval.evaluar(proyectoId, decision, obs);
            JOptionPane.showMessageDialog(this,
                    (decision == Decision.APROBADO ? "Proyecto aprobado" : "Proyecto rechazado"));
            cargarTabla();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al evaluar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
