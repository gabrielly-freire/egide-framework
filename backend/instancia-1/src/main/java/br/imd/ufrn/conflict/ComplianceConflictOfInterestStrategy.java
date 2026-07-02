package br.imd.ufrn.conflict;

import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.core.conflict.ConflictOfInterestStrategy;
import br.imd.ufrn.user.AppUser;
import br.imd.ufrn.user.AppUserRepository;
import br.imd.ufrn.user.Department;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Ponto variável de conflito de interesse da Instância 1 (Compliance): baseado em hierarquia e
 * cargos. Determinístico, usa dados estruturados (não o texto da manifestação), então funciona
 * mesmo com a descrição anonimizada.
 *
 * <p>O analista tem conflito se, para <b>algum</b> acusado da manifestação:
 * <ol>
 *   <li>o analista <b>é</b> o acusado;</li>
 *   <li>analista e acusado estão no <b>mesmo departamento</b>;</li>
 *   <li>o acusado tem <b>cargo superior</b> ao do analista (hierarquia).</li>
 * </ol>
 *
 * <p>Como é {@link Component}, desliga o default {@code NoConflictOfInterestStrategy} do Core.
 */
@Component
@RequiredArgsConstructor
public class ComplianceConflictOfInterestStrategy implements ConflictOfInterestStrategy {

    private final ManifestationAccusationRepository accusationRepository;
    private final AppUserRepository userRepository;

    @Override
    public boolean hasConflict(ConflictOfInterestContext context) {
        AppUser analyst = userRepository.findById(context.analystId()).orElse(null);
        if (analyst == null) {
            return false;
        }

        List<ManifestationAccusation> accusations =
                accusationRepository.findByManifestationId(context.manifestationId());

        for (ManifestationAccusation accusation : accusations) {
            if (conflictsWith(analyst, accusation.getAccusedUserId())) {
                return true;
            }
        }
        return false;
    }

    private boolean conflictsWith(AppUser analyst, Long accusedUserId) {
        if (analyst.getId().equals(accusedUserId)) {
            return true;
        }
        Optional<AppUser> accused = userRepository.findById(accusedUserId);
        if (accused.isEmpty()) {
            return false;
        }
        return sameDepartment(analyst, accused.get()) || outranks(accused.get(), analyst);
    }

    private boolean sameDepartment(AppUser analyst, AppUser accused) {
        Department analystDept = analyst.getDepartment();
        Department accusedDept = accused.getDepartment();
        return analystDept != null && accusedDept != null
                && analystDept.getId().equals(accusedDept.getId());
    }

    private boolean outranks(AppUser accused, AppUser analyst) {
        return accused.getRole().ordinal() > analyst.getRole().ordinal();
    }
}
