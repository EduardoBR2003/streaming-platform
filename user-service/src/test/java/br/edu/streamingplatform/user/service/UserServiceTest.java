package br.edu.streamingplatform.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import br.edu.streamingplatform.user.dto.UserDto;
import br.edu.streamingplatform.user.dto.UserRequest;
import br.edu.streamingplatform.user.exception.EmailAlreadyExistsException;
import br.edu.streamingplatform.user.exception.UsuarioNotFoundException;
import br.edu.streamingplatform.user.model.Usuario;
import br.edu.streamingplatform.user.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createShouldPersistTrimmedUserWithNormalizedEmail() {
        UserRequest request = new UserRequest(
                "  Ana Silva  ",
                "  ANA@EMAIL.COM  ",
                "  PREMIUM  "
        );

        when(usuarioRepository.existsByEmailIgnoreCase("ana@email.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            usuario.setCreatedAt(LocalDateTime.of(2026, 6, 3, 10, 0));
            usuario.setUpdatedAt(LocalDateTime.of(2026, 6, 3, 10, 0));
            return usuario;
        });

        UserDto result = userService.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Ana Silva");
        assertThat(result.email()).isEqualTo("ana@email.com");
        assertThat(result.plan()).isEqualTo("PREMIUM");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Ana Silva");
        assertThat(captor.getValue().getEmail()).isEqualTo("ana@email.com");
    }

    @Test
    void createShouldRejectDuplicateEmail() {
        when(usuarioRepository.existsByEmailIgnoreCase("ana@email.com")).thenReturn(true);

        UserRequest request = new UserRequest("Ana Silva", "ANA@EMAIL.COM", "PREMIUM");

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("ana@email.com");
    }

    @Test
    void findAllShouldReturnUsersOrderedById() {
        Usuario ana = usuario(1L, "Ana Silva", "ana@email.com", "PREMIUM");
        Usuario bruno = usuario(2L, "Bruno Lima", "bruno@email.com", "BASIC");

        when(usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(List.of(ana, bruno));

        List<UserDto> result = userService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserDto::name).containsExactly("Ana Silva", "Bruno Lima");
    }

    @Test
    void findByIdShouldThrowWhenUserDoesNotExist() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void existsByIdShouldDelegateToRepository() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        assertThat(userService.existsById(1L)).isTrue();
    }

    @Test
    void updateShouldChangeExistingUser() {
        Usuario usuario = usuario(1L, "Ana Silva", "ana@email.com", "PREMIUM");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmailIgnoreCaseAndIdNot("ana.souza@email.com", 1L)).thenReturn(false);

        UserDto result = userService.update(
                1L,
                new UserRequest(" Ana Souza ", " ANA.SOUZA@EMAIL.COM ", " FAMILY ")
        );

        assertThat(result.name()).isEqualTo("Ana Souza");
        assertThat(result.email()).isEqualTo("ana.souza@email.com");
        assertThat(result.plan()).isEqualTo("FAMILY");
    }

    @Test
    void updateShouldRejectEmailUsedByAnotherUser() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario(1L, "Ana Silva", "ana@email.com", "PREMIUM")));
        when(usuarioRepository.existsByEmailIgnoreCaseAndIdNot("bruno@email.com", 1L)).thenReturn(true);

        UserRequest request = new UserRequest("Ana Silva", "bruno@email.com", "PREMIUM");

        assertThatThrownBy(() -> userService.update(1L, request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("bruno@email.com");
    }

    private Usuario usuario(Long id, String name, String email, String plan) {
        Usuario usuario = new Usuario(name, email, plan);
        usuario.setId(id);
        usuario.setCreatedAt(LocalDateTime.of(2026, 6, 3, 10, 0));
        usuario.setUpdatedAt(LocalDateTime.of(2026, 6, 3, 10, 0));
        return usuario;
    }
}
