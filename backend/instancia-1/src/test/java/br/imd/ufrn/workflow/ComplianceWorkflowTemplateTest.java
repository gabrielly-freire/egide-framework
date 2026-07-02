package br.imd.ufrn.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.exception.WorkflowAdvanceNotAllowedException;
import br.imd.ufrn.core.exception.WorkflowAppealNotAllowedException;
import br.imd.ufrn.core.workflow.WorkflowStepResult;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class ComplianceWorkflowTemplateTest {

    private final ComplianceWorkflowTemplate template = new ComplianceWorkflowTemplate();

    private Manifestation manifestation(ManifestationStatus status) {
        Manifestation m = new Manifestation();
        m.setStatus(status);
        return m;
    }

    @Test
    void initialDeadline_deveSer5DiasDaTriagem() {
        assertThat(template.initialDeadline()).isEqualTo(Duration.ofDays(5));
    }

    @Test
    void advance_deTriagemParaInvestigacao_comPrazo30Dias() {
        WorkflowStepResult result = template.advance(manifestation(ManifestationStatus.REGISTERED));

        assertThat(result.nextStatus()).isEqualTo(ManifestationStatus.IN_REVIEW);
        assertThat(result.deadline()).isEqualTo(Duration.ofDays(30));
    }

    @Test
    void advance_deInvestigacaoParaDecisao_semPrazo() {
        WorkflowStepResult result = template.advance(manifestation(ManifestationStatus.IN_REVIEW));

        assertThat(result.nextStatus()).isEqualTo(ManifestationStatus.RESOLVED);
        assertThat(result.deadline()).isNull();
    }

    @Test
    void advance_deveFalhar_quandoStatusTerminal() {
        assertThatThrownBy(() -> template.advance(manifestation(ManifestationStatus.RESOLVED)))
                .isInstanceOf(WorkflowAdvanceNotAllowedException.class);
    }

    @Test
    void appeal_deveFalhar_semRecursoSimples() {
        assertThatThrownBy(() -> template.appeal(manifestation(ManifestationStatus.RESOLVED)))
                .isInstanceOf(WorkflowAppealNotAllowedException.class);
    }
}
