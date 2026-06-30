package br.imd.ufrn.core.domain;

import br.imd.ufrn.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "manifestations")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class Manifestation extends BaseEntity {

    @Column(unique = true, nullable = false, updatable = false)
    private String protocolNumber;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ManifestationStatus status;

    @Column(length = 100)
    private String category;

    @Column(length = 50)
    private String riskLevel;
}
