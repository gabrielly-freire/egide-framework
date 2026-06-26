package br.imd.ufrn.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.domain.AuditEntry;
import br.imd.ufrn.core.dto.AuditEntryRequest;
import br.imd.ufrn.core.dto.AuditEntryResponse;
import br.imd.ufrn.core.exception.AuditEntryNotFoundException;
import br.imd.ufrn.core.mapper.AuditEntryMapper;
import br.imd.ufrn.core.persistence.AuditEntryRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AuditEntryServiceTest {

    @Mock
    private AuditEntryRepository repository;

    @Mock
    private AuditEntryMapper mapper;

    @InjectMocks
    private AuditEntryServiceImpl service;

    private AuditEntry entry;
    private AuditEntryResponse response;
    private AuditEntryRequest request;

    private static final Long MANIFESTATION_ID = 1L;
    private static final Long ACTOR_ID = 5L;
    private static final Long ENTRY_ID = 10L;

    @BeforeEach
    void setUp() {
        request = new AuditEntryRequest(MANIFESTATION_ID, ACTOR_ID, "STATUS_CHANGED", "Status alterado para IN_REVIEW");

        entry = new AuditEntry();
        entry.setId(ENTRY_ID);
        entry.setManifestationId(MANIFESTATION_ID);
        entry.setActorId(ACTOR_ID);
        entry.setAction("STATUS_CHANGED");
        entry.setDescription("Status alterado para IN_REVIEW");

        response = new AuditEntryResponse(
                ENTRY_ID, MANIFESTATION_ID, ACTOR_ID,
                "STATUS_CHANGED", "Status alterado para IN_REVIEW",
                LocalDateTime.now());
    }

    @Test
    void create_deveSalvarEntradaComCamposDaRequest() {
        when(repository.save(any(AuditEntry.class))).thenReturn(entry);
        when(mapper.toResponse(entry)).thenReturn(response);

        AuditEntryResponse result = service.create(request);

        assertThat(result).isEqualTo(response);
        verify(repository).save(argThat(e ->
                e.getManifestationId().equals(MANIFESTATION_ID) &&
                e.getActorId().equals(ACTOR_ID) &&
                e.getAction().equals("STATUS_CHANGED")));
    }

    @Test
    void create_naoDevePersistirCampoAtivo() {
        when(repository.save(any(AuditEntry.class))).thenReturn(entry);
        when(mapper.toResponse(entry)).thenReturn(response);

        service.create(request);

        // AuditEntry não herda BaseEntity — não há campo active
        verify(repository).save(argThat(e -> e.getClass().equals(AuditEntry.class)));
    }

    @Test
    void findById_deveRetornarResponse_quandoExiste() {
        when(repository.findById(ENTRY_ID)).thenReturn(Optional.of(entry));
        when(mapper.toResponse(entry)).thenReturn(response);

        AuditEntryResponse result = service.findById(ENTRY_ID);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(AuditEntryNotFoundException.class);
    }

    @Test
    void findAllByManifestationId_deveRetornarPaginaDeResponses() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<AuditEntry> page = new PageImpl<>(List.of(entry), pageable, 1);

        when(repository.findAllByManifestationId(MANIFESTATION_ID, pageable)).thenReturn(page);
        when(mapper.toResponse(entry)).thenReturn(response);

        Page<AuditEntryResponse> result = service.findAllByManifestationId(MANIFESTATION_ID, pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findAllByManifestationId_deveRetornarPaginaVazia_quandoNaoExistemEntradas() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<AuditEntry> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(repository.findAllByManifestationId(99L, pageable)).thenReturn(emptyPage);

        Page<AuditEntryResponse> result = service.findAllByManifestationId(99L, pageable);

        assertThat(result.getContent()).isEmpty();
    }
}
