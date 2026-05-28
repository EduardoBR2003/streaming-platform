package br.edu.streamingplatform.catalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.streamingplatform.catalog.dto.ContentDto;
import br.edu.streamingplatform.catalog.dto.ContentRequest;
import br.edu.streamingplatform.catalog.exception.ConteudoNotFoundException;
import br.edu.streamingplatform.catalog.service.CatalogService;

@WebMvcTest(ContentController.class)
@Import(ApiExceptionHandler.class)
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogService catalogService;

    @Test
    void createShouldReturnCreatedContent() throws Exception {
        when(catalogService.create(any(ContentRequest.class)))
                .thenReturn(content(1L, "Matrix", "Sci-Fi"));

        mockMvc.perform(post("/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Matrix",
                                  "description": "Ficcao cientifica",
                                  "category": "Sci-Fi",
                                  "type": "MOVIE",
                                  "durationMinutes": 136
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/contents/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Matrix"));
    }

    @Test
    void createShouldReturnBadRequestForInvalidPayload() throws Exception {
        mockMvc.perform(post("/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "category": "",
                                  "type": "MOVIE",
                                  "durationMinutes": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_error"));

        verifyNoInteractions(catalogService);
    }

    @Test
    void findAllShouldReturnContentList() throws Exception {
        when(catalogService.findAll()).thenReturn(List.of(
                content(1L, "Matrix", "Sci-Fi"),
                content(2L, "Dark", "Suspense")
        ));

        mockMvc.perform(get("/contents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Matrix"))
                .andExpect(jsonPath("$[1].title").value("Dark"));
    }

    @Test
    void findByIdShouldReturnNotFound() throws Exception {
        when(catalogService.findById(99L)).thenThrow(new ConteudoNotFoundException(99L));

        mockMvc.perform(get("/contents/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void findByCategoryShouldReturnEmptyListWhenNothingMatches() throws Exception {
        when(catalogService.findByCategory("Comedia")).thenReturn(List.of());

        mockMvc.perform(get("/contents/category/Comedia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    private ContentDto content(Long id, String title, String category) {
        return new ContentDto(id, title, title + " description", category, "MOVIE", 120);
    }
}
