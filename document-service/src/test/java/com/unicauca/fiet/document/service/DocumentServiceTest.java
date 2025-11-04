package com.unicauca.fiet.document.service;

import com.unicauca.fiet.common.events.AnteproyectoSubmittedEvent;
import com.unicauca.fiet.common.messaging.EventPublisher;
import com.unicauca.fiet.document.domain.Anteproyecto;
import com.unicauca.fiet.document.repo.AnteproyectoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas del servicio de Documentos (DocumentService)")
class DocumentServiceTest {

    private AnteproyectoRepository repository;
    private EventPublisher eventPublisher;
    private DocumentService service;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(AnteproyectoRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        service = new DocumentService(repository, eventPublisher);
    }

    @Test
    @DisplayName("Debe guardar anteproyecto y publicar evento de notificación a jefatura")
    void submitAnteproyecto_savesAndPublishesEvent() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UUID projectId = UUID.randomUUID();
        String docenteEmail = "docente@unicauca.edu.co";
        String pdfUrl = "http://storage.com/anteproyecto.pdf";
        String deptHeadEmail = "jefe@unicauca.edu.co";

        Anteproyecto result = service.submitAnteproyecto(projectId, docenteEmail, pdfUrl, deptHeadEmail);

        assertNotNull(result);
        assertEquals(projectId, result.getProjectId());
        assertEquals(docenteEmail, result.getDocenteEmail());
        assertEquals(pdfUrl, result.getPdfUrl());
        assertNotNull(result.getFechaEnvio());

        verify(repository, times(1)).save(any(Anteproyecto.class));

        ArgumentCaptor<AnteproyectoSubmittedEvent> captor = ArgumentCaptor.forClass(AnteproyectoSubmittedEvent.class);
        verify(eventPublisher, times(1)).publish(captor.capture());

        AnteproyectoSubmittedEvent event = captor.getValue();
        assertEquals(projectId, event.getProjectId());
        assertEquals(deptHeadEmail, event.getDeptHeadEmail());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("Debe registrar fecha de envío automáticamente al guardar anteproyecto")
    void submitAnteproyecto_setsFechaEnvioAutomatically() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Instant before = Instant.now();
        Anteproyecto result = service.submitAnteproyecto(
                UUID.randomUUID(),
                "docente@unicauca.edu.co",
                "http://pdf.com/anteproyecto.pdf",
                "jefe@unicauca.edu.co"
        );
        Instant after = Instant.now();

        assertNotNull(result.getFechaEnvio());
        assertTrue(!result.getFechaEnvio().isBefore(before));
        assertTrue(!result.getFechaEnvio().isAfter(after));
    }

    @Test
    @DisplayName("Debe listar todos los anteproyectos ordenados por fecha descendente")
    void listAnteproyectos_returnsAllOrderedByDate() {
        Anteproyecto ante1 = Anteproyecto.builder()
                .projectId(UUID.randomUUID())
                .docenteEmail("docente1@unicauca.edu.co")
                .pdfUrl("http://pdf1.com")
                .fechaEnvio(Instant.now().minusSeconds(3600))
                .build();

        Anteproyecto ante2 = Anteproyecto.builder()
                .projectId(UUID.randomUUID())
                .docenteEmail("docente2@unicauca.edu.co")
                .pdfUrl("http://pdf2.com")
                .fechaEnvio(Instant.now())
                .build();

        when(repository.findAllByOrderByFechaEnvioDesc()).thenReturn(List.of(ante2, ante1));

        List<Anteproyecto> result = service.listAnteproyectos();

        assertEquals(2, result.size());
        assertEquals("docente2@unicauca.edu.co", result.get(0).getDocenteEmail());
        assertEquals("docente1@unicauca.edu.co", result.get(1).getDocenteEmail());

        verify(repository, times(1)).findAllByOrderByFechaEnvioDesc();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay anteproyectos")
    void listAnteproyectos_emptyList_returnsEmpty() {
        when(repository.findAllByOrderByFechaEnvioDesc()).thenReturn(List.of());

        List<Anteproyecto> result = service.listAnteproyectos();

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAllByOrderByFechaEnvioDesc();
    }

    @Test
    @DisplayName("Debe manejar múltiples anteproyectos del mismo docente")
    void submitAnteproyecto_sameDocenteMultipleTimes_savesEach() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String sameDocente = "docente@unicauca.edu.co";
        UUID project1 = UUID.randomUUID();
        UUID project2 = UUID.randomUUID();

        Anteproyecto ante1 = service.submitAnteproyecto(
                project1, sameDocente, "http://pdf1.com", "jefe@unicauca.edu.co"
        );
        Anteproyecto ante2 = service.submitAnteproyecto(
                project2, sameDocente, "http://pdf2.com", "jefe@unicauca.edu.co"
        );

        assertNotEquals(ante1.getProjectId(), ante2.getProjectId());
        assertEquals(sameDocente, ante1.getDocenteEmail());
        assertEquals(sameDocente, ante2.getDocenteEmail());

        verify(repository, times(2)).save(any(Anteproyecto.class));
        verify(eventPublisher, times(2)).publish(any(AnteproyectoSubmittedEvent.class));
    }

    @Test
    @DisplayName("Debe publicar evento con el email correcto del jefe de departamento")
    void submitAnteproyecto_publishesEventWithCorrectDeptHeadEmail() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String specificDeptHead = "jefatura.sistemas@unicauca.edu.co";
        service.submitAnteproyecto(
                UUID.randomUUID(),
                "docente@unicauca.edu.co",
                "http://pdf.com/ante.pdf",
                specificDeptHead
        );

        ArgumentCaptor<AnteproyectoSubmittedEvent> captor = ArgumentCaptor.forClass(AnteproyectoSubmittedEvent.class);
        verify(eventPublisher).publish(captor.capture());

        assertEquals(specificDeptHead, captor.getValue().getDeptHeadEmail());
    }
}

