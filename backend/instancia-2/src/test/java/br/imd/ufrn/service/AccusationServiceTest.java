package br.imd.ufrn.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.exception.ManifestationNotFoundException;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import br.imd.ufrn.domain.ManifestationAccusation;
import br.imd.ufrn.dto.AccusationRequest;
import br.imd.ufrn.dto.AccusationResponse;
import br.imd.ufrn.exception.AcademicMemberNotFoundException;
import br.imd.ufrn.exception.AccusationNotFoundException;
import br.imd.ufrn.mapper.ManifestationAccusationMapper;
import br.imd.ufrn.persistence.AcademicMemberRepository;
import br.imd.ufrn.persistence.ManifestationAccusationRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccusationServiceTest {

    private static final Long MANIFESTATION_ID = 10L;
    private static final Long ACCUSED_ID = 2L;

    @Mock
    private ManifestationAccusationRepository accusationRepository;

    @Mock
    private ManifestationRepository manifestationRepository;

    @Mock
    private AcademicMemberRepository memberRepository;

    @Mock
    private ManifestationAccusationMapper mapper;

    @InjectMocks
    private AccusationServiceImpl service;

    private final AccusationRequest request = new AccusationRequest(ACCUSED_ID);

    @Test
    void register_deveCriarVinculo_quandoManifestacaoEMembroExistem() {
        when(manifestationRepository.existsById(MANIFESTATION_ID)).thenReturn(true);
        when(memberRepository.existsById(ACCUSED_ID)).thenReturn(true);
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID)).thenReturn(Optional.empty());
        when(accusationRepository.save(any(ManifestationAccusation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(ManifestationAccusation.class)))
                .thenReturn(new AccusationResponse(1L, MANIFESTATION_ID, ACCUSED_ID));

        AccusationResponse result = service.register(MANIFESTATION_ID, request);

        assertThat(result.accusedMemberId()).isEqualTo(ACCUSED_ID);
        assertThat(result.manifestationId()).isEqualTo(MANIFESTATION_ID);
    }

    @Test
    void register_deveSubstituirDenunciadoAnterior_quandoJaExisteVinculo() {
        ManifestationAccusation existing = new ManifestationAccusation();
        existing.setId(5L);
        existing.setManifestationId(MANIFESTATION_ID);
        existing.setAccusedMemberId(99L);

        when(manifestationRepository.existsById(MANIFESTATION_ID)).thenReturn(true);
        when(memberRepository.existsById(ACCUSED_ID)).thenReturn(true);
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID)).thenReturn(Optional.of(existing));
        when(accusationRepository.save(any(ManifestationAccusation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(ManifestationAccusation.class)))
                .thenReturn(new AccusationResponse(5L, MANIFESTATION_ID, ACCUSED_ID));

        service.register(MANIFESTATION_ID, request);

        // reaproveita a mesma linha (id 5), apenas trocando o denunciado.
        assertThat(existing.getAccusedMemberId()).isEqualTo(ACCUSED_ID);
    }

    @Test
    void register_deveLancarExcecao_quandoManifestacaoNaoExiste() {
        when(manifestationRepository.existsById(MANIFESTATION_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.register(MANIFESTATION_ID, request))
                .isInstanceOf(ManifestationNotFoundException.class);
    }

    @Test
    void register_deveLancarExcecao_quandoMembroDenunciadoNaoExiste() {
        when(manifestationRepository.existsById(MANIFESTATION_ID)).thenReturn(true);
        when(memberRepository.existsById(ACCUSED_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.register(MANIFESTATION_ID, request))
                .isInstanceOf(AcademicMemberNotFoundException.class);
    }

    @Test
    void findByManifestationId_deveLancarExcecao_quandoNaoHaVinculo() {
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByManifestationId(MANIFESTATION_ID))
                .isInstanceOf(AccusationNotFoundException.class);
    }
}
