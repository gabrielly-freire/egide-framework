package br.imd.ufrn.atendimento.workflow;

import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.workflow.WorkflowTemplate;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class PublicServiceWorkflowTemplate extends WorkflowTemplate {

    @Override
    protected ManifestationStatus resolveNextStatus(ManifestationStatus current) {
        return switch (current) {
            case REGISTERED -> ManifestationStatus.IN_REVIEW;
            case IN_REVIEW -> ManifestationStatus.RESOLVED;
            default -> ManifestationStatus.CLOSED;
        };
    }

    @Override
    protected ManifestationStatus resolveAppealStatus(ManifestationStatus current) {
        return ManifestationStatus.IN_REVIEW;
    }

    @Override
    protected boolean isAppealAllowed(ManifestationStatus status) {
        return status == ManifestationStatus.IN_REVIEW || status == ManifestationStatus.RESOLVED;
    }

    @Override
    protected Duration deadlineFor(ManifestationStatus status) {
        return Duration.ofDays(20);
    }
}
