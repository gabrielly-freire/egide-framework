package br.imd.ufrn.attachment;

import br.imd.ufrn.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/** Anexo (evidência) de uma manifestação. Conteúdo guardado no banco (bytea). */
@Entity
@Table(name = "attachment")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class Attachment extends BaseEntity {

    @Column(nullable = false)
    private Long manifestationId;

    @Column(nullable = false)
    private String fileName;

    private String contentType;

    private long fileSize;

    @Column(columnDefinition = "bytea", nullable = false)
    private byte[] content;
}
