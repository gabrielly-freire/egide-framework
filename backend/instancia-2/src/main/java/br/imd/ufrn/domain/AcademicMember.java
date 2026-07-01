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
 * Membro da comunidade acadêmica (aluno, professor ou servidor) da Instância 2.
 *
 * <p>Modela a informação que o Core não conhece: a <b>unidade acadêmica</b> (centro ou
 * departamento) de cada pessoa. É usada tanto para o <em>analista</em> (identificado por
 * {@code analystId} no contexto de conflito) quanto para o <em>denunciado</em> (referenciado por
 * {@link ManifestationAccusation}), permitindo comparar as unidades e detectar conflito de
 * interesse.
 */
@Entity
@Table(name = "academic_members")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class AcademicMember extends BaseEntity {

    @Column(nullable = false)
    private String name;

    /** Centro ou departamento ao qual o membro pertence (ex.: "DIMAP", "DCA", "CCET"). */
    @Column(nullable = false, length = 100)
    private String unit;
}
