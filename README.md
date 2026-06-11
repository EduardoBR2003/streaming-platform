# Plataforma de Streaming

Projeto da disciplina de Sistemas Distribuidos com arquitetura de microsservicos em Spring Boot. A plataforma simula um servico de streaming onde usuarios acessam um catalogo, assistem conteudos, geram eventos de visualizacao e recebem recomendacoes e notificacoes processadas de forma assincrona.

## Objetivo

Implementar uma plataforma distribuida aplicando:

- Microsservicos independentes.
- Entrada unica por API Gateway.
- Service Discovery com Eureka.
- Comunicacao sincrona com gRPC.
- Comunicacao assincrona com RabbitMQ.
- Filas, exchanges e eventos Pub/Sub.
- Persistencia relacional com PostgreSQL.
- Evidencias de execucao por endpoints, logs, RabbitMQ Management e Eureka Dashboard.

## Arquitetura

Fluxo principal:

```text
Cliente / Postman
  -> API Gateway
  -> streaming-service
  -> catalog-service via gRPC
  -> PostgreSQL registra visualizacao
  -> RabbitMQ publica content.viewed
  -> recommendation-service consome recommendation.queue
  -> PostgreSQL registra recomendacao
  -> RabbitMQ publica recommendation.created
  -> notification-service consome notification.queue
  -> PostgreSQL registra notificacao
```

Service Discovery:

```text
discovery-server (Eureka)
  <- api-gateway
  <- user-service
  <- catalog-service
  <- streaming-service
  <- recommendation-service
  <- notification-service
```

O `api-gateway` roteia chamadas REST usando `lb://nome-do-servico`, resolvido pelo Eureka. A chamada gRPC entre `streaming-service` e `catalog-service` esta funcional, mas atualmente usa endereco configuravel `static://...` no Docker Compose.

## Microsservicos

| Servico | Responsabilidade | Porta |
| --- | --- | ---: |
| `discovery-server` | Eureka Server para registro e descoberta dos servicos | 8761 |
| `api-gateway` | Entrada unica REST com Spring Cloud Gateway | 8080 |
| `user-service` | Cadastro, consulta, listagem, atualizacao e validacao de usuarios | 8081 |
| `catalog-service` | Cadastro e consulta de filmes/series por REST e gRPC | 8082 / 9090 |
| `streaming-service` | Simula reproducao, consulta catalogo por gRPC, registra visualizacao e publica evento | 8083 |
| `recommendation-service` | Consome visualizacoes, gera recomendacoes e publica evento de recomendacao | 8084 |
| `notification-service` | Consome eventos de recomendacao, simula envio por log e registra notificacoes | 8085 |

## Tecnologias

- Java 17.
- Spring Boot 3.3.5.
- Spring Web.
- Spring Data JPA.
- Spring Cloud Netflix Eureka.
- Spring Cloud Gateway.
- Spring AMQP.
- RabbitMQ.
- PostgreSQL.
- gRPC.
- Protocol Buffers.
- Maven.
- Docker e Docker Compose.

## Bancos de Dados

O Docker Compose sobe um container PostgreSQL e cria os bancos:

- `user_db`.
- `catalog_db`.
- `streaming_db`.
- `recommendation_db`.
- `notification_db`.

Credenciais locais:

```text
usuario: streaming
senha: streaming
porta host: 5433
porta container: 5432
```

## RabbitMQ

Credenciais:

```text
usuario: streaming
senha: streaming
AMQP: http://localhost:5672
Management: http://localhost:15672
```

Exchanges e filas usadas:

| Exchange | Routing key | Fila | Produtor | Consumidor |
| --- | --- | --- | --- | --- |
| `content.exchange` | `content.viewed` | `recommendation.queue` | `streaming-service` | `recommendation-service` |
| `recommendation.exchange` | `recommendation.created` | `notification.queue` | `recommendation-service` | `notification-service` |

Evento `content.viewed` usado no fluxo:

```json
{
  "userId": 1,
  "contentId": 10,
  "contentTitle": "Matrix",
  "contentCategory": "Sci-Fi",
  "viewedAt": "2026-06-09T12:36:21.318963"
}
```

Evento `recommendation.created` usado no fluxo:

```json
{
  "userId": 1,
  "recommendationId": 10,
  "category": "Sci-Fi",
  "createdAt": "2026-06-09T12:36:21.619430"
}
```

## gRPC

O contrato fica em:

```text
catalog-service/src/main/proto/catalog.proto
streaming-service/src/main/proto/catalog.proto
```

