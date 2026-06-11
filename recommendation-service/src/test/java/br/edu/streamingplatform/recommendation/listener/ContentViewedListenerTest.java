package br.edu.streamingplatform.recommendation.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.edu.streamingplatform.recommendation.dto.ContentViewedEvent;
import br.edu.streamingplatform.recommendation.service.RecommendationService;

class ContentViewedListenerTest {

    private final RecommendationService recommendationService = Mockito.mock(RecommendationService.class);
    private final ContentViewedListener listener = new ContentViewedListener(recommendationService);

    @Test
    void handleShouldCallRecommendationService() {
        ContentViewedEvent receivedEvent = new ContentViewedEvent(
                1L,
                20L,
                "Dark",
                "Suspense",
                LocalDateTime.of(2026, 6, 6, 10, 30)
        );
        ArgumentCaptor<ContentViewedEvent> eventCaptor = ArgumentCaptor.forClass(ContentViewedEvent.class);

        listener.handle(receivedEvent);

        verify(recommendationService).processContentViewed(eventCaptor.capture());
        ContentViewedEvent event = eventCaptor.getValue();
        assertThat(event.userId()).isEqualTo(1L);
        assertThat(event.contentId()).isEqualTo(20L);
        assertThat(event.contentTitle()).isEqualTo("Dark");
        assertThat(event.contentCategory()).isEqualTo("Suspense");
        assertThat(event.viewedAt()).isEqualTo(LocalDateTime.of(2026, 6, 6, 10, 30));
    }
}
