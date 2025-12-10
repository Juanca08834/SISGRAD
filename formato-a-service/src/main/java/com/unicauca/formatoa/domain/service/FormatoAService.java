package com.unicauca.formatoa.domain.service;

import com.unicauca.formatoa.domain.model.Evaluacion;
import com.unicauca.formatoa.domain.model.FormatoA;
import com.unicauca.formatoa.domain.model.EstadoFormato;
import com.unicauca.formatoa.domain.ports.in.ConsultarFormatoUseCase;
import com.unicauca.formatoa.domain.ports.in.CrearFormatoUseCase;
import com.unicauca.formatoa.domain.ports.in.EvaluarFormatoUseCase;
import com.unicauca.formatoa.domain.ports.out.EvaluacionPersistencePort;
import com.unicauca.formatoa.domain.ports.out.FormatoPersistencePort;
import com.unicauca.formatoa.domain.ports.out.NotificationPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de dominio para Formato A
 * Implementa la lógica de negocio principal
 */
@Service
public class FormatoAService implements CrearFormatoUseCase, EvaluarFormatoUseCase, 
                                        ConsultarFormatoUseCase {

    private final FormatoPersistencePort formatoPersistencePort;
    private final EvaluacionPersistencePort evaluacionPersistencePort;
    private final NotificationPort notificationPort;

    public FormatoAService(FormatoPersistencePort formatoPersistencePort,
                          EvaluacionPersistencePort evaluacionPersistencePort,
                          NotificationPort notificationPort) {
        this.formatoPersistencePort = formatoPersistencePort;
        this.evaluacionPersistencePort = evaluacionPersistencePort;
        this.notificationPort = notificationPort;
    }

    @Override
    public FormatoA crear(FormatoA formatoA) {
        // REGLA DE NEGOCIO: Un formato nuevo siempre inicia en primer intento
        formatoA.setNumeroIntento(1);
        formatoA.setEstado(EstadoFormato.EN_PRIMERA_EVALUACION);
        
        // Guardar en base de datos
        FormatoA formatoGuardado = formatoPersistencePort.guardar(formatoA);
        
        // Enviar notificación asíncrona al coordinador
        try {
            notificationPort.notificarNuevoFormato(
                "coordinador@unicauca.edu.co", // En producción, obtener del sistema
                formatoGuardado.getId(),
                formatoGuardado.getTitulo()
            );
        } catch (Exception e) {
            // Log del error pero no fallar la operación
            System.err.println("Error al enviar notificación: " + e.getMessage());
        }
        
        return formatoGuardado;
    }

    @Override
    public FormatoA crearNuevaVersion(Long formatoAId, FormatoA nuevoFormato) {
        // Buscar el formato original
        FormatoA formatoOriginal = formatoPersistencePort.buscarPorId(formatoAId)
            .orElseThrow(() -> new RuntimeException("No se encontró el Formato A solicitado. Verifique el identificador e intente nuevamente."));

        // REGLA DE NEGOCIO: Verificar que puede crear nueva versión
        if (formatoOriginal.getEstado() == EstadoFormato.ACEPTADO) {
            throw new RuntimeException("No es posible crear una nueva versión porque el Formato A ya fue aprobado.");
        }
        
        if (formatoOriginal.getEstado() == EstadoFormato.RECHAZADO) {
            throw new RuntimeException("No es posible crear una nueva versión porque el Formato A fue rechazado definitivamente después de 3 intentos.");
        }
        
        // REGLA DE NEGOCIO: Solo se puede crear nueva versión si fue RECHAZADO en evaluación
        if (formatoOriginal.getEstado() == EstadoFormato.EN_PRIMERA_EVALUACION ||
            formatoOriginal.getEstado() == EstadoFormato.EN_SEGUNDA_EVALUACION ||
            formatoOriginal.getEstado() == EstadoFormato.EN_TERCERA_EVALUACION) {
            throw new RuntimeException("No puede crear una nueva versión mientras el Formato A está pendiente de evaluación. Espere a que el coordinador lo evalúe.");
        }

        // REGLA DE NEGOCIO: Verificar límite de intentos
        if (formatoOriginal.esUltimoIntento()) {
            throw new RuntimeException("Ha alcanzado el límite máximo de 3 intentos permitidos para este Formato A.");
        }
        
        // Incrementar el intento del formato original
        formatoOriginal.incrementarIntento();
        
        // Actualizar datos con la nueva versión
        formatoOriginal.setTitulo(nuevoFormato.getTitulo());
        formatoOriginal.setModalidad(nuevoFormato.getModalidad());
        formatoOriginal.setDirector(nuevoFormato.getDirector());
        formatoOriginal.setCodirector(nuevoFormato.getCodirector());
        formatoOriginal.setObjetivoGeneral(nuevoFormato.getObjetivoGeneral());
        formatoOriginal.setObjetivosEspecificos(nuevoFormato.getObjetivosEspecificos());
        formatoOriginal.setArchivoUrl(nuevoFormato.getArchivoUrl());
        formatoOriginal.actualizarFechaModificacion();
        
        // Guardar la nueva versión
        FormatoA formatoActualizado = formatoPersistencePort.actualizar(formatoOriginal);
        
        // Notificar al coordinador
        try {
            notificationPort.notificarNuevoFormato(
                "coordinador@unicauca.edu.co",
                formatoActualizado.getId(),
                "Nueva versión (Intento " + formatoActualizado.getNumeroIntento() + 
                "): " + formatoActualizado.getTitulo()
            );
        } catch (Exception e) {
            System.err.println("Error al enviar notificación: " + e.getMessage());
        }
        
        return formatoActualizado;
    }

    @Override
    public Evaluacion evaluar(Evaluacion evaluacion) {
        // Buscar el formato a evaluar
        FormatoA formato = formatoPersistencePort.buscarPorId(evaluacion.getFormatoAId())
            .orElseThrow(() -> new RuntimeException("No se encontró el Formato A solicitado para evaluar."));

        // REGLA DE NEGOCIO: Verificar que puede ser evaluado
        if (!formato.puedeSerEditado()) {
            throw new RuntimeException("Este Formato A ya fue evaluado definitivamente y no puede recibir más evaluaciones.");
        }
        
        // Guardar la evaluación
        Evaluacion evaluacionGuardada = evaluacionPersistencePort.guardar(evaluacion);
        
        // Actualizar el estado del formato según la evaluación
        if (evaluacion.esAprobada()) {
            formato.aprobar();
        } else if (evaluacion.esRechazada()) {
            formato.rechazar();
        }
        
        formatoPersistencePort.actualizar(formato);
        
        // Notificar al docente
        try {
            notificationPort.notificarEvaluacion(
                "docente@unicauca.edu.co", // En producción, obtener del formato
                formato.getId(),
                evaluacion.getResultado().getDescripcion(),
                evaluacion.getObservaciones()
            );
        } catch (Exception e) {
            System.err.println("Error al enviar notificación: " + e.getMessage());
        }
        
        return evaluacionGuardada;
    }

    @Override
    public FormatoA obtenerPorId(Long id) {
        return formatoPersistencePort.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Formato A no encontrado"));
    }

    @Override
    public List<FormatoA> obtenerPorDocente(Long docenteId) {
        return formatoPersistencePort.buscarPorDocente(docenteId);
    }

    @Override
    public List<FormatoA> obtenerPendientes() {
        // Obtener formatos en primera, segunda y tercera evaluación
        List<FormatoA> pendientes = new java.util.ArrayList<>();
        pendientes.addAll(formatoPersistencePort.buscarPorEstado(EstadoFormato.EN_PRIMERA_EVALUACION));
        pendientes.addAll(formatoPersistencePort.buscarPorEstado(EstadoFormato.EN_SEGUNDA_EVALUACION));
        pendientes.addAll(formatoPersistencePort.buscarPorEstado(EstadoFormato.EN_TERCERA_EVALUACION));
        return pendientes;
    }

    @Override
    public List<FormatoA> obtenerTodos() {
        return formatoPersistencePort.buscarTodos();
    }
}