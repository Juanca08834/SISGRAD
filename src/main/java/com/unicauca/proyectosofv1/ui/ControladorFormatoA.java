package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.modelo.Modalidad;
import com.unicauca.proyectosofv1.servicios.ServicioFormatoA;

import javax.swing.*;
import java.awt.Frame;
import java.time.LocalDate;
import java.util.List;

public class ControladorFormatoA {

    private final Frame owner;
    private final long proyectoId;
    private final ServicioFormatoA servicio;

    public ControladorFormatoA(Frame owner, long proyectoId, ServicioFormatoA servicio) {
        this.owner = owner;
        this.proyectoId = proyectoId;
        this.servicio = servicio;
    }

    /** Abre el dialogo y procesa el envio */
    public void mostrarDialogo() {
        // Holder para poder usar el dialogo dentro de la clase anonima sin problemas de inicializacion
        final DialogoFormatoA[] dlgRef = new DialogoFormatoA[1];

        dlgRef[0] = new DialogoFormatoA(owner, proyectoId, new AccionesFormatoA() {
            @Override
            public void alSubirFormatoA(long proyectoId, String titulo, Modalidad modalidad, LocalDate fecha,
                                        String director, String codirector, String objetivoGeneral,
                                        List<String> objetivosEspecificos, String pdfPath, String cartaAceptacionPath) {
                try {
                    servicio.subir(proyectoId, titulo, modalidad, fecha, director, codirector,
                            objetivoGeneral, objetivosEspecificos, pdfPath, cartaAceptacionPath);
                    JOptionPane.showMessageDialog(owner, "Formato A subido con exito");
                    // cerrar el dialogo
                    dlgRef[0].dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(owner, ex.getMessage(), "Validacion", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(owner, "Error al subir: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dlgRef[0].setVisible(true);
    }
}
