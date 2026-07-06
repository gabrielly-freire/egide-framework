package br.imd.ufrn.atendimento.workflow;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.workflow.WorkflowTemplate;
import java.time.Duration;
import org.springframework.stereotype.Component;

/**
 * Workflow baseado na Lei de Acesso à Informação (LAI): progressão linear
 * {@code REGISTERED -> IN_REVIEW -> RESOLVED -> CLOSED}, com recurso admitido em
 * até três instâncias administrativas e prazo fixo de 20 dias por etapa
 * (persistido em {@code Manifestation.deadlineAt}, ver migração V5).
 * Implementa os hooks do Template Method definido em {@link WorkflowTemplate}.
 */
@Component
public class PublicServiceWorkflowTemplate extends WorkflowTemplate {

    /** LAI: recurso admitido em até três instâncias administrativas. */
    private static final int MAX_APPEALS = 3;

    /**
     * Avança o status conforme a sequência linear da LAI. Qualquer status além de
     * {@code IN_REVIEW} (isto é, {@code RESOLVED} ou já {@code CLOSED}) resolve para
     * {@code CLOSED}, encerrando o fluxo.
     */
    @Override
    protected ManifestationStatus resolveNextStatus(ManifestationStatus current) {
        return switch (current) {
            case REGISTERED -> ManifestationStatus.IN_REVIEW;
            case IN_REVIEW -> ManifestationStatus.RESOLVED;
            default -> ManifestationStatus.CLOSED;
        };
    }

    /**
     * Todo recurso reabre a manifestação para nova análise em {@code IN_REVIEW}.
     * */
    @Override
    protected ManifestationStatus resolveAppealStatus(ManifestationStatus current) {
        return ManifestationStatus.IN_REVIEW;
    }

    /**
     * Recurso é permitido enquanto a manifestação estiver em análise ou já resolvida
     * ({@code IN_REVIEW}/{@code RESOLVED}) e o número de recursos já interpostos
     * ({@link Manifestation#getAppealCount()}) ainda não tiver atingido
     * {@link #MAX_APPEALS} — as três instâncias administrativas da LAI.
     */
    @Override
    protected boolean isAppealAllowed(Manifestation manifestation) {
        boolean statusPermiteRecurso = manifestation.getStatus() == ManifestationStatus.IN_REVIEW
                || manifestation.getStatus() == ManifestationStatus.RESOLVED;
        return statusPermiteRecurso && manifestation.getAppealCount() < MAX_APPEALS;
    }

    /**
     * Prazo único de 20 dias, independente do status, para todas as etapas da LAI.
     * */
    @Override
    protected Duration deadlineFor(ManifestationStatus status) {
        return Duration.ofDays(20);
    }
}
