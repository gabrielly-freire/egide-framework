package br.imd.ufrn.notification;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Notificações do usuário (destinadas a ele) + broadcasts (destinatário nulo), mais recentes primeiro.
    List<Notification> findByRecipientUserIdOrRecipientUserIdIsNullOrderByCreatedAtDesc(Long recipientUserId);
}
