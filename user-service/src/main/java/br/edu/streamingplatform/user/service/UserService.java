package br.edu.streamingplatform.user.service;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.streamingplatform.user.dto.UserDto;
import br.edu.streamingplatform.user.dto.UserRequest;
import br.edu.streamingplatform.user.exception.EmailAlreadyExistsException;
import br.edu.streamingplatform.user.exception.UsuarioNotFoundException;
import br.edu.streamingplatform.user.model.Usuario;
import br.edu.streamingplatform.user.repository.UsuarioRepository;

@Service
public class UserService {

    private final UsuarioRepository usuarioRepository;

    public UserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UserDto create(UserRequest request) {
        String email = normalizeEmail(request.email());
        ensureEmailAvailable(email);

        Usuario usuario = new Usuario(
                clean(request.name()),
                email,
                clean(request.plan())
        );

        return toDto(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return toDto(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return usuarioRepository.existsById(id);
    }

    @Transactional
    public UserDto update(Long id, UserRequest request) {
        Usuario usuario = findEntityById(id);
        String email = normalizeEmail(request.email());

        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new EmailAlreadyExistsException(email);
        }

        usuario.update(
                clean(request.name()),
                email,
                clean(request.plan())
        );

        return toDto(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario findEntityById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
    }

    private void ensureEmailAvailable(String email) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private UserDto toDto(Usuario usuario) {
        return new UserDto(
                usuario.getId(),
                usuario.getName(),
                usuario.getEmail(),
                usuario.getPlan(),
                usuario.getCreatedAt(),
                usuario.getUpdatedAt()
        );
    }

    private String clean(String value) {
        return value.trim();
    }

    private String normalizeEmail(String email) {
        return clean(email).toLowerCase(Locale.ROOT);
    }
}
