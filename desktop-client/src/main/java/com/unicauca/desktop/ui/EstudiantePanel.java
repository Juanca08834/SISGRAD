package com.unicauca.desktop.ui;

import com.unicauca.desktop.config.AppConfig;
import com.unicauca.desktop.service.FormatoService;
import com.unicauca.desktop.service.AnteproyectoService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Panel para estudiantes - Ver estado del proyecto de grado
 * Requisito 5: Yo como estudiante necesito entrar a la plataforma y
 * ver el estado de mi proyecto de grado.
 */
public class EstudiantePanel extends JPanel {

    private final FormatoService formatoService;
    private final AnteproyectoService anteproyectoService;
    private JTable formatosTable;
    private DefaultTableModel formatosTableModel;
    private JTable anteproyectosTable;
    private DefaultTableModel anteproyectosTableModel;
    private JPanel estadoProyectoPanel;
    private JLabel estadoGeneralLabel;

    public EstudiantePanel() {
        this.formatoService = new FormatoService();
        this.anteproyectoService = new AnteproyectoService();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UnicaucaTheme.BLANCO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ========== HEADER ==========
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UnicaucaTheme.BLANCO);

        JLabel titleLabel = new JLabel("Estado de mi Proyecto de Grado");
        titleLabel.setFont(UnicaucaTheme.FUENTE_TITULO);
        titleLabel.setForeground(UnicaucaTheme.AZUL_UNICAUCA);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshButton = UnicaucaTheme.createSecondaryButton("Actualizar");
        refreshButton.addActionListener(e -> loadData());
        headerPanel.add(refreshButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ========== CONTENIDO PRINCIPAL ==========
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UnicaucaTheme.BLANCO);

        // Panel de estado general
        estadoProyectoPanel = createEstadoGeneralPanel();
        contentPanel.add(estadoProyectoPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Panel de Formatos A
        JPanel formatosPanel = createFormatosPanel();
        contentPanel.add(formatosPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Panel de Anteproyectos
        JPanel anteproyectosPanel = createAnteproyectosPanel();
        contentPanel.add(anteproyectosPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // ========== LEYENDA ==========
        JPanel legendPanel = createLegendPanel();
        add(legendPanel, BorderLayout.SOUTH);
    }

    private JPanel createEstadoGeneralPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UnicaucaTheme.GRIS_CLARO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UnicaucaTheme.GRIS_BORDE, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel iconLabel = new JLabel("Estado:");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("Estado General del Proyecto");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerLabel.setForeground(UnicaucaTheme.TEXTO_OSCURO);

        estadoGeneralLabel = new JLabel("Cargando...");
        estadoGeneralLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        estadoGeneralLabel.setForeground(UnicaucaTheme.AZUL_UNICAUCA);

        textPanel.add(headerLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(estadoGeneralLabel);

        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormatosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UnicaucaTheme.BLANCO);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UnicaucaTheme.AZUL_UNICAUCA, 1),
            "Formatos A - Estado del Proceso",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13),
            UnicaucaTheme.AZUL_UNICAUCA
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        String[] columns = {"ID", "Título", "Modalidad", "Estado", "Intento", "Fecha"};
        formatosTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        formatosTable = new JTable(formatosTableModel);
        formatosTable.setRowHeight(28);
        formatosTable.setFont(UnicaucaTheme.FUENTE_NORMAL);
        formatosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        formatosTable.getTableHeader().setBackground(UnicaucaTheme.AZUL_UNICAUCA);
        formatosTable.getTableHeader().setForeground(UnicaucaTheme.BLANCO);
        formatosTable.setSelectionBackground(UnicaucaTheme.AZUL_CLARO);

