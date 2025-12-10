package com.unicauca.formatoa.domain.service;

import com.unicauca.formatoa.domain.model.EstadoFormato;
import com.unicauca.formatoa.domain.model.FormatoA;
import com.unicauca.formatoa.domain.ports.out.EvaluacionPersistencePort;
import com.unicauca.formatoa.domain.ports.out.FormatoPersistencePort;
import com.unicauca.formatoa.domain.ports.out.NotificationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del servicio de dominio FormatoAService (sin Spring Context).
 */
@ExtendWith(MockitoExtension.class)
public class FormatoAServiceTest {

    @Mock
    private FormatoPersistencePort formatoPersistencePort;

    @Mock
    private EvaluacionPersistencePort evaluacionPersistencePort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private FormatoAService formatoAService;

    @Test
    void crear_deberiaInicializarIntentoYEstado_yNotificar() {
        FormatoA entrada = new FormatoA();
        entrada.setTitulo("Proyecto de prueba");

        FormatoA guardado = new FormatoA();
        guardado.setId(1L);
        guardado.setTitulo("Proyecto de prueba");
        guardado.setNumeroIntento(1);
        guardado.setEstado(EstadoFormato.EN_PRIMERA_EVALUACION);

        when(formatoPersistencePort.guardar(any(FormatoA.class))).thenReturn(guardado);

        FormatoA resultado = formatoAService.crear(entrada);

        assertEquals(1L, resultado.getId());
        assertEquals(1, resultado.getNumeroIntento());
        assertEquals(EstadoFormato.EN_PRIMERA_EVALUACION, resultado.getEstado());

        verify(formatoPersistencePort).guardar(any(FormatoA.class));
        verify(notificationPort).notificarNuevoFormato(
                eq("coordinador@unicauca.edu.co"),
                eq(1L),
                eq("Proyecto de prueba")
        );
    }

    @Test
    void obtenerPorDocente_deberiaDelegarEnPuertoPersistencia() {
        FormatoA f1 = new FormatoA();
        f1.setId(1L);
        f1.setTitulo("P1");

        FormatoA f2 = new FormatoA();
        f2.setId(2L);
        f2.setTitulo("P2");

        when(formatoPersistencePort.buscarPorDocente(10L)).thenReturn(Arrays.asList(f1, f2));

        List<FormatoA> lista = formatoAService.obtenerPorDocente(10L);

        assertEquals(2, lista.size());
        verify(formatoPersistencePort).buscarPorDocente(10L);
    }

    @Test
    void obtenerPendientes_deberiaUsarEstadoEnPrimeraEvaluacion() {
        FormatoA f1 = new FormatoA();
        f1.setId(1L);
        when(formatoPersistencePort.buscarPorEstado(EstadoFormato.EN_PRIMERA_EVALUACION))
                .thenReturn(Collections.singletonList(f1));

        List<FormatoA> lista = formatoAService.obtenerPendientes();

        assertEquals(1, lista.size());
        verify(formatoPersistencePort).buscarPorEstado(EstadoFormato.EN_PRIMERA_EVALUACION);
    }

    @Test
    void obtenerTodos_deberiaDelegarEnPuertoPersistencia() {
        when(formatoPersistencePort.buscarTodos()).thenReturn(Collections.emptyList());

        List<FormatoA> lista = formatoAService.obtenerTodos();

        assertEquals(0, lista.size());
        verify(formatoPersistencePort).buscarTodos();
    }
}
