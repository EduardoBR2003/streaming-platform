package br.edu.streamingplatform.notification.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping({"/health", "/notifications/health"})
    public Map<String, String> health() {
        return Map.of(
                "service", "notification-service",
                "status", "UP",
                "message", "notification-service esta funcionando"
        );
    }
}
