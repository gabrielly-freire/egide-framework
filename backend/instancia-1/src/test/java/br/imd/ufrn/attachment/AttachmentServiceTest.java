package br.imd.ufrn.attachment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.imd.ufrn.core.persistence.ManifestationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository repository;

    @Mock
    private ManifestationRepository manifestationRepository;

    @InjectMocks
    private AttachmentService service;

    @Test
    void upload_devePersistirAnexoDaManifestacao() {
        when(manifestationRepository.existsById(1L)).thenReturn(true);
        when(repository.save(any(Attachment.class))).thenAnswer(i -> i.getArgument(0));
        MockMultipartFile file = new MockMultipartFile(
                "file", "prova.pdf", "application/pdf", "conteudo".getBytes());

        AttachmentResponse result = service.upload(1L, file);

        assertThat(result.fileName()).isEqualTo("prova.pdf");
        assertThat(result.contentType()).isEqualTo("application/pdf");
    }

    @Test
    void upload_deveLancarNotFound_quandoManifestacaoNaoExiste() {
        when(manifestationRepository.existsById(99L)).thenReturn(false);
        MockMultipartFile file = new MockMultipartFile("file", "x.txt", "text/plain", "x".getBytes());

        assertThatThrownBy(() -> service.upload(99L, file))
                .isInstanceOf(ResponseStatusException.class);
    }
}
