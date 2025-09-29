package com.unicauca.proyectosofv1.plugins.core;

import com.unicauca.proyectosofv1.infraestructura.sqlite.FabricaConexionSQLite;
import com.unicauca.proyectosofv1.repositorio.TrabajoGradoDAO;
import java.sql.Connection;
import java.util.List;

public class ReportFacade {
    public List<TrabajoGradoDTO> listarTrabajosGrado() {
        try {
            Connection conn = FabricaConexionSQLite.obtenerConexion();
            return new TrabajoGradoDAO(conn).listar();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}