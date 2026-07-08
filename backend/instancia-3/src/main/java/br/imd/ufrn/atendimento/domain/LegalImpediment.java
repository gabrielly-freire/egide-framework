package br.imd.ufrn.atendimento.domain;

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
@Table(name = "legal_impediments")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class LegalImpediment extends BaseEntity {

    @Column(nullable = false)
    private Long manifestationId;

    @Column(nullable = false)
    private Long analystId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ImpedimentReason reason;
}
