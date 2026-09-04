# User Service

Serviço responsável pelo cadastro, autenticação e gerenciamento de usuários da plataforma de controle de gastos.

## Status atual

O módulo `user` está implementado e expõe endpoints de autenticação, CRUD de usuários, alteração de role, reativação e importação de usuários via Excel.

## Responsabilidades do serviço

- registrar usuários
- autenticar usuários com JWT
- listar usuários
- buscar usuário por ID
- criar usuários por rota autenticada
- atualizar usuários
- realizar exclusão lógica
- reativar usuários
- importar usuários via planilha Excel
- gerar planilha de erros da importação

## Stack utilizada

- Java 21
- Spring Boot 3.2.5
- Spring Web
- Spring Security
- JWT
- Spring Data JPA
- PostgreSQL
- Spring Validation
- Apache POI
- Springdoc OpenAPI
- Spring Boot Actuator
- Kafka (producer presente no projeto)

## Porta e perfis

- Porta HTTP: `8001`
- Nome da aplicação: `user-service`
- Perfil padrão: `dev`
- Perfil para Docker: `docker`

## Banco de dados

Banco próprio do serviço:

- `user_db`

Configuração padrão no perfil `dev`:

- host: `localhost`
- porta: `5433`
- driver: `org.postgresql.Driver`

## Como executar localmente

Na raiz do projeto, suba a infraestrutura necessária:

```powershell
Copy-Item ".env.example" ".env" -Force
docker compose up -d zookeeper kafka postgres-user
```

Depois inicie o serviço:

```powershell
Set-Location "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api\user"
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

http://localhost:8001/swagger-ui.html

Documentação centralizada pelo gateway:

- `http://localhost:8080/swagger-ui.html`

## Endpoints implementados

### Autenticação

| Método | Rota | Descrição | Status esperados |
|--------|------|-----------|------------------|
| POST | `/api/auth/register` | Registrar novo usuário | `200 OK`, `400 Bad Request` |
| POST | `/api/auth/login` | Autenticar e gerar JWT | `200 OK`, `401 Unauthorized`, `403 Forbidden` |

### Usuários

| Método | Rota | Descrição | Status esperados |
|--------|------|-----------|------------------|
| GET | `/api/users` | Listar usuários | `200 OK` |
| GET | `/api/users/{id}` | Buscar usuário por ID | `200 OK` |
| POST | `/api/users` | Criar usuário | `200 OK` |
| PUT | `/api/users/{id}` | Atualizar usuário | `200 OK` |
| DELETE | `/api/users/{id}` | Excluir usuário | `204 No Content` |
| PATCH | `/api/users/{id}/role` | Alterar role do usuário | `200 OK` |
| PATCH | `/api/users/{id}/reactivate` | Reativar usuário | `200 OK` |

### Excel

| Método | Rota | Descrição | Status esperados |
|--------|------|-----------|------------------|
| POST | `/api/excel/upload` | Importar usuários via Excel | `200 OK`, `400 Bad Request` |
| POST | `/api/excel/errors/download` | Baixar planilha de erros da importação | `200 OK`, `400 Bad Request` |

## Exemplo de cadastro

```json
{
  "name": "Maria Silva",
  "email": "maria@email.com",
  "password": "123456",
  "type": "USER"
}
```

## Exemplo de login

```json
{
  "email": "maria@email.com",
  "password": "123456"
}
```

## Observação importante sobre autenticação

As rotas de `UserController` usam os headers propagados pelo gateway:

- `X-User`
- `X-User-Id`
- `X-User-Role`

Por isso, para as rotas protegidas, o fluxo mais seguro e aderente ao projeto atual é consumir pelo `api-gateway` em `http://localhost:8080`.

As rotas públicas de autenticação podem ser acessadas diretamente em `http://localhost:8001` ou via gateway.

## Importação de usuários via Excel

Endpoint:

- `POST /api/excel/upload`

Colunas lidas no código atual:

1. nome
2. email
3. senha

Validações identificadas na importação:

- campos obrigatórios
- formato básico de email
- senha mínima de 6 caracteres
- duplicidade de email

## Kafka

Foi identificado um producer no projeto:

- classe: `UserProducer`
- tópico: `users`

O producer serializa `UserResponseDTO` em JSON antes do envio.

## Testes existentes

- `AuthControllerTest`
- `UserControllerTest`
- `ListUsersUseCaseTest`
- `UserRepositoryJpaAdapterTest`
- `ContextTest`

## dicas para verificar tabela de usuários no banco

```sql
Entrar no banco:
docker exec -it controle-de-gastos-api-main-postgres-user-1 psql -U postgres -d user_db
Você verá:
user_db=#
Listar tabelas
\dt
Esperamos:
public | users | table | postgres
Ver estrutura da tabela
\d users
Ver quantidade de usuários
SELECT COUNT(*) FROM users;
Ver usuários sem expor senha


SELECT id, name, email, active, type, created_at
FROM users;


SELECT email, LENGTH(password) AS password_length
FROM users;

depois, se quiser confirmar que há uma senha armazenada sem expor o valor dela:

SELECT email, LENGTH(password) AS password_length
FROM users;

consulta completa para ver todos os campos, incluindo a senha (não recomendado em produção):

SELECT id, name, email, password, active, type
FROM users;

Sair
\q
Sair
\q


```

## Observações relevantes do estado atual

- o serviço utiliza `UUID` para identificação de usuários
- há autenticação JWT no próprio serviço
- o consumo via gateway é o caminho principal para as rotas protegidas
- os endpoints de Excel estão anotados com `@PreAuthorize("hasRole('ADMIN')")`
- não foi identificada, durante esta análise, configuração explícita de `@EnableMethodSecurity` no módulo
