package br.imd.ufrn.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService service;

    @Test
    void notifyBroadcast_devePersistirNotificacaoNaoLida() {
        service.notifyBroadcast(1L, NotificationType.MANIFESTATION_CREATED, "Nova manifestação");

        verify(repository).save(argThat(n ->
                n.getManifestationId().equals(1L)
                        && n.getType() == NotificationType.MANIFESTATION_CREATED
                        && !n.isReadFlag()));
    }

    @Test
    void markRead_deveMarcarComoLida() {
        Notification n = new Notification();
        n.setId(5L);
        n.setManifestationId(1L);
        n.setType(NotificationType.MANIFESTATION_CREATED);
        n.setMessage("m");
        n.setReadFlag(false);
        when(repository.findById(5L)).thenReturn(Optional.of(n));
        when(repository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        NotificationResponse result = service.markRead(5L);

        assertThat(result.read()).isTrue();
    }
}
