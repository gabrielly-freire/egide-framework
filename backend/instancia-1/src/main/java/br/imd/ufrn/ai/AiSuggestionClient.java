package br.imd.ufrn.ai;

import br.imd.ufrn.ai.dto.ResponseSuggestionAiRequest;
import br.imd.ufrn.ai.dto.ResponseSuggestionAiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Cliente do microserviço de IA para sugestão de resposta institucional. */
@Component
@RequiredArgsConstructor
public class AiSuggestionClient {

    private final RestClient aiRestClient;

    public ResponseSuggestionAiResponse suggest(ResponseSuggestionAiRequest request) {
        return aiRestClient.post()
                .uri("/compliance/sugerir-resposta")
                .body(request)
                .retrieve()
                .body(ResponseSuggestionAiResponse.class);
    }
}
