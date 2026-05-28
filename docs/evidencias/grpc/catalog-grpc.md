# Evidencia - gRPC Catalog Service

Responsavel: Integrante 2

## Contrato

Arquivo:

```text
catalog-service/src/main/proto/catalog.proto
```

Servico:

```text
streaming.catalog.CatalogContentService/GetContentById
```

Request:

```json
{
  "content_id": 1
}
```

Response:

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

Quando o conteudo nao existe, o servidor retorna status gRPC `NOT_FOUND`.

## Teste manual com grpcurl

```bash
grpcurl -plaintext \
  -import-path catalog-service/src/main/proto \
  -proto catalog.proto \
  -d '{"content_id": 1}' \
  localhost:9090 \
  streaming.catalog.CatalogContentService/GetContentById
```

## Evidencia automatizada

Executado em 27/05/2026:

```text
[INFO] Running br.edu.streamingplatform.catalog.grpc.CatalogGrpcServiceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
```

Casos cobertos:

- Conteudo existente retorna os dados do catalogo.
- Conteudo inexistente retorna `NOT_FOUND`.
