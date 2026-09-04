# Transaction Service

Serviço responsável pelo gerenciamento de transações financeiras da plataforma.

## Status atual

O módulo `transaction-service` já está implementado e oferece criação, listagem com filtros, atualização, exclusão e importação de transações via Excel. O serviço também publica evento Kafka na criação de transações.

## Responsabilidades do serviço

- criar transações financeiras
- listar transações do usuário autenticado
- filtrar transações por categoria, tipo e período
- ordenar resultados
- atualizar transações
- excluir transações
- importar transações via planilha XLSX
- converter valores para BRL quando a moeda informada não for BRL
- publicar evento Kafka de criação de transação

## Stack utilizada

- Java 21
- Spring Boot 3.2.5
- Spring Web
- Spring Security
- JWT
- Spring Data JPA
- PostgreSQL
- Spring Validation
- Apache Kafka
- Apache POI
- Springdoc OpenAPI
- Spring Boot Actuator

## Porta e perfis

- Porta HTTP: `8002`
- Nome da aplicação: `transaction-service`
- Perfil padrão: `dev`
- Perfil para Docker: `docker`

## Banco de dados

Banco próprio do serviço:

- `tx_db`

Configuração padrão no perfil `dev`:

- host: `localhost`
- porta: `5434`
- driver: `org.postgresql.Driver`

## Como executar localmente

Na raiz do projeto, suba a infraestrutura necessária:

```powershell
Copy-Item ".env.example" ".env" -Force
docker compose up -d zookeeper kafka postgres-tx
```

Depois inicie o serviço:

```powershell
Set-Location "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api\transaction-service"
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

- `http://localhost:8002/swagger-ui.html`

Documentação centralizada pelo gateway:

- `http://localhost:8080/swagger-ui.html`

## Segurança

As rotas de transação exigem autenticação JWT e aceitam perfis `USER` e `ADMIN`.

Rotas públicas identificadas na configuração atual:

- `/v3/api-docs/**`
- `/swagger-ui/**`
- `/actuator/**`
- `/h2-console/**`

## Endpoints implementados

| Método | Rota | Descrição | Status esperados |
|--------|------|-----------|------------------|
| POST | `/api/transactions` | Criar transação | `201 Created`, `400 Bad Request`, `401 Unauthorized` |
| GET | `/api/transactions` | Listar transações do usuário autenticado | `200 OK`, `401 Unauthorized` |
| GET | `/api/transactions/{id}` | Consultar transação por ID do usuário autenticado | `200 OK`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |
| PUT | `/api/transactions/{id}` | Atualizar transação | `200 OK`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |
| DELETE | `/api/transactions/{id}` | Excluir transação | `204 No Content`, `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |
| POST | `/api/transactions/upload` | Importar transações via XLSX | `200 OK`, `400 Bad Request`, `401 Unauthorized` |


## Filtros disponíveis na listagem

A rota `GET /api/transactions` aceita os seguintes parâmetros:

- `category`
- `type`
- `startDate`
- `endDate`
- `page`
- `pageSize`
- `sortBy`
- `sortDirection`

Exemplo:

```http
GET /api/transactions?category=FOOD&type=WITHDRAW&page=0&pageSize=10&sortBy=createdAt&sortDirection=DESC
```

## Exemplo de criação

```json
{
  "amount": 50.00,
  "currency": "BRL",
  "type": "WITHDRAW",
  "category": "FOOD",
  "description": "Almoço"
}
```

## Tipos de transação identificados no código

- `DEPOSIT`
- `WITHDRAW`
- `TRANSFER`
- `PURCHASE`

## Regras visíveis no código atual

- valor deve ser maior que zero
- descrição é obrigatória
- categoria é obrigatória
- moeda é obrigatória
- tipo é obrigatório
- usuário autenticado é obrigatório

## Conversão de moeda

Quando a moeda informada não é `BRL`, o serviço usa `ExchangeRateClient` para converter o valor antes de persistir a transação.

Variáveis configuráveis no ambiente Docker:

- `BRASILAPI_URL`
- `MOCKBANK_URL`

## Importação via Excel

Endpoint:

- `POST /api/transactions/upload`

Formato lido pela implementação atual:

1. `occurredAt`
2. `type`
3. `amount`
4. `currency`
5. `category`
6. `description`

Observação importante:

- o `userId` não é mais lido da planilha
- a importação utiliza o usuário autenticado no request

## Kafka

Producer identificado no projeto:

- classe: `KafkaTransactionProducer`
- tópico padrão: `transaction.created`

Payload publicado na criação inclui campos como:

- `transactionId`
- `userId`
- `amount`
- `originalAmount`
- `currency`
- `type`
- `category`
- `description`
- `createdAt`

## Testes existentes

- `CreateTransactionUseCaseTest`
- `UpdateTransactionUseCaseTest`
- `DeleteTransactionUseCaseTest`
- `ListTransactionsUseCaseTest`
- `TransactionApplicationServiceTest`
- `TransactionControllerTest`
- `JwtTokenProviderTest`
- `TransactionImportIntegrationTest`

## dicas para verificar tabela de transações no banco

```sql
Entrar no banco:
docker exec -it controle-de-gastos-api-main-postgres-tx-1 psql -
U postgres -d tx_db
Você verá:
tx_db=#
Listar tabelas
\dt
Esperamos:
public | transactions | table | postgres
Ver estrutura da tabela
\d transactions
Ver quantidade de transações
SELECT COUNT(*) FROM transactions;
Ver transações sem expor detalhes sensíveis
SELECT id, user_id, amount, currency, type, category, description, created_at
FROM transactions;
Sair
\q
Sair




    
## Observações relevantes do estado atual

- a API usa `UUID` para identificação de transações
- o contrato real do serviço deve ser considerado a partir do controller, DTOs e testes
- há suporte a importação Excel e publicação Kafka já em uso no código
- o arquivo `sql/init.sql` ainda declara `user_id` como `BIGINT`, enquanto o domínio Java utiliza `UUID`, o que caracteriza uma inconsistência técnica a ser tratada separadamente
