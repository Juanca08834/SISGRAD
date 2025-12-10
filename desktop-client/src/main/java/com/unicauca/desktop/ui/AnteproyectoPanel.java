package com.unicauca.desktop.ui;

import com.unicauca.desktop.config.AppConfig;
import com.unicauca.desktop.service.AnteproyectoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel para gestión de Anteproyectos
 */
public class AnteproyectoPanel extends JPanel {

    private final AnteproyectoService anteproyectoService;
    private JTable anteproyectosTable;
    private DefaultTableModel tableModel;
    private final boolean isDocente;
    private final boolean isJefeDepartamento;
    private List<Map<String, Object>> anteproyectosList;

    public AnteproyectoPanel() {
        this.anteproyectoService = new AnteproyectoService();
        this.isDocente = "DOCENTE".equals(AppConfig.getUserRole());
        this.isJefeDepartamento = "JEFE_DEPARTAMENTO".equals(AppConfig.getUserRole());
        initComponents();
        loadAnteproyectos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UnicaucaTheme.BLANCO);

        // Panel superior con botones
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(UnicaucaTheme.BLANCO);

        if (isDocente) {
            JButton createButton = UnicaucaTheme.createPrimaryButton("Subir Anteproyecto");
            createButton.addActionListener(e -> openCreateAnteproyectoDialog());
            topPanel.add(createButton);
        }

