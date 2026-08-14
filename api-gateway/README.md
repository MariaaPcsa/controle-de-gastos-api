# API Gateway

Serviço responsável por atuar como ponto único de entrada da plataforma de controle de gastos.

## Status atual

O módulo `api-gateway` está implementado com Spring Cloud Gateway e concentra o roteamento para os microsserviços, a validação de JWT no nível de borda e a documentação OpenAPI centralizada.

## Responsabilidades do serviço

- receber requisições externas da plataforma
- encaminhar chamadas para os microsserviços internos
- validar tokens JWT antes do encaminhamento das rotas protegidas
- propagar dados do usuário autenticado para os serviços internos
- centralizar a documentação Swagger/OpenAPI
- aplicar filtros padrão de remoção de headers sensíveis
- aplicar configuração global de CORS

## Stack utilizada

- Java 21
- Spring Boot 3.2.5
- Spring WebFlux
- Spring Cloud Gateway
- Spring Security WebFlux
- JWT
- Springdoc OpenAPI WebFlux UI
- Spring Boot Actuator

## Porta e perfis

- Porta HTTP: `8080`
- Nome da aplicação: `api-gateway`
- Perfil padrão: `dev`
- Perfil para Docker: `docker`

## Papel na arquitetura

O gateway é o ponto de entrada principal para clientes externos.

Fluxo atual:

```text
Cliente
   |
   v
API Gateway :8080
   |
   +--> user :8001
   +--> transaction-service :8002
   +--> analytics-service :8003
```

## Rotas encaminhadas

### Rotas funcionais

| ID da rota | Path | Destino no perfil `dev` | Destino no perfil `docker` |
|-----------|------|--------------------------|----------------------------|
| `user-service` | `/api/users/**`, `/api/auth/**`, `/api/excel/**` | `http://localhost:8001` | `http://user:8001` |
| `transaction-service` | `/api/transactions/**` | `http://localhost:8002` | `http://transaction-service:8002` |
| `analytics-service` | `/api/analysis/**` | `http://localhost:8003` | `http://analytics-service:8003` |

### Rotas OpenAPI agregadas

| ID da rota | Path público no gateway | Destino final |
|-----------|--------------------------|---------------|
| `user-openapi` | `/user/v3/api-docs` | `/v3/api-docs` do `user` |
| `transaction-openapi` | `/transaction/v3/api-docs` | `/v3/api-docs` do `transaction-service` |
| `analytics-openapi` | `/analytics/v3/api-docs` | `/v3/api-docs` do `analytics-service` |

## Segurança

O gateway usa duas camadas complementares:

1. `SecurityConfig`
   - desabilita `csrf`, `httpBasic` e `formLogin`
   - libera rotas públicas de autenticação e documentação
   - deixa o controle efetivo de autenticação para o filtro JWT global

2. `JwtFilter`
   - intercepta as requisições
   - permite a passagem das rotas públicas
   - exige header `Authorization: Bearer <token>` nas demais rotas
   - valida o token com a chave `jwt.secret`
   - retorna `401 Unauthorized` quando o token está ausente ou inválido

## Rotas públicas no gateway

Pelo código atual, estas rotas passam sem validação JWT no filtro:

- `/api/auth/**`
- `/swagger-ui.html`
- `/swagger-ui/**`
- `/v3/api-docs`
- `/v3/api-docs/**`
- `/user/v3/api-docs`
- `/transaction/v3/api-docs`
- `/analytics/v3/api-docs`

## Headers propagados para os microsserviços

Quando o JWT é válido, o gateway adiciona os headers abaixo antes de encaminhar a requisição:

- `X-User`
- `X-User-Id`
- `X-User-Role`

Esses headers são usados principalmente pelo `user` e fazem parte do contrato interno atual entre gateway e serviços.

## Swagger / OpenAPI centralizado

A interface centralizada está disponível em:

- `http://localhost:8080/swagger-ui.html`

No perfil atual, o Swagger do gateway agrega:

- `user`
- `transaction`
- `analytics`

Endpoints OpenAPI individuais expostos pelo gateway:

- `http://localhost:8080/user/v3/api-docs`
- `http://localhost:8080/transaction/v3/api-docs`
- `http://localhost:8080/analytics/v3/api-docs`

## CORS e filtros padrão

### Filtros padrão

O gateway aplica os seguintes filtros padrão:

- `RemoveRequestHeader=Cookie`
- `RemoveResponseHeader=Set-Cookie`
- `DedupeResponseHeader=Access-Control-Allow-Origin Access-Control-Allow-Credentials`

### CORS

No perfil `dev`, há configuração WebFlux CORS com:

- `allowedOrigins: *`
- métodos: `GET`, `POST`, `PUT`, `DELETE`
- `allowedHeaders: *`

No perfil `docker`, há configuração global CORS com:

- `allowedOrigins: *`
- métodos: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`
- `allowedHeaders: *`

## Como executar localmente

Na raiz do projeto, garanta que os demais serviços estejam disponíveis:

```powershell
Copy-Item ".env.example" ".env" -Force
docker compose up -d user transaction-service analytics-service
```

Depois inicie o gateway:

```powershell
Set-Location "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api\api-gateway"
mvn spring-boot:run
```

## Como executar com Docker Compose

Na raiz do projeto:

```powershell
Copy-Item ".env.example" ".env" -Force
docker compose up -d --build
```

## Variáveis relevantes

Variável observada no módulo:

- `JWT_SECRET`

No perfil `dev`, existe valor default em `application-dev.yml`.
No perfil `docker`, o valor vem de:

- `${JWT_SECRET}`

## Observabilidade

Configurações identificadas no módulo:

- Actuator habilitado
- endpoints expostos: `health`, `info`, `metrics`
- log `INFO` para:
  - `org.springframework.cloud.gateway`
  - `org.springframework.security`

## Docker

O `Dockerfile` do gateway:

- usa build com Maven e Temurin 21
- executa `mvn clean package -pl api-gateway -am -DskipTests`
- expõe a porta `8080`
- inicia com `java -jar app.jar`

## Testes existentes

Durante a análise atual, não foram encontrados arquivos em:

- `api-gateway/src/test/java`

Ou seja, o módulo está sem testes automatizados visíveis no estado atual do repositório.

## Observações relevantes do estado atual

- o gateway centraliza autenticação, mas a autorização ainda depende do comportamento de cada microsserviço
- o `JwtFilter` é a peça principal de autenticação efetiva no módulo
- a documentação Swagger centralizada já está configurada e funcional no nível de roteamento
- o contrato interno com os serviços inclui propagação de identidade via headers `X-User-*`
- não há controller REST próprio no gateway; o comportamento principal é configuracional e baseado em filtro/roteamento

