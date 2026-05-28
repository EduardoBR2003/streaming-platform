# Evidencia - Catalog Service REST

Responsavel: Integrante 2

## Escopo implementado

- `POST /contents`
- `GET /contents`
- `GET /contents/{id}`
- `GET /contents/category/{category}`
- Persistencia na tabela `contents` do banco `catalog_db`
- Validacao de campos obrigatorios e duracao positiva

## Exemplo de cadastro

```bash
curl -X POST http://localhost:8082/contents \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Matrix",
    "description": "Ficcao cientifica",
    "category": "Sci-Fi",
    "type": "MOVIE",
    "durationMinutes": 136
  }'
```

Resposta esperada:

```json
{
  "id": 1,
  "title": "Matrix",
  "description": "Ficcao cientifica",
  "category": "Sci-Fi",
  "type": "MOVIE",
  "durationMinutes": 136
}
```

## Consultas

```bash
curl http://localhost:8082/contents
curl http://localhost:8082/contents/1
curl http://localhost:8082/contents/category/Sci-Fi
```

## Evidencia automatizada

Executado em 27/05/2026:

```text
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Testes relacionados:

- `ContentControllerTest`
- `CatalogServiceTest`
- `ConteudoRepositoryTest`
