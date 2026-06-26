package br.imd.ufrn.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.domain.DecisionRecord;
import br.imd.ufrn.core.domain.DecisionType;
import br.imd.ufrn.core.dto.DecisionRecordRequest;
import br.imd.ufrn.core.dto.DecisionRecordResponse;
import br.imd.ufrn.core.exception.DecisionRecordNotFoundException;
import br.imd.ufrn.core.mapper.DecisionRecordMapper;
import br.imd.ufrn.core.persistence.DecisionRecordRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DecisionRecordServiceTest {

    @Mock
    private DecisionRecordRepository repository;

    @Mock
    private DecisionRecordMapper mapper;

    @InjectMocks
    private DecisionRecordServiceImpl service;

    private DecisionRecord record;
    private DecisionRecordResponse response;
    private DecisionRecordRequest request;

    private static final Long MANIFESTATION_ID = 1L;
    private static final Long AUTHOR_ID = 5L;
    private static final Long RECORD_ID = 10L;

    @BeforeEach
    void setUp() {
        request = new DecisionRecordRequest(MANIFESTATION_ID, AUTHOR_ID, DecisionType.DECISION, "Decisão formal sobre o caso.");

        record = new DecisionRecord();
        record.setId(RECORD_ID);
        record.setManifestationId(MANIFESTATION_ID);
        record.setAuthorId(AUTHOR_ID);
        record.setType(DecisionType.DECISION);
        record.setContent("Decisão formal sobre o caso.");
        record.setActive(true);

        response = new DecisionRecordResponse(
                RECORD_ID, MANIFESTATION_ID, AUTHOR_ID, DecisionType.DECISION,
                "Decisão formal sobre o caso.", null, null);
    }

    @Test
    void create_deveSalvarRegistro() {
        when(mapper.toEntity(request)).thenReturn(record);
        when(repository.save(any(DecisionRecord.class))).thenReturn(record);
        when(mapper.toResponse(record)).thenReturn(response);

        DecisionRecordResponse result = service.create(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void create_deveDefinirActiveComoTrue() {
        when(mapper.toEntity(request)).thenReturn(record);
        when(repository.save(any(DecisionRecord.class))).thenReturn(record);
        when(mapper.toResponse(record)).thenReturn(response);

        service.create(request);

        verify(repository).save(argThat(e -> Boolean.TRUE.equals(e.getActive())));
    }

    @Test
    void findById_deveRetornarResponse_quandoExiste() {
        when(repository.findById(RECORD_ID)).thenReturn(Optional.of(record));
        when(mapper.toResponse(record)).thenReturn(response);

        DecisionRecordResponse result = service.findById(RECORD_ID);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(DecisionRecordNotFoundException.class);
    }

    @Test
    void findAllByManifestationId_deveRetornarListaDeResponses() {
        when(repository.findAllByManifestationId(MANIFESTATION_ID)).thenReturn(List.of(record));
        when(mapper.toResponse(record)).thenReturn(response);

        List<DecisionRecordResponse> result = service.findAllByManifestationId(MANIFESTATION_ID);

        assertThat(result).containsExactly(response);
    }

    @Test
    void findAllByManifestationId_deveRetornarListaVazia_quandoNaoExistemRegistros() {
        when(repository.findAllByManifestationId(99L)).thenReturn(List.of());

        List<DecisionRecordResponse> result = service.findAllByManifestationId(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void delete_deveDefinirActiveComoFalse_quandoExiste() {
        when(repository.findById(RECORD_ID)).thenReturn(Optional.of(record));

        service.delete(RECORD_ID);

        verify(repository).save(argThat(e -> Boolean.FALSE.equals(e.getActive())));
    }

    @Test
    void delete_deveLancarExcecao_quandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(DecisionRecordNotFoundException.class);
    }
}
