package com.unicauca.desktop.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Tema visual con los colores institucionales de la Universidad del Cauca
 * Colores: Blanco (predominante), Azul (intermedio), Rojo (acentos)
 */
public class UnicaucaTheme {

    // Colores institucionales de la Universidad del Cauca
    public static final Color AZUL_UNICAUCA = new Color(0, 51, 102);       // Azul oscuro institucional
    public static final Color AZUL_CLARO = new Color(0, 102, 179);         // Azul más claro para hover
    public static final Color ROJO_UNICAUCA = new Color(204, 0, 0);        // Rojo institucional
    public static final Color ROJO_CLARO = new Color(220, 53, 53);         // Rojo más claro para hover

    // Colores neutros
    public static final Color BLANCO = Color.WHITE;
    public static final Color GRIS_CLARO = new Color(245, 245, 245);
    public static final Color GRIS_BORDE = new Color(200, 200, 200);
    public static final Color TEXTO_OSCURO = new Color(51, 51, 51);
    public static final Color TEXTO_GRIS = new Color(102, 102, 102);

    // Fuentes
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 18);
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    /**
     * Crea un botón primario con estilo azul Unicauca
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FUENTE_BOTON);
        button.setBackground(AZUL_UNICAUCA);
        button.setForeground(BLANCO);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(AZUL_CLARO);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(AZUL_UNICAUCA);
            }
        });

        return button;
    }

    /**
     * Crea un botón secundario con estilo blanco/borde
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FUENTE_BOTON);
        button.setBackground(BLANCO);
        button.setForeground(AZUL_UNICAUCA);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(AZUL_UNICAUCA, 2));
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(GRIS_CLARO);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BLANCO);
            }
        });

        return button;
    }

    /**
     * Crea un botón de acento/peligro con estilo rojo
     */
    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FUENTE_BOTON);
        button.setBackground(ROJO_UNICAUCA);
        button.setForeground(BLANCO);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ROJO_CLARO);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ROJO_UNICAUCA);
            }
        });

        return button;
    }

    /**
     * Estiliza un campo de texto
     */
    public static void styleTextField(JTextField field) {
        field.setFont(FUENTE_NORMAL);
        field.setBackground(BLANCO);
        field.setForeground(TEXTO_OSCURO);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BORDE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 38));
    }

    /**
     * Estiliza un campo de contraseña
     */
    public static void stylePasswordField(JPasswordField field) {
        field.setFont(FUENTE_NORMAL);
        field.setBackground(BLANCO);
        field.setForeground(TEXTO_OSCURO);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BORDE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 38));
    }

    /**
     * Estiliza un ComboBox
     */
    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(FUENTE_NORMAL);
        combo.setBackground(BLANCO);
        combo.setForeground(TEXTO_OSCURO);
    }

    /**
     * Crea una etiqueta de título
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FUENTE_TITULO);
        label.setForeground(AZUL_UNICAUCA);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Crea una etiqueta de subtítulo
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FUENTE_SUBTITULO);
        label.setForeground(TEXTO_GRIS);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Crea un panel con header azul institucional
     */
    public static JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(AZUL_UNICAUCA);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    /**
     * Crea un borde redondeado
     */
    public static Border createRoundedBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BORDE, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }

    /**
     * Carga el logo de la universidad
     * @param width Ancho deseado
     * @param height Alto deseado
     * @return ImageIcon escalado o null si no se encuentra
     */
    public static ImageIcon loadLogo(int width, int height) {
        try {
            java.net.URL imgURL = UnicaucaTheme.class.getResource("/images/logo_unicauca.png");
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } else {
                System.err.println("No se encontró el logo en /images/logo_unicauca.png");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
            return null;
        }
    }

    /**
     * Crea un panel con el logo de la universidad
     */
    public static JPanel createLogoPanel(int logoWidth, int logoHeight) {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        ImageIcon logo = loadLogo(logoWidth, logoHeight);
        if (logo != null) {
            JLabel logoLabel = new JLabel(logo);
            logoPanel.add(logoLabel);
        }

        return logoPanel;
    }
}

