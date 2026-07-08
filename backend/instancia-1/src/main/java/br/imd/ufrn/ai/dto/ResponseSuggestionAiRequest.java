package br.imd.ufrn.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Corpo do {@code POST /compliance/sugerir-resposta} do microserviço de IA. */
public record ResponseSuggestionAiRequest(
        @JsonProperty("report_id") Long reportId,
        String title,
        String description,
        @JsonProperty("protocol_number") String protocolNumber,
        String category,
        String risk
) {}
