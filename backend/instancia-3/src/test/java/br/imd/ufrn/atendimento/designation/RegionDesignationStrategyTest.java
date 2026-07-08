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

        DesignationContext context = new DesignationContext(1L, "SAUDE", "Norte");

        assertThat(strategy.resolve(context)).isEqualTo(10L);
    }

    @Test
    void resolve_deveRetornarIdDoAnalista_quandoEspecialidadeNaoEncontradaMasRegiaoSim() {
        Analyst analyst = new Analyst();
        analyst.setId(20L);
        analyst.setRegion("Norte");
        when(repository.findFirstBySpecialty("SEGURANCA")).thenReturn(Optional.empty());
        when(repository.findFirstByRegion("Norte")).thenReturn(Optional.of(analyst));

        DesignationContext context = new DesignationContext(1L, "SEGURANCA", "Norte");

        assertThat(strategy.resolve(context)).isEqualTo(20L);
    }

    @Test
    void resolve_deveRetornarNull_quandoNaoHaAnalistaNaEspecialidadeNemRegiaoInformada() {
        when(repository.findFirstBySpecialty("SEGURANCA")).thenReturn(Optional.empty());

        DesignationContext context = new DesignationContext(1L, "SEGURANCA", null);

        assertThat(strategy.resolve(context)).isNull();
    }

    @Test
    void resolve_deveRetornarNull_quandoNaoHaAnalistaNaEspecialidadeNemNaRegiao() {
        when(repository.findFirstBySpecialty("SEGURANCA")).thenReturn(Optional.empty());
        when(repository.findFirstByRegion("Sul")).thenReturn(Optional.empty());

        DesignationContext context = new DesignationContext(1L, "SEGURANCA", "Sul");

        assertThat(strategy.resolve(context)).isNull();
    }
}
