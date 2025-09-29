package com.unicauca.proyectosofv1.notificacion;

import java.util.List;

public interface Notificador {
    void enviar(String asunto, String mensaje, List<String> destinatarios);
}
