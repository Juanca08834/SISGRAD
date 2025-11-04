package com.unicauca.fiet.notification.web;

import com.unicauca.fiet.notification.facade.NotificationFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint de utilidad para probar el servicio de notificaciones (dev).
 * En producción esto se reemplaza por listeners de eventos.
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notificaciones")
public class NotificationController {
    private final NotificationFacade facade;
    public NotificationController(NotificationFacade facade) { this.facade = facade; }

    @Operation(summary = "Probar notificación por email (simulada)")
    @PostMapping("/email")
    public ResponseEntity<Void> send(
            @RequestParam @Email String to,
            @RequestParam @NotBlank String subject,
            @RequestParam @NotBlank String body) {
        facade.notifyByEmail(to, subject, body);
        return ResponseEntity.accepted().build();
    }
}
