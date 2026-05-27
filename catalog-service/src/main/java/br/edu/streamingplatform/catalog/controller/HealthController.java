package br.edu.streamingplatform.catalog.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping({"/health", "/contents/health"})
    public Map<String, String> health() {
        return Map.of(
                "service", "catalog-service",
                "status", "UP",
                "message", "catalog-service esta funcionando"
        );
    }
}
