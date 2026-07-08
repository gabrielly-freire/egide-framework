package br.imd.ufrn.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.dto.ManifestationResponse;
import br.imd.ufrn.core.exception.ManifestationNotFoundException;
import br.imd.ufrn.core.mapper.ManifestationMapper;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import br.imd.ufrn.core.workflow.WorkflowStepResult;
import br.imd.ufrn.core.workflow.WorkflowTemplate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock
    private ManifestationRepository repository;

    @Mock
    private ManifestationMapper mapper;

    @Mock
    private WorkflowTemplate workflowTemplate;

    @InjectMocks
    private WorkflowServiceImpl service;

    private Manifestation manifestation;
    private ManifestationResponse response;

    @BeforeEach
    void setUp() {
        manifestation = new Manifestation();
        manifestation.setId(1L);
        manifestation.setStatus(ManifestationStatus.REGISTERED);

        response = new ManifestationResponse(
                1L, "2026-ABCDE12345", "Título", "Descrição",
                "RECLAMAÇÃO", ManifestationStatus.IN_REVIEW, null, null, null, null, null, null, null);
    }

    @Test
    void advance_deveAtualizarStatusParaProximaFase() {
        WorkflowStepResult result = new WorkflowStepResult(ManifestationStatus.IN_REVIEW, null);
        when(repository.findById(1L)).thenReturn(Optional.of(manifestation));
        when(workflowTemplate.advance(manifestation)).thenReturn(result);
        when(repository.save(any(Manifestation.class))).thenReturn(manifestation);
        when(mapper.toResponse(manifestation)).thenReturn(response);

        ManifestationResponse actual = service.advance(1L);

        assertThat(actual).isEqualTo(response);
        verify(repository).save(any(Manifestation.class));
    }

    @Test
    void advance_deveCarimbarPrazo_quandoTemplateDefineDuracao() {
        WorkflowStepResult result = new WorkflowStepResult(ManifestationStatus.IN_REVIEW, Duration.ofDays(15));
        when(repository.findById(1L)).thenReturn(Optional.of(manifestation));
        when(workflowTemplate.advance(manifestation)).thenReturn(result);
        when(repository.save(any(Manifestation.class))).thenReturn(manifestation);
        when(mapper.toResponse(manifestation)).thenReturn(response);

        service.advance(1L);

        assertThat(manifestation.getDeadlineAt()).isNotNull().isAfter(LocalDateTime.now());
    }

    @Test
    void advance_deveDelegarParaOTemplate() {
        WorkflowStepResult result = new WorkflowStepResult(ManifestationStatus.IN_REVIEW, null);
        when(repository.findById(1L)).thenReturn(Optional.of(manifestation));
        when(workflowTemplate.advance(manifestation)).thenReturn(result);
        when(repository.save(any(Manifestation.class))).thenReturn(manifestation);
        when(mapper.toResponse(manifestation)).thenReturn(response);

        service.advance(1L);

        verify(workflowTemplate).advance(manifestation);
    }

    @Test
    void advance_deveLancarExcecao_quandoManifestacaoNaoEncontrada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.advance(99L))
                .isInstanceOf(ManifestationNotFoundException.class);
    }

    @Test
    void appeal_deveAtualizarStatusParaFaseDeRecurso() {
        WorkflowStepResult result = new WorkflowStepResult(ManifestationStatus.IN_REVIEW, null);
        when(repository.findById(1L)).thenReturn(Optional.of(manifestation));
        when(workflowTemplate.appeal(manifestation)).thenReturn(result);
        when(repository.save(any(Manifestation.class))).thenReturn(manifestation);
        when(mapper.toResponse(manifestation)).thenReturn(response);

        ManifestationResponse actual = service.appeal(1L);

        assertThat(actual).isEqualTo(response);
        verify(workflowTemplate).appeal(manifestation);
    }

    @Test
    void appeal_deveLancarExcecao_quandoManifestacaoNaoEncontrada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.appeal(99L))
                .isInstanceOf(ManifestationNotFoundException.class);
    }
}
