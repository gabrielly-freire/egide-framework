package br.imd.ufrn.domain;

import br.imd.ufrn.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * Vincula uma manifestação ao {@link AcademicMember} denunciado nela (Instância 2).
 *
 * <p>O Core registra a manifestação sem a noção de "denunciado" — esse é um dado próprio da
 * ouvidoria universitária, necessário para verificar se o analista candidato pertence à mesma
 * unidade acadêmica do denunciado. Há no máximo uma acusação por manifestação
 * ({@code manifestation_id} único).
 */
@Entity
@Table(name = "manifestation_accusations")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class ManifestationAccusation extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long manifestationId;

    @Column(nullable = false)
    private Long accusedMemberId;
}
