// RepositorioFormatoA.java
package com.unicauca.proyectosofv1.repositorio;

import com.unicauca.proyectosofv1.modelo.FormatoA;
import java.util.List;

public interface RepositorioFormatoA {
    long guardar(FormatoA formato);           // inserta y retorna id
    FormatoA buscarUltimoPorProyecto(long proyectoId);
    List<FormatoA> listarPorProyecto(long proyectoId);
}
