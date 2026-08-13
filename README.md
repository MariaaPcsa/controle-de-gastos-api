# Controle de Gastos API

Plataforma de gestão financeira baseada em microserviços, construída com Java 21, Spring Boot, PostgreSQL, Kafka, JWT e Docker.

O repositório está organizado como um projeto Maven multi-módulo e atualmente possui quatro serviços principais:

- `user`
- `transaction-service`
- `analytics-service`
- `api-gateway`

## Arquitetura atual

```text
                     ┌───────────────────┐
                     │    API Gateway    │
                     │      :8080        │
                     └─────────┬─────────┘
                               │
             ┌─────────────────┼─────────────────┐
             │                 │                 │
             ▼                 ▼                 ▼
        user-service     transaction-service   analytics-service
           :8001               :8002               :8003
             │                   │                   │
             ▼                   ▼                   ▼
        PostgreSQL          PostgreSQL          PostgreSQL
         user_db              tx_db            analytics_db
           :5433              :5434               :5435
                                  │
                                  ▼
                               Kafka
                               :29092
                                  │
                                  ▼
                              Zookeeper
                               :22181
```

## Serviços do projeto

### `user`

Responsável por:

- cadastro de usuários
- autenticação com JWT
- listagem e busca de usuários
- atualização, exclusão lógica e reativação
- alteração de role
- importação de usuários via Excel

Documentação específica:

- `user/Readme.md`

### `transaction-service`

Responsável por:

- criação de transações
- listagem com filtros
- atualização e exclusão
- importação de transações via Excel
- conversão de moeda para BRL
- publicação de evento Kafka na criação de transações

Documentação específica:

- `transaction-service/Readme.md`

### `analytics-service`

Responsável por:

- resumo financeiro por usuário
- geração de relatório Excel
- geração de relatório PDF
- consumo de eventos Kafka de transações

Documentação específica:

- `analytics-service/Readme.md`

### `api-gateway`

Responsável por:

- entrada única da plataforma
- validação de JWT no nível de borda
- roteamento para os microsserviços
- agregação de documentação Swagger/OpenAPI
- propagação de identidade via headers internos

Documentação específica:

- `api-gateway/README.md`

## Tecnologias utilizadas

- Java 21
- Spring Boot 3.2.5
- Spring Security
- Spring Web / WebFlux
- Spring Cloud Gateway
- Spring Data JPA
- PostgreSQL
- Apache Kafka
- JWT
- Swagger / OpenAPI
- Docker / Docker Compose
- Maven
- Apache POI
- OpenPDF
- Spring Boot Actuator

## Portas dos serviços

| Componente | Porta |
|-----------|------:|
| API Gateway | `8080` |
| User Service | `8001` |
| Transaction Service | `8002` |
| Analytics Service | `8003` |
| PostgreSQL User | `5433` |
| PostgreSQL Transaction | `5434` |
| PostgreSQL Analytics | `5435` |
| Kafka | `29092` |
| Zookeeper | `22181` |

## Banco de dados

Cada serviço possui seu próprio banco PostgreSQL:

- `user_db`
- `tx_db`
- `analytics_db`

Esse isolamento está refletido no `docker-compose.yml` e nas configurações `application-dev.yml` e `application-docker.yml` de cada módulo.

## Segurança

### Estratégia atual

- o `api-gateway` valida o JWT na borda
- o gateway propaga os headers internos:
  - `X-User`
  - `X-User-Id`
  - `X-User-Role`
- os microsserviços também possuem tratamentos próprios de autenticação/autorização conforme a implementação de cada módulo

### Rotas públicas principais

- `POST /api/auth/register`
- `POST /api/auth/login`
- rotas de Swagger/OpenAPI

### Observação importante

O `analytics-service` não possui Spring Security no módulo atual. Portanto, o acesso direto a `http://localhost:8003` não está protegido por JWT no próprio serviço, embora o acesso pelo gateway continue sujeito à autenticação do gateway.

## Endpoints principais via gateway

Base principal:

- `http://localhost:8080`

### Autenticação e usuários

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/users`
- `GET /api/users/{id}`
- `POST /api/users`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`
- `PATCH /api/users/{id}/role`
- `PATCH /api/users/{id}/reactivate`
- `POST /api/excel/upload`
- `POST /api/excel/errors/download`

### Transações

