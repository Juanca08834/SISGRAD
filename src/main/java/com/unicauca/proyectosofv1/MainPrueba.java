package com.unicauca.proyectosofv1;

import com.unicauca.proyectosofv1.infraestructura.sqlite.*;
import com.unicauca.proyectosofv1.plugins.core.*;
import java.nio.file.*;

public class MainPrueba {
    public static void main(String[] args) throws Exception {
        var conn = FabricaConexionSQLite.obtenerConexion();
        DBInit.init(conn);

        var data = new ReportFacade().listarTrabajosGrado();
        var pm   = new ReportPluginManager();

        String html = pm.get("html").generateReport(data);
        String json = pm.get("json").generateReport(data);

        Path out = Path.of("target/reports");
        Files.createDirectories(out);
        Files.writeString(out.resolve("reporte.html"), html);
        Files.writeString(out.resolve("reporte.json"), json);

        System.out.println(" Reportes generados en: " + out.toAbsolutePath());
    }
}