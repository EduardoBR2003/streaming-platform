package br.edu.streamingplatform.notification.listener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.streamingplatform.notification.config.RabbitMqConfig;
import br.edu.streamingplatform.notification.dto.RecommendationCreatedEvent;
import br.edu.streamingplatform.notification.service.NotificationService;

@Component
public class RecommendationCreatedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationCreatedListener.class);

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public RecommendationCreatedListener(ObjectMapper objectMapper, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMqConfig.NOTIFICATION_QUEUE)
    public void handle(Message message) throws IOException {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        LOGGER.info("Evento recommendation.created recebido: {}", payload);

        RecommendationCreatedEvent event = objectMapper.readValue(payload, RecommendationCreatedEvent.class);
        notificationService.notifyRecommendationCreated(event);
    }
}
