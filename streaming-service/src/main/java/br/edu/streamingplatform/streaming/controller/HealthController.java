package br.edu.streamingplatform.streaming.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping({"/health", "/streaming/health"})
    public Map<String, String> health() {
        return Map.of(
                "service", "streaming-service",
                "status", "UP",
                "message", "streaming-service esta funcionando"
        );
    }
}
