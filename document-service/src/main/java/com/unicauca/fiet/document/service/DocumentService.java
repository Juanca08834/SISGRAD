package com.unicauca.fiet.document.service;

import com.unicauca.fiet.common.events.AnteproyectoSubmittedEvent;
import com.unicauca.fiet.common.messaging.EventPublisher;
import com.unicauca.fiet.document.domain.Anteproyecto;
import com.unicauca.fiet.document.repo.AnteproyectoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Servicio de aplicación para manejo del anteproyecto. */
@Service
public class DocumentService {

    private final AnteproyectoRepository repository;
    private final EventPublisher eventPublisher;

    public DocumentService(AnteproyectoRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    /** Registra un anteproyecto y publica evento para notificar a jefatura (req. 6). */
    public Anteproyecto submitAnteproyecto(UUID projectId, String docenteEmail, String pdfUrl, String deptHeadEmail) {
        Anteproyecto a = Anteproyecto.builder()
                .projectId(projectId)
                .docenteEmail(docenteEmail)
                .pdfUrl(pdfUrl)
                .fechaEnvio(Instant.now())
                .build();
        a = repository.save(a);

        eventPublisher.publish(AnteproyectoSubmittedEvent.builder()
                .projectId(projectId)
                .deptHeadEmail(deptHeadEmail)
                .occurredOn(Instant.now())
                .build());
        return a;
    }

    /** Lista anteproyectos enviados (req. 7 - listar). */
    public List<Anteproyecto> listAnteproyectos() {
        return repository.findAllByOrderByFechaEnvioDesc();
    }
}
