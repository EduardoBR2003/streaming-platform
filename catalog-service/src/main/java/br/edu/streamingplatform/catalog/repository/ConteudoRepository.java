package br.edu.streamingplatform.catalog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.streamingplatform.catalog.model.Conteudo;

public interface ConteudoRepository extends JpaRepository<Conteudo, Long> {

    List<Conteudo> findByCategoryIgnoreCase(String category);
}
