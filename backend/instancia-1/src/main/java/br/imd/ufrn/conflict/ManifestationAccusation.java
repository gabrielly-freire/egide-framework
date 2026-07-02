package br.imd.ufrn.conflict;

import br.imd.ufrn.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * Vincula uma manifestação ao usuário acusado. Dado próprio da Instância 1 (o Core não modela
 * "acusado"); é a base para a regra de conflito de interesse por hierarquia/departamento.
 *
 * <p>{@code manifestationId} referencia a manifestação do Core; {@code accusedUserId} referencia
 * um {@link br.imd.ufrn.user.AppUser}.
 */
@Entity
@Table(name = "manifestation_accusation")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class ManifestationAccusation extends BaseEntity {

    @Column(nullable = false)
    private Long manifestationId;

    @Column(nullable = false)
    private Long accusedUserId;
}
