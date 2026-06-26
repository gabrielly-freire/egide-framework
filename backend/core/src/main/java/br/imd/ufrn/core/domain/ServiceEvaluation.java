package br.imd.ufrn.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "service_evaluations")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class ServiceEvaluation extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long manifestationId;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
