package br.imd.ufrn.core.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.exception.WorkflowAdvanceNotAllowedException;
import br.imd.ufrn.core.exception.WorkflowAppealNotAllowedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultWorkflowTemplateTest {

    private final WorkflowTemplate template = new DefaultWorkflowTemplate();

    private Manifestation manifestation;

    @BeforeEach
    void setUp() {
        manifestation = new Manifestation();
        manifestation.setId(1L);
    }

    @Test
    void advance_deveIrParaInReview_quandoRegistered() {
        manifestation.setStatus(ManifestationStatus.REGISTERED);

        WorkflowStepResult result = template.advance(manifestation);

        assertThat(result.nextStatus()).isEqualTo(ManifestationStatus.IN_REVIEW);
    }

    @Test
    void advance_deveIrParaResolved_quandoInReview() {
        manifestation.setStatus(ManifestationStatus.IN_REVIEW);

        WorkflowStepResult result = template.advance(manifestation);

        assertThat(result.nextStatus()).isEqualTo(ManifestationStatus.RESOLVED);
    }

    @Test
    void advance_deveLancarExcecao_quandoResolved() {
        manifestation.setStatus(ManifestationStatus.RESOLVED);

        assertThatThrownBy(() -> template.advance(manifestation))
                .isInstanceOf(WorkflowAdvanceNotAllowedException.class);
    }

    @Test
    void advance_deveLancarExcecao_quandoClosed() {
        manifestation.setStatus(ManifestationStatus.CLOSED);

        assertThatThrownBy(() -> template.advance(manifestation))
                .isInstanceOf(WorkflowAdvanceNotAllowedException.class);
    }

    @Test
    void advance_deveRetornarDeadlineNula() {
        manifestation.setStatus(ManifestationStatus.REGISTERED);

        WorkflowStepResult result = template.advance(manifestation);

        assertThat(result.deadline()).isNull();
    }

    @Test
    void appeal_deveLancarExcecao_poisRecursoNaoEPermitido() {
        manifestation.setStatus(ManifestationStatus.IN_REVIEW);

        assertThatThrownBy(() -> template.appeal(manifestation))
                .isInstanceOf(WorkflowAppealNotAllowedException.class);
    }

    @Test
    void isAppealAllowed_deveRetornarFalse_paraTodosOsStatus() {
        for (ManifestationStatus status : ManifestationStatus.values()) {
            manifestation.setStatus(status);
            assertThatThrownBy(() -> template.appeal(manifestation))
                    .isInstanceOf(WorkflowAppealNotAllowedException.class);
        }
    }
}
