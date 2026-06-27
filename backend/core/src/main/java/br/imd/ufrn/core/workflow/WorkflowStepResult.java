package br.imd.ufrn.core.workflow;

import br.imd.ufrn.core.domain.ManifestationStatus;
import java.time.Duration;

public record WorkflowStepResult(
        ManifestationStatus nextStatus,
        Duration deadline
) {}
