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
🧩 Microserviços
👤 User Service → gerenciamento de usuários e autenticação
💳 Transaction Service → gerenciamento de transações financeiras
📊 Analytics Service → processamento e análise de eventos
🌐 API Gateway → ponto único de entrada
📨 Kafka + Zookeeper → comunicação assíncrona entre serviços
⚙️ Comandos do Projeto
mvn clean package
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
User Service
http://localhost:8001/swagger-ui/index.html
Transaction Service
http://localhost:8002/swagger-ui/index.html
📡 Comunicação entre serviços

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
