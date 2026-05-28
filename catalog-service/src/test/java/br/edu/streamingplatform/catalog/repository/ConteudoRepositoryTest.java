package br.edu.streamingplatform.catalog.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.edu.streamingplatform.catalog.model.Conteudo;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ConteudoRepositoryTest {

    @Autowired
    private ConteudoRepository conteudoRepository;

    @Test
    void findByCategoryIgnoreCaseShouldMatchRegardlessOfCase() {
        conteudoRepository.save(new Conteudo("Mad Max", "Estrada da furia", "Acao", "MOVIE", 120));
        conteudoRepository.save(new Conteudo("Dark", "Serie alema", "Suspense", "SERIES", 60));

        List<Conteudo> result = conteudoRepository.findByCategoryIgnoreCase("acao");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Mad Max");
    }
}
