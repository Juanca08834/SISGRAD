package com.unicauca.proyectosofv1.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class VentanaLogin extends JFrame {
    private final JTextField campoCorreo = new JTextField();
    private final JPasswordField campoContrasenia = new JPasswordField();
    private final JButton botonIniciar = new JButton("Iniciar sesión");
    private final JButton botonRegistrarse = new JButton("Registrarse");
    private final JLabel enlaceOlvide = new JLabel("<html><u>¿Olvidé mi contraseña?</u></html>");
    private final JLabel etiquetaError = new JLabel(" ");

    private final AccionesLogin acciones;

    public VentanaLogin(AccionesLogin acciones) {
        super("Login - ProyectoSofV1");
        this.acciones = acciones;
        construirUI();
        conectarEventos();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void construirUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 300);
        setMinimumSize(new Dimension(380, 260));

        var contenedor = new JPanel(new GridBagLayout());
        contenedor.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JLabel titulo = new JLabel("Iniciar sesión");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));

        JLabel etiquetaCorreo = new JLabel("Correo institucional:");
        campoCorreo.setToolTipText("ejemplo@unicauca.edu.co");

        JLabel etiquetaContrasenia = new JLabel("Contraseña:");
        campoContrasenia.setToolTipText("Tu contraseña");

        enlaceOlvide.setForeground(new Color(0, 102, 204));
        enlaceOlvide.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        etiquetaError.setForeground(new Color(178, 34, 34));

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER;
        contenedor.add(titulo, c);

        c.gridy++;
        contenedor.add(etiquetaError, c);

        c.gridy++; c.gridwidth = 1; c.anchor = GridBagConstraints.WEST;
        contenedor.add(etiquetaCorreo, c);
        c.gridx = 1;
        contenedor.add(campoCorreo, c);

        c.gridx = 0; c.gridy++;
        contenedor.add(etiquetaContrasenia, c);
        c.gridx = 1;
        contenedor.add(campoContrasenia, c);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.add(botonRegistrarse);
        panelBotones.add(botonIniciar);

        c.gridx = 0; c.gridy++; c.gridwidth = 2;
        contenedor.add(panelBotones, c);

        JPanel panelOlvide = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelOlvide.add(enlaceOlvide);
        c.gridy++;
        contenedor.add(panelOlvide, c);

        setContentPane(contenedor);

        // Accesibilidad/Test IDs
        campoCorreo.getAccessibleContext().setAccessibleName("correoInput");
        campoContrasenia.getAccessibleContext().setAccessibleName("contraseniaInput");
        botonIniciar.getAccessibleContext().setAccessibleName("iniciarButton");
        botonRegistrarse.getAccessibleContext().setAccessibleName("registrarseButton");
        enlaceOlvide.getAccessibleContext().setAccessibleName("olvideLink");

        botonIniciar.setEnabled(false);
    }

    private void conectarEventos() {
        Action accionLogin = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { ejecutarLogin(); }
        };
        campoContrasenia.addActionListener(accionLogin);
        botonIniciar.addActionListener(accionLogin);

        botonRegistrarse.addActionListener(e -> {
            limpiarError();
            acciones.alSolicitarRegistro();
        });

        enlaceOlvide.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                limpiarError();
                acciones.alOlvideContrasenia();
            }
        });

        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validarEntradas(); }
            @Override public void removeUpdate(DocumentEvent e) { validarEntradas(); }
            @Override public void changedUpdate(DocumentEvent e) { validarEntradas(); }
        };
        campoCorreo.getDocument().addDocumentListener(dl);
        campoContrasenia.getDocument().addDocumentListener(dl);
    }

    private void ejecutarLogin() {
        limpiarError();
        String correo = campoCorreo.getText().trim();
        char[] pass = campoContrasenia.getPassword();

        // Validación mínima de UI (reglas fuertes vendrán en la capa de dominio)
        if (!correo.matches("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$")) {
            mostrarError("Usa tu correo institucional @unicauca.edu.co");
            return;
        }
        if (pass.length == 0) {
            mostrarError("Ingresa tu contraseña.");
            return;
        }
        try {
            acciones.alIniciarSesion(correo, pass);
        } finally {
            Arrays.fill(pass, '\0'); // limpiar en memoria
        }
    }

    private void validarEntradas() {
        boolean ok = !campoCorreo.getText().trim().isEmpty()
                && campoContrasenia.getPassword().length > 0;
        botonIniciar.setEnabled(ok);
    }

    public void mostrarError(String mensaje) { etiquetaError.setText(mensaje); }
    public void limpiarError() { etiquetaError.setText(" "); }
    public void limpiarContrasenia() { campoContrasenia.setText(""); }
}
