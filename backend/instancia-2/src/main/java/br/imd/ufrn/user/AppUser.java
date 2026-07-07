package br.imd.ufrn.user;

import br.imd.ufrn.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Usuário/analista da Instância 1. Implementa {@link UserDetails} para integração com o Spring
 * Security. Dado próprio da instância — o {@code id} deste usuário é o {@code analystId} que os
 * pontos fixos do Core (designação/conflito/assignment) referenciam por {@code Long}.
 *
 * <p>A autoridade é {@code ROLE_<role>} para compatibilidade com {@code @PreAuthorize("hasRole(...)")}.
 */
@Entity
@Table(name = "user_info")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class AppUser extends BaseEntity implements UserDetails {

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