Servico exposto pelo `catalog-service`:

```text
streaming.catalog.CatalogContentService/GetContentById
```

Responsabilidade no fluxo:

- O `streaming-service` recebe uma solicitacao para assistir um conteudo.
- Antes de registrar a visualizacao, ele consulta o `catalog-service` via gRPC.
- Se o conteudo existir, recebe titulo, categoria, tipo e duracao.
- Se o conteudo nao existir, o `catalog-service` responde com status gRPC `NOT_FOUND`.

Teste manual com `grpcurl` usando Docker:

```bash
docker run --rm --network streaming-platform_default \
  -v "$PWD/catalog-service/src/main/proto:/protos:ro" \
  fullstorydev/grpcurl:latest \
  -plaintext \
  -import-path /protos \
  -proto catalog.proto \
  -d '{"content_id":1}' \
  catalog-service:9090 \
  streaming.catalog.CatalogContentService/GetContentById
```

## Executando com Docker Compose

Na raiz do projeto:

```bash
docker compose up --build -d
```

Verificar containers:

```bash
docker compose ps
```

Parar tudo:

```bash
docker compose down
```

Parar e remover dados locais do PostgreSQL:

```bash
docker compose down -v
```

Acessos principais:

- API Gateway: http://localhost:8080
- Eureka Dashboard: http://localhost:8761
- RabbitMQ Management: http://localhost:15672

## Executando Manualmente

Primeiro suba pelo Docker apenas PostgreSQL e RabbitMQ, ou mantenha o Compose completo desligado para evitar conflito de portas.

Subir infraestrutura:

```bash
docker compose up -d postgres rabbitmq discovery-server
```

Rodar servicos em terminais separados:

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
POSTGRES_PORT=5433 CATALOG_GRPC_PORT=9090 mvn spring-boot:run
```

```bash
cd streaming-service
DB_HOST=localhost DB_PORT=5433 DB_NAME=streaming_db DB_USER=streaming DB_PASSWORD=streaming \
RABBITMQ_HOST=localhost CATALOG_GRPC_ADDRESS=static://localhost:9090 \
mvn spring-boot:run
```

```bash
cd recommendation-service
POSTGRES_PORT=5433 RABBITMQ_HOST=localhost mvn spring-boot:run
```

```bash
cd notification-service
POSTGRES_PORT=5433 RABBITMQ_HOST=localhost mvn spring-boot:run
```

Observacao: o host atual nao possui Maven instalado e o projeto nao possui `mvnw`. Para execucao local sem Dockerfile, instale Maven ou adicione Maven Wrapper.

## Health Checks

Chamadas diretas:

```bash
curl http://localhost:8761/health
curl http://localhost:8080/health
curl http://localhost:8081/health
curl http://localhost:8082/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/health
curl http://localhost:8085/health
```

Pelo gateway:

```bash
curl http://localhost:8080/users/health
curl http://localhost:8080/contents/health
curl http://localhost:8080/recommendations/health
curl http://localhost:8080/notifications/health
```

Observacao: o `streaming-service` atualmente expoe health pelo Actuator em `/actuator/health`; a rota `/streaming/health` ainda nao existe.

## Endpoints REST

### User Service

| Metodo | Rota direta | Rota via gateway | Descricao |
| --- | --- | --- | --- |
| `POST` | `http://localhost:8081/users` | `http://localhost:8080/users` | Cadastra usuario |
| `GET` | `http://localhost:8081/users` | `http://localhost:8080/users` | Lista usuarios |
| `GET` | `http://localhost:8081/users/{id}` | `http://localhost:8080/users/{id}` | Busca usuario por ID |
| `GET` | `http://localhost:8081/users/{id}/exists` | `http://localhost:8080/users/{id}/exists` | Valida existencia |
| `PUT` | `http://localhost:8081/users/{id}` | `http://localhost:8080/users/{id}` | Atualiza usuario |

Exemplo:

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ana Silva",
    "email": "ana@email.com",
    "plan": "PREMIUM"
  }'
```

### Catalog Service

| Metodo | Rota direta | Rota via gateway | Descricao |
| --- | --- | --- | --- |
| `POST` | `http://localhost:8082/contents` | `http://localhost:8080/contents` | Cadastra filme ou serie |
| `GET` | `http://localhost:8082/contents` | `http://localhost:8080/contents` | Lista conteudos |
| `GET` | `http://localhost:8082/contents/{id}` | `http://localhost:8080/contents/{id}` | Busca conteudo por ID |
| `GET` | `http://localhost:8082/contents/category/{category}` | `http://localhost:8080/contents/category/{category}` | Busca por categoria |

