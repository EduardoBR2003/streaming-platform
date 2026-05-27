package br.edu.streamingplatform.recommendation.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping({"/health", "/recommendations/health"})
    public Map<String, String> health() {
        return Map.of(
                "service", "recommendation-service",
                "status", "UP",
                "message", "recommendation-service esta funcionando"
        );
    }
}
