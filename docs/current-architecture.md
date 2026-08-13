# Current Architecture

Documento de discovery da arquitetura atual do projeto `controle-de-gastos-api`, baseado prioritariamente no código-fonte, configurações e testes existentes.

## 6.1 Serviços encontrados

Serviços identificados no repositório Maven multi-módulo:

- `user`
- `transaction-service`
- `analytics-service`
- `api-gateway`

## 6.2 Responsabilidade de cada serviço

### `user`

Responsável por:

- registro de usuários
- autenticação com JWT
- listagem de usuários
- busca por ID
- criação de usuários autenticada
- atualização de usuários
- exclusão lógica
- reativação de usuários
- alteração de role
- importação de usuários via Excel
- geração de arquivo de erros da importação

Camadas principais identificadas:

- controllers: `AuthController`, `UserController`, `ExcelController`
- service: `UserApplicationService`
- repositório de domínio: `UserRepository`
- adapter JPA: `UserRepositoryJpaAdapter`
- JPA repository: `UserRepositoryJpa`

### `transaction-service`

Responsável por:

- criação de transações
- listagem de transações do usuário autenticado
- filtros por categoria, tipo e período
- ordenação e paginação em memória no retorno da listagem
- atualização de transações
- exclusão de transações
- importação de transações via Excel
- conversão de moeda para BRL antes da persistência
- publicação de evento Kafka na criação de transações

Camadas principais identificadas:

- controller: `TransactionController`
- service: `TransactionApplicationService`
- use cases: `CreateTransactionUseCase`, `UpdateTransactionUseCase`, `DeleteTransactionUseCase`, `ListTransactionsUseCase`
- repositório de domínio: `TransactionRepository`
- adapter JPA: `TransactionRepositoryImpl`
- JPA repository: `TransactionRepositoryJpa`
- producer Kafka: `KafkaTransactionProducer`
- importador Excel: `TransactionExcelImporter`
- integração externa: `ExchangeRateClient`

### `analytics-service`

Responsável por:

- resumo financeiro por usuário
- geração de relatório Excel
- geração de relatório PDF
- consumo de eventos Kafka de transações
- consulta da base analítica `expenses`

Camadas principais identificadas:

- controller: `AnalysisController`
- service: `AnalysisApplicationService`
- repositório de domínio: `ExpenseRepository`
- JPA repository: `ExpenseRepositoryJpa`
- consumer Kafka: `TransactionConsumer`
- geração de relatórios: `ExcelReportGenerator`, `PdfReportGenerator`

### `api-gateway`

Responsável por:

- ponto único de entrada externo
- roteamento para os microsserviços
- validação de JWT no nível de borda
- propagação de identidade via headers internos
- agregação centralizada de OpenAPI/Swagger
- filtros de headers e CORS

Componentes principais identificados:

- aplicação: `GatewayApplication`
- segurança reativa: `SecurityConfig`
- filtro global JWT: `JwtFilter`
- rotas configuradas em `application-dev.yml` e `application-docker.yml`

## 6.3 Banco utilizado por cada serviço

### `user`

- banco: `user_db`
- PostgreSQL
- porta local no Docker Compose: `5433`
- tabela observada no SQL: `users`

### `transaction-service`

- banco: `tx_db`
- PostgreSQL
- porta local no Docker Compose: `5434`
- tabela observada no SQL: `transactions`

### `analytics-service`

- banco: `analytics_db`
- PostgreSQL
- porta local no Docker Compose: `5435`
- tabelas observadas no SQL:
  - `expenses`
  - `daily_aggregates`

### `api-gateway`

- não utiliza banco próprio

## 6.4 Comunicação síncrona

### Entrada externa principal

- clientes chamam o `api-gateway` em `:8080`
- o gateway roteia para:
  - `user` em `:8001`
  - `transaction-service` em `:8002`
  - `analytics-service` em `:8003`

### Roteamento no gateway

No perfil `dev`:

- `/api/users/**`, `/api/auth/**`, `/api/excel/**` -> `http://localhost:8001`
- `/api/transactions/**` -> `http://localhost:8002`
- `/api/analysis/**` -> `http://localhost:8003`

