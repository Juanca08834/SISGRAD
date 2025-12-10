package com.unicauca.desktop.ui;

import com.unicauca.desktop.config.AppConfig;
import com.unicauca.desktop.service.AuthService;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de la aplicación con diseño institucional Universidad del Cauca
 */
public class MainFrame extends JFrame {

    private final AuthService authService;
    private JTabbedPane tabbedPane;

    public MainFrame() {
        this.authService = new AuthService();
        initComponents();
    }

    private void initComponents() {
        setTitle(AppConfig.APP_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // Panel principal con fondo blanco
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(UnicaucaTheme.BLANCO);

        // Panel superior con información del usuario (estilo institucional)
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel de pestañas con estilo personalizado
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UnicaucaTheme.FUENTE_NORMAL);
        tabbedPane.setBackground(UnicaucaTheme.BLANCO);
        tabbedPane.setForeground(UnicaucaTheme.AZUL_UNICAUCA);

        // Agregar pestañas según el rol del usuario
        String userRole = AppConfig.getUserRole();

        // Panel de inicio (para todos)
        tabbedPane.addTab("  Inicio  ", createHomePanel());

        // Panel de Formatos A (para DOCENTE y COORDINADOR)
        if ("DOCENTE".equals(userRole) || "COORDINADOR".equals(userRole)) {
            tabbedPane.addTab("  Formatos A  ", new FormatoPanel());
        }

        // Panel de Anteproyectos (para DOCENTE y JEFE_DEPARTAMENTO)
        if ("DOCENTE".equals(userRole) || "JEFE_DEPARTAMENTO".equals(userRole)) {
            tabbedPane.addTab("  Anteproyectos  ", new AnteproyectoPanel());
        }

        // Panel de Estado de Proyecto (para ESTUDIANTE)
        if ("ESTUDIANTE".equals(userRole)) {
            tabbedPane.addTab("  Estado del Proyecto  ", new EstudiantePanel());
        }

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Footer con línea roja
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(UnicaucaTheme.ROJO_UNICAUCA);
        footerPanel.setPreferredSize(new Dimension(getWidth(), 5));
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(UnicaucaTheme.AZUL_UNICAUCA);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // Panel izquierdo con logo e información
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        // Logo pequeño
        ImageIcon logo = UnicaucaTheme.loadLogo(40, 40);
        if (logo != null) {
            JLabel logoLabel = new JLabel(logo);
            leftPanel.add(logoLabel);
        }

        // Información del usuario
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);

        JLabel userLabel = new JLabel(AppConfig.getUserName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(UnicaucaTheme.BLANCO);

        JLabel roleLabel = new JLabel(formatRole(AppConfig.getUserRole()));
        roleLabel.setFont(UnicaucaTheme.FUENTE_SMALL);
        roleLabel.setForeground(new Color(180, 200, 220));

        userInfoPanel.add(userLabel);
        userInfoPanel.add(roleLabel);

        leftPanel.add(userInfoPanel);

        // Botón de cerrar sesión (estilo rojo)
        JButton logoutButton = UnicaucaTheme.createDangerButton("Cerrar Sesión");
        logoutButton.setPreferredSize(new Dimension(130, 35));
        logoutButton.addActionListener(e -> handleLogout());

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    private String formatRole(String role) {
        return switch (role) {
            case "DOCENTE" -> "Docente";
            case "COORDINADOR" -> "Coordinador de Programa";
            case "JEFE_DEPARTAMENTO" -> "Jefe de Departamento";
            case "ESTUDIANTE" -> "Estudiante";
            default -> role;
        };
    }

    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BorderLayout());
        homePanel.setBackground(UnicaucaTheme.BLANCO);
        homePanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Panel de bienvenida
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(UnicaucaTheme.BLANCO);

        // Logo grande centrado
        JPanel logoPanel = UnicaucaTheme.createLogoPanel(120, 120);
        logoPanel.setBackground(UnicaucaTheme.BLANCO);
        welcomePanel.add(logoPanel);
        welcomePanel.add(Box.createVerticalStrut(20));

