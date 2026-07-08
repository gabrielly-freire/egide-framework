package br.imd.ufrn.designation;

import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.core.conflict.ConflictOfInterestStrategy;
import br.imd.ufrn.core.designation.DesignationContext;
import br.imd.ufrn.core.designation.DesignationStrategy;
import br.imd.ufrn.user.AppUser;
import br.imd.ufrn.user.AppUserRepository;
import br.imd.ufrn.user.Role;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Ponto variável de designação da Instância 1 (Compliance): sorteia um ouvidor ({@link Role#LISTENER})
 * entre os que <b>não têm conflito de interesse</b> com a manifestação (reusa a estratégia de
 * conflito da instância). Espelha o "designado por sorteio" do monolito.
 *
 * <p>Retorna {@code null} quando não há ouvidor elegível — o Core então trata como designação manual
 * ({@code AutoAssignmentUnavailableException}). Como é {@link Component}, desliga o default
 * {@code ManualDesignationStrategy} do Core.
 *
 * <p>Usa {@link SecureRandom} para o sorteio. O balanceamento por carga (pool dos menos carregados),
 * que o monolito faz, é uma melhoria futura (exige contar casos ativos por analista).
 */
@Component
@RequiredArgsConstructor
public class ComplianceDesignationStrategy implements DesignationStrategy {

    private final AppUserRepository userRepository;
    private final ConflictOfInterestStrategy conflictOfInterestStrategy;
    private final Random random = new SecureRandom();

    @Override
    public Long resolve(DesignationContext context) {
        List<AppUser> eligible = userRepository.findByRole(Role.LISTENER).stream()
                .filter(listener -> !conflictOfInterestStrategy.hasConflict(
                        new ConflictOfInterestContext(
                                context.manifestationId(),
                                listener.getId(),
                                context.manifestationType())))
                .toList();

        if (eligible.isEmpty()) {
            return null;
        }
        return eligible.get(random.nextInt(eligible.size())).getId();
    }
}
