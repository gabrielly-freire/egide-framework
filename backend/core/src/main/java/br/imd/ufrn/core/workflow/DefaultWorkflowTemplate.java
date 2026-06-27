package br.imd.ufrn.core.workflow;

import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.exception.WorkflowAppealNotAllowedException;
import java.time.Duration;

public class DefaultWorkflowTemplate extends WorkflowTemplate {

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
        throw new WorkflowAppealNotAllowedException(null);
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
