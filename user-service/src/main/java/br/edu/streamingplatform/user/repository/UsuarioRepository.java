package br.edu.streamingplatform.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.streamingplatform.user.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
