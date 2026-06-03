# Evidencia - User Service REST

Responsavel: Integrante 1

## Escopo implementado

- `POST /users`
- `GET /users`
- `GET /users/{id}`
- `GET /users/{id}/exists`
- `PUT /users/{id}`
- Persistencia na tabela `users` do banco `user_db`
- Validacao de nome, email e plano
- Bloqueio de email duplicado
- Registro do `user-service` no Eureka

## Exemplo de cadastro

```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ana Silva",
    "email": "ana@email.com",
    "plan": "PREMIUM"
  }'
```

Resposta esperada:

```json
{
  "id": 1,
  "name": "Ana Silva",
  "email": "ana@email.com",
  "plan": "PREMIUM",
  "createdAt": "2026-06-03T10:00:00",
  "updatedAt": "2026-06-03T10:00:00"
}
```

## Consultas

```bash
curl http://localhost:8081/users
curl http://localhost:8081/users/1
curl http://localhost:8081/users/1/exists
```

## Atualizacao

```bash
curl -X PUT http://localhost:8081/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ana Souza",
    "email": "ana@email.com",
    "plan": "FAMILY"
  }'
```

## Acesso pelo API Gateway

Com Eureka, API Gateway e `user-service` em execucao, as mesmas rotas ficam disponiveis pela porta `8080`:

```bash
curl http://localhost:8080/users
curl http://localhost:8080/users/1
curl http://localhost:8080/users/1/exists
```

## Evidencia automatizada

Executado em 03/06/2026:

```text
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Testes relacionados:

- `UserControllerTest`
- `UserServiceTest`
- `UsuarioRepositoryTest`
