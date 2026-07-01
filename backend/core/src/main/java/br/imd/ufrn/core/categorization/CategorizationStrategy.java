package br.imd.ufrn.core.categorization;

/**
 * Estratégia de triagem e categorização automática de uma manifestação (padrão Strategy).
 *
 * <p>A lógica de classificação varia por instância:
 *
 * <ul>
 *   <li><b>Compliance:</b> categoria e risco jurídico/reputacional via microserviço de IA.</li>
 *   <li><b>Universidade:</b> categoria por área (infraestrutura, matrícula etc.).</li>
 *   <li><b>Serviço Público:</b> categoria por tipo de manifestação e órgão responsável.</li>
 * </ul>
 *
 * <p>O contrato é <em>síncrono</em>; a mecânica de execução (chamada externa, timeouts,
 * assincronismo) é decidida pela instância. O Core registra {@link NoOpCategorizationStrategy}
 * como padrão via auto-configuração.
 */
public interface CategorizationStrategy {

    /**
     * Classifica a manifestação descrita pelo contexto.
     *
     * @param context dados textuais da manifestação
     * @return categoria e nível de risco; campos podem ser {@code null} quando não aplicáveis
     */
    CategorizationResult categorize(CategorizationContext context);
}
