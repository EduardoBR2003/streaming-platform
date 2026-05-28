package br.edu.streamingplatform.catalog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.streamingplatform.catalog.dto.ContentDto;
import br.edu.streamingplatform.catalog.dto.ContentRequest;
import br.edu.streamingplatform.catalog.exception.ConteudoNotFoundException;
import br.edu.streamingplatform.catalog.model.Conteudo;
import br.edu.streamingplatform.catalog.repository.ConteudoRepository;

@Service
public class CatalogService {

    private final ConteudoRepository conteudoRepository;

    public CatalogService(ConteudoRepository conteudoRepository) {
        this.conteudoRepository = conteudoRepository;
    }

    // Cria um conteudo novo, normalizando texto antes de salvar no banco.
    @Transactional
    public ContentDto create(ContentRequest request) {
        Conteudo conteudo = new Conteudo(
                clean(request.title()),
                cleanOptional(request.description()),
                clean(request.category()),
                clean(request.type()),
                request.durationMinutes()
        );

        return toDto(conteudoRepository.save(conteudo));
    }

    // Retorna todos os conteudos cadastrados ja convertidos para DTO.
    @Transactional(readOnly = true)
    public List<ContentDto> findAll() {
        return conteudoRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Busca por ID e devolve erro de dominio se o conteudo nao existir.
    @Transactional(readOnly = true)
    public ContentDto findById(Long id) {
        return toDto(findEntityById(id));
    }

    // Busca por categoria ignorando diferenca entre maiusculas e minusculas.
    @Transactional(readOnly = true)
    public List<ContentDto> findByCategory(String category) {
        return conteudoRepository.findByCategoryIgnoreCase(clean(category))
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Retorna a entidade JPA quando outra camada precisa dos dados completos.
    @Transactional(readOnly = true)
    public Conteudo findEntityById(Long id) {
        return conteudoRepository.findById(id)
                .orElseThrow(() -> new ConteudoNotFoundException(id));
    }

    // Converte entidade JPA para DTO, evitando expor o modelo do banco na API.
    private ContentDto toDto(Conteudo conteudo) {
        return new ContentDto(
                conteudo.getId(),
                conteudo.getTitle(),
                conteudo.getDescription(),
                conteudo.getCategory(),
                conteudo.getType(),
                conteudo.getDurationMinutes()
        );
    }

    // Remove espacos extras de campos obrigatorios.
    private String clean(String value) {
        return value.trim();
    }

    // Remove espacos extras de campos opcionais e guarda vazio como null.
    private String cleanOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
