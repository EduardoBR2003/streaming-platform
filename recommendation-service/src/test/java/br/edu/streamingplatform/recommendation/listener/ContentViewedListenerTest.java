package br.edu.streamingplatform.recommendation.listener;

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

import br.edu.streamingplatform.recommendation.dto.ContentViewedEvent;
import br.edu.streamingplatform.recommendation.service.RecommendationService;

class ContentViewedListenerTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private final RecommendationService recommendationService = Mockito.mock(RecommendationService.class);
    private final ContentViewedListener listener = new ContentViewedListener(objectMapper, recommendationService);

    @Test
    void handleShouldDeserializeEventAndCallRecommendationService() throws Exception {
        String payload = """
                {"userId":1,"contentId":20,"contentTitle":"Dark","contentCategory":"Suspense","viewedAt":"2026-06-06T10:30:00"}
                """;
        Message message = new Message(payload.getBytes(StandardCharsets.UTF_8), new MessageProperties());
        ArgumentCaptor<ContentViewedEvent> eventCaptor = ArgumentCaptor.forClass(ContentViewedEvent.class);

        listener.handle(message);

        verify(recommendationService).processContentViewed(eventCaptor.capture());
        ContentViewedEvent event = eventCaptor.getValue();
        assertThat(event.userId()).isEqualTo(1L);
        assertThat(event.contentId()).isEqualTo(20L);
        assertThat(event.contentTitle()).isEqualTo("Dark");
        assertThat(event.contentCategory()).isEqualTo("Suspense");
        assertThat(event.viewedAt()).isEqualTo(LocalDateTime.of(2026, 6, 6, 10, 30));
    }
}
