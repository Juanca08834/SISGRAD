package com.unicauca.proyectosofv1.excepciones;

public class SISGRADException extends Exception {
    public SISGRADException(String mensaje) { super(mensaje); }
    public SISGRADException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
