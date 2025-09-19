package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.modelo.Modalidad;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class DialogoFormatoA extends JDialog {
    private final long proyectoId;
    private final AccionesFormatoA acciones;

    private final JTextField txtTitulo = new JTextField(30);
    private final JComboBox<Modalidad> cmbModalidad = new JComboBox<>(Modalidad.values());
    private final JLabel lblFecha = new JLabel(LocalDate.now().toString());
    private final JTextField txtDirector = new JTextField(25);
    private final JTextField txtCodirector = new JTextField(25);
    private final JTextArea txtObjGeneral = new JTextArea(3, 30);
    private final JTextArea txtObjEspecificos = new JTextArea(4, 30);
    private final JTextField txtPdf = new JTextField(25);
    private final JButton btnPdf = new JButton("Examinar...");
    private final JTextField txtCarta = new JTextField(25);
    private final JButton btnCarta = new JButton("Examinar...");

    public DialogoFormatoA(Frame owner, long proyectoId, AccionesFormatoA acciones) {
        super(owner, "Subir Formato A", true);
        this.proyectoId = proyectoId;
        this.acciones = acciones;
        construirUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0;

        form.add(new JLabel("Titulo:"), g); g.gridx = 1; form.add(txtTitulo, g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Modalidad:"), g);
        g.gridx = 1; form.add(cmbModalidad, g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Fecha:"), g);
        g.gridx = 1; form.add(lblFecha, g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Director:"), g);
        g.gridx = 1; form.add(txtDirector, g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Codirector:"), g);
        g.gridx = 1; form.add(txtCodirector, g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Objetivo general:"), g);
        g.gridx = 1; form.add(new JScrollPane(txtObjGeneral), g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Objetivos especificos (1 por linea):"), g);
        g.gridx = 1; form.add(new JScrollPane(txtObjEspecificos), g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Archivo PDF:"), g);
        JPanel pPdf = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pPdf.add(txtPdf); pPdf.add(btnPdf);
        g.gridx = 1; form.add(pPdf, g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Carta aceptacion (si aplica):"), g);
        JPanel pCarta = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pCarta.add(txtCarta); pCarta.add(btnCarta);
        g.gridx = 1; form.add(pCarta, g);

        add(form, BorderLayout.CENTER);

        JPanel accionesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar = new JButton("Subir");
        JButton btnCancelar = new JButton("Cancelar");
        accionesPanel.add(btnCancelar);
        accionesPanel.add(btnAceptar);
        add(accionesPanel, BorderLayout.SOUTH);

        // eventos
        cmbModalidad.addActionListener(e -> actualizarCartaEnabled());
        actualizarCartaEnabled();

        btnPdf.addActionListener(e -> seleccionarArchivo(txtPdf, "pdf"));
        btnCarta.addActionListener(e -> seleccionarArchivo(txtCarta, null));
        btnCancelar.addActionListener(e -> dispose());
        btnAceptar.addActionListener(e -> onAceptar());
    }

    private void actualizarCartaEnabled() {
        boolean reqCarta = (cmbModalidad.getSelectedItem() == Modalidad.PRACTICA_PROFESIONAL);
        txtCarta.setEnabled(reqCarta);
        btnCarta.setEnabled(reqCarta);
    }

    private void seleccionarArchivo(JTextField target, String extEsperada) {
        JFileChooser fc = new JFileChooser();
        int r = fc.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (extEsperada != null && !f.getName().toLowerCase().endsWith("." + extEsperada)) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un archivo ." + extEsperada);
                return;
            }
            target.setText(f.getAbsolutePath());
        }
    }

    private void onAceptar() {
        // validaciones minimas
        if (txtTitulo.getText().isBlank()) { msg("Titulo es obligatorio"); return; }
        if (txtDirector.getText().isBlank()) { msg("Director es obligatorio"); return; }
        if (txtPdf.getText().isBlank()) { msg("Debe adjuntar el PDF"); return; }
        if (cmbModalidad.getSelectedItem() == Modalidad.PRACTICA_PROFESIONAL && txtCarta.getText().isBlank()) {
            msg("Para practica profesional, la carta de aceptacion es obligatoria"); return;
        }

        List<String> oes = txtObjEspecificos.getText().isBlank()
                ? List.of()
                : Arrays.stream(txtObjEspecificos.getText().split("\\R"))
                        .map(String::trim).filter(s -> !s.isEmpty()).toList();

        try {
            acciones.alSubirFormatoA(
                    proyectoId,
                    txtTitulo.getText().trim(),
                    (Modalidad) cmbModalidad.getSelectedItem(),
                    LocalDate.now(),
                    txtDirector.getText().trim(),
                    txtCodirector.getText().trim(),
                    txtObjGeneral.getText().trim(),
                    oes,
                    txtPdf.getText().trim(),
                    txtCarta.getText().trim().isEmpty() ? null : txtCarta.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Formato A subido con exito");
            dispose();
        } catch (IllegalArgumentException ex) {
            msg(ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al subir: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void msg(String s) {
        JOptionPane.showMessageDialog(this, s, "Validacion", JOptionPane.WARNING_MESSAGE);
    }
}
