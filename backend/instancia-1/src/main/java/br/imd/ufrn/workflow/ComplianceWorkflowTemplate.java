package br.imd.ufrn.workflow;

import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.workflow.WorkflowTemplate;
import java.time.Duration;
import org.springframework.stereotype.Component;

/**
 * Ponto variável de workflow da Instância 1 (Compliance): investigação sigilosa, com prazos por
 * fase e <b>sem recurso simples</b>.
 *
 * <p>Mapeia o processo de compliance nos estados universais do Core:
 * <ul>
 *   <li>{@code REGISTERED} — triagem (prazo: 5 dias);</li>
 *   <li>{@code IN_REVIEW} — investigação sigilosa (prazo: 30 dias);</li>
 *   <li>{@code RESOLVED} — decisão final (sem prazo).</li>
 * </ul>
 *
 * <p>Como é {@link Component}, desliga o default {@code DefaultWorkflowTemplate} do Core. Os prazos
 * definidos aqui são carimbados automaticamente pelo Core em {@code Manifestation.deadlineAt}.
 */
@Component
public class ComplianceWorkflowTemplate extends WorkflowTemplate {

    @Override
    protected ManifestationStatus resolveNextStatus(ManifestationStatus current) {
        return switch (current) {
            case REGISTERED -> ManifestationStatus.IN_REVIEW;
            case IN_REVIEW -> ManifestationStatus.RESOLVED;
            // RESOLVED/CLOSED são terminais e já barrados por advance(); inalcançável.
            default -> throw new IllegalStateException("Sem próximo status definido para: " + current);
        };
    }

    @Override
    protected ManifestationStatus resolveAppealStatus(ManifestationStatus current) {
        // Inalcançável: isAppealAllowed() é sempre false (compliance não admite recurso simples).
        throw new UnsupportedOperationException("O workflow de compliance não admite recurso simples");
    }

    @Override
    protected boolean isAppealAllowed(ManifestationStatus status) {
        return false;
    }

    @Override
    protected Duration deadlineFor(ManifestationStatus status) {
        return switch (status) {
            case REGISTERED -> Duration.ofDays(5);   // triagem
            case IN_REVIEW -> Duration.ofDays(30);   // investigação sigilosa
            default -> null;                          // RESOLVED/CLOSED sem prazo
        };
    }
}
