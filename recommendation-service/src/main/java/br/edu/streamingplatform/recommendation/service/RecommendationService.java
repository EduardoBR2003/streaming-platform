package br.edu.streamingplatform.recommendation.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.streamingplatform.recommendation.config.RabbitMqConfig;
import br.edu.streamingplatform.recommendation.dto.ContentViewedEvent;
import br.edu.streamingplatform.recommendation.dto.RecommendationCreatedEvent;
import br.edu.streamingplatform.recommendation.dto.RecommendationDto;
import br.edu.streamingplatform.recommendation.model.Recomendacao;
import br.edu.streamingplatform.recommendation.repository.RecomendacaoRepository;

@Service
public class RecommendationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationService.class);

    private final RecomendacaoRepository recomendacaoRepository;
    private final RabbitTemplate rabbitTemplate;

    public RecommendationService(RecomendacaoRepository recomendacaoRepository, RabbitTemplate rabbitTemplate) {
        this.recomendacaoRepository = recomendacaoRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public RecommendationDto processContentViewed(ContentViewedEvent event) {
        LocalDateTime createdAt = LocalDateTime.now();
        String title = resolveTitle(event);
        String category = normalize(event.contentCategory());
        String categoryText = category == null ? "categorias similares" : "categoria " + category;
        String message = "Recomendacao gerada para usuario " + event.userId()
                + ": novos conteudos disponiveis para " + categoryText + ".";

        Recomendacao recomendacao = new Recomendacao(
                event.userId(),
                event.contentId(),
                title,
                category,
                message,
                createdAt
        );

        Recomendacao savedRecommendation = recomendacaoRepository.save(recomendacao);
        LOGGER.info("Recomendacao gerada | userId={}, recommendationId={}, category={}",
                savedRecommendation.getUserId(), savedRecommendation.getId(), savedRecommendation.getCategory());

        RecommendationCreatedEvent recommendationCreatedEvent = new RecommendationCreatedEvent(
                savedRecommendation.getUserId(),
                savedRecommendation.getId(),
                savedRecommendation.getCategory(),
                savedRecommendation.getCreatedAt()
        );

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.RECOMMENDATION_EXCHANGE,
                RabbitMqConfig.RECOMMENDATION_CREATED_ROUTING_KEY,
                recommendationCreatedEvent
        );
        LOGGER.info("Evento recommendation.created publicado | userId={}, recommendationId={}",
                recommendationCreatedEvent.userId(), recommendationCreatedEvent.recommendationId());

        return savedRecommendation.toDto();
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> findByUserId(Long userId) {
        return recomendacaoRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(Recomendacao::toDto)
                .toList();
    }

    private String resolveTitle(ContentViewedEvent event) {
        String contentTitle = normalize(event.contentTitle());
        if (contentTitle != null) {
            return contentTitle;
        }
        return "conteudo " + event.contentId();
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
