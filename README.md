# Sobre o Serviço;



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
│        ┌─────────▼─────────┐        │
│        │      KAFKA        │◄───────┘
│        │  (EVENT BUS)      │
│        └─────────┬─────────┘
│                  │
│         ┌────────▼────────┐
│         │ ANALYTICS CONSUMER │
│         └──────────────────┘


# mvn clean package
mvn spring-boot:run

 # Cada serviço possui seu próprio banco PostgreSQL




🔥 POSSÍVEL OUTRO PROBLEMA (volume antigo)

Mesmo corrigindo isso, pode acontecer:

👉 banco não ser recriado por causa do volume

💣 Solução:
docker-compose down -v
docker-compose up -d --build

📄 README.md
# 💰 Controle de Gastos API (Microserviços)Sistema de **gestão financeira distribuído** baseado em arquitetura de microserviços, desenvolvido com **Spring Boot**, **JWT**, **Kafka** e **PostgreSQL**, com foco em escalabilidade, segurança e boas práticas de engenharia de software.---## 🚀 Arquitetura do SistemaO projeto é dividido em microserviços independentes:- 👤 **User Service** → gerenciamento de usuários e autenticação- 💳 **Transaction Service** → gerenciamento de transações financeiras- 📊 **Analytics Service** → processamento e análise de dados via eventos- 🌐 **API Gateway** → ponto único de entrada- 📨 **Kafka + Zookeeper** → comunicação assíncrona entre serviços---## 🧱 Tecnologias Utilizadas- Java 21- Spring Boot- Spring Security + JWT- Spring Data JPA- PostgreSQL- Apache Kafka- Docker & Docker Compose- Hibernate- Maven---## 🔐 AutenticaçãoO sistema utiliza **JWT (JSON Web Token)**.### Exemplo de payload do token:```json{  "sub": "user@email.com",  "id": "uuid-do-usuario",  "role": "ADMIN",  "iat": 1710000000,  "exp": 1710003600}

📦 Funcionalidades
👤 Usuários


Criar usuário


Login


Atualizar usuário


Alterar role (ADMIN / USER)


Soft delete (desativação)


Reativação de usuário


Busca por ID e email



💳 Transações


Criar transação (DEPOSIT / WITHDRAW)


Listar transações


Filtrar por usuário


Publicação de eventos no Kafka



📊 Analytics


Consumo de eventos Kafka


Processamento de dados financeiros


Base para relatórios futuros



🧪 Como rodar o projeto
1️⃣ Subir infraestrutura
docker-compose up -d

2️⃣ Rodar serviços
Cada microserviço deve ser iniciado separadamente:
cd usermvn spring-boot:run
cd transaction-servicemvn spring-boot:run
cd analytics-servicemvn spring-boot:run

🌐 Swagger


User Service:


http://localhost:8001/swagger-ui/index.html


Transaction Service:


http://localhost:8002/swagger-ui/index.html

🗄️ Banco de Dados
Cada serviço possui seu próprio banco:


user_db


transaction_db


analytics_db



📡 Comunicação entre serviços


Kafka é utilizado para eventos como:


criação de transação


atualização de saldo


eventos de auditoria





⚠️ Observações importantes


O sistema já está migrado para UUID como ID principal


JWT agora transporta IDs em formato UUID (String)


Comunicação entre serviços depende de eventos Kafka


Arquitetura segue princípios de Clean Architecture + DDD



🧠 Conceitos aplicados


Microserviços


Domain Driven Design (DDD)


Clean Architecture


Event Driven Architecture


Segurança com JWT


Soft Delete


Separação de contextos



👨‍💻 Autor
Desenvolvido por Maria
Projeto educacional com foco em arquitetura de sistemas financeiros modernos.

📌 Status do Projeto
🚧 Em evolução constante
✔️ Autenticação funcionando
✔️ Transações funcionando
✔️ Kafka integrado
⚠️ Migração completa para UUID em andamento
---Se quiser, posso te ajudar a próxima evolução disso:- :contentReference[oaicite:0]{index=0}- :contentReference[oaicite:1]{index=1}- ou :contentReference[oaicite:2]{index=2}Só me fala 👍
