package br.edu.streamingplatform.recommendation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.streamingplatform.recommendation.model.Recomendacao;

public interface RecomendacaoRepository extends JpaRepository<Recomendacao, Long> {

    List<Recomendacao> findByUserIdOrderByCreatedAtDesc(Long userId);
}
