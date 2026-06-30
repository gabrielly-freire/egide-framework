package br.imd.ufrn.core.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.anonymization.AnonymizationContext;
import br.imd.ufrn.core.anonymization.AnonymizationStrategy;
import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.dto.ManifestationRequest;
import br.imd.ufrn.core.dto.ManifestationResponse;
import br.imd.ufrn.core.event.ManifestationCreatedEvent;
import br.imd.ufrn.core.mapper.ManifestationMapper;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class ManifestationAnonymizationServiceTest {

    @Mock
    private ManifestationRepository repository;

    @Mock
    private ManifestationMapper mapper;

    @Mock
    private AnonymizationStrategy anonymizationStrategy;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ManifestationServiceImpl service;

    private static final String DESCRICAO_ORIGINAL =
            "Fui assediado pelo analista João Silva no departamento de TI.";
    private static final String DESCRICAO_ANONIMIZADA =
            "Fui assediado pelo analista [PESSOA] no departamento de TI.";

    private Manifestation entity;
    private ManifestationRequest requestAnonimo;
    private ManifestationRequest requestIdentificado;
    private ManifestationResponse response;

    @BeforeEach
    void setUp() {
        requestAnonimo = new ManifestationRequest("Título", DESCRICAO_ORIGINAL, "ASSÉDIO", true);
        requestIdentificado = new ManifestationRequest("Título", DESCRICAO_ORIGINAL, "ASSÉDIO", false);

        entity = new Manifestation();
        entity.setId(1L);
        entity.setStatus(ManifestationStatus.REGISTERED);
        entity.setActive(true);

        response = new ManifestationResponse(
                1L, "2026-ABCDE12345", "Título", DESCRICAO_ANONIMIZADA,
                "ASSÉDIO", ManifestationStatus.REGISTERED, null, null, null, null);
    }

    @Test
    void create_devePassarContextoComAnonimoTrueParaEstrategia() {
        when(anonymizationStrategy.anonymize(eq(DESCRICAO_ORIGINAL), any(AnonymizationContext.class)))
                .thenReturn(DESCRICAO_ANONIMIZADA);
        when(mapper.toEntity(requestAnonimo)).thenReturn(entity);
        when(repository.save(any(Manifestation.class))).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        service.create(requestAnonimo);

        verify(anonymizationStrategy).anonymize(
                eq(DESCRICAO_ORIGINAL),
                argThat(ctx -> ctx.anonymous() && "ASSÉDIO".equals(ctx.type())));
    }

    @Test
    void create_devePassarContextoComAnonimoFalseParaEstrategia() {
        when(anonymizationStrategy.anonymize(eq(DESCRICAO_ORIGINAL), any(AnonymizationContext.class)))
                .thenReturn(DESCRICAO_ORIGINAL);
        when(mapper.toEntity(requestIdentificado)).thenReturn(entity);
        when(repository.save(any(Manifestation.class))).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        service.create(requestIdentificado);

        verify(anonymizationStrategy).anonymize(
                eq(DESCRICAO_ORIGINAL),
                argThat(ctx -> !ctx.anonymous() && "ASSÉDIO".equals(ctx.type())));
    }

    @Test
    void create_devePersistirDescricaoRetornadaPelaEstrategia() {
        when(anonymizationStrategy.anonymize(eq(DESCRICAO_ORIGINAL), any(AnonymizationContext.class)))
                .thenReturn(DESCRICAO_ANONIMIZADA);
        when(mapper.toEntity(requestAnonimo)).thenReturn(entity);
        when(repository.save(any(Manifestation.class))).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        service.create(requestAnonimo);

        verify(repository).save(argThat(e -> DESCRICAO_ANONIMIZADA.equals(e.getDescription())));
    }

    @Test
    void create_devePublicarManifestationCreatedEvent() {
        when(anonymizationStrategy.anonymize(eq(DESCRICAO_ORIGINAL), any(AnonymizationContext.class)))
                .thenReturn(DESCRICAO_ANONIMIZADA);
        when(mapper.toEntity(requestAnonimo)).thenReturn(entity);
        when(repository.save(any(Manifestation.class))).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        service.create(requestAnonimo);

        verify(eventPublisher).publishEvent(any(ManifestationCreatedEvent.class));
    }

    @Test
    void update_deveAnonimizarDescricaoAtualizada() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(anonymizationStrategy.anonymize(eq(DESCRICAO_ORIGINAL), any(AnonymizationContext.class)))
                .thenReturn(DESCRICAO_ANONIMIZADA);
        when(repository.save(any(Manifestation.class))).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        service.update(1L, requestAnonimo);

        verify(anonymizationStrategy).anonymize(eq(DESCRICAO_ORIGINAL), any(AnonymizationContext.class));
    }
}
