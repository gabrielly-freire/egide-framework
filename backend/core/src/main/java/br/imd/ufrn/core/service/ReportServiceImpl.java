package br.imd.ufrn.core.service;

import br.imd.ufrn.core.domain.DecisionType;
import br.imd.ufrn.core.dto.ManifestationSummaryReport;
import br.imd.ufrn.core.persistence.DecisionRecordRepository;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import br.imd.ufrn.core.persistence.ServiceEvaluationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ManifestationRepository manifestationRepository;
    private final ServiceEvaluationRepository evaluationRepository;
    private final DecisionRecordRepository decisionRecordRepository;

    @Override
    public ManifestationSummaryReport summary() {
        return new ManifestationSummaryReport(
                manifestationRepository.count(),
                toStringMap(manifestationRepository.countGroupedByStatus()),
                toStringMap(manifestationRepository.countGroupedByType()),
                evaluationRepository.count(),
                evaluationRepository.averageRating(),
                decisionRecordRepository.countByType(DecisionType.DECISION),
                decisionRecordRepository.countByType(DecisionType.OPINION)
        );
    }

    @Override
    public ManifestationSummaryReport summaryByPeriod(LocalDateTime from, LocalDateTime to) {
        return new ManifestationSummaryReport(
                manifestationRepository.countByCreatedAtBetween(from, to),
                toStringMap(manifestationRepository.countGroupedByStatusBetween(from, to)),
                toStringMap(manifestationRepository.countGroupedByTypeBetween(from, to)),
                evaluationRepository.countByCreatedAtBetween(from, to),
                evaluationRepository.averageRatingBetween(from, to),
                decisionRecordRepository.countByTypeAndCreatedAtBetween(DecisionType.DECISION, from, to),
                decisionRecordRepository.countByTypeAndCreatedAtBetween(DecisionType.OPINION, from, to)
        );
    }

    private Map<String, Long> toStringMap(List<Object[]> rows) {
        return rows.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1]
                ));
    }
}
