package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.excepciones.SISGRADException;
import com.unicauca.proyectosofv1.modelo.FormatoAVersion;
import com.unicauca.proyectosofv1.servicios.ServicioFormatoA;
import com.unicauca.proyectosofv1.servicios.ServicioFormatoA.DatosFormatoA;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

public class DialogoSubirFormatoA extends JDialog {

    private final ServicioFormatoA servicio;
    private final Consumer<Long> onExito;             // callback opcional
    private final Integer trabajoGradoId;             // null => primer intento (crea TG)

    // Campos UI
    private final JTextField campoTitulo        = new JTextField();
    private final JComboBox<String> comboModalidad =
            new JComboBox<>(new String[]{"Investigación", "Práctica Profesional"});
    private final JTextField campoDirectorId    = new JTextField();
    private final JTextField campoCodirectorId  = new JTextField();
    private final JTextField campoEstudiante1Id = new JTextField();
    private final JTextField campoEstudiante2Id = new JTextField();
    private final JTextField campoProgramaId    = new JTextField();

    private final JTextArea  areaObjGeneral     = new JTextArea(3, 40);
    private final JTextArea  areaObjEspecificos = new JTextArea(5, 40);

    private final JTextField campoPdf           = new JTextField();
    private final JButton    botonElegirPdf     = new JButton("Elegir PDF...");
    private final JTextField campoCarta         = new JTextField();
    private final JButton    botonElegirCarta   = new JButton("Elegir Carta...");

    private final JButton    botonSubir         = new JButton("Subir");
    private final JButton    botonCancelar      = new JButton("Cancelar");

    /* ==== Constructores ==== */

    // 1) Uso simple (primer intento)
    public DialogoSubirFormatoA(Frame owner, ServicioFormatoA servicio) {
        this(owner, servicio, null, null);
    }

    // 2) Con callback (primer intento)
    public DialogoSubirFormatoA(Frame owner, ServicioFormatoA servicio, Consumer<Long> onExito) {
        this(owner, servicio, onExito, null);
    }

    // 3) Completo: con callback y TG existente para reintento
    public DialogoSubirFormatoA(Frame owner, ServicioFormatoA servicio,
                                Consumer<Long> onExito, Integer trabajoGradoId) {
        super(owner, "Subir Formato A", true);
        this.servicio = servicio;
        this.onExito = onExito;
        this.trabajoGradoId = trabajoGradoId;

        construirUI();
        cargarPrefill();       // ← pre-llenado si es reintento
        configurarEventos();

        pack();
        setLocationRelativeTo(owner);
    }

    /* ==== Helpers ==== */

    // Normaliza eliminando tildes y a minúsculas
    private static String norm(String s) {
        if (s == null) return "";
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase()
                .trim();
    }

    private static boolean esPdfPath(String ruta) {
        if (ruta == null || ruta.isBlank()) return false;
        File f = new File(ruta);
        return f.isFile() && norm(f.getName()).endsWith(".pdf");
    }

    /* ==== UI ==== */

    private void construirUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int y = 0;

        c.gridx=0; c.gridy=y; panel.add(new JLabel("Título:"), c);
        c.gridx=1; c.gridy=y++; panel.add(campoTitulo, c);

        c.gridx=0; c.gridy=y; panel.add(new JLabel("Modalidad:"), c);
        c.gridx=1; c.gridy=y++; panel.add(comboModalidad, c);

        c.gridx=0; c.gridy=y; panel.add(new JLabel("Director ID (docente):"), c);
        c.gridx=1; c.gridy=y++; panel.add(campoDirectorId, c);

        c.gridx=0; c.gridy=y; panel.add(new JLabel("Codirector ID (opcional):"), c);
        c.gridx=1; c.gridy=y++; panel.add(campoCodirectorId, c);

        c.gridx=0; c.gridy=y; panel.add(new JLabel("Estudiante1 ID:"), c);
        c.gridx=1; c.gridy=y++; panel.add(campoEstudiante1Id, c);

        c.gridx=0; c.gridy=y; panel.add(new JLabel("Estudiante2 ID (opcional):"), c);
        c.gridx=1; c.gridy=y++; panel.add(campoEstudiante2Id, c);

        c.gridx=0; c.gridy=y; panel.add(new JLabel("Programa ID:"), c);
        c.gridx=1; c.gridy=y++; panel.add(campoProgramaId, c);

        c.gridx=0; c.gridy=y; panel.add(new JLabel("Objetivo general:"), c);
        c.gridx=1; c.gridy=y++; panel.add(new JScrollPane(areaObjGeneral), c);

        c.gridx=0; c.gridy=y; panel.add(new JLabel("Objetivos específicos (uno por línea):"), c);
        c.gridx=1; c.gridy=y++; panel.add(new JScrollPane(areaObjEspecificos), c);

        JPanel pf = new JPanel(new BorderLayout(6,0));
        pf.add(campoPdf, BorderLayout.CENTER);
        pf.add(botonElegirPdf, BorderLayout.EAST);
        c.gridx=0; c.gridy=y; panel.add(new JLabel("PDF Formato A:"), c);
        c.gridx=1; c.gridy=y++; panel.add(pf, c);