Exemplo:

```bash
curl -X POST http://localhost:8080/contents \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Matrix",
    "description": "Ficcao cientifica",
    "category": "Sci-Fi",
    "type": "MOVIE",
    "durationMinutes": 136
  }'
```

### Streaming Service

| Metodo | Rota direta | Rota via gateway | Descricao |
| --- | --- | --- | --- |
| `POST` | `http://localhost:8083/streaming/watch` | `http://localhost:8080/streaming/watch` | Simula reproducao |
| `GET` | `http://localhost:8083/streaming/historico/{userId}` | `http://localhost:8080/streaming/historico/{userId}` | Lista historico do usuario |

Exemplo:

```bash
curl -X POST http://localhost:8080/streaming/watch \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "contentId": 1
  }'
```

### Recommendation Service

| Metodo | Rota direta | Rota via gateway | Descricao |
| --- | --- | --- | --- |
| `GET` | `http://localhost:8084/recommendations/user/{userId}` | `http://localhost:8080/recommendations/user/{userId}` | Lista recomendacoes do usuario |

Exemplo:

```bash
curl http://localhost:8080/recommendations/user/1
```

### Notification Service

| Metodo | Rota direta | Rota via gateway | Descricao |
| --- | --- | --- | --- |
| `GET` | `http://localhost:8085/notifications/user/{userId}` | `http://localhost:8080/notifications/user/{userId}` | Lista notificacoes enviadas |

Exemplo:

```bash
curl http://localhost:8080/notifications/user/1
```

## Fluxo Completo Para Teste

Execute com todos os containers rodando.

1. Criar usuario:

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Review User","email":"review@example.com","plan":"PREMIUM"}'
```

2. Criar conteudo:

```bash
curl -X POST http://localhost:8080/contents \
  -H "Content-Type: application/json" \
  -d '{"title":"Review Filme","description":"Conteudo de teste","category":"Acao","type":"MOVIE","durationMinutes":123}'
