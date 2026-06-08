# Execucao com Docker

Este projeto pode ser executado com Docker Compose, subindo a infraestrutura e os microsservicos.

## Subir tudo

Antes de subir pelo Docker, pare os servicos Spring que estiverem rodando no terminal ou na IDE para evitar conflito de portas.

```bash
docker compose up --build
```

Para rodar em segundo plano:

```bash
docker compose up --build -d
```

## Acessos

```text
API Gateway: http://localhost:8080
Eureka:      http://localhost:8761
RabbitMQ:    http://localhost:15672
```

Credenciais do RabbitMQ:

```text
usuario: streaming
senha: streaming
```

## Testes rapidos

```bash
curl http://localhost:8080/health
curl http://localhost:8080/users/health
curl http://localhost:8080/contents/health
curl http://localhost:8080/recommendations/health
curl http://localhost:8080/notifications/health
```

## Parar tudo

```bash
docker compose down
```

Para apagar tambem os dados do PostgreSQL:

```bash
docker compose down -v
```

## Observacoes

- Dentro do Docker, os servicos se comunicam pelos nomes do Compose: `postgres`, `rabbitmq`, `discovery-server` e `catalog-service`.
- O `api-gateway` continua sendo a entrada principal pela porta `8080`.
- O `catalog-service` expoe REST na porta `8082` e gRPC na porta `9090`.
