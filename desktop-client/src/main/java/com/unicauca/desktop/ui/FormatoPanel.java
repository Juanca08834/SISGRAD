package com.unicauca.desktop.ui;

import com.unicauca.desktop.config.AppConfig;
import com.unicauca.desktop.service.FormatoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel para gestión de Formatos A
 */
public class FormatoPanel extends JPanel {

    private final FormatoService formatoService;
    private JTable formatosTable;
    private DefaultTableModel tableModel;
    private final boolean isDocente;
    private final boolean isCoordinador;
    private List<Map<String, Object>> formatosList;

    public FormatoPanel() {
        this.formatoService = new FormatoService();
        this.isDocente = "DOCENTE".equals(AppConfig.getUserRole());
        this.isCoordinador = "COORDINADOR".equals(AppConfig.getUserRole());
        initComponents();
        loadFormatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UnicaucaTheme.BLANCO);

        // Panel superior con botones
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(UnicaucaTheme.BLANCO);

        if (isDocente) {
            JButton createButton = UnicaucaTheme.createPrimaryButton("Crear Formato A");
            createButton.addActionListener(e -> openCreateFormatoDialog());
            topPanel.add(createButton);
        }

        JButton refreshButton = UnicaucaTheme.createSecondaryButton("Actualizar");
        refreshButton.addActionListener(e -> loadFormatos());
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        // Tabla de formatos (sin columna de acciones)
        String[] columns = {"ID", "Título", "Modalidad", "Director", "Estado", "Intento", "Fecha"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        formatosTable = new JTable(tableModel);
        formatosTable.setRowHeight(28);
        formatosTable.setFont(UnicaucaTheme.FUENTE_NORMAL);
        formatosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        formatosTable.getTableHeader().setBackground(UnicaucaTheme.AZUL_UNICAUCA);
        formatosTable.getTableHeader().setForeground(UnicaucaTheme.BLANCO);
        formatosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        formatosTable.setSelectionBackground(UnicaucaTheme.AZUL_CLARO);

        JScrollPane scrollPane = new JScrollPane(formatosTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(UnicaucaTheme.GRIS_CLARO);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton verDetalleBtn = UnicaucaTheme.createSecondaryButton("Ver Detalle");
        verDetalleBtn.addActionListener(e -> verDetalleSeleccionado());
        actionPanel.add(verDetalleBtn);

        if (isCoordinador) {
            JButton evaluarBtn = UnicaucaTheme.createPrimaryButton("Evaluar");
            evaluarBtn.addActionListener(e -> evaluarSeleccionado());
            actionPanel.add(evaluarBtn);
        }

        if (isDocente) {
            JButton nuevaVersionBtn = UnicaucaTheme.createSecondaryButton("Nueva Versión");
            nuevaVersionBtn.addActionListener(e -> crearNuevaVersionSeleccionado());
            actionPanel.add(nuevaVersionBtn);
        }

        add(actionPanel, BorderLayout.SOUTH);
    }

    private void loadFormatos() {
        tableModel.setRowCount(0);

        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                if (isDocente) {
                    return formatoService.obtenerFormatosPorDocente(AppConfig.getUserId());
                } else if (isCoordinador) {
                    return formatoService.obtenerFormatosPendientes();
                }
                return List.of();
            }

            @Override
            protected void done() {
                try {
                    formatosList = get();
                    for (Map<String, Object> formato : formatosList) {
                        Object[] row = {
                            formato.get("id"),
                            formato.get("titulo"),
                            formato.get("modalidad"),
                            formato.get("director"),
                            formatearEstado((String) formato.get("estado")),
                            formato.get("numeroIntento"),
                            formato.get("fechaPresentacion")
                        };
                        tableModel.addRow(row);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(FormatoPanel.this,
                        "Error al cargar formatos: " + e.getMessage(),
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
            case "EN_PRIMERA_EVALUACION" -> "1ra Evaluación";
            case "EN_SEGUNDA_EVALUACION" -> "2da Evaluación";
            case "EN_TERCERA_EVALUACION" -> "3ra Evaluación";
            case "RECHAZADO_PRIMERA_EVALUACION" -> "Rechazado (1er intento) - Requiere nueva versión";
            case "RECHAZADO_SEGUNDA_EVALUACION" -> "Rechazado (2do intento) - Requiere nueva versión";
            case "ACEPTADO" -> "Aceptado";
            case "RECHAZADO" -> "Rechazado Definitivamente";
            default -> estado;
        };
    }

    private Map<String, Object> getFormatoSeleccionado() {
        int selectedRow = formatosTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione un formato de la tabla.",
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return formatosList.get(selectedRow);
    }

    private void verDetalleSeleccionado() {
        Map<String, Object> formato = getFormatoSeleccionado();
        if (formato != null) {
            showFormatoDetail(formato);
        }
    }

    private void evaluarSeleccionado() {
        Map<String, Object> formato = getFormatoSeleccionado();
        if (formato != null) {
            String estado = (String) formato.get("estado");
            if (estado != null && (estado.contains("EVALUACION") || "PENDIENTE".equals(estado))) {
                openEvaluarDialog(formato);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Este formato ya fue evaluado definitivamente y no puede ser evaluado nuevamente.",
                    "No disponible",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void crearNuevaVersionSeleccionado() {
        Map<String, Object> formato = getFormatoSeleccionado();
        if (formato != null) {
            String estado = (String) formato.get("estado");
            if ("ACEPTADO".equals(estado)) {
                JOptionPane.showMessageDialog(this,
                    "No se puede crear una nueva versión de un formato ya aprobado.",
                    "No disponible",
                    JOptionPane.WARNING_MESSAGE);
            } else if ("RECHAZADO".equals(estado)) {
                JOptionPane.showMessageDialog(this,
                    "Este formato fue rechazado definitivamente. Debe crear un nuevo Formato A.",
                    "No disponible",
                    JOptionPane.WARNING_MESSAGE);
            } else if (estado != null && estado.startsWith("EN_")) {
                // Estado en evaluación - no puede crear nueva versión
                JOptionPane.showMessageDialog(this,
                    "No puede crear una nueva versión mientras el Formato A está pendiente de evaluación.\nEspere a que el coordinador lo evalúe.",
                    "No disponible",
                    JOptionPane.WARNING_MESSAGE);
            } else if (estado != null && estado.startsWith("RECHAZADO_")) {
                // Estado rechazado temporalmente - puede crear nueva versión
                openNuevaVersionDialog(formato);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se puede crear una nueva versión para este formato.",
                    "No disponible",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void openCreateFormatoDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Crear Formato A - Inicio de Trabajo de Grado", true);
        dialog.setSize(750, 650);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Información del docente
        JLabel infoLabel = new JLabel("Docente solicitante: " + AppConfig.getUserName() + " (ID: " + AppConfig.getUserId() + ")");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(infoLabel, gbc);
        gbc.gridwidth = 1;

        // Campos del formulario
        JTextField tituloField = new JTextField(30);
        JComboBox<String> modalidadCombo = new JComboBox<>(new String[]{
            "INVESTIGACION", "PRACTICA_PROFESIONAL"
        });
        JTextField directorField = new JTextField(30);
        JTextField codirectorField = new JTextField(30);
        JTextArea objetivoGeneralArea = new JTextArea(4, 30);
        JTextArea objetivosEspecificosArea = new JTextArea(6, 30);
        JTextField archivoUrlField = new JTextField(30);

        objetivoGeneralArea.setLineWrap(true);
        objetivoGeneralArea.setWrapStyleWord(true);
        objetivosEspecificosArea.setLineWrap(true);
        objetivosEspecificosArea.setWrapStyleWord(true);

        // Agregar campos con etiquetas descriptivas
        addFormField(panel, gbc, 1, "Título del Proyecto:", tituloField);
        addFormField(panel, gbc, 2, "Modalidad:", modalidadCombo);
        addFormField(panel, gbc, 3, "Director del Proyecto:", directorField);
        addFormField(panel, gbc, 4, "Codirector (opcional):", codirectorField);
        addFormField(panel, gbc, 5, "Objetivo General:", new JScrollPane(objetivoGeneralArea));
        addFormField(panel, gbc, 6, "Objetivos Específicos:", new JScrollPane(objetivosEspecificosArea));
        addFormField(panel, gbc, 7, "URL del Archivo PDF:", archivoUrlField);

        // Nota para práctica profesional
        JLabel notaLabel = new JLabel("<html><i>Nota: Si es Práctica Profesional, el PDF debe incluir carta de aceptación de la empresa.</i></html>");
        notaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(notaLabel, gbc);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = UnicaucaTheme.createPrimaryButton("Enviar Formato A");
        JButton cancelButton = UnicaucaTheme.createSecondaryButton("Cancelar");

        saveButton.addActionListener(e -> {
            // Validaciones
            if (tituloField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Por favor ingrese el título del proyecto.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (directorField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Por favor ingrese el nombre del director del proyecto.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (objetivoGeneralArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Por favor ingrese el objetivo general.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Map<String, Object> formatoData = new HashMap<>();
            formatoData.put("titulo", tituloField.getText().trim());
            formatoData.put("modalidad", modalidadCombo.getSelectedItem());
            formatoData.put("director", directorField.getText().trim());
            formatoData.put("codirector", codirectorField.getText().trim());
            formatoData.put("objetivoGeneral", objetivoGeneralArea.getText().trim());
            formatoData.put("objetivosEspecificos", objetivosEspecificosArea.getText().trim());
            formatoData.put("archivoUrl", archivoUrlField.getText().trim());
            formatoData.put("fechaPresentacion", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            formatoData.put("docenteId", AppConfig.getUserId());

            try {
                formatoService.crearFormato(formatoData);
                JOptionPane.showMessageDialog(dialog,
                    "Formato A enviado exitosamente.\nEl coordinador será notificado para su evaluación.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadFormatos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error al crear formato: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(panel);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private void openEvaluarDialog(Map<String, Object> formato) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Evaluar Formato A", true);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Información del formato
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información del Formato A"));
        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(new JLabel(String.valueOf(formato.get("id"))));
        infoPanel.add(new JLabel("Título:"));
        infoPanel.add(new JLabel((String) formato.get("titulo")));
        infoPanel.add(new JLabel("Director:"));
        infoPanel.add(new JLabel((String) formato.get("director")));
        infoPanel.add(new JLabel("Modalidad:"));
        infoPanel.add(new JLabel((String) formato.get("modalidad")));

        panel.add(infoPanel, BorderLayout.NORTH);

        // Decisión y observaciones
        JPanel evalPanel = new JPanel(new GridBagLayout());
        evalPanel.setBorder(BorderFactory.createTitledBorder("Evaluación"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JComboBox<String> decisionCombo = new JComboBox<>(new String[]{"APROBADO", "RECHAZADO"});
        JTextArea observacionesArea = new JTextArea(8, 30);
        observacionesArea.setLineWrap(true);
        observacionesArea.setWrapStyleWord(true);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        evalPanel.add(new JLabel("Decisión:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        evalPanel.add(decisionCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        evalPanel.add(new JLabel("Observaciones:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        evalPanel.add(new JScrollPane(observacionesArea), gbc);

        panel.add(evalPanel, BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = UnicaucaTheme.createPrimaryButton("Guardar Evaluación");
        JButton cancelButton = UnicaucaTheme.createSecondaryButton("Cancelar");

        saveButton.addActionListener(e -> {
            Map<String, Object> evaluacionData = new HashMap<>();
            evaluacionData.put("formatoAId", formato.get("id"));
            evaluacionData.put("coordinadorId", AppConfig.getUserId());
            evaluacionData.put("resultado", decisionCombo.getSelectedItem());
            evaluacionData.put("observaciones", observacionesArea.getText());

            try {
                formatoService.evaluarFormato(evaluacionData);
                JOptionPane.showMessageDialog(dialog,
                    "Evaluación guardada exitosamente.\nEl docente será notificado.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadFormatos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error al guardar evaluación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void openNuevaVersionDialog(Map<String, Object> formatoOriginal) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Crear Nueva Versión del Formato A", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Info
        JLabel infoLabel = new JLabel("Creando nueva versión del Formato A ID: " + formatoOriginal.get("id"));
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(infoLabel, gbc);
        gbc.gridwidth = 1;

        // Campos prellenados
        JTextField tituloField = new JTextField((String) formatoOriginal.get("titulo"), 30);
        JTextField directorField = new JTextField((String) formatoOriginal.get("director"), 30);
        JTextField codirectorField = new JTextField((String) formatoOriginal.getOrDefault("codirector", ""), 30);
        JTextArea objetivoGeneralArea = new JTextArea((String) formatoOriginal.getOrDefault("objetivoGeneral", ""), 4, 30);
        JTextArea objetivosEspecificosArea = new JTextArea((String) formatoOriginal.getOrDefault("objetivosEspecificos", ""), 6, 30);
        JTextField archivoUrlField = new JTextField(30);

        objetivoGeneralArea.setLineWrap(true);
        objetivosEspecificosArea.setLineWrap(true);

        addFormField(panel, gbc, 1, "Título:", tituloField);
        addFormField(panel, gbc, 2, "Director:", directorField);
        addFormField(panel, gbc, 3, "Codirector:", codirectorField);
        addFormField(panel, gbc, 4, "Objetivo General:", new JScrollPane(objetivoGeneralArea));
        addFormField(panel, gbc, 5, "Objetivos Específicos:", new JScrollPane(objetivosEspecificosArea));
        addFormField(panel, gbc, 6, "Nueva URL del Archivo PDF:", archivoUrlField);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = UnicaucaTheme.createPrimaryButton("Enviar Nueva Versión");
        JButton cancelButton = UnicaucaTheme.createSecondaryButton("Cancelar");

        saveButton.addActionListener(e -> {
            Map<String, Object> nuevoFormato = new HashMap<>();
            nuevoFormato.put("titulo", tituloField.getText());
            nuevoFormato.put("modalidad", formatoOriginal.get("modalidad"));
            nuevoFormato.put("director", directorField.getText());
            nuevoFormato.put("codirector", codirectorField.getText());
            nuevoFormato.put("objetivoGeneral", objetivoGeneralArea.getText());
            nuevoFormato.put("objetivosEspecificos", objetivosEspecificosArea.getText());
            nuevoFormato.put("archivoUrl", archivoUrlField.getText());
            nuevoFormato.put("fechaPresentacion", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            nuevoFormato.put("docenteId", AppConfig.getUserId());

            try {
                Long id = ((Number) formatoOriginal.get("id")).longValue();
                formatoService.crearNuevaVersion(id, nuevoFormato);
                JOptionPane.showMessageDialog(dialog,
                    "Nueva versión enviada exitosamente.\nEl coordinador será notificado.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadFormatos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private void showFormatoDetail(Map<String, Object> formato) {
        // Obtener información actualizada del servidor
        try {
            Long formatoId = ((Number) formato.get("id")).longValue();
            Map<String, Object> formatoActualizado = formatoService.obtenerFormato(formatoId);
            formato = formatoActualizado; // Usar la información actualizada
        } catch (Exception e) {
            // Si falla, usar la información de la lista local
            System.err.println("No se pudo obtener información actualizada: " + e.getMessage());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("         DETALLE DEL FORMATO A\n");
        sb.append("==========================================\n\n");
        sb.append("ID: ").append(formato.get("id")).append("\n");
        sb.append("Título: ").append(formato.get("titulo")).append("\n");
        sb.append("Modalidad: ").append(formato.get("modalidad")).append("\n");
        sb.append("Director: ").append(formato.get("director")).append("\n");

        Object codirector = formato.get("codirector");
        if (codirector != null && !codirector.toString().isEmpty()) {
            sb.append("Codirector: ").append(codirector).append("\n");
        }

        sb.append("\n--- Estado del Proceso ---\n");
        String estado = (String) formato.get("estado");
        sb.append("Estado: ").append(formatearEstado(estado)).append("\n");

        Object numeroIntento = formato.get("numeroIntento");
        if (numeroIntento != null) {
            sb.append("Número de Intento: ").append(numeroIntento).append(" de 3\n");
        }

        sb.append("Fecha de Presentación: ").append(formato.get("fechaPresentacion")).append("\n");

        Object objetivoGeneral = formato.get("objetivoGeneral");
        if (objetivoGeneral != null && !objetivoGeneral.toString().isEmpty()) {
            sb.append("\n--- Objetivo General ---\n");
            sb.append(objetivoGeneral).append("\n");
        }

        Object objetivosEspecificos = formato.get("objetivosEspecificos");
        if (objetivosEspecificos != null && !objetivosEspecificos.toString().isEmpty()) {
            sb.append("\n--- Objetivos Específicos ---\n");
            sb.append(objetivosEspecificos).append("\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setRows(20);
        textArea.setColumns(50);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Detalle del Formato A", JOptionPane.INFORMATION_MESSAGE);
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

