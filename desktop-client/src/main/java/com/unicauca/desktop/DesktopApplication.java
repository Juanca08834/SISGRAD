package com.unicauca.desktop;

import com.formdev.flatlaf.FlatLightLaf;
import com.unicauca.desktop.ui.LoginFrame;

import javax.swing.*;

/**
 * Clase principal de la aplicación de escritorio
 */
public class DesktopApplication {

    public static void main(String[] args) {
        // Configurar Look and Feel moderno
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Error al configurar Look and Feel: " + e.getMessage());
            // Usar el Look and Feel por defecto si falla
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Configurar propiedades de Swing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Iniciar la aplicación en el EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}