No perfil `docker`:

- `/api/users/**`, `/api/auth/**`, `/api/excel/**` -> `http://user:8001`
- `/api/transactions/**` -> `http://transaction-service:8002`
- `/api/analysis/**` -> `http://analytics-service:8003`

### Integrações HTTP externas identificadas

#### `transaction-service`

- client: `ExchangeRateClient`
- tecnologia: `RestTemplate`
- destino observado no código: Brasil API
- uso: converter valores para BRL quando a moeda recebida não for `BRL`

#### `analytics-service`

- client: `BrasilApiExchangeRateClient`
- tecnologia: `RestTemplate`
- perfil: `prod`
- interface: `ExchangeRateService`
- observação: não foi identificado uso desse client nos fluxos principais do controller atual

### Comunicação síncrona serviço-a-serviço

Não foram identificados clients HTTP internos entre `user`, `transaction-service` e `analytics-service`. A comunicação interna síncrona observada é mediada pelo `api-gateway`.

## 6.5 Comunicação assíncrona

### Fluxo principal identificado

- `transaction-service` publica evento no Kafka ao criar transação
- `analytics-service` consome o tópico de criação de transação

### Observação importante

O consumer atual do `analytics-service` valida e transforma o payload, mas não há persistência explícita do evento no banco dentro do fluxo analisado.

## 6.6 Kafka existente

### Producers identificados

#### `transaction-service`

- classe: `KafkaTransactionProducer`
- tópico padrão: `transaction.created`
- chave enviada: `tx.getId().toString()`
- payload: JSON serializado com `ObjectMapper`
- conteúdo observado:
  - `transactionId`
  - `userId`
  - `amount`
  - `originalAmount`
  - `currency`
  - `type`
  - `category`
  - `description`
  - `createdAt`

#### `user`

- classe: `UserProducer`
- tópico: `users`
- payload: `UserResponseDTO` serializado em JSON

### Consumers identificados

#### `analytics-service`

- classe: `TransactionConsumer`
- anotação: `@KafkaListener(topics = "transaction.created", groupId = "analysis-service")`
- DTO esperado: `TransactionEventDTO`
- campos esperados:
  - `userId` (`Long`)
  - `description`
  - `amount`
  - `currency`
  - `category`
  - `type`
  - `createdAt`

### Configuração Kafka observada

#### `transaction-service`

- bootstrap server configurável por `spring.kafka.bootstrap-servers`
- producer com:
  - `StringSerializer` para chave
  - `StringSerializer` para valor
  - `acks=all`
  - `linger.ms=5`
  - `retries=3`

#### `analytics-service`

- consumer com:
  - `StringDeserializer` para chave
  - `StringDeserializer` para valor
  - `auto-offset-reset=earliest`
  - `group-id` configurável por propriedade

### Conflitos Kafka identificados

- o producer de transação envia `userId` derivado de `UUID`
- o consumer do analytics espera `userId` como `Long`
- o `application-docker.yml` do analytics informa `group-id: analytics-group`, enquanto o listener anota `groupId = "analysis-service"`

## 6.7 Segurança existente

## `api-gateway`

- `Spring Security WebFlux`
- `JwtFilter` é o mecanismo efetivo de autenticação na borda
- rotas públicas no filtro:
  - `/api/auth/**`
  - `/swagger-ui.html`
  - `/swagger-ui/**`
  - `/v3/api-docs`
  - `/v3/api-docs/**`
  - `/user/v3/api-docs`
  - `/transaction/v3/api-docs`
  - `/analytics/v3/api-docs`
- para demais rotas, JWT é obrigatório
- quando válido, o gateway propaga:
  - `X-User`
  - `X-User-Id`
  - `X-User-Role`

## `user`

- `Spring Security` com `SecurityFilterChain`
- filtro: `JwtAuthenticationFilter`
- rotas públicas:
  - `/api/auth/**`
  - rotas Swagger/OpenAPI
