package br.imd.ufrn.atendimento.domain;

import br.imd.ufrn.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "analysts")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class Analyst extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 100)
    private String specialty;

    @Column(nullable = false, length = 100)
    private String region;
}
