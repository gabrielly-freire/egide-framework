package br.imd.ufrn.anonymization;

import br.imd.ufrn.core.anonymization.AnonymizationContext;
import br.imd.ufrn.core.anonymization.AnonymizationResult;
import br.imd.ufrn.core.anonymization.AnonymizationStrategy;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Anonimização <b>parcial</b> da Instância 2 (Ouvidoria Universitária).
 *
 * <p>Diferente do mascaramento estrito do Compliance, o sigilo aqui é parcial: o relato
 * continua legível, mas os <em>identificadores diretos</em> que exporiam o manifestante ou
 * terceiros são substituídos por marcadores. São mascarados, no título e na descrição:
 *
 * <ul>
 *   <li><b>CPF</b> (formato {@code 000.000.000-00}) &rarr; {@code [CPF]}</li>
 *   <li><b>Matrícula</b> (precedida da palavra "matrícula") &rarr; {@code [MATRÍCULA]}</li>
 *   <li><b>E-mail</b> &rarr; {@code [E-MAIL]}</li>
 *   <li><b>Telefone</b> &rarr; {@code [TELEFONE]}</li>
 * </ul>
 *
 * <p>O nome do manifestante <em>não</em> é mascarado: na ouvidoria universitária ele permanece
 * visível, sendo o sigilo restrito aos identificadores formais acima.
 *
 * <p>O mascaramento é aplicado sempre, independentemente de o manifestante ter optado por
 * anonimato ({@link AnonymizationContext#anonymous()}): na universidade o sigilo parcial é
 * política institucional, não escolha individual. A restrição "identidade visível apenas para a
 * Ouvidoria Geral" é uma camada de autorização, fora do escopo deste contrato (que apenas
 * transforma o texto persistido).
 *
 * <p>Registrada como {@link Component}; assim o default do Core
 * ({@code TransparentAnonymizationStrategy}) é desligado via {@code @ConditionalOnMissingBean}.
 */
@Component
public class PartialAnonymizationStrategy implements AnonymizationStrategy {

    // Domínio em rótulos separados por ponto, sem englobar o ponto final da frase.
    private static final Pattern EMAIL =
            Pattern.compile("[\\w.+-]+@[\\w-]+(?:\\.[\\w-]+)+");

    private static final Pattern CPF =
            Pattern.compile("\\b\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}\\b");

    private static final Pattern TELEFONE =
            Pattern.compile("\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}\\b");

    // Captura a palavra "matrícula" + ligação ("é", "nº", ":" etc.) e mascara só os dígitos.
    private static final Pattern MATRICULA =
            Pattern.compile("(?i)(matr[íi]cula\\D{0,8}?)\\d+");

    @Override
    public AnonymizationResult anonymize(AnonymizationContext context) {
        return new AnonymizationResult(mask(context.title()), mask(context.description()));
    }

    private String mask(String text) {
        if (text == null) {
            return null;
        }
        String result = EMAIL.matcher(text).replaceAll("[E-MAIL]");
        result = CPF.matcher(result).replaceAll("[CPF]");
        // Matrícula antes de telefone: uma matrícula longa cairia no padrão de telefone.
        result = MATRICULA.matcher(result).replaceAll("$1[MATRÍCULA]");
        result = TELEFONE.matcher(result).replaceAll("[TELEFONE]");
        return result;
    }
}