- rotas `/api/**` requerem autenticação
- autenticação baseada em JWT no próprio serviço
- tratamento global de exceção identificado em `GlobalExceptionHandler`

### Observação

Há `@PreAuthorize("hasRole('ADMIN')")` em `ExcelController`, mas não foi identificada configuração explícita de `@EnableMethodSecurity` ou equivalente durante esta análise.

## `transaction-service`

- `Spring Security` com `SecurityFilterChain`
- filtro: `JwtAuthenticationFilter`
- rotas públicas:
  - `/api/auth/**`
  - `/h2-console/**`
  - `/v3/api-docs/**`
  - `/swagger-ui/**`
  - `/actuator/**`
- rotas `/api/transactions/**` exigem `ROLE_USER` ou `ROLE_ADMIN`
- handler customizado para:
  - `401` -> `{"error":"Não autenticado"}`
  - `403` -> `{"error":"Acesso negado"}`
- tratamento global de exceção identificado em `GlobalExceptionHandler`

## `analytics-service`

- não possui dependência de `Spring Security` no `pom.xml`
- endpoints do serviço, quando acessados diretamente em `:8003`, não estão protegidos por JWT no próprio módulo
- via gateway, as rotas passam pelo filtro JWT do `api-gateway`

## 6.8 Rotas existentes

## Inventário consolidado

### `user`

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

### `transaction-service`

- `POST /api/transactions`
- `GET /api/transactions`
- `PUT /api/transactions/{id}`
- `DELETE /api/transactions/{id}`
- `POST /api/transactions/upload`

### `analytics-service`

- `GET /api/analysis/summary/{userId}`
- `GET /api/analysis/report/excel/{userId}`
- `GET /api/analysis/report/pdf/{userId}`

## Mapeamento detalhado — `UserController`

### `GET /api/users/{id}`

- request:
  - path variable `id: UUID`
  - headers obrigatórios no fluxo atual: `X-User-Id`, `X-User`, `X-User-Role`
- response:
  - `UserResponseDTO`
- status observados:
  - `200 OK`
  - `400 Bad Request` em erros de regra/execução
  - `404 Not Found` quando usuário não existe
- validações/regras:
  - usuário autenticado obrigatório
  - `USER` só pode acessar o próprio ID
  - `ADMIN` pode acessar outros usuários
  - usuário alvo precisa estar ativo
- autenticação:
  - depende dos headers propagados pelo gateway
- autorização:
  - aplicada no use case `FindUserByIdUseCase`
- service utilizado:
  - `UserApplicationService#findById`
- repository utilizado:
  - `UserRepository` -> `UserRepositoryJpaAdapter` -> `UserRepositoryJpa`

### `GET /api/users`

- request:
  - headers obrigatórios: `X-User-Id`, `X-User`, `X-User-Role`
- response:
  - `List<UserResponseDTO>`
- status observados:
  - `200 OK`
  - `400 Bad Request` em erros de autenticação/regra
- validações/regras:
  - usuário autenticado obrigatório
  - `ADMIN` vê todos os usuários
  - usuário comum vê apenas o próprio registro
  - usuário comum precisa estar ativo
- autenticação:
  - depende dos headers propagados pelo gateway
- autorização:
  - aplicada em `ListUsersUseCase`
- service utilizado:
  - `UserApplicationService#list`
- repository utilizado:
  - `UserRepository` -> `UserRepositoryJpaAdapter` -> `UserRepositoryJpa`

### `POST /api/users`

- request:
  - body `UserRequestDTO`
  - headers obrigatórios: `X-User-Id`, `X-User`, `X-User-Role`
- response:
  - `UserResponseDTO`
- status observados:
  - `200 OK`
  - `400 Bad Request`
  - `409 Conflict` em duplicidade de email
- validações/regras:
  - email válido
  - senha obrigatória
  - criptografia de senha no `CreateUserUseCase`
- autenticação:
  - depende dos headers propagados pelo gateway
- autorização:
  - o controller exige identidade propagada, mas não há verificação explícita de `ADMIN` nesse endpoint
- service utilizado:
  - `UserApplicationService#create`
