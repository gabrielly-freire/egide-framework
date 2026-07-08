package br.imd.ufrn.notification;

import br.imd.ufrn.core.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * Notificação da Instância 1. {@code recipientUserId} nulo = broadcast (visível a todos).
 * Reusa a {@link BaseEntity} do Core.
 */
@Entity
@Table(name = "notification")
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
public class Notification extends BaseEntity {

    private Long recipientUserId;

    @Column(nullable = false)
    private Long manifestationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "read_flag", nullable = false)
    private boolean readFlag;
}
