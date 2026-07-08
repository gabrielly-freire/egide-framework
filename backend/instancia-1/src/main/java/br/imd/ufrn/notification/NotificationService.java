package br.imd.ufrn.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    /** Cria uma notificação broadcast (visível a todos) para uma manifestação. */
    @Transactional
    public void notifyBroadcast(Long manifestationId, NotificationType type, String message) {
        Notification n = new Notification();
        n.setManifestationId(manifestationId);
        n.setType(type);
        n.setMessage(message);
        n.setReadFlag(false);
        repository.save(n);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> findForUser(Long userId) {
        return repository.findByRecipientUserIdOrRecipientUserIdIsNullOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public NotificationResponse markRead(Long notificationId) {
        Notification n = repository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notificação não encontrada"));
        n.setReadFlag(true);
        return toResponse(repository.save(n));
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getManifestationId(), n.getType(), n.getMessage(),
                n.isReadFlag(), n.getCreatedAt());
    }
}
