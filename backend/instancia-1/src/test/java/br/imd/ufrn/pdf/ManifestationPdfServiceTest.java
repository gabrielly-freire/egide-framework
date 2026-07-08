package br.imd.ufrn.pdf;

import static org.assertj.core.api.Assertions.assertThat;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.domain.ManifestationStatus;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;

@ExtendWith(MockitoExtension.class)
class ManifestationPdfServiceTest {

    @Mock
    private ManifestationRepository manifestationRepository;

    @InjectMocks
    private ManifestationPdfService service;

    @Test
    void export_deveGerarUmPdfValido() {
        Manifestation m = new Manifestation();
        m.setId(1L);
        m.setProtocolNumber("2026-ABC");
        m.setTitle("Título");
        m.setType("DENUNCIA");
        m.setStatus(ManifestationStatus.REGISTERED);
        m.setDescription("Descrição");
        Mockito.when(manifestationRepository.findById(1L)).thenReturn(Optional.of(m));

        byte[] pdf = service.export(1L);

        assertThat(pdf).isNotEmpty();
        // Todo PDF começa com a assinatura "%PDF".
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }
}
