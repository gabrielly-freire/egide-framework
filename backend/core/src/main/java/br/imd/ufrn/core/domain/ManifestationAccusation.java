package br.imd.ufrn.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * Vincula uma manifestação à parte acusada nela. Ponto fixo de infraestrutura do Core: o dado
 * "quem é o acusado" é comum a todas as instâncias e serve de insumo ao ponto variável de
 * conflito de interesse.
 *
 * <p>{@code accusedPartyId} é um identificador <em>opaco</em> — o Core não conhece o modelo de
 * pessoas de cada instância (usuário, membro acadêmico, analista); cada instância interpreta esse
 * id contra a sua própria entidade de parte. Uma manifestação pode ter <b>várias</b> acusações.
 */
@Entity
@Table(name = "manifestation_accusations")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class ManifestationAccusation extends BaseEntity {

    @Column(nullable = false)
    private Long manifestationId;

    @Column(nullable = false)
    private Long accusedPartyId;
}
