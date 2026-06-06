package br.edu.streamingplatform.recommendation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.streamingplatform.recommendation.dto.RecommendationDto;
import br.edu.streamingplatform.recommendation.service.RecommendationService;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/user/{userId}")
    public List<RecommendationDto> findByUserId(@PathVariable Long userId) {
        return recommendationService.findByUserId(userId);
    }
}
