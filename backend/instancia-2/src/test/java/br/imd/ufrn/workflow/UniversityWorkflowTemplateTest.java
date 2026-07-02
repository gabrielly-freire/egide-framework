package br.imd.ufrn.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.exception.WorkflowAdvanceNotAllowedException;
import br.imd.ufrn.core.exception.WorkflowAppealNotAllowedException;
import br.imd.ufrn.core.workflow.WorkflowStepResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UniversityWorkflowTemplateTest {

    private final UniversityWorkflowTemplate template = new UniversityWorkflowTemplate();

    private Manifestation manifestation;

    @BeforeEach
    void setUp() {
        manifestation = new Manifestation();
        manifestation.setId(1L);
    }

    private Manifestation withStatus(ManifestationStatus status) {
        manifestation.setStatus(status);
        return manifestation;
    }

    // ── Avanço ────────────────────────────────────────────────────────────

    @Test
    void advance_deveAbrirMediacao_quandoRegistered() {
        WorkflowStepResult result = template.advance(withStatus(ManifestationStatus.REGISTERED));

        assertThat(result.nextStatus()).isEqualTo(ManifestationStatus.IN_REVIEW);
        assertThat(result.deadline()).isEqualTo(UniversityWorkflowTemplate.MEDIATION_DEADLINE);
    }

    @Test
    void advance_deveResolver_quandoMediacaoConcluida() {
        WorkflowStepResult result = template.advance(withStatus(ManifestationStatus.IN_REVIEW));

        assertThat(result.nextStatus()).isEqualTo(ManifestationStatus.RESOLVED);
        assertThat(result.deadline()).isEqualTo(UniversityWorkflowTemplate.APPEAL_DEADLINE);
    }

    @Test
    void advance_deveLancarExcecao_quandoResolved() {
        assertThatThrownBy(() -> template.advance(withStatus(ManifestationStatus.RESOLVED)))
                .isInstanceOf(WorkflowAdvanceNotAllowedException.class);
    }

    @Test
    void advance_deveLancarExcecao_quandoClosed() {
        assertThatThrownBy(() -> template.advance(withStatus(ManifestationStatus.CLOSED)))
                .isInstanceOf(WorkflowAdvanceNotAllowedException.class);
    }

    // ── Recurso ───────────────────────────────────────────────────────────

    @Test
    void appeal_deveReabrirMediacao_quandoResolved() {
        WorkflowStepResult result = template.appeal(withStatus(ManifestationStatus.RESOLVED));

        assertThat(result.nextStatus()).isEqualTo(ManifestationStatus.IN_REVIEW);
        assertThat(result.deadline()).isEqualTo(UniversityWorkflowTemplate.MEDIATION_DEADLINE);
    }

    @Test
    void appeal_deveLancarExcecao_quandoRegistered() {
        assertThatThrownBy(() -> template.appeal(withStatus(ManifestationStatus.REGISTERED)))
                .isInstanceOf(WorkflowAppealNotAllowedException.class);
    }

    @Test
    void appeal_deveLancarExcecao_quandoEmMediacao() {
        assertThatThrownBy(() -> template.appeal(withStatus(ManifestationStatus.IN_REVIEW)))
                .isInstanceOf(WorkflowAppealNotAllowedException.class);
    }

    @Test
    void appeal_deveLancarExcecao_quandoClosed() {
        assertThatThrownBy(() -> template.appeal(withStatus(ManifestationStatus.CLOSED)))
                .isInstanceOf(WorkflowAppealNotAllowedException.class);
    }
}
