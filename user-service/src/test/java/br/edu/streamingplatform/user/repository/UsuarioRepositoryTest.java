package br.edu.streamingplatform.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.edu.streamingplatform.user.model.Usuario;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.sql.init.mode=never"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void existsByEmailIgnoreCaseShouldMatchRegardlessOfCase() {
        usuarioRepository.save(new Usuario("Ana Silva", "ana@email.com", "PREMIUM"));

        assertThat(usuarioRepository.existsByEmailIgnoreCase("ANA@EMAIL.COM")).isTrue();
        assertThat(usuarioRepository.existsByEmailIgnoreCase("outro@email.com")).isFalse();
    }

    @Test
    void existsByEmailIgnoreCaseAndIdNotShouldIgnoreCurrentUser() {
        Usuario ana = usuarioRepository.save(new Usuario("Ana Silva", "ana@email.com", "PREMIUM"));
        usuarioRepository.save(new Usuario("Bruno Lima", "bruno@email.com", "BASIC"));

        assertThat(usuarioRepository.existsByEmailIgnoreCaseAndIdNot("ANA@EMAIL.COM", ana.getId())).isFalse();
        assertThat(usuarioRepository.existsByEmailIgnoreCaseAndIdNot("BRUNO@EMAIL.COM", ana.getId())).isTrue();
    }
}
