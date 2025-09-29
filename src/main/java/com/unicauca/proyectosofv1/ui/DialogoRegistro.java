package com.unicauca.proyectosofv1.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class DialogoRegistro extends JDialog {
    // Campos
    private final JTextField campoNombres = new JTextField();
    private final JTextField campoApellidos = new JTextField();
    private final JTextField campoCelular = new JTextField();
    private final JComboBox<String> comboPrograma = new JComboBox<>(new String[]{
            "Ingeniería de Sistemas",
            "Ingeniería Electrónica y Telecomunicaciones",
            "Automática industrial",
            "Tecnología en Telemática"
    });
    private final JComboBox<String> comboRol = new JComboBox<>(new String[]{"Estudiante", "Docente", "Coordinador"});
    private final JTextField campoEmail = new JTextField();
    private final JPasswordField campoContrasenia = new JPasswordField();
    private final JPasswordField campoConfirmacion = new JPasswordField();

    // UI auxiliar
    private final JButton botonCancelar = new JButton("Cancelar");
    private final JButton botonRegistrar = new JButton("Registrar");
    private final JLabel etiquetaError = new JLabel(" ");
    private final JLabel etiquetaAyuda = new JLabel(
            "<html><small>Debe cumplir todos los criterios de la lista.</small></html>");

    // Validación en vivo
    private final JLabel lblPwdTips = new JLabel(" ");
    private final JProgressBar barPwd = new JProgressBar(0, 5);
    private final JLabel lblPwdMatch = new JLabel(" ");

    private final AccionesRegistro acciones;

    public DialogoRegistro(Frame owner, AccionesRegistro acciones) {
        super(owner, "Registro de usuario", true);
        this.acciones = acciones;
        construirUI();
        conectarEventos();
        pack();
        setLocationRelativeTo(owner);
    }

    /* ===================== UI ===================== */

    private void construirUI() {
        var cont = new JPanel(new GridBagLayout());
        cont.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JLabel titulo = new JLabel("Registro");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        etiquetaError.setForeground(new Color(178, 34, 34));

        // ---- Título y error
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER;
        cont.add(titulo, c);
        c.gridy++;
        cont.add(etiquetaError, c);

        // ---- Nombres
        c.gridwidth = 1; c.gridy++; c.gridx = 0; cont.add(new JLabel("Nombres*:"), c);
        c.gridx = 1; cont.add(campoNombres, c);

        // ---- Apellidos
        c.gridy++; c.gridx = 0; cont.add(new JLabel("Apellidos*:"), c);
        c.gridx = 1; cont.add(campoApellidos, c);

        // ---- Celular
        c.gridy++; c.gridx = 0; cont.add(new JLabel("Celular (opcional):"), c);
        c.gridx = 1; cont.add(campoCelular, c);

        // ---- Programa
        c.gridy++; c.gridx = 0; cont.add(new JLabel("Programa*:"), c);
        c.gridx = 1; cont.add(comboPrograma, c);

        // ---- Rol
        c.gridy++; c.gridx = 0; cont.add(new JLabel("Rol*:"), c);
        c.gridx = 1; cont.add(comboRol, c);

        // ---- Email
        c.gridy++; c.gridx = 0; cont.add(new JLabel("Email institucional*:"), c);
        c.gridx = 1; cont.add(campoEmail, c);

        // ---- Contraseña
        c.gridy++; c.gridx = 0; cont.add(new JLabel("Contraseña*:"), c);
        c.gridx = 1; cont.add(campoContrasenia, c);

        // ---- Confirmación
        c.gridy++; c.gridx = 0; cont.add(new JLabel("Confirmar contraseña*:"), c);
        c.gridx = 1; cont.add(campoConfirmacion, c);

        // ---- Barra + coincidencia
        barPwd.setStringPainted(true);
        c.gridy++; c.gridx = 0; cont.add(new JLabel("Fuerza:"), c);
        c.gridx = 1; cont.add(barPwd, c);

        c.gridy++; c.gridx = 1; cont.add(lblPwdMatch, c);

        // ---- Checklist HTML
        c.gridy++; c.gridx = 0; c.gridwidth = 2; cont.add(lblPwdTips, c);

        // ---- Ayuda
        c.gridy++; cont.add(etiquetaAyuda, c);

        // ---- Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.add(botonCancelar);
        panelBotones.add(botonRegistrar);
        c.gridy++; cont.add(panelBotones, c);

        setContentPane(cont);

        // Accesibilidad / Test IDs
        campoNombres.getAccessibleContext().setAccessibleName("nombresInput");
        campoApellidos.getAccessibleContext().setAccessibleName("apellidosInput");
        campoCelular.getAccessibleContext().setAccessibleName("celularInput");
        comboPrograma.getAccessibleContext().setAccessibleName("programaSelect");
        comboRol.getAccessibleContext().setAccessibleName("rolSelect");
        campoEmail.getAccessibleContext().setAccessibleName("emailInput");
        campoContrasenia.getAccessibleContext().setAccessibleName("passwordInput");
        campoConfirmacion.getAccessibleContext().setAccessibleName("confirmPasswordInput");
        barPwd.getAccessibleContext().setAccessibleName("passwordStrength");

        botonRegistrar.setEnabled(false);
        setPreferredSize(new Dimension(560, 600));
    }

    /* ===================== Eventos ===================== */

    private void conectarEventos() {
        botonCancelar.addActionListener(e -> {
            acciones.alCancelar();
            dispose();
        });

        botonRegistrar.addActionListener(e -> enviar());

        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { onChange(); }
            @Override public void removeUpdate(DocumentEvent e) { onChange(); }
            @Override public void changedUpdate(DocumentEvent e) { onChange(); }
            private void onChange() {
                refrescarEstadoPassword();
                validarFormulario();
            }
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

        // Estado inicial
        refrescarEstadoPassword();
        validarFormulario();
    }

    /* ===================== Envío ===================== */

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
            dispose();
        } finally {
            Arrays.fill(pass, '\0');
            Arrays.fill(confirm, '\0');
        }
    }

    /* ===================== Validaciones ===================== */

    private String validarEntradas(String nombres, String apellidos, String celular, String email,
                                   char[] pass, char[] confirm) {
        if (nombres.isEmpty()) return "Ingresa tus nombres.";
        if (apellidos.isEmpty()) return "Ingresa tus apellidos.";

        if (!celular.isEmpty() && !celular.matches("^\\d{7,15}$"))
            return "El celular debe contener solo dígitos (7 a 15).";

        if (!email.matches("^[A-Za-z0-9._%+-]+@unicauca\\.edu\\.co$"))
            return "Usa tu correo institucional @unicauca.edu.co";

        // Política alineada con el servicio: >=6, mayúscula, minúscula, dígito, símbolo
        String contrasenia = new String(pass);
        if (contrasenia.length() < 6) return "La contraseña debe tener al menos 6 caracteres.";
        if (!contrasenia.matches(".*[A-Z].*")) return "La contraseña debe tener al menos una mayúscula.";
        if (!contrasenia.matches(".*[a-z].*")) return "La contraseña debe tener al menos una minúscula.";
        if (!contrasenia.matches(".*\\d.*")) return "La contraseña debe tener al menos un dígito.";
        if (!contrasenia.matches(".*[^A-Za-z0-9].*")) return "La contraseña debe tener al menos un símbolo.";

        if (!Arrays.equals(pass, confirm)) return "Las contraseñas no coinciden.";
        return null;
    }

    private void validarFormulario() {
        boolean camposOk = !campoNombres.getText().trim().isEmpty()
                && !campoApellidos.getText().trim().isEmpty()
                && campoEmail.getText().trim().endsWith("@unicauca.edu.co");

        // Cumplimiento de política + confirmación
        PasswordResultado r = evaluarPassword(campoContrasenia.getPassword());
        boolean coincide = Arrays.equals(campoContrasenia.getPassword(), campoConfirmacion.getPassword());

        botonRegistrar.setEnabled(camposOk && r.ok && coincide);
    }

    private void mostrarError(String msg) { etiquetaError.setText(msg); }
    private void limpiarError() { etiquetaError.setText(" "); }

    /* ===================== Password Live ===================== */

    private static final class PasswordResultado {
        final boolean hasLen, hasUpper, hasLower, hasDigit, hasSymbol;
        final int score; // 0..5
        final boolean ok;
        PasswordResultado(boolean len, boolean up, boolean lo, boolean di, boolean sy) {
            this.hasLen=len; this.hasUpper=up; this.hasLower=lo; this.hasDigit=di; this.hasSymbol=sy;
            int s=0; if(len)s++; if(up)s++; if(lo)s++; if(di)s++; if(sy)s++;
            this.score=s;
            this.ok = len && up && lo && di && sy;
        }
    }

    private static PasswordResultado evaluarPassword(char[] pwd) {
        if (pwd == null) pwd = new char[0];
        boolean len = pwd.length >= 6, up=false, lo=false, di=false, sy=false;
        for (char c : pwd) {
            if      (Character.isUpperCase(c)) up = true;
            else if (Character.isLowerCase(c)) lo = true;
            else if (Character.isDigit(c))     di = true;
            else                                sy = true;
        }
        PasswordResultado r = new PasswordResultado(len, up, lo, di, sy);
        // no limpiamos aquí porque el mismo array lo usa confirmación/comparación
        return r;
    }

    private static String htmlChecklist(PasswordResultado r) {
        return """
        <html>
          <div style='font-family:Segoe UI, sans-serif; font-size:11pt'>
            <b>Requisitos de la contraseña</b>
            <ul style='margin:4 0 0 16'>
              <li>%s 6+ caracteres</li>
              <li>%s 1 mayúscula (A-Z)</li>
              <li>%s 1 minúscula (a-z)</li>
              <li>%s 1 dígito (0-9)</li>
              <li>%s 1 símbolo (!@#$...)</li>
            </ul>
          </div>
        </html>
        """.formatted(
                mark(r.hasLen), mark(r.hasUpper), mark(r.hasLower), mark(r.hasDigit), mark(r.hasSymbol)
        );
    }

    private static String mark(boolean ok) {
        return ok ? "<span style='color:#2e7d32'>&#10003;</span>"
                  : "<span style='color:#c62828'>&#10007;</span>";
    }

    private void refrescarEstadoPassword() {
        PasswordResultado r = evaluarPassword(campoContrasenia.getPassword());

        // Checklist
        lblPwdTips.setText(htmlChecklist(r));

        // Barra fuerza
        barPwd.setValue(r.score);
        barPwd.setString(r.score + "/5");
        barPwd.setForeground(colorFuerza(r.score));

        // Coincidencia
        boolean coincide = Arrays.equals(campoContrasenia.getPassword(), campoConfirmacion.getPassword());
        lblPwdMatch.setText(coincide
                ? "<html><span style='color:#2e7d32'>Las contraseñas coinciden</span></html>"
                : "<html><span style='color:#c62828'>Las contraseñas no coinciden</span></html>");
    }

    private static Color colorFuerza(int score) {
        if (score <= 2) return new Color(198,40,40);      // rojo
        if (score <= 4) return new Color(251,140,0);      // naranja
        return new Color(46,125,50);                      // verde
    }
}
