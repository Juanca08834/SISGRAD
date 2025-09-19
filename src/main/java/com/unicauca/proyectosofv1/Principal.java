package com.unicauca.proyectosofv1;

import com.unicauca.proyectosofv1.infraestructura.sqlite.FabricaConexionSQLite;
import com.unicauca.proyectosofv1.modelo.Usuario;
import com.unicauca.proyectosofv1.repositorio.RepositorioUsuario;
import com.unicauca.proyectosofv1.repositorio.sqlite.RepositorioUsuarioSQLite;
import com.unicauca.proyectosofv1.seguridad.EncriptadorContrasenia;
import com.unicauca.proyectosofv1.servicios.ServicioAuth;
import com.unicauca.proyectosofv1.servicios.ServicioRegistro;
import com.unicauca.proyectosofv1.servicios.impl.ServicioAuthImpl;
import com.unicauca.proyectosofv1.servicios.impl.ServicioRegistroImpl;
import com.unicauca.proyectosofv1.ui.*;

import javax.swing.*;
import java.sql.Connection;
import java.util.Arrays;

public class Principal {

    public static void main(String[] args) {
        // Look & Feel nativo
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        // Infraestructura y servicios basicos
        Connection conn = FabricaConexionSQLite.obtenerConexion();
        RepositorioUsuario repo = new RepositorioUsuarioSQLite(conn);
        EncriptadorContrasenia encriptador = new EncriptadorContrasenia();
        ServicioRegistro servicioRegistro = new ServicioRegistroImpl(repo, encriptador);
        ServicioAuth servicioAuth = new ServicioAuthImpl(repo, encriptador);

        SwingUtilities.invokeLater(() -> {
            final VentanaLogin[] loginRef = new VentanaLogin[1];

            loginRef[0] = new VentanaLogin(new AccionesLogin() {
                @Override
                public void alIniciarSesion(String correo, char[] contrasenia) {
                    try {
                        Usuario u = servicioAuth.loginYObtener(correo, new String(contrasenia));
                        if (u != null) {
                            if (loginRef[0] != null) loginRef[0].dispose();

                            String rol = (u.getRol() == null ? "" : u.getRol().trim().toLowerCase());
                                if ("docente".equals(rol)) {
                                    new TableroDocente(u).setVisible(true);
                                } else if ("coordinador".equals(rol)) {
                                    new TableroCoordinador(u).setVisible(true);
                                } else {
                                    new TableroEstudiante(u).setVisible(true);
                                }

                        } else {
                            JOptionPane.showMessageDialog(
                                    loginRef[0],
                                    "Credenciales incorrectas",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                loginRef[0],
                                "Error inesperado: " + ex.getMessage(),
                                "Autenticacion",
                                JOptionPane.ERROR_MESSAGE
                        );
                    } finally {
                        Arrays.fill(contrasenia, '\0'); // limpiar por seguridad
                    }
                }

                @Override
                public void alSolicitarRegistro() {
                    final DialogoRegistro[] dlgRef = new DialogoRegistro[1];

                    AccionesRegistro acciones = new AccionesRegistro() {
                        @Override
                        public void alEnviar(DatosRegistro datos) {
                            try {
                                servicioRegistro.registrar(
                                        datos.nombres(), datos.apellidos(), datos.celular(),
                                        datos.programa(), datos.rol(), datos.email(), datos.password()
                                );
                                JOptionPane.showMessageDialog(dlgRef[0], "Registro exitoso");
                                dlgRef[0].dispose();
                            } catch (com.unicauca.proyectosofv1.excepciones.SISGRADException ex) {
                                JOptionPane.showMessageDialog(
                                        dlgRef[0],
                                        ex.getMessage(),
                                        "Registro",
                                        JOptionPane.WARNING_MESSAGE
                                );
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(
                                        dlgRef[0],
                                        "Error inesperado: " + ex.getMessage(),
                                        "Registro",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }

                        @Override public void alCancelar() { dlgRef[0].dispose(); }
                    };

                    dlgRef[0] = new DialogoRegistro(null, acciones);
                    dlgRef[0].setVisible(true);
                }

                @Override
                public void alOlvideContrasenia() {
                    final DialogoRecuperarContrasenia[] dlgRef = new DialogoRecuperarContrasenia[1];

                    AccionesRecuperacion acciones = new AccionesRecuperacion() {
                        @Override
                        public void alEnviarSolicitud(DatosRecuperacion datos) {
                            JOptionPane.showMessageDialog(
                                    dlgRef[0],
                                    "Si el correo existe, te enviaremos instrucciones.",
                                    "Recuperacion",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            dlgRef[0].dispose();
                        }
                        @Override public void alCancelar() { dlgRef[0].dispose(); }
                    };

                    dlgRef[0] = new DialogoRecuperarContrasenia(null, acciones);
                    dlgRef[0].setVisible(true);
                }
            });

            loginRef[0].setVisible(true);
        });
    }
}
