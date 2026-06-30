package br.imd.ufrn.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.domain.DecisionType;
import br.imd.ufrn.core.dto.ManifestationSummaryReport;
import br.imd.ufrn.core.persistence.DecisionRecordRepository;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import br.imd.ufrn.core.persistence.ServiceEvaluationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ManifestationRepository manifestationRepository;

    @Mock
    private ServiceEvaluationRepository evaluationRepository;

    @Mock
    private DecisionRecordRepository decisionRecordRepository;

    @InjectMocks
    private ReportServiceImpl service;

    @Test
    void summary_deveAgregardadosDeTodasAsFontes() {
        when(manifestationRepository.count()).thenReturn(10L);
        when(manifestationRepository.countGroupedByStatus()).thenReturn(List.of(
                new Object[]{"REGISTERED", 6L},
                new Object[]{"RESOLVED", 4L}
        ));
        when(manifestationRepository.countGroupedByType()).thenReturn(List.of(
                new Object[]{"RECLAMAÇÃO", 7L},
                new Object[]{"SUGESTÃO", 3L}
        ));
        when(evaluationRepository.count()).thenReturn(5L);
        when(evaluationRepository.averageRating()).thenReturn(4.2);
        when(decisionRecordRepository.countByType(DecisionType.DECISION)).thenReturn(3L);
        when(decisionRecordRepository.countByType(DecisionType.OPINION)).thenReturn(2L);

        ManifestationSummaryReport report = service.summary();

        assertThat(report.totalManifestations()).isEqualTo(10L);
        assertThat(report.byStatus()).containsEntry("REGISTERED", 6L).containsEntry("RESOLVED", 4L);
        assertThat(report.byType()).containsEntry("RECLAMAÇÃO", 7L).containsEntry("SUGESTÃO", 3L);
        assertThat(report.totalEvaluations()).isEqualTo(5L);
        assertThat(report.averageRating()).isEqualTo(4.2);
        assertThat(report.totalDecisions()).isEqualTo(3L);
        assertThat(report.totalOpinions()).isEqualTo(2L);
    }

    @Test
    void summary_deveRetornarMediaNula_quandoNaoExistemAvaliacoes() {
        when(manifestationRepository.count()).thenReturn(0L);
        when(manifestationRepository.countGroupedByStatus()).thenReturn(List.of());
        when(manifestationRepository.countGroupedByType()).thenReturn(List.of());
        when(evaluationRepository.count()).thenReturn(0L);
        when(evaluationRepository.averageRating()).thenReturn(null);
        when(decisionRecordRepository.countByType(DecisionType.DECISION)).thenReturn(0L);
        when(decisionRecordRepository.countByType(DecisionType.OPINION)).thenReturn(0L);

        ManifestationSummaryReport report = service.summary();

        assertThat(report.averageRating()).isNull();
        assertThat(report.byStatus()).isEmpty();
        assertThat(report.byType()).isEmpty();
    }

    @Test
    void summaryByPeriod_deveAgregardadosFiltradosPorPeriodo() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 6, 30, 23, 59, 59);

        when(manifestationRepository.countByCreatedAtBetween(from, to)).thenReturn(4L);
        when(manifestationRepository.countGroupedByStatusBetween(from, to)).thenReturn(List.of(
                new Object[]{"REGISTERED", 2L},
                new Object[]{"CLOSED", 2L}
        ));
        when(manifestationRepository.countGroupedByTypeBetween(from, to)).thenReturn(List.<Object[]>of(
                new Object[]{"DENÚNCIA", 4L}
        ));
        when(evaluationRepository.countByCreatedAtBetween(from, to)).thenReturn(2L);
        when(evaluationRepository.averageRatingBetween(from, to)).thenReturn(3.5);
        when(decisionRecordRepository.countByTypeAndCreatedAtBetween(DecisionType.DECISION, from, to)).thenReturn(1L);
        when(decisionRecordRepository.countByTypeAndCreatedAtBetween(DecisionType.OPINION, from, to)).thenReturn(1L);

        ManifestationSummaryReport report = service.summaryByPeriod(from, to);

        assertThat(report.totalManifestations()).isEqualTo(4L);
        assertThat(report.byStatus()).containsEntry("REGISTERED", 2L).containsEntry("CLOSED", 2L);
        assertThat(report.totalEvaluations()).isEqualTo(2L);
        assertThat(report.averageRating()).isEqualTo(3.5);
        assertThat(report.totalDecisions()).isEqualTo(1L);
        assertThat(report.totalOpinions()).isEqualTo(1L);
    }
}
