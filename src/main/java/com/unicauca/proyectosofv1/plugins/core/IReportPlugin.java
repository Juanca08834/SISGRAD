package com.unicauca.proyectosofv1.plugins.core;

import java.util.List;

public interface IReportPlugin {
    /**
     * Genera un reporte (HTML, JSON, PDF...) a partir de los datos.
     */
    String generateReport(List<TrabajoGradoDTO> data);
}