- repository utilizado:
  - `UserRepository` -> `UserRepositoryJpaAdapter` -> `UserRepositoryJpa`

### `PUT /api/users/{id}`

- request:
  - path variable `id: UUID`
  - body `UserRequestDTO`
  - headers obrigatórios: `X-User-Id`, `X-User`, `X-User-Role`
- response:
  - `UserResponseDTO`
- status observados:
  - `200 OK`
  - `400 Bad Request`
- validações/regras:
  - usuário autenticado obrigatório
  - `ADMIN` pode atualizar qualquer usuário
  - usuário comum só pode atualizar o próprio registro
  - alteração de `type` só ocorre se o requisitante for `ADMIN`
  - senha recebida é recriptografada
- autenticação:
  - depende dos headers propagados pelo gateway
- autorização:
  - aplicada em `UpdateUserUseCase`
- service utilizado:
  - `UserApplicationService#update`
- repository utilizado:
  - `UserRepository` -> `UserRepositoryJpaAdapter` -> `UserRepositoryJpa`

### `DELETE /api/users/{id}`

- request:
  - path variable `id: UUID`
  - headers obrigatórios: `X-User-Id`, `X-User`, `X-User-Role`
- response:
  - sem corpo
- status observados:
  - `204 No Content`
  - `400 Bad Request`
- validações/regras:
  - apenas `ADMIN` pode deletar
  - exclusão lógica: `active=false`
  - se já estiver inativo, o fluxo retorna sem erro adicional
- autenticação:
  - depende dos headers propagados pelo gateway
- autorização:
  - aplicada em `DeleteUserUseCase`
- service utilizado:
  - `UserApplicationService#delete`
- repository utilizado:
  - `UserRepository` -> `UserRepositoryJpaAdapter` -> `UserRepositoryJpa`

### `PATCH /api/users/{id}/role`

- request:
  - path variable `id: UUID`
  - body `Map<String, String>` com chave `type`
  - headers obrigatórios: `X-User-Id`, `X-User`, `X-User-Role`
- response:
  - `UserResponseDTO`
- status observados:
  - `200 OK`
  - `400 Bad Request`
- validações/regras:
  - `type` convertido com `UserType.valueOf(...)`
  - apenas `ADMIN` pode alterar role
  - usuário alvo precisa estar ativo
- autenticação:
  - depende dos headers propagados pelo gateway
- autorização:
  - aplicada em `UpdateUserRoleUseCase`
- service utilizado:
  - `UserApplicationService#updateRole`
- repository utilizado:
  - `UserRepository` -> `UserRepositoryJpaAdapter` -> `UserRepositoryJpa`

### `PATCH /api/users/{id}/reactivate`

- request:
  - path variable `id: UUID`
  - headers obrigatórios: `X-User-Id`, `X-User`, `X-User-Role`
- response:
  - `UserResponseDTO`
- status observados:
  - `200 OK`
  - `400 Bad Request`
- validações/regras:
  - apenas `ADMIN` pode reativar
  - usuário precisa estar inativo
- autenticação:
  - depende dos headers propagados pelo gateway
- autorização:
  - aplicada em `ReactivateUserUseCase`
- service utilizado:
  - `UserApplicationService#reactivate`
- repository utilizado:
  - `UserRepository` -> `UserRepositoryJpaAdapter` -> `UserRepositoryJpa`

## Mapeamento complementar — `AuthController`

### `POST /api/auth/register`

- request:
  - body `UserRequestDTO`
- response:
  - `UserResponseDTO` ou payload de erro
- status observados:
  - `200 OK`
  - `400 Bad Request`
  - `409 Conflict`
- validações/regras:
  - email e senha obrigatórios no controller
  - role forçada para `USER` no controller
  - email validado no use case
  - senha criptografada antes da persistência
- autenticação:
  - pública
- service utilizado:
  - `UserApplicationService#create`
- repository utilizado:
  - `UserRepository` -> `UserRepositoryJpaAdapter` -> `UserRepositoryJpa`

### `POST /api/auth/login`

