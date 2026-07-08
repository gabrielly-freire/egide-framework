package br.imd.ufrn.core.conflict;

import java.util.List;

/**
 * Contexto passado à estratégia de conflito de interesse.
 *
 * <p>Além dos identificadores, o Core já carrega e entrega os ids das partes acusadas da
 * manifestação ({@code accusedPartyIds}), a partir do registro genérico de acusações
 * ({@link br.imd.ufrn.core.domain.ManifestationAccusation}). Assim a estratégia da instância não
 * precisa de repositório/serviço próprio para descobrir quem foi acusado — recebe o dado pronto e
 * aplica apenas a sua regra.
 *
 * @param manifestationId   id da manifestação sob análise
 * @param analystId         id do analista candidato à designação
 * @param manifestationType tipo da manifestação (ex.: "DENUNCIA", "RECLAMACAO")
 * @param accusedPartyIds   ids das partes acusadas na manifestação (vazio se não houver)
 */
public record ConflictOfInterestContext(
        Long manifestationId,
        Long analystId,
        String manifestationType,
        List<Long> accusedPartyIds
) {

    /**
     * Construtor de compatibilidade (sem partes acusadas): mantém válido o código que instanciava
     * o contexto antes do campo {@code accusedPartyIds}, assumindo lista vazia. Estratégias que
     * ainda resolvem os acusados por conta própria não são afetadas.
     */
    public ConflictOfInterestContext(Long manifestationId, Long analystId, String manifestationType) {
        this(manifestationId, analystId, manifestationType, List.of());
    }
}
