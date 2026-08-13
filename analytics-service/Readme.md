# Analytics Service

Serviço responsável por expor consultas analíticas e geração de relatórios financeiros da plataforma.

## Status atual

O módulo `analytics-service` possui endpoints REST implementados para resumo financeiro e geração de relatórios em Excel e PDF. O serviço também possui consumer Kafka para o tópico `transaction.created`.

## Responsabilidades do serviço

- consultar resumo financeiro por usuário
- gerar relatório em Excel
- gerar relatório em PDF
- consumir eventos Kafka de transações
- consolidar dados analíticos a partir da base `expenses`

## Stack utilizada

- Java 21
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- PostgreSQL
- Spring Validation
- Apache Kafka
- Apache POI
- OpenPDF
- Springdoc OpenAPI
- Spring Boot Actuator

## Porta e perfis

- Porta HTTP: `8003`
- Nome da aplicação: `analytics-service`
- Perfil padrão: `dev`
- Perfil para Docker: `docker`

## Banco de dados

Banco próprio do serviço:

- `analytics_db`

Configuração padrão no perfil `dev`:

- host: `localhost`
- porta: `5435`
- driver: `org.postgresql.Driver`

Estruturas identificadas no SQL do serviço:

- tabela `expenses`
- tabela `daily_aggregates`

## Como executar localmente

Na raiz do projeto, suba a infraestrutura necessária:

```powershell
Copy-Item ".env.example" ".env" -Force
docker compose up -d zookeeper kafka postgres-analytics
```

Depois inicie o serviço:

```powershell
Set-Location "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api\analytics-service"
mvn spring-boot:run
```

## Como executar com Docker Compose

Na raiz do projeto:

```powershell
Copy-Item ".env.example" ".env" -Force
docker compose up -d --build
```

## Swagger / OpenAPI

Documentação individual do serviço:

- `http://localhost:8003/swagger-ui.html`

Documentação centralizada pelo gateway:

- `http://localhost:8080/swagger-ui.html`

## Endpoints implementados

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/analysis/summary/{userId}` | Retorna resumo financeiro por usuário |
| GET | `/api/analysis/report/excel/{userId}` | Gera relatório Excel |
| GET | `/api/analysis/report/pdf/{userId}` | Gera relatório PDF |

Observação sobre o identificador:

- o parâmetro `{userId}` usa `UUID`
- exemplo: `4967ead6-10b1-450d-af05-7605a1ced37d`

## Exemplo de resposta do resumo

A classe `ExpenseSummary` expõe atualmente os seguintes campos:

- `totalMes`
- `totalAno`
- `totalPorCategoria`

Exemplo de retorno:

```json
{
  "totalMes": 1500.00,
  "totalAno": 1500.00,
  "totalPorCategoria": {
    "FOOD": 300.00,
    "TRANSPORT": 120.00
  }
}
```

## Kafka

Consumer identificado no projeto:

- classe: `TransactionConsumer`
- tópico: `transaction.created`
- consumer group anotado: `analysis-service`

Campos esperados no payload consumido:

- `userId`
- `description`
- `amount`
- `currency`
- `category`
- `type`
- `createdAt`

## Origem dos dados analíticos

No estado atual:

- o resumo usa `ExpenseRepository#getSummaryByUser`
- o PDF consulta `ExpenseRepositoryJpa#findByUserId`
- o Excel está disponível por endpoint e possui geração própria

## Limitações relevantes do estado atual

### Persistência via Kafka

O consumer Kafka valida e transforma o payload recebido, porém **não foi identificada persistência explícita do evento no banco** dentro do fluxo atual do consumer/application service.

### Relatório Excel

O gerador de Excel está implementado e disponível por endpoint, mas o layout atual ainda contém valores demonstrativos fixos em parte do código.

### Segurança

O módulo `analytics-service` não possui dependência de `Spring Security` no `pom.xml` atual. Na prática:

- acesso direto ao serviço em `http://localhost:8003` não está protegido por JWT no próprio módulo
- acesso pelo `api-gateway` em `http://localhost:8080` continua sujeito à autenticação do gateway

### Inconsistência de identificação do usuário

O contrato atual do `analytics-service` foi alinhado para consumir e expor `userId` como `UUID`, em aderência ao padrão já utilizado em `user` e `transaction-service`.

## Testes existentes

- `PdfReportGeneratorTest`
- `ExcelReportGeneratorTest`
- `AnalysisControllerTest`
- `TransactionConsumerTest`

Observação:

- `AnalysisControllerTest` cobre endpoints com `UUID` e valida erro `400` para `userId` inválido

## Observações relevantes do estado atual

- este serviço já possui endpoints ativos e não deve mais ser documentado apenas como planejado
- a documentação centralizada no gateway já está configurada para o OpenAPI do analytics
- existem pontos de evolução pendentes principalmente em persistência do consumer Kafka e qualidade do relatório Excel
