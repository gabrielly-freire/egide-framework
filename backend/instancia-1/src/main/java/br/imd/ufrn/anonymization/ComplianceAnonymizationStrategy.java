package br.imd.ufrn.anonymization;

import br.imd.ufrn.ai.AiAnonymizationClient;
import br.imd.ufrn.ai.dto.AnonymizationAiRequest;
import br.imd.ufrn.ai.dto.AnonymizationAiResponse;
import br.imd.ufrn.core.anonymization.AnonymizationContext;
import br.imd.ufrn.core.anonymization.AnonymizationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Ponto variável de anonimização da Instância 1 (Compliance Corporativo).
 *
 * <p>Delega a anonimização ao microserviço de IA ({@code POST /compliance/anonimizar}), que faz
 * pseudonimização inteligente (LGPD). Só atua em denúncias anônimas; manifestações identificadas
 * ({@code anonymous == false}) passam intactas.
 *
 * <p>Como é um {@link Component}, o Spring registra este bean e o
 * {@code @ConditionalOnMissingBean(AnonymizationStrategy.class)} do Core desliga o default
 * {@code TransparentAnonymizationStrategy} — a instância personaliza sem tocar no Core.
 *
 * <p><b>Nota:</b> o contrato do Core entrega apenas a {@code description} (sem título/id e antes do
 * save). Por isso enviamos só a descrição e usamos {@code anonymized_description}. Se a IA falhar
 * ou não retornar texto, lançamos erro em vez de gravar o texto original — para não vazar dados de
 * uma denúncia anônima.
 */
@Component
@RequiredArgsConstructor
public class ComplianceAnonymizationStrategy implements AnonymizationStrategy {

    private final AiAnonymizationClient aiAnonymizationClient;

    @Override
    public String anonymize(String text, AnonymizationContext context) {
        if (text == null || text.isBlank() || !context.anonymous()) {
            return text;
        }

        AnonymizationAiResponse response = aiAnonymizationClient.anonymize(
                new AnonymizationAiRequest(null, "", text));

        if (response == null || response.anonymizedDescription() == null
                || response.anonymizedDescription().isBlank()) {
            throw new IllegalStateException(
                    "Serviço de IA não retornou descrição anonimizada para uma denúncia anônima");
        }
        return response.anonymizedDescription();
    }
}
