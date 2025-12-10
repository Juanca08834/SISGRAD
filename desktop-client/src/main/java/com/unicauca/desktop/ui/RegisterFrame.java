package com.unicauca.desktop.ui;

import com.unicauca.desktop.config.AppConfig;
import com.unicauca.desktop.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Ventana de registro de nuevos usuarios con diseño institucional
 */
public class RegisterFrame extends JFrame {

    private final AuthService authService;
    private final JFrame previousFrame;

    private JTextField nombresField;
    private JTextField apellidosField;
    private JTextField celularField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> programaCombo;
    private JComboBox<String> rolCombo;

    public RegisterFrame(JFrame previousFrame) {
        this.authService = new AuthService();
        this.previousFrame = previousFrame;
        initComponents();
    }

    private void initComponents() {
        setTitle(AppConfig.APP_NAME + " - Registro");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(550, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con fondo blanco
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBackground(UnicaucaTheme.BLANCO);

        // ========== HEADER AZUL ==========
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UnicaucaTheme.AZUL_UNICAUCA);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Logo
        JPanel logoPanel = UnicaucaTheme.createLogoPanel(60, 60);
        logoPanel.setOpaque(false);
        headerPanel.add(logoPanel);
        headerPanel.add(Box.createVerticalStrut(10));

        JLabel titleLabel = new JLabel("Registro de Usuario");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(UnicaucaTheme.BLANCO);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        // ========== CONTENIDO ==========
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UnicaucaTheme.BLANCO);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        // Panel de formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(UnicaucaTheme.BLANCO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        // Campos del formulario
        nombresField = new JTextField(20);
        apellidosField = new JTextField(20);
        celularField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

        programaCombo = new JComboBox<>(new String[]{
            "INGENIERIA_SISTEMAS",
            "INGENIERIA_ELECTRONICA",
            "AUTOMATICA_INDUSTRIAL",
            "TECNOLOGIA_TELEMATICA"
        });

        rolCombo = new JComboBox<>(new String[]{
                "ESTUDIANTE",
            "DOCENTE",
            "COORDINADOR",
            "JEFE_DEPARTAMENTO"
        });

        addStyledFormField(formPanel, gbc, 0, "Nombres *", nombresField);
        addStyledFormField(formPanel, gbc, 1, "Apellidos *", apellidosField);
        addStyledFormField(formPanel, gbc, 2, "Celular", celularField);
        addStyledFormField(formPanel, gbc, 3, "Email institucional *", emailField);
        addStyledFormField(formPanel, gbc, 4, "Programa", programaCombo);
        addStyledFormField(formPanel, gbc, 5, "Rol", rolCombo);
        addStyledFormField(formPanel, gbc, 6, "Contraseña *", passwordField);
        addStyledFormField(formPanel, gbc, 7, "Confirmar contraseña *", confirmPasswordField);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(UnicaucaTheme.BLANCO);
        scrollPane.getViewport().setBackground(UnicaucaTheme.BLANCO);
        contentPanel.add(scrollPane);

        contentPanel.add(Box.createVerticalStrut(20));

        // ========== BOTONES ==========
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(UnicaucaTheme.BLANCO);

        JButton registerButton = UnicaucaTheme.createPrimaryButton("Registrarse");
        registerButton.addActionListener(e -> handleRegister());

        JButton cancelButton = UnicaucaTheme.createSecondaryButton("Cancelar");
        cancelButton.addActionListener(e -> {
            previousFrame.setVisible(true);
            dispose();
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel);

        // ========== FOOTER ROJO ==========
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(UnicaucaTheme.ROJO_UNICAUCA);
        footerPanel.setPreferredSize(new Dimension(getWidth(), 8));

        // Agregar al panel principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addStyledFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(UnicaucaTheme.FUENTE_NORMAL);
        label.setForeground(UnicaucaTheme.TEXTO_OSCURO);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;

        if (field instanceof JTextField) {
            UnicaucaTheme.styleTextField((JTextField) field);
        } else if (field instanceof JPasswordField) {
            UnicaucaTheme.stylePasswordField((JPasswordField) field);
        } else if (field instanceof JComboBox) {
            UnicaucaTheme.styleComboBox((JComboBox<?>) field);
        }

        panel.add(field, gbc);
    }

    private void handleRegister() {
        // Validar campos
        String nombres = nombresField.getText().trim();
        String apellidos = apellidosField.getText().trim();
        String celular = celularField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String programa = (String) programaCombo.getSelectedItem();
        String rol = (String) rolCombo.getSelectedItem();

        // Validaciones
        if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor complete todos los campos obligatorios",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.endsWith("@unicauca.edu.co")) {
            JOptionPane.showMessageDialog(this,
                "Debe usar un email institucional (@unicauca.edu.co)",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "La contraseña debe tener al menos 6 caracteres",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar que tenga al menos un dígito
        if (!password.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this,
                "La contraseña debe tener al menos un dígito",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar que tenga al menos una mayúscula
        if (!password.matches(".*[A-Z].*")) {
            JOptionPane.showMessageDialog(this,
                "La contraseña debe tener al menos una letra mayúscula",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar que tenga al menos un carácter especial
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            JOptionPane.showMessageDialog(this,
                "La contraseña debe tener al menos un carácter especial (!@#$%^&*()_+-=[]{}|;':\",./<>?)",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Las contraseñas no coinciden",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear objeto de datos
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombres", nombres);
        userData.put("apellidos", apellidos);
        userData.put("celular", celular);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("programa", programa);
        userData.put("rol", rol);

        // Mostrar indicador de carga
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Ejecutar en hilo separado
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authService.register(userData);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Registro exitoso. Bienvenido " + AppConfig.getUserName(),
                            "Registro Exitoso",
                            JOptionPane.INFORMATION_MESSAGE);
                        openMainWindow();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                        "Error en el registro: " + e.getCause().getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void openMainWindow() {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        dispose();
    }
}

