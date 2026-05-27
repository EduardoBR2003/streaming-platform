package br.edu.streamingplatform.user.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping({"/health", "/users/health"})
    public Map<String, String> health() {
        return Map.of(
                "service", "user-service",
                "status", "UP",
                "message", "user-service esta funcionando"
        );
    }
}
