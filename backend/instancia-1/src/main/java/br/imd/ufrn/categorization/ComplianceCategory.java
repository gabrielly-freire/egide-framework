package br.imd.ufrn.categorization;

/**
 * Vocabulário de categorias da Instância 1 (Compliance), espelhando o que o microserviço de IA
 * retorna. É o "enum na instância" — o Core armazena apenas a String correspondente
 * ({@code name()}), mantendo-se genérico.
 */
public enum ComplianceCategory {
    DENUNCIATION,
    COMPLAINT,
    COMPLIMENT,
    SUGGESTION,
    REQUEST
}
