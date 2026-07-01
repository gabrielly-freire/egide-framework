package br.imd.ufrn.core.workflow;

import br.imd.ufrn.core.domain.ManifestationStatus;
import java.time.Duration;

/**
 * Implementação padrão do workflow registrada pelo Core via auto-configuração
 * ({@code @ConditionalOnMissingBean}). Define um fluxo linear mínimo, sem prazos e sem recurso,
 * adequado a qualquer instância que não personalize o ponto variável de workflow.
 *
 * <p>Avanço: {@code REGISTERED → IN_REVIEW → RESOLVED}. Recurso nunca é permitido.
 * Instâncias com regras próprias (ex.: as 5 fases do Compliance) substituem este bean
 * estendendo {@link WorkflowTemplate}.
 */
public class DefaultWorkflowTemplate extends WorkflowTemplate {

    @Override
    protected ManifestationStatus resolveNextStatus(ManifestationStatus current) {
        return switch (current) {
            case REGISTERED -> ManifestationStatus.IN_REVIEW;
            case IN_REVIEW -> ManifestationStatus.RESOLVED;
            // RESOLVED e CLOSED são terminais e já barrados por advance(); inalcançável.
            default -> throw new IllegalStateException("Sem próximo status definido para: " + current);
        };
    }

    @Override
    protected ManifestationStatus resolveAppealStatus(ManifestationStatus current) {
        // Inalcançável: isAppealAllowed() sempre retorna false, então appeal() barra antes daqui.
        throw new UnsupportedOperationException("O workflow padrão não admite recurso");
    }

    @Override
    protected boolean isAppealAllowed(ManifestationStatus status) {
        return false;
    }

    @Override
    protected Duration deadlineFor(ManifestationStatus status) {
        return null;
    }
}
