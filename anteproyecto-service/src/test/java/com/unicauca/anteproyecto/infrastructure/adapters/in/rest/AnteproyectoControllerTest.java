package com.unicauca.anteproyecto.infrastructure.adapters.in.rest;

import com.unicauca.anteproyecto.application.dto.AnteproyectoResponse;
import com.unicauca.anteproyecto.application.dto.CrearAnteproyectoRequest;
import com.unicauca.anteproyecto.application.dto.EvaluadorDto;
import com.unicauca.anteproyecto.application.mapper.AnteproyectoMapper;
import com.unicauca.anteproyecto.domain.model.Anteproyecto;
import com.unicauca.anteproyecto.domain.model.EvaluadorAsignado;
import com.unicauca.anteproyecto.domain.ports.in.AsignarEvaluadoresUseCase;
import com.unicauca.anteproyecto.domain.ports.in.ConsultarAnteproyectoUseCase;
import com.unicauca.anteproyecto.domain.ports.in.CrearAnteproyectoUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del controlador AnteproyectoController (sin Spring).
 */
@ExtendWith(MockitoExtension.class)
public class AnteproyectoControllerTest {

    @Mock
    private CrearAnteproyectoUseCase crearUseCase;

    @Mock
    private AsignarEvaluadoresUseCase asignarUseCase;

    @Mock
    private ConsultarAnteproyectoUseCase consultarUseCase;

    @Mock
    private AnteproyectoMapper mapper;

    @InjectMocks
    private AnteproyectoController controller;

    @Test
    void crear_deberiaRetornarCreated() {
        CrearAnteproyectoRequest request = new CrearAnteproyectoRequest();
        request.setFormatoAId(100L);
        request.setArchivoUrl("http://example.com/archivo.pdf");
        request.setDocenteId(1L);

        Anteproyecto dominio = new Anteproyecto();
        dominio.setId(5L);
        dominio.setFormatoAId(100L);

        AnteproyectoResponse dto = new AnteproyectoResponse();
        dto.setId(5L);
        dto.setFormatoAId(100L);

        when(mapper.toAnteproyecto(any(CrearAnteproyectoRequest.class))).thenReturn(dominio);
        when(crearUseCase.crear(any(Anteproyecto.class))).thenReturn(dominio);
        when(mapper.toResponse(dominio)).thenReturn(dto);

        ResponseEntity<AnteproyectoResponse> response = controller.crear(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(5L, response.getBody().getId());
    }

    @Test
    void obtenerEvaluadores_deberiaRetornarOk() {
        EvaluadorAsignado eval = new EvaluadorAsignado();
        eval.setNombreEvaluador("Docente");
        eval.setEmailEvaluador("docente@unicauca.edu.co");

        EvaluadorDto dto = new EvaluadorDto();
        dto.setNombreEvaluador("Docente");
        dto.setEmailEvaluador("docente@unicauca.edu.co");

        when(consultarUseCase.obtenerEvaluadores(1L))
                .thenReturn(Collections.singletonList(eval));
        when(mapper.toEvaluadorDto(eval)).thenReturn(dto);

        ResponseEntity<List<EvaluadorDto>> response = controller.obtenerEvaluadores(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Docente", response.getBody().get(0).getNombreEvaluador());
    }
}
