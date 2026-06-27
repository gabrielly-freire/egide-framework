package br.imd.ufrn.atendimento.designation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.imd.ufrn.atendimento.domain.Analyst;
import br.imd.ufrn.atendimento.persistence.AnalystRepository;
import br.imd.ufrn.core.designation.DesignationContext;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegionDesignationStrategyTest {

    @Mock
    private AnalystRepository repository;

    @InjectMocks
    private RegionDesignationStrategy strategy;

    @Test
    void resolve_deveRetornarIdDoAnalista_quandoEspecialidadeEncontrada() {
        Analyst analyst = new Analyst();
        analyst.setId(10L);
        analyst.setSpecialty("SAUDE");
        when(repository.findFirstBySpecialty("SAUDE")).thenReturn(Optional.of(analyst));

        DesignationContext context = new DesignationContext(1L, "SAUDE");

        assertThat(strategy.resolve(context)).isEqualTo(10L);
    }

    @Test
    void resolve_deveRetornarNull_quandoNaoHaAnalistaNaEspecialidade() {
        when(repository.findFirstBySpecialty("SEGURANCA")).thenReturn(Optional.empty());

        DesignationContext context = new DesignationContext(1L, "SEGURANCA");

        assertThat(strategy.resolve(context)).isNull();
    }
}
