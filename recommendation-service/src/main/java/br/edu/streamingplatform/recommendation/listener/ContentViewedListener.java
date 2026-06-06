package br.edu.streamingplatform.recommendation.listener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.streamingplatform.recommendation.config.RabbitMqConfig;
import br.edu.streamingplatform.recommendation.dto.ContentViewedEvent;
import br.edu.streamingplatform.recommendation.service.RecommendationService;

@Component
public class ContentViewedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentViewedListener.class);

    private final ObjectMapper objectMapper;
    private final RecommendationService recommendationService;

    public ContentViewedListener(ObjectMapper objectMapper, RecommendationService recommendationService) {
        this.objectMapper = objectMapper;
        this.recommendationService = recommendationService;
    }

    @RabbitListener(queues = RabbitMqConfig.RECOMMENDATION_QUEUE)
    public void handle(Message message) throws IOException {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        LOGGER.info("Evento content.viewed recebido: {}", payload);

        ContentViewedEvent event = objectMapper.readValue(payload, ContentViewedEvent.class);
        recommendationService.processContentViewed(event);
    }
}
