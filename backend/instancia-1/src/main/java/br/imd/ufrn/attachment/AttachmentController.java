package br.imd.ufrn.attachment;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Anexos de uma manifestação (exige autenticação). */
@RestController
@RequestMapping("/v1/manifestations/{manifestationId}/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping
    public AttachmentResponse upload(
            @PathVariable Long manifestationId,
            @RequestParam("file") MultipartFile file) {
        return attachmentService.upload(manifestationId, file);
    }

    @GetMapping
    public List<AttachmentResponse> list(@PathVariable Long manifestationId) {
        return attachmentService.findByManifestation(manifestationId);
    }

    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long manifestationId,
            @PathVariable Long attachmentId) {
        Attachment a = attachmentService.download(attachmentId);
        MediaType type = a.getContentType() == null
                ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(a.getContentType());
        return ResponseEntity.ok()
                .contentType(type)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + a.getFileName() + "\"")
                .body(a.getContent());
    }
}
