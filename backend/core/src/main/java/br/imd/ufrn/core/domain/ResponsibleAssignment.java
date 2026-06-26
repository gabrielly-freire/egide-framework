package br.imd.ufrn.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "responsible_assignments")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class ResponsibleAssignment extends BaseEntity {

    @Column(nullable = false)
    private Long manifestationId;

    @Column(nullable = false)
    private Long responsibleId;

    private Long assignedById;
}