- request:
  - body `AuthRequestDTO`
- response:
  - `JwtResponseDTO` ou payload `{"error": ...}`
- status observados:
  - `200 OK`
  - `401 Unauthorized` para credenciais inválidas
  - `403 Forbidden` para usuário inativo
- validações/regras:
  - usuário buscado por email
  - senha validada com `PasswordEncoder.matches`
  - gera token via `JwtService`
- autenticação:
  - pública
- service utilizado:
  - `UserApplicationService#findByEmail`
- repository utilizado:
  - `UserRepository` -> `UserRepositoryJpaAdapter` -> `UserRepositoryJpa`

## Mapeamento complementar — `ExcelController`

### `POST /api/excel/upload`

- request:
  - multipart `file`
- response:
  - `UserImportResult`
- status observados:
  - `200 OK`
  - `400 Bad Request` quando arquivo ausente/vazio
- validações/regras:
  - anotado com `@PreAuthorize("hasRole('ADMIN')")`
  - colunas lidas: nome, email, senha
  - valida campos obrigatórios, email, senha mínima e duplicidade
- autenticação:
  - protegida
- autorização:
  - tentativa de restrição por role `ADMIN`
- service utilizado:
  - `UserExcelImporter` / `UserApplicationService`
- repository utilizado:
  - `UserRepository` -> `UserRepositoryJpaAdapter` -> `UserRepositoryJpa`

### `POST /api/excel/errors/download`

- request:
  - body `UserImportResult`
- response:
  - `byte[]` com arquivo XLSX
- status observados:
  - `200 OK`
  - `400 Bad Request` quando não há erros para exportar
- validações/regras:
  - anotado com `@PreAuthorize("hasRole('ADMIN')")`
- autenticação:
  - protegida
- autorização:
  - tentativa de restrição por role `ADMIN`
- service utilizado:
  - `UserImportErrorExporter`
- repository utilizado:
  - não acessa repositório diretamente

## Mapeamento detalhado — `TransactionController`

### `POST /api/transactions`

- request:
  - body `TransactionRequestDTO`
  - principal autenticado `CustomUserDetails`
- response:
  - `TransactionResponseDTO`
- status observados:
  - `201 Created`
  - `400 Bad Request`
  - `401 Unauthorized`
- validações:
  - `amount` obrigatório e `> 0`
  - `currency` obrigatória
  - `type` obrigatório
  - `category` obrigatória
  - `description` obrigatória
  - principal autenticado obrigatório
- autenticação:
  - JWT obrigatório no serviço/gateway
- autorização:
  - `ROLE_USER` ou `ROLE_ADMIN`
- regras de negócio:
  - conversão para BRL se moeda != `BRL`
  - geração de `UUID` para a transação
- persistência:
  - `TransactionApplicationService#create` -> `CreateTransactionUseCase` -> `TransactionRepository`
- eventos Kafka:
  - publica `transaction.created` após persistência
  - falha no Kafka não desfaz a criação da transação

### `GET /api/transactions`

- request:
  - principal autenticado `CustomUserDetails`
  - query params opcionais: `category`, `type`, `startDate`, `endDate`, `page`, `pageSize`, `sortBy`, `sortDirection`
- response:
  - `PagedResponseDTO<TransactionResponseDTO>`
- status observados:
  - `200 OK`
  - `401 Unauthorized`
  - `400 Bad Request` para tipo inválido ou filtros inválidos
- validações:
  - principal autenticado obrigatório
  - `type` é convertido para `TransactionType`
- autenticação:
  - JWT obrigatório no serviço/gateway
- autorização:
  - `ROLE_USER` ou `ROLE_ADMIN`
- regras de negócio:
  - filtros aplicados em memória sobre lista do usuário
  - ordenação por `createdAt` ou `amount`
  - total retornado usa `result.size()` após filtros
- persistência:
  - leitura via `TransactionRepository#findByUserId`
- eventos Kafka:
  - não publica

### `PUT /api/transactions/{id}`

