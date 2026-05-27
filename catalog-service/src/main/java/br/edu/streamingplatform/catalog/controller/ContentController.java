package br.edu.streamingplatform.catalog.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.streamingplatform.catalog.dto.ContentDto;
import br.edu.streamingplatform.catalog.dto.ContentRequest;
import br.edu.streamingplatform.catalog.service.CatalogService;

@RestController
@RequestMapping("/contents")
public class ContentController {

    // Controller REST publico do catalogo, usado direto ou pelo API Gateway.
    private final CatalogService catalogService;

    public ContentController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // Recebe o JSON do novo conteudo, valida os campos e devolve 201 Created.
    @PostMapping
    public ResponseEntity<ContentDto> create(@Valid @RequestBody ContentRequest request) {
        ContentDto content = catalogService.create(request);
        return ResponseEntity
                .created(URI.create("/contents/" + content.id()))
                .body(content);
    }

    // Lista todos os filmes e series cadastrados no catalogo.
    @GetMapping
    public List<ContentDto> findAll() {
        return catalogService.findAll();
    }

    // Busca um conteudo especifico pelo ID informado na URL.
    @GetMapping("/{id}")
    public ContentDto findById(@PathVariable Long id) {
        return catalogService.findById(id);
    }

    // Filtra os conteudos por categoria, como Acao, Drama ou Sci-Fi.
    @GetMapping("/category/{category}")
    public List<ContentDto> findByCategory(@PathVariable String category) {
        return catalogService.findByCategory(category);
    }
}
