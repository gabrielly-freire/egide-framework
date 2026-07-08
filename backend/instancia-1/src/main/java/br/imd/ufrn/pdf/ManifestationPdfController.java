package br.imd.ufrn.pdf;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Download do PDF de uma manifestação (exige autenticação). */
@RestController
@RequestMapping("/v1/manifestations/{manifestationId}/pdf")
@RequiredArgsConstructor
public class ManifestationPdfController {

    private final ManifestationPdfService pdfService;

    @GetMapping
    public ResponseEntity<byte[]> export(@PathVariable Long manifestationId) {
        byte[] pdf = pdfService.export(manifestationId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=manifestacao-" + manifestationId + ".pdf")
                .body(pdf);
    }
}
