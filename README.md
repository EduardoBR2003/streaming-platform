# Plataforma de Streaming

Monorepo inicial para uma plataforma de streaming baseada em microsservicos, criado para a disciplina de Sistemas Distribuidos.

Nesta etapa o projeto contem a estrutura base dos microsservicos e a primeira implementacao de negocio do `catalog-service`, com endpoints REST, persistencia em PostgreSQL e servidor gRPC para consulta de conteudo.

## Arquitetura proposta

Cliente ou Postman acessa o `api-gateway`, que roteia as chamadas REST para os microsservicos registrados no `discovery-server` via Eureka. O `catalog-service` tambem expoe um servidor gRPC para consulta sincrona de conteudo pelo futuro cliente do `streaming-service`. Nas proximas etapas, o `streaming-service` publicara eventos no RabbitMQ para processamento assincrono pelo `recommendation-service` e `notification-service`.

## Microsservicos

- `discovery-server`: Eureka Server para registro e descoberta dos servicos.
- `api-gateway`: entrada unica da plataforma com Spring Cloud Gateway.
- `user-service`: base para cadastro e consulta de usuarios.
- `catalog-service`: cadastro e consulta de conteudos por REST, alem de consulta por gRPC.
- `streaming-service`: base para simulacao de reproducao.
- `recommendation-service`: base para processamento de recomendacoes.
- `notification-service`: base para notificacoes.

## Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring Cloud Netflix Eureka
- Spring Cloud Gateway
- Maven
- PostgreSQL
- RabbitMQ
- Docker Compose
- gRPC e Protocol Buffers

## Portas

| Servico | Porta |
| --- | ---: |
| Eureka Dashboard | 8761 |
| API Gateway | 8080 |
| user-service | 8081 |
| catalog-service | 8082 |
| catalog-service gRPC | 9090 |
| streaming-service | 8083 |
| recommendation-service | 8084 |
| notification-service | 8085 |
| PostgreSQL | 5433 |
| RabbitMQ | 5672 |
| RabbitMQ Management | 15672 |

## Bancos de dados

O Docker Compose sobe um unico container PostgreSQL e cria os bancos:

- `user_db`
- `catalog_db`
- `streaming_db`
- `recommendation_db`
- `notification_db`

Credenciais locais:

- Usuario: `streaming`
- Senha: `streaming`

## Executando a infraestrutura

Na raiz do monorepo:

```bash
docker compose up -d
```

Acesse:

- RabbitMQ Management: http://localhost:15672

Credenciais do RabbitMQ:

- Usuario: `streaming`
- Senha: `streaming`

## Executando os servicos

Inicie primeiro o Eureka:

```bash
cd discovery-server
mvn spring-boot:run
```

Depois disso, o Eureka Dashboard fica disponivel em http://localhost:8761.

Depois, em terminais separados, inicie os demais:

Como o PostgreSQL do Docker Compose fica exposto em `localhost:5433`, execute os servicos que usam banco com `POSTGRES_PORT=5433`.

```bash
cd api-gateway
mvn spring-boot:run
```

```bash
cd user-service
POSTGRES_PORT=5433 mvn spring-boot:run
```

```bash
cd catalog-service
POSTGRES_PORT=5433 mvn spring-boot:run
```

```bash
cd streaming-service
POSTGRES_PORT=5433 mvn spring-boot:run
```

```bash
cd recommendation-service
POSTGRES_PORT=5433 mvn spring-boot:run
```

```bash
cd notification-service
POSTGRES_PORT=5433 mvn spring-boot:run
```

## Testando os endpoints de health

Chamadas diretas:

```bash
curl http://localhost:8761/health
curl http://localhost:8080/health
curl http://localhost:8081/health
curl http://localhost:8082/health
curl http://localhost:8083/health
curl http://localhost:8084/health
curl http://localhost:8085/health
```

Rotas iniciais pelo gateway:

```bash
curl http://localhost:8080/users/health
curl http://localhost:8080/contents/health
curl http://localhost:8080/streaming/health
curl http://localhost:8080/recommendations/health
curl http://localhost:8080/notifications/health
```

## Catalog Service

O `catalog-service` e a parte do Integrante 2. Ele persiste conteudos na tabela `contents` do banco `catalog_db` e expoe a API REST abaixo.

| Metodo | Rota | Descricao |
| --- | --- | --- |
| POST | `/contents` | Cadastra filme ou serie |
| GET | `/contents` | Lista todos os conteudos |
| GET | `/contents/{id}` | Busca conteudo por ID |
| GET | `/contents/category/{category}` | Lista conteudos por categoria |

Exemplo de cadastro:

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

As mesmas rotas REST tambem podem ser acessadas pelo API Gateway quando ele estiver rodando:

```bash
curl http://localhost:8080/contents
curl http://localhost:8080/contents/category/Sci-Fi
```

## gRPC do Catalog Service

O contrato gRPC fica em `catalog-service/src/main/proto/catalog.proto`. O servico exposto e:

```text
streaming.catalog.CatalogContentService/GetContentById
```

Ele recebe `content_id` e retorna `id`, `title`, `description`, `category`, `type` e `duration_minutes`. Quando o conteudo nao existe, o servidor responde com status gRPC `NOT_FOUND`.

Com o `catalog-service` rodando, a chamada pode ser testada com `grpcurl`:

```bash
grpcurl -plaintext \
  -import-path catalog-service/src/main/proto \
  -proto catalog.proto \
  -d '{"content_id": 1}' \
  localhost:9090 \
  streaming.catalog.CatalogContentService/GetContentById
```

A porta gRPC padrao e `9090`, podendo ser alterada pela variavel `CATALOG_GRPC_PORT`.

## Testes do Catalog Service

No Windows deste ambiente, o Maven esta disponivel pelo cache do usuario. Para rodar os testes:

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
& 'C:\Users\otavi\.m2\wrapper\dists\apache-maven-3.9.9-bin\33b4b2b4\apache-maven-3.9.9\bin\mvn.cmd' test
```

Os testes cobrem controller REST, validacao, service, repository JPA com H2 e o servidor gRPC.

## Notification Service

Rota adicionada para consulta de notificacoes:

| Metodo | Rota | Descricao |
| --- | --- | --- |
| GET | `/notifications/user/{userId}` | Lista notificacoes enviadas para o usuario |

Contrato RabbitMQ consumido pelo `notification-service`:

```text
Exchange: recommendation.exchange
Routing key: recommendation.created
Fila: notification.queue
```

## Proximos passos

1. Implementar as regras de negocio dos demais microsservicos.
2. Implementar o cliente gRPC no `streaming-service` para chamar o `catalog-service`.
3. Configurar exchanges, filas e eventos RabbitMQ.
4. Publicar o evento `content.viewed` no `streaming-service`.
5. Consumir eventos no `recommendation-service`.
6. Simular notificacoes no `notification-service`.
7. Adicionar prints finais das integracoes completas.
