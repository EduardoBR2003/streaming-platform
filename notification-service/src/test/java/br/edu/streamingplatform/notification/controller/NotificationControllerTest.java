package br.edu.streamingplatform.notification.controller;

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

import br.edu.streamingplatform.notification.dto.NotificationDto;
import br.edu.streamingplatform.notification.service.NotificationService;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void findByUserIdShouldReturnNotifications() throws Exception {
        when(notificationService.findByUserId(1L)).thenReturn(List.of(
                new NotificationDto(
                        5L,
                        1L,
                        10L,
                        "Acao",
                        "Notificacao enviada para usuario 1: novas recomendacoes disponiveis para categoria Acao.",
                        "recommendation.created",
                        LocalDateTime.of(2026, 5, 28, 12, 8),
                        LocalDateTime.of(2026, 5, 28, 12, 9)
                )
        ));

        mockMvc.perform(get("/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].recommendationId").value(10))
                .andExpect(jsonPath("$[0].category").value("Acao"))
                .andExpect(jsonPath("$[0].eventType").value("recommendation.created"))
                .andExpect(jsonPath("$[0].message")
                        .value("Notificacao enviada para usuario 1: novas recomendacoes disponiveis para categoria Acao."));
    }

    @Test
    void findByUserIdShouldReturnEmptyListWhenUserHasNoNotifications() throws Exception {
        when(notificationService.findByUserId(2L)).thenReturn(List.of());

        mockMvc.perform(get("/notifications/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
