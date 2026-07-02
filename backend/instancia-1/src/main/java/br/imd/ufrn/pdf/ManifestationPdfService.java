package br.imd.ufrn.pdf;

import br.imd.ufrn.core.domain.Manifestation;
import br.imd.ufrn.core.persistence.ManifestationRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Exporta uma manifestação em PDF (OpenPDF). */
@Service
@RequiredArgsConstructor
public class ManifestationPdfService {

    private final ManifestationRepository manifestationRepository;

    @Transactional(readOnly = true)
    public byte[] export(Long manifestationId) {
        Manifestation m = manifestationRepository.findById(manifestationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Manifestação não encontrada"));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Relatório de Manifestação"));
            document.add(new Paragraph("Protocolo: " + m.getProtocolNumber()));
            document.add(new Paragraph("Título: " + m.getTitle()));
            document.add(new Paragraph("Tipo: " + m.getType()));
            document.add(new Paragraph("Status: " + m.getStatus()));
            document.add(new Paragraph("Categoria: " + valueOrDash(m.getCategory())));
            document.add(new Paragraph("Risco: " + valueOrDash(m.getRiskLevel())));
            document.add(new Paragraph("Prazo: " + (m.getDeadlineAt() == null ? "-" : m.getDeadlineAt())));
            document.add(new Paragraph("Descrição:"));
            document.add(new Paragraph(m.getDescription()));
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao gerar PDF", e);
        }
    }

    private String valueOrDash(String value) {
        return value == null ? "-" : value;
    }
}
