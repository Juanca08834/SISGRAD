package com.unicauca.proyectosofv1.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogoRecuperarContrasenia extends JDialog {
    private final JTextField campoEmail = new JTextField();
    private final JButton botonCancelar = new JButton("Cancelar");
    private final JButton botonEnviar = new JButton("Enviar instrucciones");
    private final JLabel etiquetaError = new JLabel(" ");
    private final JLabel etiquetaAyuda = new JLabel(
            "<html><small>Ingresa tu correo institucional @unicauca.edu.co y te enviaremos instrucciones para restablecer tu contraseña.</small></html>");

    private final AccionesRecuperacion acciones;

    public DialogoRecuperarContrasenia(Frame owner, AccionesRecuperacion acciones) {
        super(owner, "Recuperar contraseña", true);
        this.acciones = acciones;
        construirUI();
        conectarEventos();
        pack();
        setLocationRelativeTo(owner);
    }

    private void construirUI() {
        var contenedor = new JPanel(new GridBagLayout());
        contenedor.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JLabel titulo = new JLabel("Recuperar contraseña");
        titulo.setFont(titulo.getFont().deriveFont(titulo.getFont().getStyle() | Font.BOLD, 18f));
        etiquetaError.setForeground(new Color(178,34,34));

        c.gridx=0; c.gridy=0; c.gridwidth=2; c.anchor=GridBagConstraints.CENTER;
        contenedor.add(titulo, c);

        c.gridy++;
        contenedor.add(etiquetaError, c);

        // Email
        c.gridwidth=1; c.gridy++; c.gridx=0; contenedor.add(new JLabel("Email institucional*:"), c);
        c.gridx=1; contenedor.add(campoEmail, c);

        // Ayuda
        c.gridy++; c.gridx=0; c.gridwidth=2; contenedor.add(etiquetaAyuda, c);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.add(botonCancelar);
        panelBotones.add(botonEnviar);
        c.gridy++; contenedor.add(panelBotones, c);

        setContentPane(contenedor);

        // Accesibilidad
        campoEmail.getAccessibleContext().setAccessibleName("emailRecuperacionInput");
        botonEnviar.setEnabled(false);
        setPreferredSize(new Dimension(480, 220));
    }

    private void conectarEventos() {
        botonCancelar.addActionListener(e -> {
            acciones.alCancelar();
            dispose();
        });

        botonEnviar.addActionListener(e -> enviar());

        // activar botón con email válido
        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validar(); }
            @Override public void removeUpdate(DocumentEvent e) { validar(); }
            @Override public void changedUpdate(DocumentEvent e) { validar(); }
        };
        campoEmail.getDocument().addDocumentListener(dl);

        // Enter envía
        campoEmail.addActionListener(e -> enviar());

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                acciones.alCancelar();
            }
        });
    }

    private void enviar() {
        limpiarError();
        String email = campoEmail.getText().trim();
        if (!email.matches("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$")) {
            mostrarError("Usa tu correo institucional @unicauca.edu.co");
            return;
        }
        acciones.alEnviarSolicitud(new DatosRecuperacion(email));
        dispose();
    }

    private void validar() {
        boolean ok = campoEmail.getText().trim().matches("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$");
        botonEnviar.setEnabled(ok);
    }

    private void mostrarError(String msg) { etiquetaError.setText(msg); }
    private void limpiarError() { etiquetaError.setText(" "); }
}
