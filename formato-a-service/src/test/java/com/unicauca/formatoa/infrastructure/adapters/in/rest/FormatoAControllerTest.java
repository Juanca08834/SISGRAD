package com.unicauca.formatoa.infrastructure.adapters.in.rest;

import com.unicauca.formatoa.application.dto.CrearFormatoRequest;
import com.unicauca.formatoa.application.dto.FormatoResponse;
import com.unicauca.formatoa.application.mapper.FormatoMapper;
import com.unicauca.formatoa.domain.model.FormatoA;
import com.unicauca.formatoa.domain.ports.in.ConsultarFormatoUseCase;
import com.unicauca.formatoa.domain.ports.in.CrearFormatoUseCase;
import com.unicauca.formatoa.domain.ports.in.EvaluarFormatoUseCase;
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
 * Pruebas unitarias del controlador FormatoAController (sin levantar Spring).
 */
@ExtendWith(MockitoExtension.class)
public class FormatoAControllerTest {

    @Mock
    private CrearFormatoUseCase crearFormatoUseCase;

    @Mock
    private EvaluarFormatoUseCase evaluarFormatoUseCase;

    @Mock
    private ConsultarFormatoUseCase consultarFormatoUseCase;

    @Mock
    private FormatoMapper mapper;

    @InjectMocks
    private FormatoAController controller;

    @Test
    void obtenerTodos_deberiaRetornarOk_yListaMapeada() {
        FormatoA dominio = new FormatoA();
        dominio.setId(1L);
        dominio.setTitulo("Proyecto");

        FormatoResponse dto = new FormatoResponse();
        dto.setId(1L);
        dto.setTitulo("Proyecto");

        when(consultarFormatoUseCase.obtenerTodos()).thenReturn(Collections.singletonList(dominio));
        when(mapper.toFormatoResponse(dominio)).thenReturn(dto);

        ResponseEntity<List<FormatoResponse>> response = controller.obtenerTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Proyecto", response.getBody().get(0).getTitulo());
    }

    @Test
    void crear_deberiaRetornarCreated_yDto() {
        CrearFormatoRequest request = new CrearFormatoRequest();
        request.setTitulo("Nuevo");

        FormatoA dominio = new FormatoA();
        dominio.setId(10L);
        dominio.setTitulo("Nuevo");

        FormatoResponse dto = new FormatoResponse();
        dto.setId(10L);
        dto.setTitulo("Nuevo");

        when(mapper.toFormatoA(any(CrearFormatoRequest.class))).thenReturn(dominio);
        when(crearFormatoUseCase.crear(any(FormatoA.class))).thenReturn(dominio);
        when(mapper.toFormatoResponse(dominio)).thenReturn(dto);

        ResponseEntity<FormatoResponse> response = controller.crear(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
        assertEquals("Nuevo", response.getBody().getTitulo());
    }
}
