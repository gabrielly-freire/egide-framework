package br.imd.ufrn.core.workflow;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.exception.WorkflowAdvanceNotAllowedException;
import br.imd.ufrn.core.exception.WorkflowAppealNotAllowedException;
import java.time.Duration;

/**
 * Define o esqueleto do processo de workflow de uma manifestação (padrão Template Method).
 *
 * <p>O Core fixa a estrutura do algoritmo — validação de status terminal, delegação aos passos
 * variáveis e chamada aos hooks — sem conhecer as regras de negócio específicas de cada
 * instância. As instâncias personalizam o comportamento estendendo esta classe e implementando
 * os métodos abstratos.
 * </p>
 */
public abstract class WorkflowTemplate {

    // ── Template methods (estrutura fixa, não sobreponíveis) ─────────────────

    /**
     * Avança a manifestação para a próxima fase do workflow.
     *
     * <p>Algoritmo fixo:
     * <ol>
     *   <li>Rejeita o avanço se o status atual for terminal ({@code RESOLVED} ou {@code CLOSED}).</li>
     *   <li>Delega a determinação do próximo status a {@link #resolveNextStatus}.</li>
     *   <li>Chama o hook {@link #onBeforeAdvance} antes de retornar.</li>
     *   <li>Retorna {@link WorkflowStepResult} com o novo status e o prazo da fase.</li>
     * </ol>
     *
     * @param manifestation a manifestação a ser avançada
     * @return resultado com o próximo status e o prazo associado (pode ser {@code null})
     * @throws WorkflowAdvanceNotAllowedException se a manifestação estiver em status terminal
     */
    public final WorkflowStepResult advance(Manifestation manifestation) {
        if (isTerminal(manifestation.getStatus())) {
            throw new WorkflowAdvanceNotAllowedException(manifestation.getId());
        }
        ManifestationStatus next = resolveNextStatus(manifestation.getStatus());
        onBeforeAdvance(manifestation, next);
        return new WorkflowStepResult(next, deadlineFor(next));
    }

    /**
     * Registra um recurso sobre a manifestação, retrocedendo-a para a fase de revisão definida
     * pela instância.
     *
     * <p>Algoritmo fixo:
     * <ol>
     *   <li>Rejeita o recurso se {@link #isAppealAllowed} retornar {@code false} para o status
     *       atual.</li>
     *   <li>Delega a determinação do status de recurso a {@link #resolveAppealStatus}.</li>
     *   <li>Chama o hook {@link #onBeforeAppeal} antes de retornar.</li>
     *   <li>Retorna {@link WorkflowStepResult} com o status de recurso e o prazo associado.</li>
     * </ol>
     *
     * @param manifestation a manifestação sobre a qual o recurso é interposto
     * @return resultado com o status de recurso e o prazo associado (pode ser {@code null})
     * @throws WorkflowAppealNotAllowedException se o recurso não for permitido no status atual
     */
    public final WorkflowStepResult appeal(Manifestation manifestation) {
        if (!isAppealAllowed(manifestation.getStatus())) {
            throw new WorkflowAppealNotAllowedException(manifestation.getId());
        }
        ManifestationStatus appealStatus = resolveAppealStatus(manifestation.getStatus());
        onBeforeAppeal(manifestation, appealStatus);
        return new WorkflowStepResult(appealStatus, deadlineFor(appealStatus));
    }

    // ── Passo fixo auxiliar ──────────────────────────────────────────────────

    /**
     * Indica se o status é terminal, ou seja, não admite mais avanços no workflow.
     *
     * <p>São terminais: {@link ManifestationStatus#RESOLVED} e {@link ManifestationStatus#CLOSED}.
     *
     * @param status o status a verificar
     * @return {@code true} se o status for terminal
     */
    protected final boolean isTerminal(ManifestationStatus status) {
        return status == ManifestationStatus.RESOLVED || status == ManifestationStatus.CLOSED;
    }

    // ── Passos variáveis (obrigatórios na subclasse) ─────────────────────────

    /**
     * Retorna o próximo status do workflow a partir do status atual.
     *
     * <p>Este método é chamado apenas quando o status atual <em>não é terminal</em> (garantido
     * por {@link #advance}). A implementação não precisa tratar os casos {@code RESOLVED} e
     * {@code CLOSED}.
     *
     * @param current o status atual da manifestação
     * @return o status para o qual a manifestação deve avançar
     */
    protected abstract ManifestationStatus resolveNextStatus(ManifestationStatus current);

    /**
     * Retorna o status para o qual a manifestação deve ser encaminhada quando um recurso é
     * interposto.
     *
     * <p>Este método só é invocado quando {@link #isAppealAllowed} retorna {@code true} para o
     * status atual. Implementações que nunca permitem recurso podem lançar
     * {@link WorkflowAppealNotAllowedException} como salvaguarda.
     *
     * @param current o status atual da manifestação
     * @return o status de recurso
     */
    protected abstract ManifestationStatus resolveAppealStatus(ManifestationStatus current);

    /**
     * Indica se é permitido interpor recurso para o status informado.
     *
     * @param status o status atual da manifestação
     * @return {@code true} se o recurso for permitido neste status
     */
    protected abstract boolean isAppealAllowed(ManifestationStatus status);

    /**
     * Retorna o prazo para conclusão da fase identificada pelo status informado.
     *
     * <p>Retorne {@code null} quando a fase não tiver prazo definido pelo workflow.
     *
     * @param status o status da fase para a qual o prazo é calculado
     * @return duração do prazo, ou {@code null} se não houver prazo
     */
    protected abstract Duration deadlineFor(ManifestationStatus status);

    // ── Hooks (opcionais na subclasse) ───────────────────────────────────────

    /**
     * Hook chamado imediatamente antes de a manifestação avançar para {@code next}.
     *
     * <p>Use para registrar auditoria, enviar notificações ou aplicar regras adicionais
     * sem alterar a estrutura do algoritmo. A implementação padrão é vazia.
     *
     * @param manifestation a manifestação que está avançando
     * @param next          o status para o qual ela avançará
     */
    protected void onBeforeAdvance(Manifestation manifestation, ManifestationStatus next) {}

    /**
     * Hook chamado imediatamente antes de a manifestação ser encaminhada para
     * {@code appealStatus} em decorrência de um recurso.
     *
     * <p>Use para registrar o histórico do recurso ou notificar o responsável.
     * A implementação padrão é vazia.
     *
     * @param manifestation a manifestação sobre a qual o recurso foi interposto
     * @param appealStatus  o status para o qual ela será encaminhada
     */
    protected void onBeforeAppeal(Manifestation manifestation, ManifestationStatus appealStatus) {}
}