        JButton refreshButton = UnicaucaTheme.createSecondaryButton("Actualizar");
        refreshButton.addActionListener(e -> loadAnteproyectos());
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        // Tabla de anteproyectos (sin columna de acciones)
        String[] columns = {"ID", "Formato A ID", "Archivo URL", "Docente ID", "Estado", "Fecha Subida"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        anteproyectosTable = new JTable(tableModel);
        anteproyectosTable.setRowHeight(28);
        anteproyectosTable.setFont(UnicaucaTheme.FUENTE_NORMAL);
        anteproyectosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        anteproyectosTable.getTableHeader().setBackground(UnicaucaTheme.AZUL_UNICAUCA);
        anteproyectosTable.getTableHeader().setForeground(UnicaucaTheme.BLANCO);
        anteproyectosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        anteproyectosTable.setSelectionBackground(UnicaucaTheme.AZUL_CLARO);

        JScrollPane scrollPane = new JScrollPane(anteproyectosTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(UnicaucaTheme.GRIS_CLARO);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton verDetalleBtn = UnicaucaTheme.createSecondaryButton("Ver Detalle");
        verDetalleBtn.addActionListener(e -> verDetalleSeleccionado());
        actionPanel.add(verDetalleBtn);

        if (isJefeDepartamento) {
            JButton asignarBtn = UnicaucaTheme.createPrimaryButton("Asignar Evaluadores");
            asignarBtn.addActionListener(e -> asignarEvaluadoresSeleccionado());
            actionPanel.add(asignarBtn);
        }

        add(actionPanel, BorderLayout.SOUTH);
    }

    private void loadAnteproyectos() {
        tableModel.setRowCount(0);

        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return anteproyectoService.obtenerTodosAnteproyectos();
            }

            @Override
            protected void done() {
                try {
                    anteproyectosList = get();
                    for (Map<String, Object> anteproyecto : anteproyectosList) {
                        Object[] row = {
                            anteproyecto.get("id"),
                            anteproyecto.get("formatoAId"),
                            anteproyecto.get("archivoUrl"),
                            anteproyecto.get("docenteId"),
                            formatearEstado((String) anteproyecto.get("estado")),
                            anteproyecto.get("fechaSubida")
                        };
                        tableModel.addRow(row);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AnteproyectoPanel.this,
                        "Error al cargar anteproyectos: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private String formatearEstado(String estado) {
        if (estado == null) return "Desconocido";
        return switch (estado) {
            case "PENDIENTE_ASIGNACION" -> "Pendiente Asignación";
            case "EN_EVALUACION" -> "En Evaluación";
            case "APROBADO" -> "Aprobado";
            case "RECHAZADO" -> "Rechazado";
            default -> estado;
        };
    }

    private Map<String, Object> getAnteproyectoSeleccionado() {
        int selectedRow = anteproyectosTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un anteproyecto de la tabla.",
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return anteproyectosList.get(selectedRow);
    }

    private void verDetalleSeleccionado() {
        Map<String, Object> anteproyecto = getAnteproyectoSeleccionado();
        if (anteproyecto != null) {
            showAnteproyectoDetail(anteproyecto);
        }
    }

    private void asignarEvaluadoresSeleccionado() {
        Map<String, Object> anteproyecto = getAnteproyectoSeleccionado();
        if (anteproyecto != null) {
            String estado = (String) anteproyecto.get("estado");
            if ("PENDIENTE_ASIGNACION".equals(estado)) {
                openAsignarEvaluadoresDialog(anteproyecto);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Este anteproyecto ya tiene evaluadores asignados.",
                    "No disponible",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void openCreateAnteproyectoDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Subir Anteproyecto", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Información del docente actual
        JLabel infoLabel = new JLabel("Docente: " + AppConfig.getUserName() + " (ID: " + AppConfig.getUserId() + ")");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(infoLabel, gbc);

        // Nota explicativa
        JLabel notaLabel = new JLabel("<html><b>Nota:</b> Ingrese el ID del Formato A que fue aprobado previamente.<br>" +
            "Solo puede subir anteproyecto para formatos con estado ACEPTADO.</html>");
        notaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gbc.gridy = 1;
        panel.add(notaLabel, gbc);
        gbc.gridwidth = 1;

        // Campos del formulario
        JTextField formatoAIdField = new JTextField(30);
        JTextField archivoUrlField = new JTextField(30);

        // Agregar campos
        addFormField(panel, gbc, 2, "ID del Formato A Aprobado:", formatoAIdField);
        addFormField(panel, gbc, 3, "URL del Archivo PDF:", archivoUrlField);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = UnicaucaTheme.createPrimaryButton("Subir Anteproyecto");
        JButton cancelButton = UnicaucaTheme.createSecondaryButton("Cancelar");

        saveButton.addActionListener(e -> {
            // Validar campos
            if (formatoAIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Por favor ingrese el ID del Formato A aprobado.",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (archivoUrlField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Por favor ingrese la URL del archivo PDF.",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Long formatoAId = Long.parseLong(formatoAIdField.getText().trim());

                Map<String, Object> anteproyectoData = new HashMap<>();
                anteproyectoData.put("formatoAId", formatoAId);
                anteproyectoData.put("archivoUrl", archivoUrlField.getText().trim());
                anteproyectoData.put("docenteId", AppConfig.getUserId());

                anteproyectoService.crearAnteproyecto(anteproyectoData);
                JOptionPane.showMessageDialog(dialog,
                    "Anteproyecto subido exitosamente.\nEl jefe de departamento será notificado.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadAnteproyectos();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dialog,
                    "El ID del Formato A debe ser un número válido.",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error al subir anteproyecto: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void openAsignarEvaluadoresDialog(Map<String, Object> anteproyecto) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Asignar Evaluadores", true);
        dialog.setSize(650, 650);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Información del anteproyecto
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información del Anteproyecto"));
        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(new JLabel(String.valueOf(anteproyecto.get("id"))));
        infoPanel.add(new JLabel("Formato A ID:"));
        infoPanel.add(new JLabel(String.valueOf(anteproyecto.get("formatoAId"))));
        infoPanel.add(new JLabel("Docente ID:"));
        infoPanel.add(new JLabel(String.valueOf(anteproyecto.get("docenteId"))));
        infoPanel.add(new JLabel("Estado:"));
        infoPanel.add(new JLabel(formatearEstado((String) anteproyecto.get("estado"))));

        panel.add(infoPanel, BorderLayout.NORTH);

        // Panel de evaluadores
        JPanel evalPanel = new JPanel(new GridBagLayout());
        evalPanel.setBorder(BorderFactory.createTitledBorder("Asignar 2 Evaluadores (Docentes del Departamento)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Evaluador 1
        JLabel eval1Label = new JLabel("Evaluador 1:");
        eval1Label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JTextField eval1IdField = new JTextField(20);
        JTextField eval1NombreField = new JTextField(20);
        JTextField eval1EmailField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        evalPanel.add(eval1Label, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        evalPanel.add(new JLabel("ID Docente:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        evalPanel.add(eval1IdField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        evalPanel.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        evalPanel.add(eval1NombreField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        evalPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        evalPanel.add(eval1EmailField, gbc);

        // Separador
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        evalPanel.add(new JSeparator(), gbc);

        // Evaluador 2
        JLabel eval2Label = new JLabel("Evaluador 2:");
        eval2Label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JTextField eval2IdField = new JTextField(20);
        JTextField eval2NombreField = new JTextField(20);
        JTextField eval2EmailField = new JTextField(20);

        gbc.gridy = 5;
        evalPanel.add(eval2Label, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        evalPanel.add(new JLabel("ID Docente:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        evalPanel.add(eval2IdField, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        evalPanel.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        evalPanel.add(eval2NombreField, gbc);

        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        evalPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        evalPanel.add(eval2EmailField, gbc);

        panel.add(evalPanel, BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton asignarButton = UnicaucaTheme.createPrimaryButton("Asignar Evaluadores");
        JButton cancelButton = UnicaucaTheme.createSecondaryButton("Cancelar");

        asignarButton.addActionListener(e -> {
            String eval1Id = eval1IdField.getText().trim();
            String eval1Nombre = eval1NombreField.getText().trim();
            String eval1Email = eval1EmailField.getText().trim();
            String eval2Id = eval2IdField.getText().trim();
            String eval2Nombre = eval2NombreField.getText().trim();
            String eval2Email = eval2EmailField.getText().trim();

            if (eval1Id.isEmpty() || eval1Nombre.isEmpty() || eval1Email.isEmpty() ||
                eval2Id.isEmpty() || eval2Nombre.isEmpty() || eval2Email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Por favor complete todos los campos de los evaluadores (ID, Nombre y Email).",
                    "Campos requeridos",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Long eval1DocenteId = Long.parseLong(eval1Id);
                Long eval2DocenteId = Long.parseLong(eval2Id);

                List<Map<String, Object>> evaluadores = new ArrayList<>();

                Map<String, Object> eval1 = new HashMap<>();
                eval1.put("docenteEvaluadorId", eval1DocenteId);
                eval1.put("nombreEvaluador", eval1Nombre);
                eval1.put("emailEvaluador", eval1Email);
                evaluadores.add(eval1);

                Map<String, Object> eval2 = new HashMap<>();
                eval2.put("docenteEvaluadorId", eval2DocenteId);
                eval2.put("nombreEvaluador", eval2Nombre);
                eval2.put("emailEvaluador", eval2Email);
                evaluadores.add(eval2);

                Long anteproyectoId = ((Number) anteproyecto.get("id")).longValue();
                anteproyectoService.asignarEvaluadores(anteproyectoId, evaluadores);
                JOptionPane.showMessageDialog(dialog,
                    "Evaluadores asignados exitosamente.\nSe ha enviado notificación por email a los evaluadores.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadAnteproyectos();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dialog,
                    "Los IDs de los docentes evaluadores deben ser números válidos.",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error al asignar evaluadores: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(asignarButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAnteproyectoDetail(Map<String, Object> anteproyecto) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("       DETALLE DEL ANTEPROYECTO\n");
        sb.append("==========================================\n\n");
        sb.append("ID: ").append(anteproyecto.get("id")).append("\n");
        sb.append("Formato A ID: ").append(anteproyecto.get("formatoAId")).append("\n");
        sb.append("Docente ID: ").append(anteproyecto.get("docenteId")).append("\n");
        sb.append("Archivo URL: ").append(anteproyecto.get("archivoUrl")).append("\n");
        sb.append("Estado: ").append(formatearEstado((String) anteproyecto.get("estado"))).append("\n");
        sb.append("Fecha Subida: ").append(anteproyecto.get("fechaSubida")).append("\n");

        // Mostrar evaluadores si existen
        Object evaluadores = anteproyecto.get("evaluadores");
        if (evaluadores instanceof List && !((List<?>) evaluadores).isEmpty()) {
            sb.append("\n--- Evaluadores Asignados ---\n");
            List<?> evalList = (List<?>) evaluadores;
            for (int i = 0; i < evalList.size(); i++) {
                if (evalList.get(i) instanceof Map) {
                    Map<?, ?> eval = (Map<?, ?>) evalList.get(i);
                    sb.append("Evaluador ").append(i + 1).append(": ");
                    sb.append(eval.get("nombreEvaluador"));
                    sb.append(" (").append(eval.get("emailEvaluador")).append(")\n");
                }
            }
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setRows(18);
        textArea.setColumns(45);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 350));

        JOptionPane.showMessageDialog(this, scrollPane, "Detalle del Anteproyecto", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }
}

