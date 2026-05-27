package br.edu.streamingplatform.gateway.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "service", "api-gateway",
                "status", "UP",
                "message", "api-gateway esta funcionando"
        );
    }
}
