package br.imd.ufrn.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseSuggestionAiResponse(
        @JsonProperty("report_id") Long reportId,
        @JsonProperty("suggested_response") String suggestedResponse
) {}
