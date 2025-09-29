package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.servicios.ServicioFormatoA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public class DialogoHistorialFormatoA extends JDialog {
    private final ServicioFormatoA servicio;
    private final int trabajoGradoId;

    private final JTable tabla = new JTable();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[][]{}, new String[]{"Intento", "Estado", "Observaciones", "PDF", "Carta"}) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JButton btnAbrirPdf = new JButton("Abrir PDF");
    private final JButton btnAbrirCarta = new JButton("Abrir Carta");
    private final JButton btnCerrar = new JButton("Cerrar");
    private final JTextField txtBuscar = new JTextField(24);
    private TableRowSorter<DefaultTableModel> sorter;

    public DialogoHistorialFormatoA(Frame owner, ServicioFormatoA servicio, int trabajoGradoId) {
        super(owner, "Historial Formato A (TG " + trabajoGradoId + ")", true);
        this.servicio = servicio;
        this.trabajoGradoId = trabajoGradoId;
        construirUI();
        cargar();
        setSize(900, 520);
        setLocationRelativeTo(owner);
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));

        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        north.add(new JLabel("Buscar:"));
        north.add(txtBuscar);
        add(north, BorderLayout.NORTH);

        tabla.setModel(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        // Doble clic: abre PDF por defecto; si se hace doble clic en "Carta", abre la carta.
        tabla.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int vRow = tabla.rowAtPoint(e.getPoint());
                    int vCol = tabla.columnAtPoint(e.getPoint());
                    if (vRow >= 0) {
                        int mRow = tabla.convertRowIndexToModel(vRow);
                        int col = (vCol == 4) ? 4 : 3; // 3=PDF, 4=Carta
                        abrirArchivo(String.valueOf(modelo.getValueAt(mRow, col)));
                    }
                }
            }
        });

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnAbrirPdf);
        south.add(btnAbrirCarta);
        south.add(btnCerrar);
        add(south, BorderLayout.SOUTH);

        btnAbrirPdf.addActionListener(e -> abrirSeleccion(3));
        btnAbrirCarta.addActionListener(e -> abrirSeleccion(4));
        btnCerrar.addActionListener(e -> dispose());

        // Habilitar/deshabilitar "Abrir Carta" según selección
        tabla.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> actualizarBotonesAbrir());

        // Filtro
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        actualizarBotonesAbrir();
    }

    private void cargar() {
        modelo.setRowCount(0);
        List<String[]> hist = servicio.historialSimple(trabajoGradoId);
        for (String[] row : hist) {
            // row: intento, estado, obs, pdf, carta
            modelo.addRow(row);
        }
        actualizarBotonesAbrir();
    }

    private void filtrar() {
        String q = txtBuscar.getText().trim();
        if (q.isEmpty()) { sorter.setRowFilter(null); return; }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(q), 0,1,2,3,4));
    }

    private void actualizarBotonesAbrir() {
        int vRow = tabla.getSelectedRow();
        boolean hasSel = vRow >= 0;
        btnAbrirPdf.setEnabled(hasSel);
        if (!hasSel) { btnAbrirCarta.setEnabled(false); return; }
        int mRow = tabla.convertRowIndexToModel(vRow);
        Object carta = modelo.getValueAt(mRow, 4);
        btnAbrirCarta.setEnabled(carta != null && !String.valueOf(carta).isBlank());
    }

    private void abrirSeleccion(int col) {
        int vRow = tabla.getSelectedRow();
        if (vRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int mRow = tabla.convertRowIndexToModel(vRow);
        abrirArchivo(String.valueOf(modelo.getValueAt(mRow, col)));
    }

    private void abrirArchivo(String ruta) {
        try {
            if (ruta == null || ruta.isBlank()) {
                JOptionPane.showMessageDialog(this, "No hay archivo asociado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!Desktop.isDesktopSupported()) {
                JOptionPane.showMessageDialog(this, "No se puede abrir el archivo en este sistema.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Desktop.getDesktop().open(new File(ruta));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
