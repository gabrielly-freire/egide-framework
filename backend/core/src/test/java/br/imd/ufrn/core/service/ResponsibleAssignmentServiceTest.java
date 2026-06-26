package br.imd.ufrn.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.domain.ResponsibleAssignment;
import br.imd.ufrn.core.dto.ResponsibleAssignmentRequest;
import br.imd.ufrn.core.dto.ResponsibleAssignmentResponse;
import br.imd.ufrn.core.exception.ResponsibleAssignmentNotFoundException;
import br.imd.ufrn.core.mapper.ResponsibleAssignmentMapper;
import br.imd.ufrn.core.persistence.ResponsibleAssignmentRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResponsibleAssignmentServiceTest {

    @Mock
    private ResponsibleAssignmentRepository repository;

    @Mock
    private ResponsibleAssignmentMapper mapper;

    @InjectMocks
    private ResponsibleAssignmentServiceImpl service;

    private ResponsibleAssignment assignment;
    private ResponsibleAssignmentResponse response;
    private ResponsibleAssignmentRequest request;

    private static final Long MANIFESTATION_ID = 1L;
    private static final Long RESPONSIBLE_ID = 42L;
    private static final Long ASSIGNMENT_ID = 10L;

    @BeforeEach
    void setUp() {
        request = new ResponsibleAssignmentRequest(MANIFESTATION_ID, RESPONSIBLE_ID, null);

        assignment = new ResponsibleAssignment();
        assignment.setId(ASSIGNMENT_ID);
        assignment.setManifestationId(MANIFESTATION_ID);
        assignment.setResponsibleId(RESPONSIBLE_ID);
        assignment.setActive(true);

        response = new ResponsibleAssignmentResponse(
                ASSIGNMENT_ID, MANIFESTATION_ID, RESPONSIBLE_ID, null, null, null);
    }

    @Test
    void assign_deveCriarDesignacao_quandoNaoExisteDesignacaoAtiva() {
        when(repository.findByManifestationId(MANIFESTATION_ID)).thenReturn(Optional.empty());
        when(mapper.toEntity(request)).thenReturn(assignment);
        when(repository.save(any(ResponsibleAssignment.class))).thenReturn(assignment);
        when(mapper.toResponse(assignment)).thenReturn(response);

        ResponsibleAssignmentResponse result = service.assign(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void assign_deveDesativarDesignacaoAnterior_quandoJaExisteDesignacaoAtiva() {
        ResponsibleAssignment existing = new ResponsibleAssignment();
        existing.setId(99L);
        existing.setManifestationId(MANIFESTATION_ID);
        existing.setResponsibleId(7L);
        existing.setActive(true);

        when(repository.findByManifestationId(MANIFESTATION_ID)).thenReturn(Optional.of(existing));
        when(mapper.toEntity(request)).thenReturn(assignment);
        when(repository.save(any(ResponsibleAssignment.class))).thenReturn(assignment);
        when(mapper.toResponse(assignment)).thenReturn(response);

        service.assign(request);

        verify(repository).save(argThat(e -> e.getId().equals(99L) && Boolean.FALSE.equals(e.getActive())));
    }

    @Test
    void assign_naoDeveBuscarDesignacaoAnterior_quandoNaoExiste() {
        when(repository.findByManifestationId(MANIFESTATION_ID)).thenReturn(Optional.empty());
        when(mapper.toEntity(request)).thenReturn(assignment);
        when(repository.save(any(ResponsibleAssignment.class))).thenReturn(assignment);
        when(mapper.toResponse(assignment)).thenReturn(response);

        service.assign(request);

        verify(repository, never()).save(argThat(e -> Boolean.FALSE.equals(e.getActive())));
    }

    @Test
    void findById_deveRetornarResponse_quandoExiste() {
        when(repository.findById(ASSIGNMENT_ID)).thenReturn(Optional.of(assignment));
        when(mapper.toResponse(assignment)).thenReturn(response);

        ResponsibleAssignmentResponse result = service.findById(ASSIGNMENT_ID);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResponsibleAssignmentNotFoundException.class);
    }

    @Test
    void findByManifestationId_deveRetornarResponse_quandoExiste() {
        when(repository.findByManifestationId(MANIFESTATION_ID)).thenReturn(Optional.of(assignment));
        when(mapper.toResponse(assignment)).thenReturn(response);

        ResponsibleAssignmentResponse result = service.findByManifestationId(MANIFESTATION_ID);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void findByManifestationId_deveLancarExcecao_quandoNaoExiste() {
        when(repository.findByManifestationId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByManifestationId(99L))
                .isInstanceOf(ResponsibleAssignmentNotFoundException.class);
    }

    @Test
    void unassign_deveDesativarDesignacao_quandoExiste() {
        when(repository.findByManifestationId(MANIFESTATION_ID)).thenReturn(Optional.of(assignment));

        service.unassign(MANIFESTATION_ID);

        verify(repository).save(argThat(e -> Boolean.FALSE.equals(e.getActive())));
    }

    @Test
    void unassign_deveLancarExcecao_quandoNaoExiste() {
        when(repository.findByManifestationId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.unassign(99L))
                .isInstanceOf(ResponsibleAssignmentNotFoundException.class);
    }
}
