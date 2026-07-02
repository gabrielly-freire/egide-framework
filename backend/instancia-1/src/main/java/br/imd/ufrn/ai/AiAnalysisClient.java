package br.imd.ufrn.ai;

import br.imd.ufrn.ai.dto.AnalysisAiRequest;
import br.imd.ufrn.ai.dto.AnalysisAiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente do microserviço de IA para triagem/classificação de manifestações
 * ({@code POST /analysis/analisar}).
 */
@Component
@RequiredArgsConstructor
public class AiAnalysisClient {

    private final RestClient aiRestClient;

    public AnalysisAiResponse analyze(AnalysisAiRequest request) {
        return aiRestClient.post()
                .uri("/analysis/analisar")
                .body(request)
                .retrieve()
                .body(AnalysisAiResponse.class);
    }
}
