package com.unicauca.desktop.ui;

import com.unicauca.desktop.config.AppConfig;
import com.unicauca.desktop.service.AuthService;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de inicio de sesión con diseño institucional Universidad del Cauca
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        this.authService = new AuthService();
        initComponents();
    }

    private void initComponents() {
        setTitle(AppConfig.APP_NAME + " - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con fondo blanco
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBackground(UnicaucaTheme.BLANCO);

        // ========== HEADER AZUL CON LOGO ==========
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UnicaucaTheme.AZUL_UNICAUCA);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // Logo de la universidad
        JPanel logoPanel = UnicaucaTheme.createLogoPanel(80, 80);
        logoPanel.setOpaque(false);
        headerPanel.add(logoPanel);
        headerPanel.add(Box.createVerticalStrut(15));

        // Título en blanco
        JLabel titleLabel = new JLabel("Universidad del Cauca");
        titleLabel.setFont(UnicaucaTheme.FUENTE_TITULO);
        titleLabel.setForeground(UnicaucaTheme.BLANCO);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        headerPanel.add(Box.createVerticalStrut(5));

        JLabel subtitleLabel = new JLabel("Sistema de Gestión de Trabajos de Grado");
        subtitleLabel.setFont(UnicaucaTheme.FUENTE_SMALL);
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitleLabel);

        // ========== CONTENIDO (FORMULARIO) ==========
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UnicaucaTheme.BLANCO);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Título del formulario
        JLabel formTitle = new JLabel("Iniciar Sesión");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(UnicaucaTheme.AZUL_UNICAUCA);
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(formTitle);
        contentPanel.add(Box.createVerticalStrut(25));

        // Panel del formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(UnicaucaTheme.BLANCO);
        formPanel.setMaximumSize(new Dimension(400, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel emailLabel = new JLabel("Correo institucional");
        emailLabel.setFont(UnicaucaTheme.FUENTE_NORMAL);
        emailLabel.setForeground(UnicaucaTheme.TEXTO_OSCURO);
        formPanel.add(emailLabel, gbc);

        gbc.gridy = 1;
        emailField = new JTextField(25);
        UnicaucaTheme.styleTextField(emailField);
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 5, 8, 5);
        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setFont(UnicaucaTheme.FUENTE_NORMAL);
        passwordLabel.setForeground(UnicaucaTheme.TEXTO_OSCURO);
        formPanel.add(passwordLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(8, 5, 8, 5);
        passwordField = new JPasswordField(25);
        UnicaucaTheme.stylePasswordField(passwordField);
        formPanel.add(passwordField, gbc);

        contentPanel.add(formPanel);
        contentPanel.add(Box.createVerticalStrut(25));

        // ========== BOTONES ==========
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(UnicaucaTheme.BLANCO);
        buttonPanel.setMaximumSize(new Dimension(400, 50));

        loginButton = UnicaucaTheme.createPrimaryButton("Iniciar Sesión");
        loginButton.addActionListener(e -> handleLogin());

        JButton registerButton = UnicaucaTheme.createSecondaryButton("Registrarse");
        registerButton.addActionListener(e -> openRegisterWindow());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        contentPanel.add(buttonPanel);

        // ========== FOOTER CON LÍNEA ROJA ==========
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(UnicaucaTheme.ROJO_UNICAUCA);
        footerPanel.setPreferredSize(new Dimension(getWidth(), 8));

        // Agregar paneles al panel principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Enter para login
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor ingrese email y contraseña",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mostrar indicador de carga
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Ejecutar en hilo separado para no bloquear UI
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authService.login(email, password);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                            "Bienvenido " + AppConfig.getUserName(),
                            "Login Exitoso",
                            JOptionPane.INFORMATION_MESSAGE);
                        openMainWindow();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "Error: " + e.getCause().getMessage(),
                        "Error de Login",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void openRegisterWindow() {
        RegisterFrame registerFrame = new RegisterFrame(this);
        registerFrame.setVisible(true);
        this.setVisible(false);
    }

    private void openMainWindow() {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        this.dispose();
    }
}

