package br.imd.ufrn.event;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import br.imd.ufrn.core.event.ManifestationCreatedEvent;
import br.imd.ufrn.core.service.CategorizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManifestationCreatedListenerTest {

    @Mock
    private CategorizationService categorizationService;

    @InjectMocks
    private ManifestationCreatedListener listener;

    @Test
    void onManifestationCreated_deveDispararCategorizacao() {
        listener.onManifestationCreated(new ManifestationCreatedEvent(1L));

        verify(categorizationService).categorize(1L);
    }

    @Test
    void onManifestationCreated_deveEngolirExcecao_paraNaoQuebrarOFluxo() {
        doThrow(new RuntimeException("IA fora do ar"))
                .when(categorizationService).categorize(eq(1L));

        // Não deve propagar exceção (best-effort).
        listener.onManifestationCreated(new ManifestationCreatedEvent(1L));

        verify(categorizationService).categorize(1L);
    }
}
