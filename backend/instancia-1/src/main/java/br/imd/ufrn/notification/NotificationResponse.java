package br.imd.ufrn.notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long manifestationId,
        NotificationType type,
        String message,
        boolean read,
        LocalDateTime createdAt
) {}
