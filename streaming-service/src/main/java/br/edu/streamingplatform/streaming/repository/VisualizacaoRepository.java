package br.edu.streamingplatform.streaming.repository;

import br.edu.streamingplatform.streaming.entity.Visualizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VisualizacaoRepository extends JpaRepository<Visualizacao, Long> {

    List<Visualizacao> findByUserIdOrderByWatchedAtDesc(Long userId);

    List<Visualizacao> findByContentIdOrderByWatchedAtDesc(Long contentId);
}
