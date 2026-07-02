package br.imd.ufrn.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Resposta do endpoint {@code POST /analysis/analisar} do microserviço de IA.
 *
 * <p>O endpoint devolve categoria, risco E dados de conflito numa só chamada. A categorização
 * (Fase 2) usa {@code category}/{@code riskLevel}; os campos de conflito ficam prontos para a
 * detecção de conflito de interesse (Fase 4), permitindo reaproveitar a mesma resposta.
 */
public record AnalysisAiResponse(
        @JsonProperty("report_id") Long reportId,
        String category,
        @JsonProperty("risk_level") String riskLevel,
        @JsonProperty("conflict_detected") Boolean conflictDetected,
        @JsonProperty("conflicted_user_ids") List<String> conflictedUserIds,
        @JsonProperty("manager_conflict") Boolean managerConflict
) {}
