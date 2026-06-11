package br.edu.streamingplatform.recommendation.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.edu.streamingplatform.recommendation.config.RabbitMqConfig;
import br.edu.streamingplatform.recommendation.dto.ContentViewedEvent;
import br.edu.streamingplatform.recommendation.service.RecommendationService;

@Component
public class ContentViewedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentViewedListener.class);

    private final RecommendationService recommendationService;

    public ContentViewedListener(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @RabbitListener(queues = RabbitMqConfig.RECOMMENDATION_QUEUE)
    public void handle(ContentViewedEvent event) {
        LOGGER.info("Evento content.viewed recebido: {}", event);
        recommendationService.processContentViewed(event);
    }
}
