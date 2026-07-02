package br.imd.ufrn.anonymization;

import br.imd.ufrn.ai.AiAnonymizationClient;
import br.imd.ufrn.ai.dto.AnonymizationAiRequest;
import br.imd.ufrn.ai.dto.AnonymizationAiResponse;
import br.imd.ufrn.core.anonymization.AnonymizationContext;
import br.imd.ufrn.core.anonymization.AnonymizationResult;
import br.imd.ufrn.core.anonymization.AnonymizationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Ponto variável de anonimização da Instância 1 (Compliance Corporativo).
 *
 * <p>Delega ao microserviço de IA ({@code POST /compliance/anonimizar}), que faz pseudonimização
 * inteligente (LGPD) de <b>título e descrição</b>. Só atua em denúncias anônimas; manifestações
 * identificadas ({@code anonymous == false}) passam intactas.
 *
 * <p>Como é um {@link Component}, o Spring registra este bean e o
 * {@code @ConditionalOnMissingBean(AnonymizationStrategy.class)} do Core desliga o default
 * {@code TransparentAnonymizationStrategy}.
 *
 * <p>Se a IA falhar ou não retornar descrição numa denúncia anônima, lançamos erro em vez de gravar
 * o texto original — para não vazar dados de uma denúncia anônima.
 */
@Component
@RequiredArgsConstructor
public class ComplianceAnonymizationStrategy implements AnonymizationStrategy {

    private final AiAnonymizationClient aiAnonymizationClient;

    @Override
    public AnonymizationResult anonymize(AnonymizationContext context) {
        if (!context.anonymous()
                || context.description() == null || context.description().isBlank()) {
            return new AnonymizationResult(context.title(), context.description());
        }

        // report_id é obrigatório (int) no MS, mas a anonimização ocorre antes do save (sem id) e o
        // endpoint apenas ecoa esse campo — enviamos 0 como placeholder; usamos só os textos anonimizados.
        AnonymizationAiResponse response = aiAnonymizationClient.anonymize(
                new AnonymizationAiRequest(0L, context.title(), context.description()));

        if (response == null || response.anonymizedDescription() == null
                || response.anonymizedDescription().isBlank()) {
            throw new IllegalStateException(
                    "Serviço de IA não retornou descrição anonimizada para uma denúncia anônima");
        }
        return new AnonymizationResult(response.anonymizedTitle(), response.anonymizedDescription());
    }
}
