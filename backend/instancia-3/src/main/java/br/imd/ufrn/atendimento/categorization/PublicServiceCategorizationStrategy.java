package br.imd.ufrn.atendimento.categorization;

import br.imd.ufrn.core.categorization.CategorizationContext;
import br.imd.ufrn.core.categorization.CategorizationResult;
import br.imd.ufrn.core.categorization.CategorizationStrategy;
import org.springframework.stereotype.Component;

/**
 * Categoriza a manifestação pelo próprio tipo informado, que é a mesma chave usada por
 * {@link br.imd.ufrn.atendimento.designation.RegionDesignationStrategy} para localizar o
 * órgão responsável (via {@code Analyst.specialty}). Não há conceito de risco na LAI.
 */
@Component
public class PublicServiceCategorizationStrategy implements CategorizationStrategy {

    /**
     * Retorna o próprio {@code type} da manifestação como categoria e {@code riskLevel}
     * sempre {@code null}, já que a LAI não define níveis de risco.
     */
    @Override
    public CategorizationResult categorize(CategorizationContext context) {
        return new CategorizationResult(context.type(), null);
    }
}
