package br.imd.ufrn.core.dto;

import java.util.Map;

public record ManifestationSummaryReport(
        long totalManifestations,
        Map<String, Long> byStatus,
        Map<String, Long> byType,
        long totalEvaluations,
        Double averageRating,
        long totalDecisions,
        long totalOpinions
) {}
