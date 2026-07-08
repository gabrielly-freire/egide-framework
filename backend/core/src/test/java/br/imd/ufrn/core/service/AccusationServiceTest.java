package br.imd.ufrn.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.domain.ManifestationAccusation;
import br.imd.ufrn.core.dto.AccusationRequest;
import br.imd.ufrn.core.dto.AccusationResponse;
import br.imd.ufrn.core.exception.ManifestationNotFoundException;
import br.imd.ufrn.core.mapper.ManifestationAccusationMapper;
import br.imd.ufrn.core.persistence.ManifestationAccusationRepository;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccusationServiceTest {

    private static final Long MANIFESTATION_ID = 10L;
    private static final Long ACCUSED_PARTY_ID = 2L;

    @Mock
    private ManifestationAccusationRepository accusationRepository;

    @Mock
    private ManifestationRepository manifestationRepository;

    @Mock
    private ManifestationAccusationMapper mapper;

    @InjectMocks
    private AccusationServiceImpl service;

    private final AccusationRequest request = new AccusationRequest(ACCUSED_PARTY_ID);

    @Test
    void register_deveCriarAcusacao_quandoManifestacaoExiste() {
        when(manifestationRepository.existsById(MANIFESTATION_ID)).thenReturn(true);
        when(accusationRepository.save(any(ManifestationAccusation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(ManifestationAccusation.class)))
                .thenReturn(new AccusationResponse(1L, MANIFESTATION_ID, ACCUSED_PARTY_ID));

        AccusationResponse result = service.register(MANIFESTATION_ID, request);

        assertThat(result.manifestationId()).isEqualTo(MANIFESTATION_ID);
        assertThat(result.accusedPartyId()).isEqualTo(ACCUSED_PARTY_ID);
    }

    @Test
    void register_deveLancarExcecao_quandoManifestacaoNaoExiste() {
        when(manifestationRepository.existsById(MANIFESTATION_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.register(MANIFESTATION_ID, request))
                .isInstanceOf(ManifestationNotFoundException.class);
    }

    @Test
    void findByManifestationId_deveRetornarTodasAsAcusacoes() {
        ManifestationAccusation a = new ManifestationAccusation();
        a.setManifestationId(MANIFESTATION_ID);
        a.setAccusedPartyId(ACCUSED_PARTY_ID);
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID)).thenReturn(List.of(a));
        when(mapper.toResponse(a)).thenReturn(new AccusationResponse(1L, MANIFESTATION_ID, ACCUSED_PARTY_ID));

        List<AccusationResponse> result = service.findByManifestationId(MANIFESTATION_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).accusedPartyId()).isEqualTo(ACCUSED_PARTY_ID);
    }
}
