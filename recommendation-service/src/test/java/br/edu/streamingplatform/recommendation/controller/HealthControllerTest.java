package br.edu.streamingplatform.recommendation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthShouldReturnServiceStatus() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("recommendation-service"))
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void prefixedHealthShouldReturnServiceStatus() throws Exception {
        mockMvc.perform(get("/recommendations/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("recommendation-service"))
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
