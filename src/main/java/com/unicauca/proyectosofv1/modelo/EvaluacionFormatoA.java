package com.unicauca.proyectosofv1.modelo;

import java.time.LocalDateTime;

public class EvaluacionFormatoA {
    public final int id;
    public final int formatoAVersionId;
    public final String estado; // 'aprobado' o 'rechazado'
    public final String observaciones;
    public final int evaluadorId;
    public final LocalDateTime fecha;

    public EvaluacionFormatoA(int id, int formatoAVersionId, String estado, String observaciones, int evaluadorId, LocalDateTime fecha) {
        this.id = id;
        this.formatoAVersionId = formatoAVersionId;
        this.estado = estado;
        this.observaciones = observaciones;
        this.evaluadorId = evaluadorId;
        this.fecha = fecha;
    }
}
