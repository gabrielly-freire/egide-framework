package br.imd.ufrn.designation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.conflict.ConflictOfInterestContext;
import br.imd.ufrn.core.conflict.ConflictOfInterestStrategy;
import br.imd.ufrn.core.designation.DesignationContext;
import br.imd.ufrn.user.AppUser;
import br.imd.ufrn.user.AppUserRepository;
import br.imd.ufrn.user.Role;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComplianceDesignationStrategyTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private ConflictOfInterestStrategy conflictOfInterestStrategy;

    @InjectMocks
    private ComplianceDesignationStrategy strategy;

    private final DesignationContext context = new DesignationContext(100L, "DENUNCIA", null);

    private AppUser listener(Long id) {
        AppUser u = new AppUser();
        u.setId(id);
        u.setRole(Role.LISTENER);
        return u;
    }

    @Test
    void resolve_deveSortearUmListenerElegivel() {
        when(userRepository.findByRole(Role.LISTENER)).thenReturn(List.of(listener(1L), listener(2L)));
        when(conflictOfInterestStrategy.hasConflict(any(ConflictOfInterestContext.class))).thenReturn(false);

        Long result = strategy.resolve(context);

        assertThat(result).isIn(1L, 2L);
    }

    @Test
    void resolve_deveExcluirListenerComConflito() {
        when(userRepository.findByRole(Role.LISTENER)).thenReturn(List.of(listener(1L), listener(2L)));
        when(conflictOfInterestStrategy.hasConflict(argThat(c -> c != null && c.analystId().equals(1L)))).thenReturn(true);
        when(conflictOfInterestStrategy.hasConflict(argThat(c -> c != null && c.analystId().equals(2L)))).thenReturn(false);

        Long result = strategy.resolve(context);

        assertThat(result).isEqualTo(2L);
    }

    @Test
    void resolve_deveRetornarNull_quandoNaoHaListener() {
        when(userRepository.findByRole(Role.LISTENER)).thenReturn(List.of());

        assertThat(strategy.resolve(context)).isNull();
    }

    @Test
    void resolve_deveRetornarNull_quandoTodosComConflito() {
        when(userRepository.findByRole(Role.LISTENER)).thenReturn(List.of(listener(1L), listener(2L)));
        when(conflictOfInterestStrategy.hasConflict(any(ConflictOfInterestContext.class))).thenReturn(true);

        assertThat(strategy.resolve(context)).isNull();
    }
}
