package br.edu.streamingplatform.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.streamingplatform.notification.config.RabbitMqConfig;
import br.edu.streamingplatform.notification.dto.NotificationDto;
import br.edu.streamingplatform.notification.dto.RecommendationCreatedEvent;
import br.edu.streamingplatform.notification.model.NotificationLog;
import br.edu.streamingplatform.notification.repository.NotificationLogRepository;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationLogRepository notificationLogRepository;

    public NotificationService(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    @Transactional
    public NotificationDto notifyRecommendationCreated(RecommendationCreatedEvent event) {
        String categoryText = event.category() == null || event.category().isBlank()
                ? "novas categorias"
                : "categoria " + event.category();
        String message = "Notificacao enviada para usuario " + event.userId()
                + ": novas recomendacoes disponiveis para " + categoryText + ".";

        NotificationLog notificationLog = new NotificationLog(
                event.userId(),
                event.recommendationId(),
                event.category(),
                message,
                RabbitMqConfig.RECOMMENDATION_CREATED_ROUTING_KEY,
                event.createdAt(),
                LocalDateTime.now()
        );

        NotificationLog savedNotification = notificationLogRepository.save(notificationLog);
        LOGGER.info(message);
        return savedNotification.toDto();
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> findByUserId(Long userId) {
        return notificationLogRepository.findByUserIdOrderBySentAtDesc(userId)
                .stream()
                .map(NotificationLog::toDto)
                .toList();
    }
}
