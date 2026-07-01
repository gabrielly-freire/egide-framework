package br.imd.ufrn.categorization;

import br.imd.ufrn.core.categorization.CategorizationContext;
import br.imd.ufrn.core.categorization.CategorizationResult;
import br.imd.ufrn.core.categorization.CategorizationStrategy;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Triagem automática da Instância 2 (Ouvidoria Universitária) por heurística de palavras-chave.
 *
 * <p>Concatena título e descrição, normaliza (minúsculas, sem acento) e conta, para cada
 * {@link UniversityCategory}, quantas de suas palavras-chave aparecem. Escolhe a categoria com
 * mais ocorrências; em empate vence a de menor ordem no enum; sem nenhuma ocorrência cai em
 * {@link UniversityCategory#OUTROS}.
 *
 * <p>O nível de risco é sempre {@code null}: a ouvidoria universitária não trabalha com o conceito
 * de risco jurídico/reputacional (diferente do Compliance da Instância 1).
 *
 * <p>Registrada como {@link Component}; desliga o default do Core
 * ({@code NoOpCategorizationStrategy}) via {@code @ConditionalOnMissingBean}.
 */
@Component
public class UniversityCategorizationStrategy implements CategorizationStrategy {

    @Override
    public CategorizationResult categorize(CategorizationContext context) {
        String normalized = normalize(
                safe(context.title()) + " " + safe(context.description()));
        Set<String> tokens = Arrays.stream(normalized.split("[^\\p{L}\\p{N}]+"))
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toSet());

        UniversityCategory best = UniversityCategory.OUTROS;
        int bestScore = 0;
        for (UniversityCategory category : UniversityCategory.values()) {
            int score = countMatches(category, normalized, tokens);
            if (score > bestScore) {
                bestScore = score;
                best = category;
            }
        }
        return new CategorizationResult(best.name(), null);
    }

    private int countMatches(UniversityCategory category, String normalizedText, Set<String> tokens) {
        int score = 0;
        for (String keyword : category.keywords()) {
            boolean matched = keyword.contains(" ")
                    ? normalizedText.contains(keyword)   // expressão composta
                    : tokens.contains(keyword);          // palavra isolada (token exato)
            if (matched) {
                score++;
            }
        }
        return score;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
