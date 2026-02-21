📌 User Service — Controle de Gastos API

Este README documenta especificamente o serviço de usuários (user) da API de controle de gastos desenvolvida em Java com Spring Boot. Essa parte do projeto é responsável pelo cadastro, autenticação e gerenciamento de usuários da aplicação.

🚀 Sobre o Serviço

O módulo user faz parte da arquitetura da Controle de Gastos API, uma REST API para gestão financeira pessoal. O serviço de usuários inclui funcionalidades como:

✔️ Registro de novos usuários
✔️ Login com geração e validação de tokens JWT
✔️ Consulta, atualização e exclusão de usuários
✔️ Integração com segurança Spring Security

Esse módulo é fundamental para proteger rotas da API e garantir que cada usuário só acesse seus próprios dados de maneira segura.

🧱 Estrutura da Pasta
user/
├── src/
│   ├── main/java/...
│   │   ├── controller/        # Endpoints REST para usuários
│   │   ├── service/           # Lógica de negócio de usuário
│   │   ├── repository/        # Repositório de dados de usuário
│   │   ├── model/             # Entidades (User, Role, etc.)
│   │   └── security/          # Configurações de JWT e segurança
├── pom.xml                    # Dependências e build Maven
└── application.yml            # Configurações específicas do módulo

📋 Funcionalidades Principais
🔐 Autenticação e Autorização

Registro de usuário: cria novo usuário com dados básicos (nome, email, senha).

Login: gera token JWT para acesso às rotas protegidas.

Proteção de rotas: uso de Spring Security para garantir que apenas usuários autenticados acessem recursos privados.

🛠️ Como Rodar o Serviço Localmente

Antes de executar, certifique-se de ter configurado:

✔️ Java 21
✔️ Maven
✔️ Base de dados configurada no arquivo application.yml

📌 Executando sem Docker

No diretório user, rode:

mvn clean install
mvn spring-boot:run


O servidor iniciará no endereço:

http://localhost:8080


⚠️ Use os parâmetros de banco de dados e JWT definidos no application.yml antes de subir o serviço.

📍 Endpoints Principais
Método	Rota	Descrição
POST	/api/auth/register	Cadastrar novo usuário
POST	/api/auth/login	Login e geração de token JWT
GET	/api/users	Listar todos usuários
GET	/api/users/{id}	Buscar usuário por ID
DELETE	/api/users/{id}	Deletar usuário

Para endpoints protegidos, envie o token JWT no header:
Authorization: Bearer SEU_TOKEN_AQUI

🧪 Testes

Para rodar os testes automáticos do módulo:

mvn test

📌 Boas Práticas Usadas

✔️ Arquitetura em camadas (Controller → Service → Repository)
✔️ Validações de entrada e tratamento de exceções
✔️ Segurança com JWT e Spring Security
✔️ Separação de responsabilidades
✔️ Documentação de endpoints via Swagger (na raíz da API)


📖 Documentação da API (Swagger)

Após subir o serviço user, acesse a documentação interativa:

http://localhost:8080/swagger-ui.html


ou (dependendo da config do Spring Boot 3):

http://localhost:8080/swagger-ui/index.html


Lá você pode:

Testar endpoints

Ver schemas de request/response

Autenticar via JWT no botão Authorize

🔑 Exemplos de Requisições (Insomnia / Postman / cURL)
✅ Cadastro de Usuário

POST /api/auth/register

Body (JSON):

{
"name": "Maria Silva",
"email": "maria@email.com",
"password": "123456",
"type": "USER"
}


cURL:

curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{
"name": "Maria Silva",
"email": "maria@email.com",
"password": "123456",
"type": "USER"
}'

🔐 Login

POST /api/auth/login

Body (JSON):

{
"email": "maria@email.com",
"password": "123456"
}


Resposta esperada:

{
"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}


cURL:

curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{
"email": "maria@email.com",
"password": "123456"
}'

👤 Buscar Usuário (rota protegida)

GET /api/users/{id}

Header:

Authorization: Bearer SEU_TOKEN_AQUI


cURL:

curl -X GET http://localhost:8080/api/users/1 \
-H "Authorization: Bearer SEU_TOKEN_AQUI"

📋 Listar Usuários (rota protegida)

GET /api/users

cURL:

curl -X GET http://localhost:8080/api/users \
-H "Authorization: Bearer SEU_TOKEN_AQUI"

❌ Deletar Usuário (rota protegida)

DELETE /api/users/{id}

curl -X DELETE http://localhost:8080/api/users/1 \
-H "Authorization: Bearer SEU_TOKEN_AQUI"

🧠 Fluxo de Autenticação (JWT)
[Cliente]
|
| POST /api/auth/login
v
[User Service]
|
| Gera JWT
v
[Cliente recebe Token]
|
| Envia "Authorization: Bearer TOKEN"
v
[Rotas Protegidas]

🧱 Arquitetura do Módulo User (Clean Architecture)
┌──────────────────────────────┐
│        Controller            │  ← Camada de entrada (REST)
│  (AuthController, UserController)
└───────────────▲──────────────┘
│
┌───────────────┴──────────────┐
│      Application Service     │  ← Regras de negócio
│     (UserApplicationService) │
└───────────────▲──────────────┘
│
┌───────────────┴──────────────┐
│           Domain             │  ← Entidades e regras
│           (User)             │
└───────────────▲──────────────┘
│
┌───────────────┴──────────────┐
│        Infrastructure        │  ← Banco, JPA, JWT
│     (Repository, Security)   │
└──────────────────────────────┘

🗄️ Exemplo de Configuração (application.yml)
server:
port: 8080

spring:
datasource:
url: jdbc:postgresql://localhost:5432/controle_gastos
username: postgres
password: postgres
jpa:
hibernate:
ddl-auto: update
show-sql: true

security:
jwt:
secret: MINHA_CHAVE_SECRETA_SUPER_SEGURA
expiration: 86400000

🧪 Usuário de Teste (Opcional)

Você pode criar um usuário inicial para testes:

{
"name": "Admin",
"email": "admin@email.com",
"password": "admin123",
"type": "ADMIN"
}

⚠️ Erros Comuns

❌ 401 Unauthorized
➡️ Token JWT não enviado ou expirado

❌ 400 Bad Request
➡️ JSON inválido ou campos obrigatórios ausentes

❌ 409 Conflict
➡️ Email já cadastrado


Este serviço faz parte de um projeto open source. Se quiser contribuir:

Faça um fork

Crie uma branch com a mudança (git checkout -b feature/minha-feature)

Envie um pull request

Toda contribuição é bem-vinda!