package br.imd.ufrn.categorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.imd.ufrn.ai.AiAnalysisClient;
import br.imd.ufrn.ai.dto.AnalysisAiRequest;
import br.imd.ufrn.ai.dto.AnalysisAiResponse;
import br.imd.ufrn.core.categorization.CategorizationContext;
import br.imd.ufrn.core.categorization.CategorizationResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AiCategorizationStrategyTest {

    @Mock
    private AiAnalysisClient aiAnalysisClient;

    @InjectMocks
    private AiCategorizationStrategy strategy;

    private CategorizationContext context() {
        return new CategorizationContext(1L, "Título", "Descrição", "DENUNCIA");
    }

    @Test
    void categorize_deveMapearCategoriaERiscoDaIa() {
        when(aiAnalysisClient.analyze(any(AnalysisAiRequest.class)))
                .thenReturn(new AnalysisAiResponse(1L, "DENUNCIATION", "CRITICAL", true, List.of("7"), true));

        CategorizationResult result = strategy.categorize(context());

        assertThat(result.category()).isEqualTo("DENUNCIATION");
        assertThat(result.riskLevel()).isEqualTo("CRITICAL");
    }

    @Test
    void categorize_deveTratarValoresDesconhecidosComoNull() {
        when(aiAnalysisClient.analyze(any(AnalysisAiRequest.class)))
                .thenReturn(new AnalysisAiResponse(1L, "XPTO", "SEVERO", false, List.of(), false));

        CategorizationResult result = strategy.categorize(context());

        assertThat(result.category()).isNull();
        assertThat(result.riskLevel()).isNull();
    }

    @Test
    void categorize_deveRetornarVazio_quandoIaNaoResponde() {
        when(aiAnalysisClient.analyze(any(AnalysisAiRequest.class))).thenReturn(null);

        CategorizationResult result = strategy.categorize(context());

        assertThat(result.category()).isNull();
        assertThat(result.riskLevel()).isNull();
    }
}
