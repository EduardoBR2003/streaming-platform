package br.edu.streamingplatform.recommendation.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.streamingplatform.recommendation.dto.RecommendationDto;
import br.edu.streamingplatform.recommendation.service.RecommendationService;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    void findByUserIdShouldReturnRecommendations() throws Exception {
        when(recommendationService.findByUserId(1L)).thenReturn(List.of(
                new RecommendationDto(
                        10L,
                        1L,
                        20L,
                        "Dark",
                        "Suspense",
                        "Recomendacao gerada para usuario 1: novos conteudos disponiveis para categoria Suspense.",
                        LocalDateTime.of(2026, 6, 6, 10, 31)
                )
        ));

        mockMvc.perform(get("/recommendations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].sourceContentId").value(20))
                .andExpect(jsonPath("$[0].sourceContentTitle").value("Dark"))
                .andExpect(jsonPath("$[0].category").value("Suspense"))
                .andExpect(jsonPath("$[0].message")
                        .value("Recomendacao gerada para usuario 1: novos conteudos disponiveis para categoria Suspense."));
    }

    @Test
    void findByUserIdShouldReturnEmptyListWhenUserHasNoRecommendations() throws Exception {
        when(recommendationService.findByUserId(2L)).thenReturn(List.of());

        mockMvc.perform(get("/recommendations/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
