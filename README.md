💰 Sobre o Serviço
🧱 Arquitetura do Sistema


                ┌──────────────────────┐
                │     API GATEWAY      │
                │ Spring Cloud Gateway │
                └──────────┬───────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ USER SERVICE │   │ TRANSACTION  │   │ ANALYTICS    │
│              │   │ SERVICE      │   │ SERVICE      │
└──────┬───────┘   └──────┬───────┘   └──────┬───────┘
       │                  │                  │
       │                  │                  │
       └──────────┬───────▼────────┬─────────┘
                  │     KAFKA      │
                  │  (EVENT BUS)   │
                  └──────┬─────────┘
                         │
              ┌──────────▼──────────┐
              │ ANALYTICS CONSUMER  │
              └──────────────────────┘

                 ┌─────────────────────┐
                 │     API Gateway     │
                 │       :8080         │
                 └──────────┬──────────┘
                            │
            ┌───────────────┼───────────────┐
            ↓               ↓               ↓
       User :8001      Transaction :8002   Analytics :8003
            │               │               │
            ↓               ↓               ↓
       PostgreSQL       PostgreSQL       PostgreSQL
        :5433             :5434             :5435
                            │
                            ↓
                         Kafka
                        :29092
                            │
                            ↓
                       Zookeeper
                        :22181




Guia rápido para executar o sistema localmente (Docker Compose)

Requisitos:
- Docker Desktop (Windows)
- Maven (para build local) ou usar os JARs em /target

Passos:
1) Build dos microsserviços (no Windows PowerShell):

   cd "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api"; mvn -T1C -DskipTests package

2) Subir infra e serviços com docker-compose:

   cd "C:\Users\maria\Desktop\desafio beca\controle-de-gastos-api"; docker compose up --build

   Isso iniciará: Zookeeper, Kafka, 3 Postgres, api-gateway (8080), user (8001), transaction-service (8002), analytics-service (8003)

3) Inicializar tópicos Kafka (dentro de um container com kafka-tools ou usar script local):

   # exemplo usando o container kafka
   docker exec -it <kafka_container_id> bash -c "kafka-topics.sh --create --bootstrap-server localhost:9092 --topic transaction.created --partitions 3 --replication-factor 1"

   Ou no host (se tiver kafka tools): ./scripts/init-topics.sh