- request:
  - path variable `id: UUID`
  - body `UpdateTransactionDTO`
  - principal autenticado `CustomUserDetails`
- response:
  - `TransactionResponseDTO`
- status observados:
  - `200 OK`
  - `400 Bad Request`
  - `401 Unauthorized`
  - `403 Forbidden` esperado pelo contrato, mas depende do tratamento de `SecurityException`
  - `404 Not Found` documentado em anotação OpenAPI, porém o fluxo atual lança `IllegalArgumentException` para inexistência
- validações:
  - campos do DTO obrigatórios
  - principal autenticado obrigatório
- autenticação:
  - JWT obrigatório no serviço/gateway
- autorização:
  - proprietário da transação apenas
- regras de negócio:
  - revalidação do payload
  - conversão de moeda para BRL
  - `originalAmount` cai para `amount` quando ausente
- persistência:
  - `TransactionRepository#findById` + `save`
- eventos Kafka:
  - não publica evento de atualização

### `DELETE /api/transactions/{id}`

- request:
  - path variable `id: UUID`
  - principal autenticado `CustomUserDetails`
  - query param `confirmDelete` default `true`
- response:
  - sem corpo
- status observados:
  - `204 No Content`
  - `400 Bad Request`
  - `401 Unauthorized`
  - `403 Forbidden` esperado por regra de propriedade, mas depende do tratamento de `SecurityException`
  - `404 Not Found` documentado em OpenAPI, porém o fluxo atual lança `IllegalArgumentException` para inexistência
- validações:
  - principal autenticado obrigatório
  - `confirmDelete` precisa ser `true`
- autenticação:
  - JWT obrigatório no serviço/gateway
- autorização:
  - proprietário da transação apenas
- regras de negócio:
  - não permite deletar transações com mais de 240 dias
- persistência:
  - `TransactionRepository#findById` + `deleteById`
- eventos Kafka:
  - não publica evento de remoção

### `POST /api/transactions/upload`

- request:
  - multipart `file`
  - principal autenticado `CustomUserDetails`
- response:
  - `ImportResultDTO`
- status observados:
  - `200 OK`
  - `400 Bad Request`
  - `401 Unauthorized`
  - `501 Not Implemented` se `TransactionExcelImporter` estiver indisponível
- validações:
  - arquivo obrigatório
  - principal autenticado obrigatório
- autenticação:
  - JWT obrigatório no serviço/gateway
- autorização:
  - `ROLE_USER` ou `ROLE_ADMIN`
- regras de negócio:
  - `userId` vem do usuário autenticado
  - colunas lidas: `occurredAt`, `type`, `amount`, `currency`, `category`, `description`
  - linhas inválidas viram erros em `ImportResultDTO`
- persistência:
  - chama `TransactionApplicationService#create` para cada linha válida
- eventos Kafka:
  - publica `transaction.created` para cada transação criada com sucesso

## Mapeamento detalhado — `AnalysisController`

### `GET /api/analysis/summary/{userId}`

- request:
  - path variable `userId: Long`
- response:
  - `ExpenseSummary`
- filtros:
  - não há query params
- período:
  - não há filtro temporal real no endpoint
- agrupamentos:
  - agrupamento por categoria em `ExpenseRepositoryJpa#getSummaryByUser`
- cálculos:
  - `totalMes`: soma de todos os valores encontrados para o usuário
  - `totalAno`: atualmente igual a `totalMes`
  - `totalPorCategoria`: soma por categoria
- fonte dos dados:
  - `ExpenseRepository` / `ExpenseRepositoryJpa` / tabela `expenses`

### `GET /api/analysis/report/excel/{userId}`

- request:
  - path variable `userId: Long`
- response:
  - `ResponseEntity<byte[]>` com `application/octet-stream`
- filtros:
  - não há
- período:
  - não há filtro temporal exposto
- agrupamentos:
  - não expostos pelo endpoint
- cálculos:
  - o gerador atual contém parte do layout com valores demonstrativos fixos
- fonte dos dados:
  - `AnalysisApplicationService#generateExcel` -> `ExcelReportGenerator`

