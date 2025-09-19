package com.unicauca.proyectosofv1.ui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class DialogoRegistro extends JDialog {
    private final JTextField campoNombres = new JTextField();
    private final JTextField campoApellidos = new JTextField();
    private final JTextField campoCelular = new JTextField();
    private final JComboBox<String> comboPrograma = new JComboBox<>(new String[]{
            "Ingeniería de Sistemas",
            "Ingeniería Electrónica y Telecomunicaciones",
            "Automática industrial",
            "Tecnología en Telemática"
    });
    private final JComboBox<String> comboRol = new JComboBox<>(new String[]{"Estudiante", "Docente"});
    private final JTextField campoEmail = new JTextField();
    private final JPasswordField campoContrasenia = new JPasswordField();
    private final JPasswordField campoConfirmacion = new JPasswordField();

    private final JButton botonCancelar = new JButton("Cancelar");
    private final JButton botonRegistrar = new JButton("Registrar");
    private final JLabel etiquetaError = new JLabel(" ");
    private final JLabel etiquetaAyuda = new JLabel(
            "<html><small>Mín. 6 caracteres, con 1 dígito, 1 mayúscula y 1 caracter especial.</small></html>");

    private final AccionesRegistro acciones;

    public DialogoRegistro(Frame owner, AccionesRegistro acciones) {
        super(owner, "Registro de usuario", true);
        this.acciones = acciones;
        construirUI();
        conectarEventos();
        pack();
        setLocationRelativeTo(owner);
    }

    private void construirUI() {
        var contenedor = new JPanel(new GridBagLayout());
        contenedor.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JLabel titulo = new JLabel("Registro");
        titulo.setFont(titulo.getFont().deriveFont(titulo.getFont().getStyle() | java.awt.Font.BOLD, 18f));
        etiquetaError.setForeground(new Color(178, 34, 34));

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER;
        contenedor.add(titulo, c);

        c.gridy++;
        contenedor.add(etiquetaError, c);

        // Nombres
        c.gridwidth = 1; c.gridy++; c.gridx = 0; contenedor.add(new JLabel("Nombres*:"), c);
        c.gridx = 1; contenedor.add(campoNombres, c);

        // Apellidos
        c.gridy++; c.gridx = 0; contenedor.add(new JLabel("Apellidos*:"), c);
        c.gridx = 1; contenedor.add(campoApellidos, c);

        // Celular (opcional)
        c.gridy++; c.gridx = 0; contenedor.add(new JLabel("Celular (opcional):"), c);
        c.gridx = 1; contenedor.add(campoCelular, c);

        // Programa
        c.gridy++; c.gridx = 0; contenedor.add(new JLabel("Programa*:"), c);
        c.gridx = 1; contenedor.add(comboPrograma, c);

        // Rol
        c.gridy++; c.gridx = 0; contenedor.add(new JLabel("Rol*:"), c);
        c.gridx = 1; contenedor.add(comboRol, c);

        // Email
        c.gridy++; c.gridx = 0; contenedor.add(new JLabel("Email institucional*:"), c);
        c.gridx = 1; contenedor.add(campoEmail, c);

        // Contraseña
        c.gridy++; c.gridx = 0; contenedor.add(new JLabel("Contraseña*:"), c);
        c.gridx = 1; contenedor.add(campoContrasenia, c);

        // Confirmación
        c.gridy++; c.gridx = 0; contenedor.add(new JLabel("Confirmar contraseña*:"), c);
        c.gridx = 1; contenedor.add(campoConfirmacion, c);

        // Ayuda
        c.gridy++; c.gridx = 0; c.gridwidth = 2; contenedor.add(etiquetaAyuda, c);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.add(botonCancelar);
        panelBotones.add(botonRegistrar);
        c.gridy++; contenedor.add(panelBotones, c);

        setContentPane(contenedor);

        // Accesibilidad / Test IDs
        campoNombres.getAccessibleContext().setAccessibleName("nombresInput");
        campoApellidos.getAccessibleContext().setAccessibleName("apellidosInput");
        campoCelular.getAccessibleContext().setAccessibleName("celularInput");
        comboPrograma.getAccessibleContext().setAccessibleName("programaSelect");
        comboRol.getAccessibleContext().setAccessibleName("rolSelect");
        campoEmail.getAccessibleContext().setAccessibleName("emailInput");
        campoContrasenia.getAccessibleContext().setAccessibleName("passwordInput");
        campoConfirmacion.getAccessibleContext().setAccessibleName("confirmPasswordInput");

        botonRegistrar.setEnabled(false);
        setPreferredSize(new Dimension(520, 520));
    }

    private void conectarEventos() {
        botonCancelar.addActionListener(e -> {
            acciones.alCancelar();
            dispose();
        });

        botonRegistrar.addActionListener(e -> enviar());

        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validarFormulario(); }
            @Override public void removeUpdate(DocumentEvent e) { validarFormulario(); }
            @Override public void changedUpdate(DocumentEvent e) { validarFormulario(); }
        };
        campoNombres.getDocument().addDocumentListener(dl);
        campoApellidos.getDocument().addDocumentListener(dl);
        campoCelular.getDocument().addDocumentListener(dl);
        campoEmail.getDocument().addDocumentListener(dl);
        campoContrasenia.getDocument().addDocumentListener(dl);
        campoConfirmacion.getDocument().addDocumentListener(dl);

        // Enter envía
        campoConfirmacion.addActionListener(e -> enviar());

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                acciones.alCancelar();
            }
        });
    }

    private void enviar() {
        limpiarError();
        String nombres = campoNombres.getText().trim();
        String apellidos = campoApellidos.getText().trim();
        String celular = campoCelular.getText().trim();
        String programa = (String) comboPrograma.getSelectedItem();
        String rol = (String) comboRol.getSelectedItem();
        String email = campoEmail.getText().trim();
        char[] pass = campoContrasenia.getPassword();
        char[] confirm = campoConfirmacion.getPassword();

        try {
            String err = validarEntradas(nombres, apellidos, celular, email, pass, confirm);
            if (err != null) {
                mostrarError(err);
                return;
            }
            acciones.alEnviar(new DatosRegistro(
                    nombres, apellidos, celular, programa, rol, email, new String(pass)
            ));
            dispose(); // cierra tras submit exitoso en la vista
        } finally {
            Arrays.fill(pass, '\0');
            Arrays.fill(confirm, '\0');
        }
    }

    private String validarEntradas(String nombres, String apellidos, String celular, String email, char[] pass, char[] confirm) {
        if (nombres.isEmpty()) return "Ingresa tus nombres.";
        if (apellidos.isEmpty()) return "Ingresa tus apellidos.";

        if (!celular.isEmpty() && !celular.matches("^\\d{7,15}$"))
            return "El celular debe contener solo dígitos (7 a 15).";

        if (!email.matches("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$"))
            return "Usa tu correo institucional @unicauca.edu.co";

        String contrasenia = new String(pass);
        if (contrasenia.length() < 6) return "La contraseña debe tener al menos 6 caracteres.";
        if (!contrasenia.matches(".*[A-Z].*")) return "La contraseña debe tener al menos una mayúscula.";
        if (!contrasenia.matches(".*\\d.*")) return "La contraseña debe tener al menos un dígito.";
        if (!contrasenia.matches(".*[^A-Za-z0-9].*")) return "La contraseña debe tener al menos un caracter especial.";

        if (!Arrays.equals(pass, confirm)) return "Las contraseñas no coinciden.";
        return null;
    }

    private void validarFormulario() {
        boolean ok = !campoNombres.getText().trim().isEmpty()
                && !campoApellidos.getText().trim().isEmpty()
                && campoEmail.getText().trim().endsWith("@unicauca.edu.co")
                && campoContrasenia.getPassword().length >= 6
                && campoConfirmacion.getPassword().length >= 6;
        botonRegistrar.setEnabled(ok);
    }

    private void mostrarError(String msg) { etiquetaError.setText(msg); }
    private void limpiarError() { etiquetaError.setText(" "); }
}
