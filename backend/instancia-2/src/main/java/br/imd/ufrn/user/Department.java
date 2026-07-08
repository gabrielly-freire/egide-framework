package br.imd.ufrn.user;

import br.imd.ufrn.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * Departamento/unidade institucional a que um {@link AppUser} pertence. Dado próprio da instância
 * (o Core não conhece). Reusa a {@link BaseEntity} do Core (id/timestamps/soft-delete).
 */
@Entity
@Table(name = "department")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class Department extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 30)
    private String acronym;
}
