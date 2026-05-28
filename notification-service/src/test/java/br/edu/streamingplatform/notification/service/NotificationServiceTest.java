package br.edu.streamingplatform.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.edu.streamingplatform.notification.dto.NotificationDto;
import br.edu.streamingplatform.notification.dto.RecommendationCreatedEvent;
import br.edu.streamingplatform.notification.model.NotificationLog;
import br.edu.streamingplatform.notification.repository.NotificationLogRepository;

class NotificationServiceTest {

    private final NotificationLogRepository notificationLogRepository = Mockito.mock(NotificationLogRepository.class);
    private final NotificationService notificationService = new NotificationService(notificationLogRepository);

    @Test
    void notifyRecommendationCreatedShouldSaveNotificationLog() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 28, 12, 8);
        RecommendationCreatedEvent event = new RecommendationCreatedEvent(1L, 10L, "Acao", createdAt);
        ArgumentCaptor<NotificationLog> notificationCaptor = ArgumentCaptor.forClass(NotificationLog.class);

        when(notificationLogRepository.save(any(NotificationLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationDto notification = notificationService.notifyRecommendationCreated(event);

        verify(notificationLogRepository).save(notificationCaptor.capture());
        assertThat(notification.userId()).isEqualTo(1L);
        assertThat(notification.recommendationId()).isEqualTo(10L);
        assertThat(notification.category()).isEqualTo("Acao");
        assertThat(notification.eventType()).isEqualTo("recommendation.created");
        assertThat(notification.eventCreatedAt()).isEqualTo(createdAt);
        assertThat(notification.message())
                .isEqualTo("Notificacao enviada para usuario 1: novas recomendacoes disponiveis para categoria Acao.");
        assertThat(notification.sentAt()).isNotNull();
        assertThat(notificationCaptor.getValue().toDto().message()).isEqualTo(notification.message());
    }

    @Test
    void findByUserIdShouldReturnNotificationsOrderedByRepository() {
        NotificationLog notificationLog = new NotificationLog(
                1L,
                10L,
                "Drama",
                "Notificacao enviada para usuario 1: novas recomendacoes disponiveis para categoria Drama.",
                "recommendation.created",
                LocalDateTime.of(2026, 5, 28, 12, 8),
                LocalDateTime.of(2026, 5, 28, 12, 9)
        );

        when(notificationLogRepository.findByUserIdOrderBySentAtDesc(1L)).thenReturn(List.of(notificationLog));

        List<NotificationDto> notifications = notificationService.findByUserId(1L);

        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).category()).isEqualTo("Drama");
        verify(notificationLogRepository).findByUserIdOrderBySentAtDesc(1L);
    }
}