        JScrollPane tableScroll = new JScrollPane(formatosTable);
        tableScroll.setPreferredSize(new Dimension(0, 150));
        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAnteproyectosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UnicaucaTheme.BLANCO);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UnicaucaTheme.ROJO_UNICAUCA, 1),
            "Anteproyectos - Estado del Proceso",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13),
            UnicaucaTheme.ROJO_UNICAUCA
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        String[] columns = {"ID", "Título", "Estado", "Evaluadores", "Fecha"};
        anteproyectosTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        anteproyectosTable = new JTable(anteproyectosTableModel);
        anteproyectosTable.setRowHeight(28);
        anteproyectosTable.setFont(UnicaucaTheme.FUENTE_NORMAL);
        anteproyectosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        anteproyectosTable.getTableHeader().setBackground(UnicaucaTheme.ROJO_UNICAUCA);
        anteproyectosTable.getTableHeader().setForeground(UnicaucaTheme.BLANCO);
        anteproyectosTable.setSelectionBackground(new Color(255, 230, 230));

        JScrollPane tableScroll = new JScrollPane(anteproyectosTable);
        tableScroll.setPreferredSize(new Dimension(0, 150));
        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(UnicaucaTheme.GRIS_CLARO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UnicaucaTheme.GRIS_BORDE),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel legendTitle = new JLabel("Estados posibles:");
        legendTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        panel.add(legendTitle);

        addLegendItem(panel, "[*]", "En evaluación");
        addLegendItem(panel, "[OK]", "Aceptado");
        addLegendItem(panel, "[X]", "Rechazado");
        addLegendItem(panel, "[?]", "Pendiente asignación");

        return panel;
    }

    private void addLegendItem(JPanel panel, String icon, String text) {
        JLabel label = new JLabel(icon + " " + text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(UnicaucaTheme.TEXTO_OSCURO);
        panel.add(label);
    }

    private void loadData() {
        // Limpiar tablas
        formatosTableModel.setRowCount(0);
        anteproyectosTableModel.setRowCount(0);
        estadoGeneralLabel.setText("Cargando...");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private List<Map<String, Object>> formatos;
            private List<Map<String, Object>> anteproyectos;
            private String estadoGeneral = "Sin proyectos registrados";

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    formatos = formatoService.obtenerTodosLosFormatos();
                } catch (Exception e) {
                    formatos = List.of();
                }

                try {
                    anteproyectos = anteproyectoService.obtenerTodosAnteproyectos();
                } catch (Exception e) {
                    anteproyectos = List.of();
                }

                // Determinar estado general
                estadoGeneral = determinarEstadoGeneral(formatos, anteproyectos);

                return null;
            }

            @Override
            protected void done() {
                try {
                    // Cargar Formatos A
                    if (formatos != null) {
                        for (Map<String, Object> formato : formatos) {
                            Object[] row = {
                                formato.get("id"),
                                formato.get("titulo"),
                                formato.get("modalidad"),
                                formatearEstado((String) formato.get("estado")),
                                formato.get("numeroIntento"),
                                formato.get("fechaPresentacion")
                            };
                            formatosTableModel.addRow(row);
                        }
                    }

                    // Cargar Anteproyectos
                    if (anteproyectos != null) {
                        for (Map<String, Object> anteproyecto : anteproyectos) {
                            String evaluadores = obtenerEvaluadores(anteproyecto);
                            Object[] row = {
                                anteproyecto.get("id"),
                                anteproyecto.get("titulo"),
                                formatearEstado((String) anteproyecto.get("estado")),
                                evaluadores,
                                anteproyecto.get("fechaCreacion")
                            };
                            anteproyectosTableModel.addRow(row);
                        }
                    }

                    // Actualizar estado general
                    estadoGeneralLabel.setText(estadoGeneral);

                } catch (Exception e) {
                    estadoGeneralLabel.setText("Error al cargar datos");
                    JOptionPane.showMessageDialog(EstudiantePanel.this,
                        "Error al cargar los datos: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private String determinarEstadoGeneral(List<Map<String, Object>> formatos,
                                           List<Map<String, Object>> anteproyectos) {
        if (formatos == null || formatos.isEmpty()) {
            return "No hay proyectos registrados";
        }

        // Buscar el formato más reciente
        Map<String, Object> ultimoFormato = formatos.get(formatos.size() - 1);
        String estadoFormato = (String) ultimoFormato.get("estado");

        if ("RECHAZADO".equals(estadoFormato)) {
            return "[X] Formato A rechazado definitivamente";
        } else if ("ACEPTADO".equals(estadoFormato)) {
            // Verificar estado del anteproyecto
            if (anteproyectos != null && !anteproyectos.isEmpty()) {
                Map<String, Object> ultimoAnteproyecto = anteproyectos.get(anteproyectos.size() - 1);
                String estadoAnteproyecto = (String) ultimoAnteproyecto.get("estado");

                if ("PENDIENTE_ASIGNACION".equals(estadoAnteproyecto)) {
                    return "[?] Anteproyecto pendiente de asignación de evaluadores";
                } else if ("EN_EVALUACION".equals(estadoAnteproyecto)) {
                    return "[*] Anteproyecto en evaluación";
                } else if ("APROBADO".equals(estadoAnteproyecto)) {
                    return "[OK] Anteproyecto aprobado";
                }
            }
            return "[OK] Formato A aprobado - Pendiente subir anteproyecto";
        } else if (estadoFormato != null && estadoFormato.contains("EVALUACION")) {
            Integer intento = (Integer) ultimoFormato.get("numeroIntento");
            return "[*] En evaluación de Formato A (Intento " + (intento != null ? intento : 1) + " de 3)";
        }

        return "Proyecto en proceso";
    }

    private String formatearEstado(String estado) {
        if (estado == null) return "Desconocido";

        return switch (estado) {
            case "EN_PRIMERA_EVALUACION" -> "[*] 1ra Evaluación";
            case "EN_SEGUNDA_EVALUACION" -> "[*] 2da Evaluación";
            case "EN_TERCERA_EVALUACION" -> "[*] 3ra Evaluación";
            case "RECHAZADO_PRIMERA_EVALUACION" -> "[!] Rechazado 1er intento - Nueva versión requerida";
            case "RECHAZADO_SEGUNDA_EVALUACION" -> "[!] Rechazado 2do intento - Nueva versión requerida";
            case "ACEPTADO" -> "[OK] Aceptado";
            case "RECHAZADO" -> "[X] Rechazado Definitivamente";
            case "PENDIENTE" -> "[*] Pendiente";
            case "PENDIENTE_ASIGNACION" -> "[?] Pendiente Asignación";
            case "EN_EVALUACION" -> "[*] En Evaluación";
            case "APROBADO" -> "[OK] Aprobado";
            default -> estado;
        };
    }

    private String obtenerEvaluadores(Map<String, Object> anteproyecto) {
        StringBuilder sb = new StringBuilder();
        Object eval1 = anteproyecto.get("evaluador1Id");
        Object eval2 = anteproyecto.get("evaluador2Id");

        if (eval1 != null || eval2 != null) {
            if (eval1 != null) sb.append("Evaluador 1: Si");
            if (eval1 != null && eval2 != null) sb.append(" | ");
            if (eval2 != null) sb.append("Evaluador 2: Si");
        } else {
            sb.append("Sin asignar");
        }

        return sb.toString();
    }
}

