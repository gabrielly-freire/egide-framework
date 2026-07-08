package br.imd.ufrn.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.core.conflict.ConflictOfInterestStrategy;
import br.imd.ufrn.core.designation.DesignationContext;
import br.imd.ufrn.core.designation.DesignationStrategy;
import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.dto.ResponsibleAssignmentRequest;
import br.imd.ufrn.core.dto.ResponsibleAssignmentResponse;
import br.imd.ufrn.core.exception.AutoAssignmentUnavailableException;
import br.imd.ufrn.core.exception.ConflictOfInterestException;
import br.imd.ufrn.core.exception.ManifestationNotFoundException;
import br.imd.ufrn.core.persistence.ManifestationAccusationRepository;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DesignationServiceTest {

    @Mock
    private ManifestationRepository manifestationRepository;

    @Mock
    private ResponsibleAssignmentService assignmentService;

    @Mock
    private DesignationStrategy designationStrategy;

    @Mock
    private ConflictOfInterestStrategy conflictOfInterestStrategy;

    @Mock
    private ManifestationAccusationRepository accusationRepository;

    @InjectMocks
    private DesignationServiceImpl service;

    private Manifestation manifestation;
    private ResponsibleAssignmentResponse assignmentResponse;

    @BeforeEach
    void setUp() {
        manifestation = new Manifestation();
        manifestation.setId(1L);
        manifestation.setStatus(ManifestationStatus.REGISTERED);
        manifestation.setType("DENUNCIA");

        assignmentResponse = new ResponsibleAssignmentResponse(1L, 1L, 10L, null, null, null);
    }

    @Test
    void autoAssign_deveCriarDesignacao_quandoEstrategiaRetornaResponsavel() {
        when(manifestationRepository.findById(1L)).thenReturn(Optional.of(manifestation));
        when(designationStrategy.resolve(any(DesignationContext.class))).thenReturn(10L);
        when(accusationRepository.findByManifestationId(1L)).thenReturn(List.of());
        when(conflictOfInterestStrategy.hasConflict(any(ConflictOfInterestContext.class))).thenReturn(false);
        when(assignmentService.assign(any(ResponsibleAssignmentRequest.class))).thenReturn(assignmentResponse);

        ResponsibleAssignmentResponse result = service.autoAssign(1L);

        assertThat(result).isEqualTo(assignmentResponse);
        verify(assignmentService).assign(new ResponsibleAssignmentRequest(1L, 10L, null));
    }

    @Test
    void autoAssign_deveLancarExcecao_quandoEstrategiaRetornaNull() {
        when(manifestationRepository.findById(1L)).thenReturn(Optional.of(manifestation));
        when(designationStrategy.resolve(any(DesignationContext.class))).thenReturn(null);

        assertThatThrownBy(() -> service.autoAssign(1L))
                .isInstanceOf(AutoAssignmentUnavailableException.class);

        verify(assignmentService, never()).assign(any());
    }

    @Test
    void autoAssign_deveLancarExcecao_quandoAnalystaPossuiConflito() {
        when(manifestationRepository.findById(1L)).thenReturn(Optional.of(manifestation));
        when(designationStrategy.resolve(any(DesignationContext.class))).thenReturn(10L);
        when(accusationRepository.findByManifestationId(1L)).thenReturn(List.of());
        when(conflictOfInterestStrategy.hasConflict(any(ConflictOfInterestContext.class))).thenReturn(true);

        assertThatThrownBy(() -> service.autoAssign(1L))
                .isInstanceOf(ConflictOfInterestException.class);

        verify(assignmentService, never()).assign(any());
    }

    @Test
    void autoAssign_deveLancarExcecao_quandoManifestacaoNaoEncontrada() {
        when(manifestationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.autoAssign(99L))
                .isInstanceOf(ManifestationNotFoundException.class);
    }

    @Test
    void hasConflict_deveRetornarTrue_quandoEstrategiaDetectaConflito() {
        when(manifestationRepository.findById(1L)).thenReturn(Optional.of(manifestation));
        when(accusationRepository.findByManifestationId(1L)).thenReturn(List.of());
        when(conflictOfInterestStrategy.hasConflict(any(ConflictOfInterestContext.class))).thenReturn(true);

        assertThat(service.hasConflict(1L, 10L)).isTrue();
    }

    @Test
    void hasConflict_deveRetornarFalse_quandoSemConflito() {
        when(manifestationRepository.findById(1L)).thenReturn(Optional.of(manifestation));
        when(accusationRepository.findByManifestationId(1L)).thenReturn(List.of());
        when(conflictOfInterestStrategy.hasConflict(any(ConflictOfInterestContext.class))).thenReturn(false);

        assertThat(service.hasConflict(1L, 10L)).isFalse();
    }

    @Test
    void hasConflict_deveLancarExcecao_quandoManifestacaoNaoEncontrada() {
        when(manifestationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.hasConflict(99L, 10L))
                .isInstanceOf(ManifestationNotFoundException.class);
    }
}
