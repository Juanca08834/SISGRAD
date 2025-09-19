// ServicioConsultaEstado.java
package com.unicauca.proyectosofv1.servicios;

import com.unicauca.proyectosofv1.modelo.EstadoProyecto;

public interface ServicioConsultaEstado {
    EstadoProyecto consultar(long proyectoId);
}
