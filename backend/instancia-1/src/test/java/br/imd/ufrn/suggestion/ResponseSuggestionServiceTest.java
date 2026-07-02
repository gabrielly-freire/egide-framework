package br.imd.ufrn.suggestion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.imd.ufrn.ai.AiSuggestionClient;
import br.imd.ufrn.ai.dto.ResponseSuggestionAiRequest;
import br.imd.ufrn.ai.dto.ResponseSuggestionAiResponse;
import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ResponseSuggestionServiceTest {

    @Mock
    private ManifestationRepository manifestationRepository;

    @Mock
    private AiSuggestionClient aiSuggestionClient;

    @InjectMocks
    private ResponseSuggestionService service;

    @Test
    void suggest_deveRetornarTextoDaIa() {
        Manifestation m = new Manifestation();
        m.setId(1L);
        when(manifestationRepository.findById(1L)).thenReturn(Optional.of(m));
        when(aiSuggestionClient.suggest(any(ResponseSuggestionAiRequest.class)))
                .thenReturn(new ResponseSuggestionAiResponse(1L, "Prezado(a), sua denúncia foi recebida..."));

        assertThat(service.suggest(1L)).startsWith("Prezado(a)");
    }

    @Test
    void suggest_deveLancarNotFound_quandoManifestacaoNaoExiste() {
        when(manifestationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.suggest(99L))
                .isInstanceOf(ResponseStatusException.class);
    }
}
