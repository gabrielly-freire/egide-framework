package br.imd.ufrn.categorization;

import br.imd.ufrn.core.event.ManifestationCreatedEvent;
import br.imd.ufrn.core.service.CategorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Gancho da Instância 2: ao registrar uma manifestação, dispara sua categorização.
 *
 * <p>O Core publica {@link ManifestationCreatedEvent} em {@code ManifestationService.create()}, mas
 * não o consome — cabe à instância reagir. Aqui a escuta é <b>síncrona</b>: como a triagem é uma
 * heurística em memória ({@link UniversityCategorizationStrategy}), sem chamada externa, ela roda
 * dentro da mesma transação do registro e a categoria já retorna na resposta do POST. A Instância 1,
 * que depende de IA externa (lenta), usa escuta assíncrona.
 */
@Component
@RequiredArgsConstructor
public class ManifestationCategorizationListener {

    private final CategorizationService categorizationService;

    @EventListener
    public void onManifestationCreated(ManifestationCreatedEvent event) {
        categorizationService.categorize(event.manifestationId());
    }
}
