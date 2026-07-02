package br.imd.ufrn.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Corpo enviado ao endpoint {@code POST /analysis/analisar} do microserviço de IA.
 * {@code files} e {@code responsible_users} são opcionais no schema Python (default vazio),
 * então não são enviados na categorização — só título e descrição bastam para classificar.
 */
public record AnalysisAiRequest(
        @JsonProperty("report_id") Long reportId,
        String title,
        String description
) {}