        JLabel welcomeLabel = new JLabel("Bienvenido al Sistema de Gestión");
        welcomeLabel.setFont(UnicaucaTheme.FUENTE_TITULO);
        welcomeLabel.setForeground(UnicaucaTheme.AZUL_UNICAUCA);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomePanel.add(welcomeLabel);

        welcomePanel.add(Box.createVerticalStrut(5));

        JLabel subtitleLabel = new JLabel("Trabajos de Grado - Universidad del Cauca");
        subtitleLabel.setFont(UnicaucaTheme.FUENTE_SUBTITULO);
        subtitleLabel.setForeground(UnicaucaTheme.TEXTO_GRIS);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomePanel.add(subtitleLabel);

        welcomePanel.add(Box.createVerticalStrut(10));

        JLabel infoLabel = new JLabel("Versión " + AppConfig.APP_VERSION);
        infoLabel.setFont(UnicaucaTheme.FUENTE_SMALL);
        infoLabel.setForeground(UnicaucaTheme.TEXTO_GRIS);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomePanel.add(infoLabel);

        welcomePanel.add(Box.createVerticalStrut(40));

        // Panel de instrucciones con estilo de tarjeta
        JPanel instructionsCard = new JPanel();
        instructionsCard.setLayout(new BoxLayout(instructionsCard, BoxLayout.Y_AXIS));
        instructionsCard.setBackground(UnicaucaTheme.GRIS_CLARO);
        instructionsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UnicaucaTheme.GRIS_BORDE, 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        instructionsCard.setMaximumSize(new Dimension(600, 300));
        instructionsCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instructionsTitle = new JLabel("¿Qué puedes hacer?");
        instructionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        instructionsTitle.setForeground(UnicaucaTheme.AZUL_UNICAUCA);
        instructionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionsCard.add(instructionsTitle);
        instructionsCard.add(Box.createVerticalStrut(15));

        String role = AppConfig.getUserRole();
        String[] instructions = getInstructionsByRole(role);

        for (String instruction : instructions) {
            JLabel label = new JLabel("• " + instruction);
            label.setFont(UnicaucaTheme.FUENTE_NORMAL);
            label.setForeground(UnicaucaTheme.TEXTO_OSCURO);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            instructionsCard.add(label);
            instructionsCard.add(Box.createVerticalStrut(10));
        }

        welcomePanel.add(instructionsCard);

        homePanel.add(welcomePanel, BorderLayout.CENTER);

        return homePanel;
    }

    private String[] getInstructionsByRole(String role) {
        return switch (role) {
            case "DOCENTE" -> new String[]{
                    "En la pestaña 'Formatos A' puede crear y ver sus formatos de trabajo de grado",
                    "Una vez aprobado un Formato A, puede subir el anteproyecto en la pestaña 'Anteproyectos'",
                    "Puede ver el estado de sus formatos y anteproyectos en cualquier momento"
            };
            case "COORDINADOR" -> new String[]{
                    "En la pestaña 'Formatos A' puede evaluar los formatos pendientes",
                    "Puede aprobar o rechazar formatos con observaciones",
                    "Los docentes pueden crear hasta 3 versiones de un formato rechazado"
            };
            case "JEFE_DEPARTAMENTO" -> new String[]{
                    "En la pestaña 'Anteproyectos' puede ver todos los anteproyectos subidos",
                    "Puede asignar 2 evaluadores a cada anteproyecto",
                    "Los evaluadores serán notificados automáticamente"
            };
            case "ESTUDIANTE" -> new String[]{
                    "En la pestaña 'Estado del Proyecto' puede ver el estado actual de su trabajo de grado",
                    "Puede consultar el estado del Formato A (en evaluación, aprobado o rechazado)",
                    "Puede ver si el anteproyecto ha sido subido y su estado de evaluación"
            };
            default -> new String[]{"Bienvenido al sistema"};
        };
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cerrar sesión?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        }
    }
}

