package br.edu.streamingplatform.streaming.messaging;

import br.edu.streamingplatform.streaming.config.RabbitMQConfig;
import br.edu.streamingplatform.streaming.dto.ContentViewedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMQPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishContentViewed(ContentViewedEvent event) {
        log.info("[RabbitMQ] Publishing 'content.viewed' | userId={}, contentId={}, category='{}'",
                event.getUserId(), event.getContentId(), event.getContentCategory());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CONTENT_EXCHANGE,
                RabbitMQConfig.CONTENT_VIEWED_ROUTING_KEY,
                event
        );

        log.info("[RabbitMQ] Event successfully published to queue: {}", RabbitMQConfig.RECOMMENDATION_QUEUE);
    }
}
