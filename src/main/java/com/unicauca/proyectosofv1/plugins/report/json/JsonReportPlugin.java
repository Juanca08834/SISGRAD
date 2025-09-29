package com.unicauca.proyectosofv1.plugins.report.json;

import com.unicauca.proyectosofv1.plugins.core.IReportPlugin;
import com.unicauca.proyectosofv1.plugins.core.TrabajoGradoDTO;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JsonReportPlugin implements IReportPlugin {
    @Override
    public String generateReport(List<TrabajoGradoDTO> data) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder().append("[\n");
        for (int i = 0; i < data.size(); i++) {
            TrabajoGradoDTO t = data.get(i);
            sb.append("  {\n")
              .append("    \"id\": \"").append(esc(t.id)).append("\",\n")
              .append("    \"titulo\": \"").append(esc(t.titulo)).append("\",\n")
              .append("    \"fechaFormatoA\": \"")
                    .append(t.fechaFormatoA == null ? "" : t.fechaFormatoA.format(fmt)).append("\",\n")
              .append("    \"estudiante1\": \"").append(esc(t.estudiante1)).append("\",\n")
              .append("    \"estudiante2\": ")
                    .append(t.estudiante2 == null ? "null" : "\"" + esc(t.estudiante2) + "\"").append(",\n")
              .append("    \"director\": \"").append(esc(t.director)).append("\",\n")
              .append("    \"modalidad\": \"").append(esc(t.modalidad)).append("\",\n")
              .append("    \"programa\": \"").append(esc(t.programa)).append("\"\n")
              .append("  }").append(i < data.size() - 1 ? "," : "").append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String esc(String s) {
        return s == null ? "" :
            s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r");
    }
}