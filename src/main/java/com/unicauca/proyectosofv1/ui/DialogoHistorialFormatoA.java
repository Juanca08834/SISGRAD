package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.modelo.FormatoA;
import com.unicauca.proyectosofv1.servicios.ServicioHistorialFormatoA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class DialogoHistorialFormatoA extends JDialog {

    private final long proyectoId;
    private final ServicioHistorialFormatoA servicio;

    private final JTable tabla = new JTable();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"id", "fecha", "titulo", "modalidad", "director", "codirector", "pdf", "carta"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JButton btnAbrirPdf = new JButton("Abrir PDF");
    private final JButton btnAbrirCarta = new JButton("Abrir carta");
    private final JButton btnCerrar = new JButton("Cerrar");
    private final JLabel lblResumen = new JLabel("0 versiones");

    public DialogoHistorialFormatoA(Frame owner, long proyectoId, ServicioHistorialFormatoA servicio) {
        super(owner, "Historial Formato A", true);
        this.proyectoId = proyectoId;
        this.servicio = servicio;
        construirUI();
        cargar();
        pack();
        setLocationRelativeTo(owner);
    }

    private void construirUI() {
        setLayout(new BorderLayout(8,8));
        tabla.setModel(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.add(lblResumen, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnAbrirCarta);
        south.add(btnAbrirPdf);
        south.add(btnCerrar);
        add(south, BorderLayout.SOUTH);

        btnCerrar.addActionListener(e -> dispose());
        btnAbrirPdf.addActionListener(e -> abrirArchivoSeleccionado(true));
        btnAbrirCarta.addActionListener(e -> abrirArchivoSeleccionado(false));
    }

    private void cargar() {
        modelo.setRowCount(0);
        List<FormatoA> lista = servicio.listar(proyectoId);
        for (FormatoA f : lista) {
            modelo.addRow(new Object[]{
                    f.id, f.fecha.toString(), f.titulo, f.modalidad.name(),
                    f.director, f.codirector, f.pdfPath, f.cartaAceptacionPath
            });
        }
        lblResumen.setText(lista.size() + " versiones");
    }

    private void abrirArchivoSeleccionado(boolean abrirPdf) {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ruta = String.valueOf(modelo.getValueAt(row, abrirPdf ? 6 : 7));
        if (ruta == null || ruta.equals("null") || ruta.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    abrirPdf ? "No hay ruta de PDF" : "No hay ruta de carta", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            File f = new File(ruta);
            if (!f.exists()) {
                JOptionPane.showMessageDialog(this, "Archivo no existe: " + ruta, "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(f);
            } else {
                JOptionPane.showMessageDialog(this, "Desktop no soportado para abrir archivos",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
