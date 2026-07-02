package br.imd.ufrn.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Corpo enviado ao endpoint {@code POST /compliance/anonimizar} do microserviço de IA.
 * Os nomes JSON seguem o schema Python (snake_case).
 */
public record AnonymizationAiRequest(
        @JsonProperty("report_id") Long reportId,
        String title,
        String description
) {}