- `POST /api/transactions`
- `GET /api/transactions`
- `PUT /api/transactions/{id}`
- `DELETE /api/transactions/{id}`
- `POST /api/transactions/upload`

### Analytics

- `GET /api/analysis/summary/{userId}`
- `GET /api/analysis/report/excel/{userId}`
- `GET /api/analysis/report/pdf/{userId}`

## Swagger / OpenAPI

### Documentação centralizada

- `http://localhost:8080/swagger-ui.html`

### Documentação individual

- `http://localhost:8001/swagger-ui.html`
- `http://localhost:8002/swagger-ui.html`
- `http://localhost:8003/swagger-ui.html`

### OpenAPI agregado pelo gateway

- `http://localhost:8080/user/v3/api-docs`
- `http://localhost:8080/transaction/v3/api-docs`
- `http://localhost:8080/analytics/v3/api-docs`

## Kafka

### Situação atual identificada no código

- producer em `transaction-service` para o tópico `transaction.created`
- consumer em `analytics-service` para o tópico `transaction.created`
- producer em `user` para o tópico `users`

### Observação importante

Foram identificadas inconsistências técnicas em contratos Kafka do estado atual, especialmente na representação de `userId` entre `transaction-service` e `analytics-service`.

## Como executar com Docker Compose

Pré-requisito:

- copiar `.env.example` para `.env`

```powershell
Set-Location "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api"
Copy-Item ".env.example" ".env" -Force
docker compose up -d --build
```

Para verificar o status:

```powershell
docker compose ps
```

Para acompanhar logs:

```powershell
docker compose logs api-gateway --tail=100
docker compose logs user --tail=100
docker compose logs transaction-service --tail=100
docker compose logs analytics-service --tail=100
```

Se houver problemas com volumes antigos do PostgreSQL:

```powershell
docker compose down -v
docker compose up -d --build
```

## Como executar localmente

### 1. Subir infraestrutura compartilhada

```powershell
Set-Location "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api"
Copy-Item ".env.example" ".env" -Force
docker compose up -d zookeeper kafka postgres-user postgres-tx postgres-analytics
```

### 2. Subir os serviços

Em terminais separados:

```powershell
Set-Location "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api\user"
mvn spring-boot:run
```

```powershell
Set-Location "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api\transaction-service"
mvn spring-boot:run
```

```powershell
Set-Location "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api\analytics-service"
mvn spring-boot:run
```

```powershell
Set-Location "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api\api-gateway"
mvn spring-boot:run
```

## Build do projeto

Na raiz:

```powershell
Set-Location "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api"
mvn clean package
```

## Testes

### Módulos com testes identificados

- `user`
- `transaction-service`
- `analytics-service`

### Situação atual observada

- `user` possui testes de controller, domínio e persistência
- `transaction-service` possui testes unitários, controller e integração com Kafka embutido
- `analytics-service` possui testes de geração de relatório e classe de teste de controller
- `api-gateway` não possui testes automatizados visíveis em `src/test/java`

Para rodar os testes do projeto inteiro:

```powershell
Set-Location "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api"
mvn test
```

## Observabilidade

Itens identificados no projeto atual:

- Spring Boot Actuator
- endpoint de health/info/metrics no gateway
- logs configurados em nível `INFO` para gateway e security
- healthcheck no `docker-compose.yml` para Kafka e bancos PostgreSQL

## Arquivos auxiliares úteis

- `.env.example`
- `docker-compose.yml`
- `scripts/up.ps1`
- `scripts/create-secrets.ps1`
- `scripts/init-topics.sh`
- `postman/collection.json`
- `insomnia/finance-api-collection.json`

## Problemas e limitações já identificados

- inconsistência entre tipos de `userId` em partes do domínio e integrações
- inconsistências de contrato Kafka entre `transaction-service` e `analytics-service`
- documentação anterior muito divergente do código atual
- cobertura de testes desigual entre os módulos
- ausência de testes automatizados no `api-gateway`
- `analytics-service` com lacunas de segurança quando acessado diretamente

## Observação final

Este repositório deve ser tratado como uma plataforma em evolução incremental.

A fonte primária de verdade do sistema é:

1. implementação atual
2. testes existentes
3. documentação dos módulos
4. configurações e `docker-compose.yml`

Para detalhes operacionais de cada serviço, consulte também:

- `user/Readme.md`
- `transaction-service/Readme.md`
- `analytics-service/Readme.md`
- `api-gateway/README.md`
