package com.unicauca.proyectosofv1.notificacion;

import java.util.List;
import java.util.logging.Logger;

public class NotificadorLogger implements Notificador {
    private static final Logger LOG = Logger.getLogger(NotificadorLogger.class.getName());

    @Override
    public void enviar(String asunto, String mensaje, List<String> destinatarios) {
        LOG.info(() -> "[NOTIFICACIÓN] " + asunto + " :: " + destinatarios + " :: " + mensaje);
    }
}
