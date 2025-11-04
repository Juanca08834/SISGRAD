package com.unicauca.fiet.notification.email;

/**
 * Puerto para envío de emails. Permite cambiar de proveedor (Adapter pattern).
 * En el perfil 'dev', se usa un emisor que solo escribe en logs.
 */
public interface EmailSender {
    /** Envía un correo a una dirección con asunto y contenido. */
    void send(String to, String subject, String body);
}
