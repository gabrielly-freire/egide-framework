package br.imd.ufrn.atendimento.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.exception.WorkflowAdvanceNotAllowedException;
import br.imd.ufrn.core.exception.WorkflowAppealNotAllowedException;
import br.imd.ufrn.core.workflow.WorkflowStepResult;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublicServiceWorkflowTemplateTest {

    private final PublicServiceWorkflowTemplate template = new PublicServiceWorkflowTemplate();

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
    void advance_deveRetornarPrazo20Dias() {
        manifestation.setStatus(ManifestationStatus.REGISTERED);

        WorkflowStepResult result = template.advance(manifestation);

        assertThat(result.deadline()).isEqualTo(Duration.ofDays(20));
    }

    @Test
    void appeal_devePermitir_quandoInReview() {
        manifestation.setStatus(ManifestationStatus.IN_REVIEW);

        WorkflowStepResult result = template.appeal(manifestation);

        assertThat(result.nextStatus()).isEqualTo(ManifestationStatus.IN_REVIEW);
    }

    @Test
    void appeal_devePermitir_quandoResolved() {
        manifestation.setStatus(ManifestationStatus.RESOLVED);

        WorkflowStepResult result = template.appeal(manifestation);

        assertThat(result.nextStatus()).isEqualTo(ManifestationStatus.IN_REVIEW);
    }

    @Test
    void appeal_deveIncrementarContadorDeRecursos() {
        manifestation.setStatus(ManifestationStatus.IN_REVIEW);

        template.appeal(manifestation);

        assertThat(manifestation.getAppealCount()).isEqualTo(1);
    }

    @Test
    void appeal_devePermitirAteATerceiraInstanciaAdministrativa() {
        manifestation.setStatus(ManifestationStatus.IN_REVIEW);
        manifestation.setAppealCount(2);

        WorkflowStepResult result = template.appeal(manifestation);

        assertThat(result.nextStatus()).isEqualTo(ManifestationStatus.IN_REVIEW);
        assertThat(manifestation.getAppealCount()).isEqualTo(3);
    }

    @Test
    void appeal_naoDevePermitir_apósAsTresInstanciasAdministrativas() {
        manifestation.setStatus(ManifestationStatus.IN_REVIEW);
        manifestation.setAppealCount(3);

        assertThatThrownBy(() -> template.appeal(manifestation))
                .isInstanceOf(WorkflowAppealNotAllowedException.class);
    }

    @Test
    void appeal_naoDevePermitir_quandoRegistered() {
        manifestation.setStatus(ManifestationStatus.REGISTERED);

        assertThatThrownBy(() -> template.appeal(manifestation))
                .isInstanceOf(WorkflowAppealNotAllowedException.class);
    }

    @Test
    void appeal_naoDevePermitir_quandoClosed() {
        manifestation.setStatus(ManifestationStatus.CLOSED);

        assertThatThrownBy(() -> template.appeal(manifestation))
                .isInstanceOf(WorkflowAppealNotAllowedException.class);
    }
}
