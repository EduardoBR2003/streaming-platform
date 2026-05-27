# Requisitos - Plataforma de Streaming

Este documento resume os pontos principais do arquivo `Requisitos Tecnologias Plataforma Streaming.pdf` para orientar a implementacao incremental do monorepo.

## Objetivo

Construir uma plataforma de streaming com arquitetura de microsservicos usando Spring Boot, aplicando conceitos de sistemas distribuidos: service discovery, API Gateway, comunicacao REST, comunicacao gRPC, mensageria com RabbitMQ, filas, eventos e persistencia com PostgreSQL.

## Microsservicos

- `discovery-server`: registro e descoberta dos servicos com Eureka.
- `api-gateway`: entrada unica das APIs REST com Spring Cloud Gateway.
- `user-service`: gerenciamento de usuarios.
- `catalog-service`: gerenciamento do catalogo de filmes e series.
- `streaming-service`: simulacao de reproducao e publicacao futura de eventos de visualizacao.
- `recommendation-service`: processamento futuro de recomendacoes.
- `notification-service`: simulacao futura de notificacoes.

## Fluxo alvo

1. Cliente acessa a plataforma pelo `api-gateway`.
2. O gateway roteia chamadas REST para os microsservicos.
3. O `streaming-service` consulta futuramente o `catalog-service` via gRPC.
4. O `streaming-service` publica futuramente o evento `content.viewed` no RabbitMQ.
5. O `recommendation-service` consome eventos e gera recomendacoes.
6. O `notification-service` simula o envio de notificacoes.

## Requisitos tecnicos

- Java 17 ou superior.
- Spring Boot.
- Maven.
- Spring Cloud Netflix Eureka.
- Spring Cloud Gateway.
- PostgreSQL.
- RabbitMQ com management plugin.
- gRPC e Protocol Buffers em etapa futura.
- Docker Compose para infraestrutura local.

## Escopo desta etapa

Esta etapa cria apenas a base do projeto:

- Estrutura do monorepo.
- Projetos Maven Spring Boot minimos.
- Eureka Server configurado.
- Eureka Client nos demais servicos.
- Gateway com rotas iniciais.
- Configuracoes iniciais de PostgreSQL e RabbitMQ.
- Endpoint `GET /health` em cada aplicacao.

Ficam para etapas futuras:

- Entidades de dominio.
- Controllers de negocio.
- Repositories.
- Services de regra de negocio.
- Contratos e servidores gRPC.
- Eventos RabbitMQ.
- Filas e exchanges.
- Logica de recomendacao.
- Logica de notificacao.
