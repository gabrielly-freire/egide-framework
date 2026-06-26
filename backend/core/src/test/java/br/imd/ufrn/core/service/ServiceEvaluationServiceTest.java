package br.imd.ufrn.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.domain.ServiceEvaluation;
import br.imd.ufrn.core.dto.ServiceEvaluationRequest;
import br.imd.ufrn.core.dto.ServiceEvaluationResponse;
import br.imd.ufrn.core.exception.EvaluationAlreadyExistsException;
import br.imd.ufrn.core.exception.ServiceEvaluationNotFoundException;
import br.imd.ufrn.core.mapper.ServiceEvaluationMapper;
import br.imd.ufrn.core.persistence.ServiceEvaluationRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceEvaluationServiceTest {

    @Mock
    private ServiceEvaluationRepository repository;

    @Mock
    private ServiceEvaluationMapper mapper;

    @InjectMocks
    private ServiceEvaluationServiceImpl service;

    private ServiceEvaluation evaluation;
    private ServiceEvaluationResponse response;
    private ServiceEvaluationRequest request;

    private static final Long MANIFESTATION_ID = 1L;
    private static final Long EVALUATION_ID = 10L;

    @BeforeEach
    void setUp() {
        request = new ServiceEvaluationRequest(MANIFESTATION_ID, 5, "Atendimento excelente");

        evaluation = new ServiceEvaluation();
        evaluation.setId(EVALUATION_ID);
        evaluation.setManifestationId(MANIFESTATION_ID);
        evaluation.setRating(5);
        evaluation.setComment("Atendimento excelente");
        evaluation.setActive(true);

        response = new ServiceEvaluationResponse(
                EVALUATION_ID, MANIFESTATION_ID, 5, "Atendimento excelente", null, null);
    }

    @Test
    void create_deveSalvarAvaliacao_quandoNaoExisteParaManifestacao() {
        when(repository.existsByManifestationId(MANIFESTATION_ID)).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(evaluation);
        when(repository.save(any(ServiceEvaluation.class))).thenReturn(evaluation);
        when(mapper.toResponse(evaluation)).thenReturn(response);

        ServiceEvaluationResponse result = service.create(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void create_deveLancarExcecao_quandoAvaliacaoJaExiste() {
        when(repository.existsByManifestationId(MANIFESTATION_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(EvaluationAlreadyExistsException.class);
    }

    @Test
    void create_deveDefinirActiveComoTrue() {
        when(repository.existsByManifestationId(MANIFESTATION_ID)).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(evaluation);
        when(repository.save(any(ServiceEvaluation.class))).thenReturn(evaluation);
        when(mapper.toResponse(evaluation)).thenReturn(response);

        service.create(request);

        verify(repository).save(argThat(e -> Boolean.TRUE.equals(e.getActive())));
    }

    @Test
    void findById_deveRetornarResponse_quandoExiste() {
        when(repository.findById(EVALUATION_ID)).thenReturn(Optional.of(evaluation));
        when(mapper.toResponse(evaluation)).thenReturn(response);

        ServiceEvaluationResponse result = service.findById(EVALUATION_ID);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ServiceEvaluationNotFoundException.class);
    }

    @Test
    void findByManifestationId_deveRetornarResponse_quandoExiste() {
        when(repository.findByManifestationId(MANIFESTATION_ID)).thenReturn(Optional.of(evaluation));
        when(mapper.toResponse(evaluation)).thenReturn(response);

        ServiceEvaluationResponse result = service.findByManifestationId(MANIFESTATION_ID);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void findByManifestationId_deveLancarExcecao_quandoNaoExiste() {
        when(repository.findByManifestationId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByManifestationId(99L))
                .isInstanceOf(ServiceEvaluationNotFoundException.class);
    }

    @Test
    void delete_deveDefinirActiveComoFalse_quandoExiste() {
        when(repository.findById(EVALUATION_ID)).thenReturn(Optional.of(evaluation));

        service.delete(EVALUATION_ID);

        verify(repository).save(argThat(e -> Boolean.FALSE.equals(e.getActive())));
    }

    @Test
    void delete_deveLancarExcecao_quandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ServiceEvaluationNotFoundException.class);
    }
}