### `GET /api/analysis/report/pdf/{userId}`

- request:
  - path variable `userId: Long`
- response:
  - `ResponseEntity<byte[]>` com `application/pdf`
- filtros:
  - não há
- período:
  - não há filtro temporal exposto
- agrupamentos:
  - não há agrupamento formal no endpoint
- cálculos:
  - soma total via `double` no `PdfReportGenerator`
  - quantidade baseada na lista retornada do repositório
- fonte dos dados:
  - `AnalysisApplicationService#generatePdf` -> `PdfReportGenerator` -> `ExpenseRepositoryJpa#findByUserId`

## 6.9 Testes existentes

### `user`

Arquivos encontrados:

- `ContextTest`
- `ListUsersUseCaseTest`
- `UserControllerTest`
- `AuthControllerTest`
- `UserRepositoryJpaAdapterTest`

Observações:

- `AuthControllerTest` possui testes reais ativos
- `UserControllerTest` está majoritariamente comentado no estado atual
- `ContextTest` contém código comentado
- há evidência de cobertura parcial e inconsistente

### `transaction-service`

Arquivos encontrados:

- `TransactionImportIntegrationTest`
- `UpdateTransactionUseCaseTest`
- `ListTransactionsUseCaseTest`
- `DeleteTransactionUseCaseTest`
- `CreateTransactionUseCaseTest`
- `JwtTokenProviderTest`
- `TransactionControllerTest`
- `TransactionApplicationServiceTest`

Observações:

- existe teste de integração com `@EmbeddedKafka`
- existe teste de controller via instanciação direta
- existe cobertura relevante de regras de negócio

### `analytics-service`

Arquivos encontrados:

- `AnalysisControllerTest`
- `PdfReportGeneratorTest`
- `ExcelReportGeneratorTest`

Observações:

- `AnalysisControllerTest` está praticamente vazio
- cobertura mais concentrada nos geradores de relatório

### `api-gateway`

- não foram encontrados testes em `api-gateway/src/test/java`

## 6.10 Problemas encontrados

### CRÍTICO

- consumer Kafka do analytics não demonstra persistência explícita no fluxo atual

### ALTO

- `analytics-service` não protege acesso direto com Spring Security
- `transaction-service` documenta e anota alguns status (`403`, `404`) sem tratamento global correspondente para exceções como `SecurityException` e `IllegalStateException`

### MÉDIO

- `transaction-service/sql/init.sql` define `user_id BIGINT`, enquanto o domínio Java usa `UUID`
- `ExcelReportGenerator` do analytics usa valores demonstrativos fixos em parte do relatório
- `PdfReportGenerator` usa `double` para somar valores monetários
- `api-gateway` não possui testes automatizados

### BAIXO

- README anterior estava fortemente divergente do estado real
- parte dos testes do módulo `user` está comentada ou incompleta
- há inconsistência entre `groupId` do listener do analytics e a configuração declarada em propriedades

## 6.11 Dívidas técnicas

- ausência de padronização completa do identificador de usuário entre serviços
- ausência de contrato versionado de eventos Kafka
- ausência de testes automatizados no gateway
- ausência de testes mais robustos para `analytics-service`
- tratamento de exceções não padronizado entre os módulos
- paginação da listagem de transações é montada em memória após carregar todos os dados do usuário
- regras de segurança parcialmente distribuídas entre gateway e serviços sem documentação contratual formal

## 6.12 Riscos de alteração

- qualquer mudança em `userId` impacta banco, DTOs, JWT, Kafka, testes e contratos HTTP
- alterar o payload Kafka sem versionamento pode quebrar o consumer do analytics
- endurecer segurança no `analytics-service` pode quebrar consumidores atuais que acessam o módulo diretamente
- modificar headers propagados pelo gateway quebra o `user` e qualquer outro serviço que dependa de `X-User-*`
- refatorar exclusão, update ou contratos HTTP das transações pode invalidar testes existentes e coleções Postman/Insomnia
- corrigir o contrato UUID/Long exigirá estratégia de migração cuidadosa para persistência, eventos e documentação

