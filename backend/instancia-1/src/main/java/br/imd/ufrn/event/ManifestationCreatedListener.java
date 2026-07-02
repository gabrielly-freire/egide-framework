package br.imd.ufrn.event;

import br.imd.ufrn.core.event.ManifestationCreatedEvent;
import br.imd.ufrn.core.service.CategorizationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Dispara a categorização por IA de forma assíncrona quando uma manifestação é registrada.
 *
 * <p>Usa {@code AFTER_COMMIT} para garantir que a manifestação já esteja persistida antes de a IA
 * lê-la, e o pool {@code aiExecutor} para não bloquear a thread da requisição. Falhas são
 * capturadas e logadas — categorização é best-effort e não deve afetar o registro.
 */
@Component
@RequiredArgsConstructor
public class ManifestationCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(ManifestationCreatedListener.class);

    private final CategorizationService categorizationService;

    @Async("aiExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onManifestationCreated(ManifestationCreatedEvent event) {
        try {
            categorizationService.categorize(event.manifestationId());
        } catch (Exception e) {
            log.error("Falha ao categorizar a manifestação {}", event.manifestationId(), e);
        }
    }
}
