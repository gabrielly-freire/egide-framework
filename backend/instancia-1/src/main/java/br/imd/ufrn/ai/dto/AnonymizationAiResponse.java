package br.imd.ufrn.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resposta do endpoint {@code POST /compliance/anonimizar} do microserviço de IA.
 */
public record AnonymizationAiResponse(
        @JsonProperty("report_id") Long reportId,
        @JsonProperty("anonymized_title") String anonymizedTitle,
        @JsonProperty("anonymized_description") String anonymizedDescription
) {}