        JPanel pc = new JPanel(new BorderLayout(6,0));
        pc.add(campoCarta, BorderLayout.CENTER);
        pc.add(botonElegirCarta, BorderLayout.EAST);
        c.gridx=0; c.gridy=y; panel.add(new JLabel("Carta aceptación (Práctica):"), c);
        c.gridx=1; c.gridy=y++; panel.add(pc, c);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(botonCancelar);
        acciones.add(botonSubir);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(acciones, BorderLayout.SOUTH);
    }

    /** Pre-llena datos cuando es reintento y bloquea campos que no cambian */
    private void cargarPrefill() {
        if (trabajoGradoId == null || trabajoGradoId <= 0) return;

        DatosFormatoA d = servicio.cargarDatosParaReintento(trabajoGradoId);
        if (d == null) return;

        campoTitulo.setText(d.titulo());
        comboModalidad.setSelectedItem(d.modalidad());
        campoDirectorId.setText(String.valueOf(d.directorId()));
        campoCodirectorId.setText(d.codirectorId() == null ? "" : String.valueOf(d.codirectorId()));
        campoEstudiante1Id.setText(String.valueOf(d.estudiante1Id()));
        campoEstudiante2Id.setText(d.estudiante2Id() == null ? "" : String.valueOf(d.estudiante2Id()));
        campoProgramaId.setText(String.valueOf(d.programaId()));
        if (d.objetivoGeneral() != null)     areaObjGeneral.setText(d.objetivoGeneral());
        if (d.objetivosEspecificos() != null) areaObjEspecificos.setText(d.objetivosEspecificos());

        // Bloquear campos “estables”
        campoDirectorId.setEditable(false);
        campoEstudiante1Id.setEditable(false);
        campoProgramaId.setEditable(false);
        // (Deja editables codirector y estudiante2 si lo deseas)
        botonSubir.setText("Subir reintento");
    }

    private void configurarEventos() {
        botonElegirPdf.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                campoPdf.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });

        botonElegirCarta.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                campoCarta.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });

        botonCancelar.addActionListener(e -> dispose());

        botonSubir.addActionListener(e -> onSubir());
    }

    /* ==== Lógica de envío ==== */

    private void onSubir() {
        try {
            final String titulo = campoTitulo.getText().trim();
            final String modalidad = (String) comboModalidad.getSelectedItem();
            final String objetivoGeneral = areaObjGeneral.getText().trim();
            final String objetivosEspecificos = areaObjEspecificos.getText().trim();
            final String pdfPath = campoPdf.getText().trim();
            final String cartaPath = campoCarta.getText().trim();

            // Validaciones
            if (titulo.isBlank()) {
                JOptionPane.showMessageDialog(this, "Ingresa el título del trabajo.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (objetivoGeneral.isBlank()) {
                JOptionPane.showMessageDialog(this, "Ingresa el objetivo general.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!esPdfPath(pdfPath)) {
                JOptionPane.showMessageDialog(this, "Adjunta el Formato A en PDF válido.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean esPractica = norm(modalidad).startsWith("practica");
            if (esPractica && !esPdfPath(cartaPath)) {
                JOptionPane.showMessageDialog(this, "Para modalidad Práctica Profesional, adjunta la carta en PDF.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (campoDirectorId.getText().isBlank()
                    || campoEstudiante1Id.getText().isBlank()
                    || campoProgramaId.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Director ID, Estudiante1 ID y Programa ID son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int directorId    = Integer.parseInt(campoDirectorId.getText().trim());
            Integer codirectorId  = campoCodirectorId.getText().isBlank() ? null
                    : Integer.parseInt(campoCodirectorId.getText().trim());
            int estudiante1Id = Integer.parseInt(campoEstudiante1Id.getText().trim());
            Integer estudiante2Id = campoEstudiante2Id.getText().isBlank() ? null
                    : Integer.parseInt(campoEstudiante2Id.getText().trim());
            int programaId    = Integer.parseInt(campoProgramaId.getText().trim());

            // UX: deshabilitar mientras procesa
            botonSubir.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // Llamada al servicio (si hay TG, lo reutiliza)
            FormatoAVersion fav = servicio.subirFormatoA(
                    (trabajoGradoId == null ? 0 : trabajoGradoId),
                    titulo, modalidad,
                    directorId, codirectorId,
                    estudiante1Id, estudiante2Id, programaId,
                    objetivoGeneral, objetivosEspecificos,
                    pdfPath,
                    esPractica ? cartaPath : null
            );

            JOptionPane.showMessageDialog(this,
                    "Formato A enviado correctamente. Versión #" + fav.intento,
                    "OK", JOptionPane.INFORMATION_MESSAGE);

            if (onExito != null) onExito.accept((long) fav.trabajoGradoId);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "IDs deben ser numéricos.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SISGRADException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            botonSubir.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }
}
