package br.imd.ufrn.categorization;

import br.imd.ufrn.ai.AiAnalysisClient;
import br.imd.ufrn.ai.dto.AnalysisAiRequest;
import br.imd.ufrn.ai.dto.AnalysisAiResponse;
import br.imd.ufrn.core.categorization.CategorizationContext;
import br.imd.ufrn.core.categorization.CategorizationResult;
import br.imd.ufrn.core.categorization.CategorizationStrategy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Ponto variável de categorização da Instância 1 (Compliance).
 *
 * <p>Delega a triagem ao microserviço de IA ({@code POST /analysis/analisar}) e converte a resposta
 * (Strings) para os enums da instância ({@link ComplianceCategory}/{@link ComplianceRisk}) apenas
 * para <b>validar</b> — devolvendo ao Core novamente como {@code String} via {@link CategorizationResult}.
 * Valores desconhecidos são logados e tratados como {@code null} (categorização é best-effort).
 *
 * <p>Como é {@link Component}, desliga o default {@code NoOpCategorizationStrategy} do Core.
 */
@Component
@RequiredArgsConstructor
public class AiCategorizationStrategy implements CategorizationStrategy {

    private static final Logger log = LoggerFactory.getLogger(AiCategorizationStrategy.class);

    private final AiAnalysisClient aiAnalysisClient;

    @Override
    public CategorizationResult categorize(CategorizationContext context) {
        AnalysisAiResponse response = aiAnalysisClient.analyze(new AnalysisAiRequest(
                context.manifestationId(), context.title(), context.description()));

        if (response == null) {
            log.warn("IA não retornou análise para a manifestação {}", context.manifestationId());
            return new CategorizationResult(null, null);
        }
        return new CategorizationResult(
                normalizeCategory(response.category()),
                normalizeRisk(response.riskLevel()));
    }

    private String normalizeCategory(String value) {
        if (value == null) {
            return null;
        }
        try {
            return ComplianceCategory.valueOf(value).name();
        } catch (IllegalArgumentException e) {
            log.warn("Categoria desconhecida retornada pela IA: {}", value);
            return null;
        }
    }

    private String normalizeRisk(String value) {
        if (value == null) {
            return null;
        }
        try {
            return ComplianceRisk.valueOf(value).name();
        } catch (IllegalArgumentException e) {
            log.warn("Risco desconhecido retornado pela IA: {}", value);
            return null;
        }
    }
}
