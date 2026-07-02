package br.imd.ufrn.notification;

import br.imd.ufrn.core.event.ManifestationCreatedEvent;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Cria uma notificação broadcast quando uma manifestação é registrada (ouve o evento do Core). */
@Component
@RequiredArgsConstructor
public class ManifestationNotificationListener {

    private final ManifestationRepository manifestationRepository;
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onManifestationCreated(ManifestationCreatedEvent event) {
        manifestationRepository.findById(event.manifestationId()).ifPresent(m ->
                notificationService.notifyBroadcast(
                        m.getId(),
                        NotificationType.MANIFESTATION_CREATED,
                        "Nova manifestação registrada: " + m.getProtocolNumber()));
    }
}
