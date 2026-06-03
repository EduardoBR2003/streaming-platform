package br.edu.streamingplatform.streaming.controller;

import br.edu.streamingplatform.streaming.dto.WatchRequest;
import br.edu.streamingplatform.streaming.dto.WatchResponse;
import br.edu.streamingplatform.streaming.entity.Visualizacao;
import br.edu.streamingplatform.streaming.service.StreamingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller REST do Streaming Service.
 *
 * Endpoints:
 *   POST /streaming/watch                    → assiste um conteúdo
 *   GET  /streaming/historico/{userId}       → histórico do usuário
 */
@RestController
@RequestMapping("/streaming")
@RequiredArgsConstructor
@Slf4j
public class StreamingController {

    private final StreamingService streamingService;

    /**
     * Registra que um usuário assistiu a um conteúdo.
     *
     * Body esperado:
     * {
     *   "userId": 1,
     *   "contentId": 10
     * }
     */
    @PostMapping("/watch")
    public ResponseEntity<WatchResponse> watch(@Valid @RequestBody WatchRequest request) {
        log.info("POST /streaming/watch | userId={}, contentId={}",
                request.getUserId(), request.getContentId());

        WatchResponse response = streamingService.watch(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna o histórico de visualizações de um usuário.
     * (Endpoint extra útil para evidências e testes)
     */
    @GetMapping("/historico/{userId}")
    public ResponseEntity<List<Visualizacao>> getHistorico(@PathVariable Long userId) {
        log.info("GET /streaming/historico/{}", userId);
        List<Visualizacao> historico = streamingService.getHistoricoByUser(userId);
        return ResponseEntity.ok(historico);
    }
}

