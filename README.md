# Plataforma de Streaming

Monorepo inicial para uma plataforma de streaming baseada em microsservicos, criado para a disciplina de Sistemas Distribuidos.

Nesta etapa o projeto contem apenas a estrutura base: servicos Spring Boot minimos, Eureka Server, API Gateway, configuracoes iniciais de PostgreSQL/RabbitMQ e endpoints simples de health. Regras de negocio, gRPC, filas, eventos e persistencia real ficam para as proximas etapas.

## Arquitetura proposta

Cliente ou Postman acessa o `api-gateway`, que roteia as chamadas REST para os microsservicos registrados no `discovery-server` via Eureka. Nas proximas etapas, o `streaming-service` consultara o `catalog-service` via gRPC e publicara eventos no RabbitMQ para processamento assicrono pelo `recommendation-service` e `notification-service`.

## Microsservicos

- `discovery-server`: Eureka Server para registro e descoberta dos servicos.
- `api-gateway`: entrada unica da plataforma com Spring Cloud Gateway.
- `user-service`: base para cadastro e consulta de usuarios.
- `catalog-service`: base para cadastro e consulta de conteudos.
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
- gRPC e Protocol Buffers previstos para etapa futura

## Portas

| Servico | Porta |
| --- | ---: |
| Eureka Dashboard | 8761 |
| API Gateway | 8080 |
| user-service | 8081 |
| catalog-service | 8082 |
| streaming-service | 8083 |
| recommendation-service | 8084 |
| notification-service | 8085 |
| PostgreSQL | 5432 |
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

```bash
cd api-gateway
mvn spring-boot:run
```

```bash
cd user-service
mvn spring-boot:run
```

```bash
cd catalog-service
mvn spring-boot:run
```

```bash
cd streaming-service
mvn spring-boot:run
```

```bash
cd recommendation-service
mvn spring-boot:run
```

```bash
cd notification-service
mvn spring-boot:run
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
```

## Proximos passos

1. Implementar entidades, repositories e controllers de negocio.
2. Criar contratos `.proto` e comunicacao gRPC entre `streaming-service` e `catalog-service`.
3. Configurar exchanges, filas e eventos RabbitMQ.
4. Publicar o evento `content.viewed` no `streaming-service`.
5. Consumir eventos no `recommendation-service`.
6. Simular notificacoes no `notification-service`.
7. Adicionar testes automatizados e evidencias de execucao.
