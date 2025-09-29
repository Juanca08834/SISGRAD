package com.unicauca.proyectosofv1.servicios;

import com.unicauca.proyectosofv1.excepciones.SISGRADException;
import com.unicauca.proyectosofv1.modelo.EstadoFormatoA;
import com.unicauca.proyectosofv1.modelo.FormatoAVersion;

import java.util.List;

public interface ServicioFormatoA {

    /**
     * Crea/reutiliza un TG y registra una nueva versión del Formato A.
     */
    FormatoAVersion subirFormatoA(
            int trabajoGradoId,
            String titulo,
            String modalidad, // 'Investigación' | 'Práctica Profesional'
            int directorId,
            Integer codirectorId,
            int estudiante1Id,
            Integer estudiante2Id,
            int programaId,
            String objetivoGeneral,
            String objetivosEspecificos,
            String pdfPath,
            String cartaAceptacionPath // requerido si modalidad es Práctica Profesional
    ) throws SISGRADException;

    /**
     * Aprueba o rechaza una versión específica del Formato A.
     */
    void evaluarFormatoA(int formatoAVersionId, String estado, String observaciones, int evaluadorUsuarioId)
            throws SISGRADException;

    /**
     * Devuelve el estado actual de un TG según evaluaciones e intentos.
     */
    EstadoFormatoA estadoActual(int trabajoGradoId);

    /**
     * Número de intentos registrados para un TG.
     */
    int contarIntentos(int trabajoGradoId);

    /**
     * Lista la ÚLTIMA versión de cada TG que aún esté pendiente de evaluación.
     */
    List<FormatoAVersion> listarPendientes();

    /**
     * Busca el TG más reciente asociado a un estudiante por email
     * institucional.
     */
    Integer buscarTrabajoIdPorEstudianteEmail(String email);

    /**
     * Busca el TG más reciente asociado a un docente (director o codirector)
     * por email.
     */
    Integer buscarTrabajoIdPorDocenteEmail(String email);

    /**
     * Historial simplificado: intento, estado (o pendiente), observaciones,
     * rutas de PDF y carta.
     */
    List<String[]> historialSimple(int trabajoGradoId);

    public static record DatosFormatoA(
            String titulo,
            String modalidad,
            int directorId,
            Integer codirectorId,
            int estudiante1Id,
            Integer estudiante2Id,
            int programaId,
            String objetivoGeneral,
            String objetivosEspecificos
            ) {

    }

    DatosFormatoA cargarDatosParaReintento(int trabajoGradoId);

}
