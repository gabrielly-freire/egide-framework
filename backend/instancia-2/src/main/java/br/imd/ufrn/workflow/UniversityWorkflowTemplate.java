package br.imd.ufrn.workflow;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.workflow.WorkflowTemplate;
import java.time.Duration;
import org.springframework.stereotype.Component;

/**
 * Workflow da Instância 2 (Ouvidoria Universitária): mediação interna com direito a recurso.
 *
 * <p>Sobre o enum fixo do Core, a fase de <b>mediação interna</b> é representada por
 * {@link ManifestationStatus#IN_REVIEW}:
 *
 * <ul>
 *   <li><b>Avanço:</b> {@code REGISTERED → IN_REVIEW} (abre a mediação) {@code → RESOLVED}.</li>
 *   <li><b>Recurso:</b> permitido apenas em {@code RESOLVED}; reabre a mediação
 *       ({@code RESOLVED → IN_REVIEW}).</li>
 *   <li><b>Prazos:</b> {@link #MEDIATION_DEADLINE} para a mediação e {@link #APPEAL_DEADLINE} para
 *       a janela de recurso (atrelada ao calendário acadêmico).</li>
 * </ul>
 *
 * <p>O prazo (deadline) faz parte do contrato do {@link WorkflowTemplate}, mas o
 * {@code WorkflowService} do Core persiste apenas o status — o prazo é informativo neste nível.
 *
 * <p>Registrada como {@link Component}; desliga o {@code DefaultWorkflowTemplate} do Core via
 * {@code @ConditionalOnMissingBean}.
 */
@Component
public class UniversityWorkflowTemplate extends WorkflowTemplate {

    /** Prazo para conclusão da mediação interna. */
    static final Duration MEDIATION_DEADLINE = Duration.ofDays(15);

    /** Janela para interposição de recurso (atrelada ao calendário acadêmico). */
    static final Duration APPEAL_DEADLINE = Duration.ofDays(30);

    @Override
    protected ManifestationStatus resolveNextStatus(ManifestationStatus current) {
        return switch (current) {
            case REGISTERED -> ManifestationStatus.IN_REVIEW; // entra em mediação interna
            case IN_REVIEW -> ManifestationStatus.RESOLVED;   // mediação concluída
            // RESOLVED e CLOSED são terminais e já barrados por advance(); inalcançável.
            default -> throw new IllegalStateException("Sem próximo status definido para: " + current);
        };
    }

    @Override
    protected ManifestationStatus resolveAppealStatus(ManifestationStatus current) {
        // Só é invocado quando isAppealAllowed() é true (status RESOLVED): o recurso reabre a mediação.
        return ManifestationStatus.IN_REVIEW;
    }

    @Override
    protected boolean isAppealAllowed(Manifestation manifestation) {
        return manifestation.getStatus() == ManifestationStatus.RESOLVED;
    }

    @Override
    protected Duration deadlineFor(ManifestationStatus status) {
        return switch (status) {
            case IN_REVIEW -> MEDIATION_DEADLINE;
            case RESOLVED -> APPEAL_DEADLINE;
            default -> null;
        };
    }
}
