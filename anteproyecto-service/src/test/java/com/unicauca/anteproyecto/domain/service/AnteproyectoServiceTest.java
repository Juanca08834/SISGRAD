package com.unicauca.anteproyecto.domain.service;

import com.unicauca.anteproyecto.domain.model.Anteproyecto;
import com.unicauca.anteproyecto.domain.model.EvaluadorAsignado;
import com.unicauca.anteproyecto.domain.ports.out.AnteproyectoPersistencePort;
import com.unicauca.anteproyecto.domain.ports.out.EvaluadorPersistencePort;
import com.unicauca.anteproyecto.domain.ports.out.NotificationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del servicio de dominio AnteproyectoService (sin Spring Context).
 */
@ExtendWith(MockitoExtension.class)
public class AnteproyectoServiceTest {

    @Mock
    private AnteproyectoPersistencePort anteproyectoPort;

    @Mock
    private EvaluadorPersistencePort evaluadorPort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private AnteproyectoService anteproyectoService;

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornar() {
        Anteproyecto ap = new Anteproyecto();
        ap.setId(1L);
        ap.setFormatoAId(100L);

        when(anteproyectoPort.buscarPorId(1L)).thenReturn(Optional.of(ap));

        Anteproyecto res = anteproyectoService.obtenerPorId(1L);

        assertEquals(1L, res.getId());
        verify(anteproyectoPort).buscarPorId(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(anteproyectoPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> anteproyectoService.obtenerPorId(99L));
        verify(anteproyectoPort).buscarPorId(99L);
    }

    @Test
    void asignarEvaluadores_siNoSonDos_deberiaLanzarExcepcion() {
        List<EvaluadorAsignado> lista = Collections.singletonList(new EvaluadorAsignado());
        assertThrows(RuntimeException.class, () -> anteproyectoService.asignarEvaluadores(1L, lista));
    }

    @Test
    void obtenerTodos_deberiaDelegarEnPuerto() {
        Anteproyecto a = new Anteproyecto();
        a.setId(1L);
        Anteproyecto b = new Anteproyecto();
        b.setId(2L);

        when(anteproyectoPort.buscarTodos()).thenReturn(Arrays.asList(a, b));

        List<Anteproyecto> lista = anteproyectoService.obtenerTodos();

        assertEquals(2, lista.size());
        verify(anteproyectoPort).buscarTodos();
    }
}
