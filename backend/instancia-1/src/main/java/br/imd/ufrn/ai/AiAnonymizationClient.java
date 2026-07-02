package br.imd.ufrn.ai;

import br.imd.ufrn.ai.dto.AnonymizationAiRequest;
import br.imd.ufrn.ai.dto.AnonymizationAiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente do microserviço de IA para anonimização (pseudonimização) de manifestações.
 */
@Component
@RequiredArgsConstructor
public class AiAnonymizationClient {

    private final RestClient aiRestClient;

    public AnonymizationAiResponse anonymize(AnonymizationAiRequest request) {
        return aiRestClient.post()
                .uri("/compliance/anonimizar")
                .body(request)
                .retrieve()
                .body(AnonymizationAiResponse.class);
    }
}
