package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.excepciones.SISGRADException;
import com.unicauca.proyectosofv1.modelo.FormatoAVersion;
import com.unicauca.proyectosofv1.servicios.ServicioFormatoA;
import com.unicauca.proyectosofv1.Principal;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.ListSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public class TableroCoordinador extends JFrame {

    private final ServicioFormatoA servicio;

    // UI principal
    private final JTable tabla = new JTable();
    private final JTextField txtBuscar = new JTextField(24);

    private final JButton btnRefrescar = new JButton("Refrescar");
    private final JButton btnAbrirPdf = new JButton("Abrir PDF");
    private final JButton btnAbrirCarta = new JButton("Abrir Carta");
    private final JButton btnAprobar = new JButton("Aprobar seleccionado");
    private final JButton btnRechazar = new JButton("Rechazar seleccionado");
    private final JButton btnVerHistorial = new JButton("Ver historial TG");
    private final JButton btnCerrarSesion = new JButton("Cerrar sesión");

    private final JTextArea txtObs = new JTextArea(3, 40);

    private TableRowSorter<DefaultTableModel> sorter;

    public TableroCoordinador(ServicioFormatoA servicio) {
        super("Coordinación - Evaluación Formato A");
        this.servicio = servicio;

        construirUI();
        recargar();

        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Si vuelve el foco a esta ventana, recarga (por si aprobaron/rechazaron desde otro lado)
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                recargar();
            }
        });
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));

        // ----- Norte: buscar + acciones rápidas
        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        north.add(new JLabel("Buscar:"));
        north.add(txtBuscar);
        north.add(btnRefrescar);
        north.add(btnAbrirPdf);
        north.add(btnAbrirCarta);
        north.add(btnVerHistorial);
        north.add(btnCerrarSesion);
        add(north, BorderLayout.NORTH);

        // ----- Centro: tabla
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Versión", "TG", "Intento", "Objetivo general", "PDF", "Carta", "Codirector"}
        ) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla.setModel(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        // Doble clic: abre el archivo de la columna apuntada (PDF o Carta)
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int viewRow = tabla.rowAtPoint(e.getPoint());
                    int viewCol = tabla.columnAtPoint(e.getPoint());
                    if (viewRow >= 0) {
                        int modelRow = tabla.convertRowIndexToModel(viewRow);
                        int col = (viewCol == 5) ? 5 : 4; // 4=PDF, 5=Carta
                        String ruta = String.valueOf(tabla.getModel().getValueAt(modelRow, col));
                        abrirArchivo(ruta);
                    }
                }
            }
        });

        JScrollPane spTabla = new JScrollPane(tabla);
        add(spTabla, BorderLayout.CENTER);

        // ----- Sur: observaciones + aprobar/rechazar
        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);

        JPanel south = new JPanel(new BorderLayout(6, 6));
        south.add(new JLabel("Observaciones:"), BorderLayout.NORTH);
        south.add(new JScrollPane(txtObs), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(btnRechazar);
        acciones.add(btnAprobar);
        south.add(acciones, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);

        // ----- Listeners
        btnRefrescar.addActionListener(e -> recargar());
        btnAprobar.addActionListener(e -> evaluar("aprobado"));
        btnRechazar.addActionListener(e -> evaluar("rechazado"));
        btnAbrirPdf.addActionListener(e -> abrirSeleccion("pdf"));
        btnAbrirCarta.addActionListener(e -> abrirSeleccion("carta"));
        btnVerHistorial.addActionListener(e -> abrirHistorialTG());

        btnCerrarSesion.addActionListener(e -> {
            dispose();
            Principal.volverALogin();
        });

        tabla.getSelectionModel().addListSelectionListener(e -> actualizarBotonesAbrir());

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        actualizarBotonesAbrir();
    }

    private void recargar() {
        List<FormatoAVersion> pendientes = servicio.listarPendientes();
        DefaultTableModel m = (DefaultTableModel) tabla.getModel();
        m.setRowCount(0);
        for (FormatoAVersion v : pendientes) {
            m.addRow(new Object[]{
                    v.id,
                    v.trabajoGradoId,
                    v.intento,
                    v.objetivoGeneral,
                    v.pdfPath,
                    v.cartaAceptacionPath,
                    v.codirectorId
            });
        }
        if (m.getRowCount() > 0) {
            tabla.setRowSelectionInterval(0, 0);
        }
        actualizarBotonesAbrir();
    }

    private void evaluar(String estado) {
        int viewRow = tabla.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = tabla.convertRowIndexToModel(viewRow);
        int versionId = Integer.parseInt(tabla.getModel().getValueAt(modelRow, 0).toString());
        int tgId = Integer.parseInt(tabla.getModel().getValueAt(modelRow, 1).toString());
        int intento = Integer.parseInt(tabla.getModel().getValueAt(modelRow, 2).toString());

        String obs = txtObs.getText().trim();
        if ("rechazado".equalsIgnoreCase(estado) && obs.isBlank()) {
            JOptionPane.showMessageDialog(this, "Explique brevemente por qué se rechaza.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (obs.isBlank()) obs = "(sin observaciones)";

        // Confirmación
        int conf = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmas marcar la versión " + versionId + " (TG " + tgId + ", intento " + intento + ") como " + estado + "?",
                "Confirmar " + estado,
                JOptionPane.YES_NO_OPTION,
                ("aprobado".equalsIgnoreCase(estado) ? JOptionPane.QUESTION_MESSAGE : JOptionPane.WARNING_MESSAGE)
        );
        if (conf != JOptionPane.YES_OPTION) return;

        try {
            int evaluadorUsuarioId = 9; // TODO: si luego autenticas coordinador, usa su id real
            servicio.evaluarFormatoA(versionId, estado, obs, evaluadorUsuarioId);
            JOptionPane.showMessageDialog(this, "Versión " + versionId + " " + estado + " correctamente.");
            txtObs.setText("");
            recargar();
        } catch (SISGRADException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirHistorialTG() {
        int viewRow = tabla.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila para ver el historial.", "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = tabla.convertRowIndexToModel(viewRow);
        int tgId = Integer.parseInt(tabla.getModel().getValueAt(modelRow, 1).toString());
        new DialogoHistorialFormatoA(this, servicio, tgId).setVisible(true);
    }

    private void filtrar() {
        if (sorter == null) return;
        String q = txtBuscar.getText().trim();
        if (q.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        // Busca en columnas: 0(ID),1(TG),2(Intento),3(Objetivo),4(PDF),6(Codirector)
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(q), 0, 1, 2, 3, 4, 6));
    }

    private void abrirSeleccion(String tipo) {
        int viewRow = tabla.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = tabla.convertRowIndexToModel(viewRow);
        int col = "carta".equalsIgnoreCase(tipo) ? 5 : 4;
        String ruta = String.valueOf(tabla.getModel().getValueAt(modelRow, col));
        abrirArchivo(ruta);
    }

    private void actualizarBotonesAbrir() {
        int viewRow = tabla.getSelectedRow();
        boolean hasSel = viewRow >= 0;
        btnAbrirPdf.setEnabled(hasSel);
        btnVerHistorial.setEnabled(hasSel);

        if (!hasSel) {
            btnAbrirCarta.setEnabled(false);
            return;
        }
        int modelRow = tabla.convertRowIndexToModel(viewRow);
        Object carta = tabla.getModel().getValueAt(modelRow, 5);
        boolean cartaOk = carta != null && !String.valueOf(carta).isBlank();
        btnAbrirCarta.setEnabled(cartaOk);
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
