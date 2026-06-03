package br.edu.streamingplatform.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.streamingplatform.user.dto.UserDto;
import br.edu.streamingplatform.user.dto.UserRequest;
import br.edu.streamingplatform.user.exception.EmailAlreadyExistsException;
import br.edu.streamingplatform.user.exception.UsuarioNotFoundException;
import br.edu.streamingplatform.user.service.UserService;

@WebMvcTest(UserController.class)
@Import(ApiExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void createShouldReturnCreatedUser() throws Exception {
        when(userService.create(any(UserRequest.class)))
                .thenReturn(user(1L, "Ana Silva", "ana@email.com", "PREMIUM"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ana Silva",
                                  "email": "ana@email.com",
                                  "plan": "PREMIUM"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/users/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ana Silva"))
                .andExpect(jsonPath("$.email").value("ana@email.com"));
    }

    @Test
    void createShouldReturnBadRequestForInvalidPayload() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "email": "email-invalido",
                                  "plan": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_error"));

        verifyNoInteractions(userService);
    }

    @Test
    void createShouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        when(userService.create(any(UserRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("ana@email.com"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ana Silva",
                                  "email": "ana@email.com",
                                  "plan": "PREMIUM"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("duplicate_email"));
    }

    @Test
    void findAllShouldReturnUserList() throws Exception {
        when(userService.findAll()).thenReturn(List.of(
                user(1L, "Ana Silva", "ana@email.com", "PREMIUM"),
                user(2L, "Bruno Lima", "bruno@email.com", "BASIC")
        ));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ana Silva"))
                .andExpect(jsonPath("$[1].name").value("Bruno Lima"));
    }

    @Test
    void findByIdShouldReturnNotFound() throws Exception {
        when(userService.findById(99L)).thenThrow(new UsuarioNotFoundException(99L));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void existsByIdShouldReturnBooleanFlag() throws Exception {
        when(userService.existsById(1L)).thenReturn(true);

        mockMvc.perform(get("/users/1/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.exists").value(true));
    }

    @Test
    void updateShouldReturnUpdatedUser() throws Exception {
        when(userService.update(any(Long.class), any(UserRequest.class)))
                .thenReturn(user(1L, "Ana Souza", "ana@email.com", "FAMILY"));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ana Souza",
                                  "email": "ana@email.com",
                                  "plan": "FAMILY"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana Souza"))
                .andExpect(jsonPath("$.plan").value("FAMILY"));
    }

    private UserDto user(Long id, String name, String email, String plan) {
        LocalDateTime now = LocalDateTime.of(2026, 6, 3, 10, 0);
        return new UserDto(id, name, email, plan, now, now);
    }
}
