package br.imd.ufrn.core.service;

import br.imd.ufrn.core.dto.ManifestationSummaryReport;
import java.time.LocalDateTime;

public interface ReportService {

    ManifestationSummaryReport summary();

    ManifestationSummaryReport summaryByPeriod(LocalDateTime from, LocalDateTime to);
}
