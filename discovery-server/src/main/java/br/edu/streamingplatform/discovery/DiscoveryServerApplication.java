package br.edu.streamingplatform.discovery;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableEurekaServer
@SpringBootApplication
public class DiscoveryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }
}

@RestController
class HealthController {

    @GetMapping("/health")
    Map<String, String> health() {
        return Map.of(
                "service", "discovery-server",
                "status", "UP",
                "message", "discovery-server esta funcionando"
        );
    }
}