4) Endpoints principais (via API Gateway - 8080):
    - POST /auth/register -> registrar usuário
    - POST /auth/login -> obter token JWT
    - /users/** -> CRUD usuários
    - /transactions/** -> CRUD transações
    - /analytics/** -> relatórios e downloads

5) 🌐 Swagger/OpenAPI:
    - user: http://localhost:8001/swagger-ui.html (ou via gateway /api/users/swagger-ui.html)
    - transaction: http://localhost:8002/swagger-ui.html
    - analytics: http://localhost:8003/swagger-ui.html

6) Import Excel:
    - Envie via endpoint POST /transactions/upload (multipart/form-data)
    - O serviço processará linhas e retornará relatório de importação

7) Observabilidade básica:
    - /actuator/health em cada serviço

8) Repositório contém exemplos de upload: `user/users_upload.xlsx`, `user_upload_test_cases.xlsx`, `examples/transactions_upload_example.csv`

Se você prefere não usar Docker, você pode executar cada serviço via IDE (Spring Boot) apontando as variáveis de ambiente para os bancos locais e para Kafka.





🧩 Microserviços
👤 User Service → gerenciamento de usuários e autenticação
💳 Transaction Service → gerenciamento de transações financeiras
📊 Analytics Service → processamento e análise de eventos
🌐 API Gateway → ponto único de entrada
📨 Kafka + Zookeeper → comunicação assíncrona entre serviços
⚙️ Comandos do Projeto

mes antes de rodar o projeto, certifique-se de que não há containers antigos em execução. Se houver, use:
docker compose down -v
depois docker compose up --build -d
Serviços e portas
mvn clean package
Agora vamos reconstruir

Primeiro derrube os containers:

docker compose down

Depois reconstrua:

docker compose up -d --build

Aguarde uns 20–30 segundos e execute:

docker compose ps

O objetivo é aparecer:

api-gateway          Up
user                 Up
transaction-service  Up
analytics-service    Up
kafka                Up (healthy)
postgres-user        Up (healthy)
postgres-tx          Up (healthy)
postgres-analytics   Up (healthy)

Depois confira especificamente o user:

docker compose logs user --tail=100

E o Transaction:

docker compose logs transaction-service --tail=100

E Analytics:

docker compose logs analytics-service --tail=100

ou rodar o Gateway localmente:
z
docker compose stop api-gateway

mvn spring-boot:run


🗄️ Banco de Dados

Cada serviço possui seu próprio banco PostgreSQL:

user_db
transaction_db
analytics_db
🔥 Possível Problema (Volume Docker Antigo)

Mesmo corrigindo configurações, o banco pode não atualizar por causa de volumes antigos.

💣 Solução:
docker-compose down -v
docker-compose up -d --build
📄 README.md
💰 Controle de Gastos API (Microserviços)

Sistema de gestão financeira distribuído baseado em arquitetura de microserviços, desenvolvido com Spring Boot, JWT, Kafka e PostgreSQL, com foco em escalabilidade, segurança e boas práticas de engenharia de software.

🚀 Arquitetura do Sistema
👤 User Service → usuários e autenticação
💳 Transaction Service → transações financeiras
📊 Analytics Service → análise via eventos
🌐 API Gateway → entrada única
📨 Kafka → mensageria entre serviços
🧱 Tecnologias Utilizadas
Java 21
Spring Boot
Spring Security + JWT
Spring Data JPA
PostgreSQL
Apache Kafka
Docker & Docker Compose
Hibernate
Maven
🔐 Autenticação (JWT)
Exemplo de payload:
{
  "sub": "user@email.com",
  "id": "uuid-do-usuario",
  "role": "ADMIN",
  "iat": 1710000000,
  "exp": 1710003600
}
📦 Funcionalidades
👤 Usuários
Criar usuário
Login
Atualizar usuário
Alterar role (ADMIN / USER)
Soft delete
Reativação
Busca por ID e email
💳 Transações
Criar transação (DEPOSIT / WITHDRAW)
Listar transações
Filtrar por usuário

Como agora todas as rotas do User estão funcionando, podemos testar tudo pelo Insomnia usando uma única base:

http://localhost:8080/api

O ponto mais importante é: faça o Login primeiro e use o JWT nas rotas protegidas.

1. Login

POST

http://localhost:8080/api/auth/login

Body → JSON:

{
"email": "admin@finance.com",
"password": "SUA_SENHA"
}

Resposta esperada:

{
"token": "eyJhbGciOiJIUzI1NiJ9..."
}

Copie somente o valor do token.

2. Listar usuários

GET

http://localhost:8080/api/users

No Insomnia:

Auth → Bearer Token

Cole:

SEU_TOKEN

Resposta esperada:

[
{
"id": "...",
"name": "Admin",
"email": "admin@finance.com",
"type": "ADMIN"
}
]
3. Buscar usuário

Pegue o id de um usuário retornado anteriormente.

GET

http://localhost:8080/api/users/{id}

Exemplo:

http://localhost:8080/api/users/606a1fa8-a18f-4215-9452-d0824b54486e

Auth → Bearer Token

SEU_TOKEN

Esperado:

200 OK
4. Criar usuário

POST

http://localhost:8080/api/users

Auth → Bearer Token

SEU_TOKEN

Body → JSON

{
"name": "Maria Teste",
"email": "maria.teste@finance.com",
"password": "123456",
"type": "USER"
}

Esperado:

200 OK

Guarde o id retornado. Vamos utilizá-lo nos próximos testes.

5. Atualizar usuário

PUT

http://localhost:8080/api/users/ID_DO_USUARIO

Exemplo:

http://localhost:8080/api/users/12345678-1234-1234-1234-123456789012

Auth → Bearer Token

Body:

{
"name": "Maria Atualizada",
"email": "maria.atualizada@finance.com",
"password": "654321",
"type": "USER"
}

Esperado:

200 OK
6. Alterar role

Agora vamos testar:

USER → ADMIN

PATCH

http://localhost:8080/api/users/ID_DO_USUARIO/role

Auth → Bearer Token

Body:

{
"type": "ADMIN"
}

Esperado:

200 OK

Resposta deve apresentar:

{
"type": "ADMIN"
}
7. Reativar usuário

PATCH

http://localhost:8080/api/users/ID_DO_USUARIO/reactivate

Auth → Bearer Token

Não precisa de Body.

Esperado:

200 OK
8. Deletar usuário

Faça por último, porque depois desse teste o usuário poderá não existir mais.

DELETE

http://localhost:8080/api/users/ID_DO_USUARIO

Auth → Bearer Token

Esperado:

204 No Content
9. Teste muito importante: sem TOKEN

Agora queremos confirmar que a segurança realmente está funcionando.

Faça:

GET

http://localhost:8080/api/users

Remova o Bearer Token.

Resultado esperado:

401 Unauthorized

Se retornar 401, ótimo. 🔐

10. Testar TOKEN inválido

Coloque:

Auth → Bearer Token

token-invalido

Faça:

GET http://localhost:8080/api/users

Esperado:

401 Unauthorized

Estrutura da Collection
Controle de Gastos API
│
├── 🔐 Auth
│   ├── Registrar usuário
│   └── Login
│
├── 👤 Usuários
│   ├── Listar usuários
│   ├── Buscar usuário por ID
│   ├── Criar usuário
│   ├── Atualizar usuário
│   ├── Deletar usuário
│   ├── Alterar role
│   └── Reativar usuário
│
├── 💰 Transações
│   ├── Criar transação
│   ├── Listar transações
│   ├── Buscar transação
│   ├── Atualizar transação
│   └── Deletar transação
│
├── 📊 Analytics
│   └── Resumo financeiro
│
└── 📥 Excel
├── Upload usuários
└── Download erros


Publicação de eventos no Kafka
📊 Analytics
Consumo de eventos Kafka
Processamento de dados financeiros
Base para relatórios
🧪 Como rodar o projeto
1️⃣ Subir infraestrutura
docker-compose up -d
2️⃣ Rodar serviços
cd user
mvn spring-boot:run

cd transaction-service
mvn spring-boot:run

cd analytics-service
mvn spring-boot:run
🌐 Swagger


Eventos via Kafka:

criação de transação
atualização de saldo
auditoria de eventos
⚠️ Observações
IDs migrados para UUID
JWT usa UUID como String
Kafka centraliza comunicação
Arquitetura baseada em Clean Architecture + DDD
🧠 Conceitos aplicados
Microserviços
DDD
Clean Architecture
Event Driven Architecture
JWT Security
Soft Delete
Separação de contextos
👨‍💻 Autor

Desenvolvido por Maria
Projeto educacional focado em arquitetura de sistemas financeiros modernos.