```

3. Assistir conteudo:

```bash
curl -X POST http://localhost:8080/streaming/watch \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"contentId":1}'
```

4. Consultar historico:

```bash
curl http://localhost:8080/streaming/historico/1
```

5. Consultar recomendacoes:

```bash
curl http://localhost:8080/recommendations/user/1
```

6. Consultar notificacoes:

```bash
curl http://localhost:8080/notifications/user/1
```

## Evidencias Do Sistema Funcionando

Resultados observados durante validacao:

- Todos os containers principais ficaram `running`.
- PostgreSQL, RabbitMQ e Eureka ficaram `healthy`.
- Eureka registrou `API-GATEWAY`, `USER-SERVICE`, `CATALOG-SERVICE`, `STREAMING-SERVICE`, `RECOMMENDATION-SERVICE` e `NOTIFICATION-SERVICE` como `UP`.
- O fluxo via API Gateway criou usuario e conteudo, registrou visualizacao, gerou recomendacao e gravou notificacao.
- A chamada gRPC direta para `CatalogContentService/GetContentById` retornou conteudo com `id`, `title`, `description`, `category`, `type` e `durationMinutes`.
- RabbitMQ exibiu as filas `recommendation.queue` e `notification.queue` com consumidor ativo.
- Bindings confirmados:
  - `content.exchange -> recommendation.queue` com `content.viewed`.
  - `recommendation.exchange -> notification.queue` com `recommendation.created`.
- Apos o consumo, as filas ficaram com `messages=0`.
- Logs confirmaram:
  - `[gRPC] Querying catalog-service` no `streaming-service`.
  - Publicacao de `content.viewed` no RabbitMQ.
  - Recebimento de `content.viewed` no `recommendation-service`.
  - Publicacao de `recommendation.created`.
  - Recebimento de `recommendation.created` no `notification-service`.
  - Log de notificacao enviada.

Arquivos adicionais de evidencia no repositorio:

- `docs/evidencias/user-service/rest.md`.
- `docs/evidencias/catalog-service/rest.md`.
- `docs/evidencias/grpc/catalog-grpc.md`.
- `docs/evidencias/eureka/servicos-registrados.png`.
- `docs/evidencias/rabbitmq/`.
- `docs/evidencias/notification-service/`.
- `docs/evidencias/api-gateway/`.

## Testes Automatizados

Com Maven disponivel:

```bash
cd user-service && mvn test
cd catalog-service && mvn test
cd streaming-service && mvn test
cd recommendation-service && mvn test
cd notification-service && mvn test
```

Resultado da revisao:

| Servico | Resultado |
| --- | --- |
| `user-service` | 16 testes passaram |
| `catalog-service` | 13 testes passaram |
| `streaming-service` | 1 teste falhou no contexto |
| `recommendation-service` | nao executado na suite completa porque parou no `streaming-service` |
| `notification-service` | nao executado na suite completa porque parou no `streaming-service` |

Falha conhecida do `streaming-service`:

```text
StreamingServiceApplicationTests.contextLoads
Connection to localhost:5433 refused
```

Causa: o teste sobe o contexto real e tenta conectar no PostgreSQL externo. O recomendado e criar perfil de teste com H2 ou mockar dependencias externas.

## Relacao Entre Codigo E Teoria

- Microsservicos: cada dominio foi separado em aplicacoes independentes, com responsabilidade propria e banco separado.
- Service Discovery: o Eureka reduz dependencia direta de IPs para rotas REST e permite registro dinamico dos servicos.
- API Gateway: centraliza o acesso externo e evita que o cliente conheca as portas internas de todos os servicos.
- gRPC: usado para comunicacao sincrona entre `streaming-service` e `catalog-service`, com contrato forte via Protocol Buffers.
- Mensageria: RabbitMQ desacopla a reproducao do processamento de recomendacoes e notificacoes.
- Filas: `recommendation.queue` e `notification.queue` permitem processamento assincrono e tolerancia a atrasos.
- Eventos: `content.viewed` e `recommendation.created` propagam fatos de negocio entre servicos.
- Persistencia por servico: cada servico grava seus proprios dados, evitando acoplamento direto entre modelos internos.

## Decisoes Arquiteturais

- PostgreSQL foi usado como banco principal para a versao final.
- H2 ficou restrito aos testes de alguns servicos.
- RabbitMQ foi configurado com Topic Exchanges para permitir evolucao do roteamento de eventos.
- O gateway usa rotas explicitas em vez de depender apenas do discovery locator automatico.
- O `streaming-service` guarda titulo e categoria no historico para manter a visualizacao autocontida.
- O `notification-service` registra logs de notificacao para evidenciar o consumo do evento.

## Dificuldades Encontradas

- Coordenar a inicializacao dos servicos com dependencias de PostgreSQL, RabbitMQ e Eureka.
- Configurar gRPC e Protocol Buffers em projetos Maven separados.
- Serializar eventos com `LocalDateTime` entre produtores e consumidores RabbitMQ.
- Manter contratos equivalentes entre DTOs de eventos em servicos diferentes.
- Executar testes sem Maven instalado no host e sem Maven Wrapper no repositorio.
- Isolar testes do `streaming-service` das dependencias externas.

## Melhorias Futuras

- Adicionar Maven Wrapper (`mvnw`) na raiz ou em cada servico.
- Corrigir testes do `streaming-service` com perfil `test`, H2 e mocks.
- Adicionar testes para `recommendation-service` e `notification-service` na validacao final.
- Mapear erro gRPC `NOT_FOUND` para HTTP `404` no `streaming-service`.
- Criar endpoint `/streaming/health` ou padronizar todos os servicos com Actuator.
- Remover o endereco gRPC estatico e usar descoberta dinamica quando possivel.
- Adicionar retry, timeout e circuit breaker nas comunicacoes entre servicos.
- Melhorar observabilidade com tracing distribuido e metricas.
- Adicionar autenticacao/autorizacao no Gateway.
- Adicionar colecao Postman/Insomnia com o fluxo completo.

## Checklist De Conformidade

| Item | Status |
| --- | --- |
| `RT01` Arquitetura de microsservicos | Atendido |
| `RT02` Comunicacao gRPC | Atendido com ressalva do endereco estatico |
| `RT03` RabbitMQ para mensageria | Atendido |
| `RT04` Fila de recomendacoes | Atendido |
| `RT05` Publish/Subscribe com eventos | Atendido |
| `RT06` Service Discovery | Parcial, REST via Eureka e gRPC estatico |
| `RT07` README tecnico | Atendido por este documento, com pendencias conhecidas registradas |

## Pendencias Conhecidas

- `streaming-service` nao possui rota `/streaming/health`.
- Teste `StreamingServiceApplicationTests.contextLoads` depende de PostgreSQL externo e falha fora do ambiente esperado.
- Cliente gRPC usa `static://catalog-service:9090` no Docker Compose.
- O erro de conteudo inexistente no fluxo de streaming retorna HTTP `400`; semanticamente deveria retornar `404`.
