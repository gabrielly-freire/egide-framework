package br.imd.ufrn.suggestion;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Sugestão de resposta por IA para uma manifestação (exige autenticação). */
@RestController
@RequestMapping("/v1/manifestations/{manifestationId}/response-suggestion")
@RequiredArgsConstructor
public class ResponseSuggestionController {

    private final ResponseSuggestionService responseSuggestionService;

    public record SuggestionResponse(String suggestedResponse) {}

    @PostMapping
    public SuggestionResponse suggest(@PathVariable Long manifestationId) {
        return new SuggestionResponse(responseSuggestionService.suggest(manifestationId));
    }
}
