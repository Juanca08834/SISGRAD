package com.unicauca.proyectosofv1.plugins.report.html;

import com.unicauca.proyectosofv1.plugins.core.IReportPlugin;
import com.unicauca.proyectosofv1.plugins.core.TrabajoGradoDTO;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HtmlReportPlugin implements IReportPlugin {
    @Override
    public String generateReport(List<TrabajoGradoDTO> data) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n")
          .append("<meta charset=\"UTF-8\">\n<title>Reporte Trabajos de Grado</title>\n</head>\n<body>\n")
          .append("<h1>Reporte de Trabajos de Grado</h1>\n")
          .append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"6\">\n<thead><tr>")
          .append("<th>ID</th><th>Título</th><th>Fecha Formato A</th><th>Estudiante(s)</th>")
          .append("<th>Director</th><th>Modalidad</th><th>Programa</th>")
          .append("</tr></thead><tbody>\n");

        for (TrabajoGradoDTO t : data) {
            sb.append("<tr>")
              .append("<td>").append(esc(t.id)).append("</td>")
              .append("<td>").append(esc(t.titulo)).append("</td>")
              .append("<td>").append(t.fechaFormatoA == null ? "" : t.fechaFormatoA.format(fmt)).append("</td>")
              .append("<td>").append(esc(t.estudiante1));
            if (t.estudiante2 != null && !t.estudiante2.isBlank()) {
                sb.append(", ").append(esc(t.estudiante2));
            }
            sb.append("</td>")
              .append("<td>").append(esc(t.director)).append("</td>")
              .append("<td>").append(esc(t.modalidad)).append("</td>")
              .append("<td>").append(esc(t.programa)).append("</td>")
              .append("</tr>\n");
        }
        sb.append("</tbody></table>\n</body>\n</html>");
        return sb.toString();
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}