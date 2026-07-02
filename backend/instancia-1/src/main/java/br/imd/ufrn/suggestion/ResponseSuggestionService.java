package br.imd.ufrn.suggestion;

import br.imd.ufrn.ai.AiSuggestionClient;
import br.imd.ufrn.ai.dto.ResponseSuggestionAiRequest;
import br.imd.ufrn.ai.dto.ResponseSuggestionAiResponse;
import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Gera uma sugestão de resposta institucional para uma manifestação, delegando ao microserviço de
 * IA ({@code POST /compliance/sugerir-resposta}) com os dados já classificados (categoria/risco).
 */
@Service
@RequiredArgsConstructor
public class ResponseSuggestionService {

    private final ManifestationRepository manifestationRepository;
    private final AiSuggestionClient aiSuggestionClient;

    @Transactional(readOnly = true)
    public String suggest(Long manifestationId) {
        Manifestation m = manifestationRepository.findById(manifestationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Manifestação não encontrada"));

        ResponseSuggestionAiResponse response = aiSuggestionClient.suggest(new ResponseSuggestionAiRequest(
                m.getId(), m.getTitle(), m.getDescription(),
                m.getProtocolNumber(), m.getCategory(), m.getRiskLevel()));

        if (response == null || response.suggestedResponse() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "IA não retornou sugestão de resposta");
        }
        return response.suggestedResponse();
    }
}
