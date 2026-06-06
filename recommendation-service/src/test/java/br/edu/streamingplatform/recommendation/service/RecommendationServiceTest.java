package br.edu.streamingplatform.recommendation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import br.edu.streamingplatform.recommendation.config.RabbitMqConfig;
import br.edu.streamingplatform.recommendation.dto.ContentViewedEvent;
import br.edu.streamingplatform.recommendation.dto.RecommendationCreatedEvent;
import br.edu.streamingplatform.recommendation.dto.RecommendationDto;
import br.edu.streamingplatform.recommendation.model.Recomendacao;
import br.edu.streamingplatform.recommendation.repository.RecomendacaoRepository;

class RecommendationServiceTest {

    private final RecomendacaoRepository recomendacaoRepository = Mockito.mock(RecomendacaoRepository.class);
    private final RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
    private final RecommendationService recommendationService = new RecommendationService(
            recomendacaoRepository,
            rabbitTemplate
    );

    @Test
    void processContentViewedShouldSaveRecommendationAndPublishEvent() {
        ContentViewedEvent event = new ContentViewedEvent(
                1L,
                20L,
                "Dark",
                "Suspense",
                LocalDateTime.of(2026, 6, 6, 10, 30)
        );
        ArgumentCaptor<Recomendacao> recommendationCaptor = ArgumentCaptor.forClass(Recomendacao.class);
        ArgumentCaptor<RecommendationCreatedEvent> eventCaptor = ArgumentCaptor.forClass(RecommendationCreatedEvent.class);

        when(recomendacaoRepository.save(any(Recomendacao.class))).thenAnswer(invocation -> {
            Recomendacao recomendacao = invocation.getArgument(0);
            ReflectionTestUtils.setField(recomendacao, "id", 10L);
            return recomendacao;
        });

        RecommendationDto recommendation = recommendationService.processContentViewed(event);

        verify(recomendacaoRepository).save(recommendationCaptor.capture());
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.RECOMMENDATION_EXCHANGE),
                eq(RabbitMqConfig.RECOMMENDATION_CREATED_ROUTING_KEY),
                eventCaptor.capture()
        );

        Recomendacao savedRecommendation = recommendationCaptor.getValue();
        assertThat(savedRecommendation.getUserId()).isEqualTo(1L);
        assertThat(savedRecommendation.getSourceContentId()).isEqualTo(20L);
        assertThat(savedRecommendation.getSourceContentTitle()).isEqualTo("Dark");
        assertThat(savedRecommendation.getCategory()).isEqualTo("Suspense");
        assertThat(savedRecommendation.getMessage())
                .isEqualTo("Recomendacao gerada para usuario 1: novos conteudos disponiveis para categoria Suspense.");

        RecommendationCreatedEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.userId()).isEqualTo(1L);
        assertThat(publishedEvent.recommendationId()).isEqualTo(10L);
        assertThat(publishedEvent.category()).isEqualTo("Suspense");
        assertThat(publishedEvent.createdAt()).isNotNull();

        assertThat(recommendation.id()).isEqualTo(10L);
        assertThat(recommendation.category()).isEqualTo("Suspense");
    }

    @Test
    void findByUserIdShouldReturnRecommendationsOrderedByRepository() {
        Recomendacao recomendacao = new Recomendacao(
                1L,
                20L,
                "Dark",
                "Suspense",
                "Recomendacao gerada para usuario 1: novos conteudos disponiveis para categoria Suspense.",
                LocalDateTime.of(2026, 6, 6, 10, 31)
        );

        when(recomendacaoRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(recomendacao));

        List<RecommendationDto> recommendations = recommendationService.findByUserId(1L);

        assertThat(recommendations).hasSize(1);
        assertThat(recommendations.get(0).category()).isEqualTo("Suspense");
        verify(recomendacaoRepository).findByUserIdOrderByCreatedAtDesc(1L);
    }
}
