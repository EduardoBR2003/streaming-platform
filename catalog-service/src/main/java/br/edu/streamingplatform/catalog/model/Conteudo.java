package br.edu.streamingplatform.catalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "contents")
public class Conteudo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String type;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    // Construtor vazio exigido pelo JPA para recriar a entidade a partir do banco.
    protected Conteudo() {
    }

    // Construtor usado pelo service ao cadastrar um novo conteudo.
    public Conteudo(String title, String description, String category, String type, Integer durationMinutes) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.type = type;
        this.durationMinutes = durationMinutes;
    }

    // Getters e setters usados pelo JPA, service e testes.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
