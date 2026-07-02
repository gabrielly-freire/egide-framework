package br.imd.ufrn.categorization;

import static org.mockito.Mockito.verify;

import br.imd.ufrn.core.event.ManifestationCreatedEvent;
import br.imd.ufrn.core.service.CategorizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManifestationCategorizationListenerTest {

    @Mock
    private CategorizationService categorizationService;

    @InjectMocks
    private ManifestationCategorizationListener listener;

    @Test
    void onManifestationCreated_deveDispararCategorizacaoDaManifestacao() {
        listener.onManifestationCreated(new ManifestationCreatedEvent(42L));

        verify(categorizationService).categorize(42L);
    }
}
