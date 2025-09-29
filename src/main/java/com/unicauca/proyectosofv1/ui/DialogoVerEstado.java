package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.modelo.EstadoFormatoA;
import com.unicauca.proyectosofv1.servicios.ServicioFormatoA;
import com.unicauca.proyectosofv1.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class DialogoVerEstado extends JDialog {
    private final ServicioFormatoA servicio;
    private final Usuario usuario;

    private final JLabel lblEstado = new JLabel(" ");
    private final JTextArea areaDetalle = new JTextArea(10, 50);

    public DialogoVerEstado(Frame owner, ServicioFormatoA servicio, Usuario usuario) {
        super(owner, "Estado de mi Formato A", true);
        this.servicio = servicio;
        this.usuario = usuario;
        construirUI();
        cargar();
        pack();
        setLocationRelativeTo(owner);
    }

    private void construirUI() {
        areaDetalle.setEditable(false);
        areaDetalle.setLineWrap(true);
        areaDetalle.setWrapStyleWord(true);

        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.add(lblEstado, BorderLayout.NORTH);
        p.add(new JScrollPane(areaDetalle), BorderLayout.CENTER);

        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> dispose());
        p.add(cerrar, BorderLayout.SOUTH);

        setContentPane(p);
    }

    private void cargar() {
        // Para demo: si el estudiante no tiene TG asignado, usamos TG 101 si es id 1, o 205 si es id 4
        // En una versión real, se consultaría por el TG del estudiante
        int tgId = (usuario.getEmail().contains("ana") || usuario.getEmail().contains("luis")) ? 101 : 205;
        EstadoFormatoA estado = servicio.estadoActual(tgId);
        lblEstado.setText("Estado actual: " + estado);
        int intentos = servicio.contarIntentos(tgId);
        StringBuilder sb = new StringBuilder();
        sb.append("Intentos enviados: ").append(intentos).append("\n");
        switch (estado) {
            case EN_PRIMERA_EVALUACION -> sb.append("Tu Formato A está en primera evaluación.");
            case EN_SEGUNDA_EVALUACION -> sb.append("Tu Formato A está en segunda evaluación.");
            case EN_TERCERA_EVALUACION -> sb.append("Tu Formato A está en tercera (última) evaluación.");
            case ACEPTADO_FORMATO_A -> sb.append("¡Felicidades! Tu Formato A fue aceptado.");
            case RECHAZADO_FORMATO_A -> sb.append("Tu Formato A fue rechazado definitivamente luego de 3 intentos.");
        }
        areaDetalle.setText(sb.toString());
    }
}
