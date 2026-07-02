package br.imd.ufrn.core.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.anonymization.AnonymizationContext;
import br.imd.ufrn.core.anonymization.AnonymizationResult;
import br.imd.ufrn.core.anonymization.AnonymizationStrategy;
import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.dto.ManifestationRequest;
import br.imd.ufrn.core.dto.ManifestationResponse;
import br.imd.ufrn.core.event.ManifestationCreatedEvent;
import br.imd.ufrn.core.mapper.ManifestationMapper;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import br.imd.ufrn.core.workflow.WorkflowTemplate;
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

    @Mock
    private WorkflowTemplate workflowTemplate;

    @InjectMocks
    private ManifestationServiceImpl service;

    private static final String DESCRICAO_ORIGINAL =
            "Fui assediado pelo analista João Silva no departamento de TI.";
    private static final String TITULO_ANONIMIZADO = "Denúncia [MASCARADA]";
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
                "ASSÉDIO", ManifestationStatus.REGISTERED, null, null, null, null, null);
    }

    @Test
    void create_devePassarContextoComTextosParaEstrategia() {
        when(anonymizationStrategy.anonymize(any(AnonymizationContext.class)))
                .thenReturn(new AnonymizationResult("Título", DESCRICAO_ANONIMIZADA));
        when(mapper.toEntity(requestAnonimo)).thenReturn(entity);
        when(repository.save(any(Manifestation.class))).thenReturn(entity);

        service.create(requestAnonimo);

        verify(anonymizationStrategy).anonymize(argThat(ctx ->
                ctx.anonymous()
                        && "ASSÉDIO".equals(ctx.type())
                        && "Título".equals(ctx.title())
                        && DESCRICAO_ORIGINAL.equals(ctx.description())));
    }

    @Test
    void create_devePassarAnonimoFalse_quandoIdentificada() {
        when(anonymizationStrategy.anonymize(any(AnonymizationContext.class)))
                .thenReturn(new AnonymizationResult("Título", DESCRICAO_ORIGINAL));
        when(mapper.toEntity(requestIdentificado)).thenReturn(entity);
        when(repository.save(any(Manifestation.class))).thenReturn(entity);

        service.create(requestIdentificado);

        verify(anonymizationStrategy).anonymize(argThat(ctx -> !ctx.anonymous()));
    }

    @Test
    void create_devePersistirTituloEDescricaoAnonimizados() {
        when(anonymizationStrategy.anonymize(any(AnonymizationContext.class)))
                .thenReturn(new AnonymizationResult(TITULO_ANONIMIZADO, DESCRICAO_ANONIMIZADA));
        when(mapper.toEntity(requestAnonimo)).thenReturn(entity);
        when(repository.save(any(Manifestation.class))).thenReturn(entity);

        service.create(requestAnonimo);

        verify(repository).save(argThat(e ->
                TITULO_ANONIMIZADO.equals(e.getTitle())
                        && DESCRICAO_ANONIMIZADA.equals(e.getDescription())));
    }

    @Test
    void create_devePublicarManifestationCreatedEvent() {
        when(anonymizationStrategy.anonymize(any(AnonymizationContext.class)))
                .thenReturn(new AnonymizationResult("Título", DESCRICAO_ANONIMIZADA));
        when(mapper.toEntity(requestAnonimo)).thenReturn(entity);
        when(repository.save(any(Manifestation.class))).thenReturn(entity);

        service.create(requestAnonimo);

        verify(eventPublisher).publishEvent(any(ManifestationCreatedEvent.class));
    }

    @Test
    void update_deveAnonimizarTextosAtualizados() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(anonymizationStrategy.anonymize(any(AnonymizationContext.class)))
                .thenReturn(new AnonymizationResult(TITULO_ANONIMIZADO, DESCRICAO_ANONIMIZADA));
        when(repository.save(any(Manifestation.class))).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        service.update(1L, requestAnonimo);

        verify(repository).save(argThat(e ->
                TITULO_ANONIMIZADO.equals(e.getTitle())
                        && DESCRICAO_ANONIMIZADA.equals(e.getDescription())));
    }
}
