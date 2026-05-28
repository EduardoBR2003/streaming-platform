package br.edu.streamingplatform.notification.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.edu.streamingplatform.notification.dto.RecommendationCreatedEvent;
import br.edu.streamingplatform.notification.service.NotificationService;

class RecommendationCreatedListenerTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private final NotificationService notificationService = Mockito.mock(NotificationService.class);
    private final RecommendationCreatedListener listener = new RecommendationCreatedListener(
            objectMapper,
            notificationService
    );

    @Test
    void handleShouldDeserializeEventAndCallNotificationService() throws Exception {
        String payload = """
                {"userId":1,"recommendationId":10,"category":"Acao","createdAt":"2026-05-28T12:08:00"}
                """;
        Message message = new Message(payload.getBytes(StandardCharsets.UTF_8), new MessageProperties());
        ArgumentCaptor<RecommendationCreatedEvent> eventCaptor = ArgumentCaptor.forClass(RecommendationCreatedEvent.class);

        listener.handle(message);

        verify(notificationService).notifyRecommendationCreated(eventCaptor.capture());
        RecommendationCreatedEvent event = eventCaptor.getValue();
        assertThat(event.userId()).isEqualTo(1L);
        assertThat(event.recommendationId()).isEqualTo(10L);
        assertThat(event.category()).isEqualTo("Acao");
        assertThat(event.createdAt()).isEqualTo(LocalDateTime.of(2026, 5, 28, 12, 8));
    }
}
