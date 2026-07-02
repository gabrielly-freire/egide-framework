package br.imd.ufrn.core.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.categorization.CategorizationContext;
import br.imd.ufrn.core.categorization.CategorizationResult;
import br.imd.ufrn.core.categorization.CategorizationStrategy;
import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.exception.ManifestationNotFoundException;
import br.imd.ufrn.core.mapper.ManifestationMapper;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategorizationServiceTest {

    @Mock
    private ManifestationRepository repository;

    @Mock
    private ManifestationMapper mapper;

    @Mock
    private CategorizationStrategy categorizationStrategy;

    @InjectMocks
    private CategorizationServiceImpl service;

    private Manifestation manifestation;

    @BeforeEach
    void setUp() {
        manifestation = new Manifestation();
        manifestation.setId(1L);
        manifestation.setTitle("Título");
        manifestation.setDescription("Descrição");
        manifestation.setType("ASSÉDIO");
    }

    @Test
    void categorize_deveGravarCategoriaERiscoNaManifestacao() {
        when(repository.findById(1L)).thenReturn(Optional.of(manifestation));
        when(categorizationStrategy.categorize(any(CategorizationContext.class)))
                .thenReturn(new CategorizationResult("DENUNCIATION", "CRITICAL"));
        when(repository.save(any(Manifestation.class))).thenReturn(manifestation);

        service.categorize(1L);

        verify(repository).save(argThat(m ->
                "DENUNCIATION".equals(m.getCategory()) && "CRITICAL".equals(m.getRiskLevel())));
    }

    @Test
    void categorize_deveLancarNotFound_quandoManifestacaoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.categorize(99L))
                .isInstanceOf(ManifestationNotFoundException.class);
    }
}
