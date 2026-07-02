package br.imd.ufrn.anonymization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.imd.ufrn.ai.AiAnonymizationClient;
import br.imd.ufrn.ai.dto.AnonymizationAiRequest;
import br.imd.ufrn.ai.dto.AnonymizationAiResponse;
import br.imd.ufrn.core.anonymization.AnonymizationContext;
import br.imd.ufrn.core.anonymization.AnonymizationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComplianceAnonymizationStrategyTest {

    private static final String TITULO = "Denuncia contra Joao Silva";
    private static final String DESCRICAO = "O gestor Joao Silva desviou verbas.";
    private static final String TITULO_ANON = "Denuncia contra Pedro Souza";
    private static final String DESCRICAO_ANON = "O coordenador Pedro Souza desviou verbas.";

    @Mock
    private AiAnonymizationClient aiAnonymizationClient;

    @InjectMocks
    private ComplianceAnonymizationStrategy strategy;

    private AnonymizationContext ctx(boolean anonymous, String title, String description) {
        return new AnonymizationContext(anonymous, "DENUNCIA", title, description);
    }

    @Test
    void anonymize_deveAnonimizarTituloEDescricaoViaIa_quandoAnonima() {
        when(aiAnonymizationClient.anonymize(any(AnonymizationAiRequest.class)))
                .thenReturn(new AnonymizationAiResponse(0L, TITULO_ANON, DESCRICAO_ANON));

        AnonymizationResult result = strategy.anonymize(ctx(true, TITULO, DESCRICAO));

        assertThat(result.title()).isEqualTo(TITULO_ANON);
        assertThat(result.description()).isEqualTo(DESCRICAO_ANON);
        verify(aiAnonymizationClient).anonymize(any(AnonymizationAiRequest.class));
    }

    @Test
    void anonymize_naoDeveChamarIa_quandoNaoAnonima() {
        AnonymizationResult result = strategy.anonymize(ctx(false, TITULO, DESCRICAO));

        assertThat(result.title()).isEqualTo(TITULO);
        assertThat(result.description()).isEqualTo(DESCRICAO);
        verify(aiAnonymizationClient, never()).anonymize(any());
    }

    @Test
    void anonymize_naoDeveChamarIa_quandoDescricaoVazia() {
        AnonymizationResult result = strategy.anonymize(ctx(true, TITULO, "  "));

        assertThat(result.description()).isEqualTo("  ");
        verify(aiAnonymizationClient, never()).anonymize(any());
    }

    @Test
    void anonymize_deveLancarErro_quandoIaNaoRetornaDescricao() {
        when(aiAnonymizationClient.anonymize(any(AnonymizationAiRequest.class)))
                .thenReturn(new AnonymizationAiResponse(0L, TITULO_ANON, null));

        assertThatThrownBy(() -> strategy.anonymize(ctx(true, TITULO, DESCRICAO)))
                .isInstanceOf(IllegalStateException.class);
    }
}
