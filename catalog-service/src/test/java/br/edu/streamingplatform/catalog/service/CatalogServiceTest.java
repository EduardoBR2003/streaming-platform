package br.edu.streamingplatform.catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.streamingplatform.catalog.dto.ContentDto;
import br.edu.streamingplatform.catalog.dto.ContentRequest;
import br.edu.streamingplatform.catalog.exception.ConteudoNotFoundException;
import br.edu.streamingplatform.catalog.model.Conteudo;
import br.edu.streamingplatform.catalog.repository.ConteudoRepository;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private ConteudoRepository conteudoRepository;

    @InjectMocks
    private CatalogService catalogService;

    @Test
    void createShouldPersistTrimmedContent() {
        ContentRequest request = new ContentRequest(
                "  Matrix  ",
                "  Ficcao cientifica  ",
                "  Sci-Fi  ",
                "  MOVIE  ",
                136
        );

        when(conteudoRepository.save(any(Conteudo.class))).thenAnswer(invocation -> {
            Conteudo conteudo = invocation.getArgument(0);
            conteudo.setId(1L);
            return conteudo;
        });

        ContentDto result = catalogService.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Matrix");
        assertThat(result.description()).isEqualTo("Ficcao cientifica");
        assertThat(result.category()).isEqualTo("Sci-Fi");
        assertThat(result.type()).isEqualTo("MOVIE");
        assertThat(result.durationMinutes()).isEqualTo(136);

        ArgumentCaptor<Conteudo> captor = ArgumentCaptor.forClass(Conteudo.class);
        verify(conteudoRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Matrix");
    }

    @Test
    void findAllShouldReturnContentList() {
        Conteudo movie = conteudo(1L, "Matrix", "Sci-Fi");
        Conteudo series = conteudo(2L, "Dark", "Suspense");

        when(conteudoRepository.findAll()).thenReturn(List.of(movie, series));

        List<ContentDto> result = catalogService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ContentDto::title).containsExactly("Matrix", "Dark");
    }

    @Test
    void findByIdShouldReturnExistingContent() {
        when(conteudoRepository.findById(1L)).thenReturn(Optional.of(conteudo(1L, "Matrix", "Sci-Fi")));

        ContentDto result = catalogService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Matrix");
    }

    @Test
    void findByIdShouldThrowWhenContentDoesNotExist() {
        when(conteudoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> catalogService.findById(99L))
                .isInstanceOf(ConteudoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findByCategoryShouldDelegateCaseInsensitiveSearch() {
        when(conteudoRepository.findByCategoryIgnoreCase("Acao"))
                .thenReturn(List.of(conteudo(1L, "Mad Max", "Acao")));

        List<ContentDto> result = catalogService.findByCategory("  Acao  ");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).category()).isEqualTo("Acao");
        verify(conteudoRepository).findByCategoryIgnoreCase("Acao");
    }

    private Conteudo conteudo(Long id, String title, String category) {
        Conteudo conteudo = new Conteudo(title, title + " description", category, "MOVIE", 120);
        conteudo.setId(id);
        return conteudo;
    }
}
