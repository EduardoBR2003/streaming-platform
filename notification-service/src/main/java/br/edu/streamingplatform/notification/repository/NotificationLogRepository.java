package br.edu.streamingplatform.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.streamingplatform.notification.model.NotificationLog;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    List<NotificationLog> findByUserIdOrderBySentAtDesc(Long userId);
}
