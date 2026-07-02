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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComplianceAnonymizationStrategyTest {

    private static final String ORIGINAL = "O gestor João Silva aprovou notas frias.";
    private static final String ANONIMIZADO = "O coordenador Carlos Souza aprovou notas frias.";

    @Mock
    private AiAnonymizationClient aiAnonymizationClient;

    @InjectMocks
    private ComplianceAnonymizationStrategy strategy;

    @Test
    void anonymize_deveChamarIaERetornarDescricaoAnonimizada_quandoAnonima() {
        when(aiAnonymizationClient.anonymize(any(AnonymizationAiRequest.class)))
                .thenReturn(new AnonymizationAiResponse(null, "", ANONIMIZADO));

        String result = strategy.anonymize(ORIGINAL, new AnonymizationContext(true, "DENUNCIA"));

        assertThat(result).isEqualTo(ANONIMIZADO);
        verify(aiAnonymizationClient).anonymize(any(AnonymizationAiRequest.class));
    }

    @Test
    void anonymize_naoDeveChamarIa_quandoNaoAnonima() {
        String result = strategy.anonymize(ORIGINAL, new AnonymizationContext(false, "DENUNCIA"));

        assertThat(result).isEqualTo(ORIGINAL);
        verify(aiAnonymizationClient, never()).anonymize(any());
    }

    @Test
    void anonymize_naoDeveChamarIa_quandoTextoVazio() {
        String result = strategy.anonymize("  ", new AnonymizationContext(true, "DENUNCIA"));

        assertThat(result).isEqualTo("  ");
        verify(aiAnonymizationClient, never()).anonymize(any());
    }

    @Test
    void anonymize_deveLancarErro_quandoIaNaoRetornaTexto() {
        when(aiAnonymizationClient.anonymize(any(AnonymizationAiRequest.class)))
                .thenReturn(new AnonymizationAiResponse(null, "", null));

        assertThatThrownBy(() ->
                strategy.anonymize(ORIGINAL, new AnonymizationContext(true, "DENUNCIA")))
                .isInstanceOf(IllegalStateException.class);
    }
}
