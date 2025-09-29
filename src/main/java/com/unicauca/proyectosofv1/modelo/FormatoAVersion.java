package com.unicauca.proyectosofv1.modelo;

public class FormatoAVersion {
    public final int id;
    public final int trabajoGradoId;
    public final int intento;
    public final String objetivoGeneral;
    public final String objetivosEspecificos;
    public final String pdfPath;
    public final String cartaAceptacionPath;
    public final Integer codirectorId;

    public FormatoAVersion(int id, int trabajoGradoId, int intento, String objetivoGeneral, String objetivosEspecificos,
                           String pdfPath, String cartaAceptacionPath, Integer codirectorId) {
        this.id = id;
        this.trabajoGradoId = trabajoGradoId;
        this.intento = intento;
        this.objetivoGeneral = objetivoGeneral;
        this.objetivosEspecificos = objetivosEspecificos;
        this.pdfPath = pdfPath;
        this.cartaAceptacionPath = cartaAceptacionPath;
        this.codirectorId = codirectorId;
    }
}
