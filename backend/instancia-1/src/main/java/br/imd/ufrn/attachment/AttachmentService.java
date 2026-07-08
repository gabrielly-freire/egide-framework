package br.imd.ufrn.attachment;

import br.imd.ufrn.core.persistence.ManifestationRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository repository;
    private final ManifestationRepository manifestationRepository;

    @Transactional
    public AttachmentResponse upload(Long manifestationId, MultipartFile file) {
        if (!manifestationRepository.existsById(manifestationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Manifestação não encontrada");
        }
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo vazio");
        }
        try {
            Attachment a = new Attachment();
            a.setManifestationId(manifestationId);
            a.setFileName(file.getOriginalFilename());
            a.setContentType(file.getContentType());
            a.setFileSize(file.getSize());
            a.setContent(file.getBytes());
            return toResponse(repository.save(a));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao ler o arquivo", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> findByManifestation(Long manifestationId) {
        return repository.findByManifestationId(manifestationId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Attachment download(Long attachmentId) {
        return repository.findById(attachmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anexo não encontrado"));
    }

    private AttachmentResponse toResponse(Attachment a) {
        return new AttachmentResponse(a.getId(), a.getManifestationId(), a.getFileName(),
                a.getContentType(), a.getFileSize());
    }
}
