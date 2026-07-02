package br.imd.ufrn.conflict;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.user.AppUser;
import br.imd.ufrn.user.AppUserRepository;
import br.imd.ufrn.user.Department;
import br.imd.ufrn.user.Role;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComplianceConflictOfInterestStrategyTest {

    private static final Long MANIFESTATION_ID = 100L;

    @Mock
    private ManifestationAccusationRepository accusationRepository;

    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private ComplianceConflictOfInterestStrategy strategy;

    private AppUser user(Long id, Role role, Long deptId) {
        AppUser u = new AppUser();
        u.setId(id);
        u.setRole(role);
        if (deptId != null) {
            Department d = new Department();
            d.setId(deptId);
            u.setDepartment(d);
        }
        return u;
    }

    private ManifestationAccusation accusation(Long accusedId) {
        ManifestationAccusation a = new ManifestationAccusation();
        a.setManifestationId(MANIFESTATION_ID);
        a.setAccusedUserId(accusedId);
        return a;
    }

    private ConflictOfInterestContext ctx(Long analystId) {
        return new ConflictOfInterestContext(MANIFESTATION_ID, analystId, "DENUNCIA");
    }

    @Test
    void hasConflict_quandoAnalistaEhOProprioAcusado() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, Role.LISTENER, 10L)));
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID))
                .thenReturn(List.of(accusation(1L)));

        assertThat(strategy.hasConflict(ctx(1L))).isTrue();
    }

    @Test
    void hasConflict_quandoMesmoDepartamento() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, Role.LISTENER, 10L)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, Role.LISTENER, 10L)));
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID))
                .thenReturn(List.of(accusation(2L)));

        assertThat(strategy.hasConflict(ctx(1L))).isTrue();
    }

    @Test
    void hasConflict_quandoAcusadoTemCargoSuperior() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, Role.LISTENER, 10L)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, Role.MANAGER, 20L)));
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID))
                .thenReturn(List.of(accusation(2L)));

        assertThat(strategy.hasConflict(ctx(1L))).isTrue();
    }

    @Test
    void hasConflict_falso_quandoSemVinculoDeHierarquiaOuDepartamento() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, Role.MANAGER, 10L)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, Role.LISTENER, 20L)));
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID))
                .thenReturn(List.of(accusation(2L)));

        assertThat(strategy.hasConflict(ctx(1L))).isFalse();
    }

    @Test
    void hasConflict_falso_quandoNaoHaAcusados() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, Role.LISTENER, 10L)));
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID)).thenReturn(List.of());

        assertThat(strategy.hasConflict(ctx(1L))).isFalse();
    }

    @Test
    void hasConflict_quandoAcusadoOuvidorGeralSuperaAnalistaManager() {
        // GENERAL_LISTENER está acima de MANAGER na hierarquia (level 3 > 2).
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, Role.MANAGER, 10L)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, Role.GENERAL_LISTENER, 20L)));
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID))
                .thenReturn(List.of(accusation(2L)));

        assertThat(strategy.hasConflict(ctx(1L))).isTrue();
    }

    @Test
    void hasConflict_falso_quandoAnalistaOuvidorGeralNaoEhSuperadoPorManager() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, Role.GENERAL_LISTENER, 10L)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, Role.MANAGER, 20L)));
        when(accusationRepository.findByManifestationId(MANIFESTATION_ID))
                .thenReturn(List.of(accusation(2L)));

        assertThat(strategy.hasConflict(ctx(1L))).isFalse();
    }
}
