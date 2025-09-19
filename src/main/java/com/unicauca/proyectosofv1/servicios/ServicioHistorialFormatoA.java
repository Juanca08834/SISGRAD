package com.unicauca.proyectosofv1.servicios;

import com.unicauca.proyectosofv1.modelo.FormatoA;
import java.util.List;

public interface ServicioHistorialFormatoA {
    List<FormatoA> listar(long proyectoId);
}
