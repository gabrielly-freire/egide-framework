package br.imd.ufrn.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * Parte envolvida no fluxo de ouvidoria (analista candidato ou pessoa acusada), com a sua unidade
 * organizacional. Ponto fixo de infraestrutura: o dado "pessoa → unidade" é comum às instâncias e
 * alimenta o ponto variável de conflito de interesse.
 *
 * <p>{@code unit} é um rótulo genérico da unidade organizacional — cada instância o interpreta no
 * seu vocabulário (centro/departamento na universidade, departamento no compliance, órgão/região
 * no serviço público). O Core não impõe semântica; apenas armazena e entrega o valor.
 */
@Entity
@Table(name = "parties")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class Party extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 100)
    private String unit;
}
