package com.unicauca.anteproyecto.domain.service;

import com.unicauca.anteproyecto.domain.model.Anteproyecto;
import com.unicauca.anteproyecto.domain.model.EvaluadorAsignado;
import com.unicauca.anteproyecto.domain.ports.in.AsignarEvaluadoresUseCase;
import com.unicauca.anteproyecto.domain.ports.in.ConsultarAnteproyectoUseCase;
import com.unicauca.anteproyecto.domain.ports.in.CrearAnteproyectoUseCase;
import com.unicauca.anteproyecto.domain.ports.out.AnteproyectoPersistencePort;
import com.unicauca.anteproyecto.domain.ports.out.EvaluadorPersistencePort;
import com.unicauca.anteproyecto.domain.ports.out.NotificationPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de dominio para Anteproyectos
 * UBICACIÓN: domain/service/AnteproyectoService.java
 */
@Service
public class AnteproyectoService implements CrearAnteproyectoUseCase, 
                                            AsignarEvaluadoresUseCase,
                                            ConsultarAnteproyectoUseCase {

    private final AnteproyectoPersistencePort anteproyectoPort;
    private final EvaluadorPersistencePort evaluadorPort;
    private final NotificationPort notificationPort;

    public AnteproyectoService(AnteproyectoPersistencePort anteproyectoPort,
                              EvaluadorPersistencePort evaluadorPort,
                              NotificationPort notificationPort) {
        this.anteproyectoPort = anteproyectoPort;
        this.evaluadorPort = evaluadorPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public Anteproyecto crear(Anteproyecto anteproyecto) {
        // Guardar anteproyecto
        Anteproyecto guardado = anteproyectoPort.guardar(anteproyecto);
        
        // Notificar al jefe de departamento
        try {
            notificationPort.notificarNuevoAnteproyecto(
                "jefe.departamento@unicauca.edu.co",
                guardado.getId()
            );
        } catch (Exception e) {
            System.err.println("Error al notificar: " + e.getMessage());
        }
        
        return guardado;
    }

    @Override
    public List<EvaluadorAsignado> asignarEvaluadores(Long anteproyectoId, 
                                                       List<EvaluadorAsignado> evaluadores) {
        // REGLA DE NEGOCIO: Deben ser exactamente 2 evaluadores
        if (evaluadores.size() != 2) {
            throw new RuntimeException("Debe asignar exactamente 2 evaluadores al anteproyecto. Actualmente ha seleccionado " + evaluadores.size() + ".");
        }
        
        // Buscar el anteproyecto
        Anteproyecto anteproyecto = anteproyectoPort.buscarPorId(anteproyectoId)
            .orElseThrow(() -> new RuntimeException("No se encontró el anteproyecto solicitado. Verifique el identificador e intente nuevamente."));

        // REGLA DE NEGOCIO: Verificar que puede asignar evaluadores
        if (!anteproyecto.puedeAsignarEvaluadores()) {
            throw new RuntimeException("Este anteproyecto ya tiene evaluadores asignados y no se pueden modificar.");
        }
        
        // Asignar el anteproyecto ID a cada evaluador
        evaluadores.forEach(e -> e.setAnteproyectoId(anteproyectoId));
        
        // Guardar evaluadores
        List<EvaluadorAsignado> guardados = evaluadorPort.guardarTodos(evaluadores);
        
        // Cambiar estado del anteproyecto
        anteproyecto.iniciarEvaluacion();
        anteproyectoPort.actualizar(anteproyecto);
        
        // Notificar a cada evaluador (REQUISITO 8)
        guardados.forEach(evaluador -> {
            try {
                notificationPort.notificarEvaluadoresAsignados(
                    evaluador.getEmailEvaluador(),
                    anteproyectoId
                );
                evaluador.marcarComoNotificado();
                evaluadorPort.guardar(evaluador);
            } catch (Exception e) {
                System.err.println("Error al notificar evaluador: " + e.getMessage());
            }
        });
        
        return guardados;
    }

    @Override
    public Anteproyecto obtenerPorId(Long id) {
        return anteproyectoPort.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el anteproyecto solicitado."));
    }

    @Override
    public List<Anteproyecto> obtenerTodos() {
        return anteproyectoPort.buscarTodos();
    }

    @Override
    public List<EvaluadorAsignado> obtenerEvaluadores(Long anteproyectoId) {
        return evaluadorPort.buscarPorAnteproyecto(anteproyectoId);
    }
}