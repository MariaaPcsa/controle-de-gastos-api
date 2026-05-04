# 🧾 Transaction Service

Este módulo é responsável pelo **CRUD de transações** (despesas e receitas) do sistema.

⚠️ **Status:** Em desenvolvimento — ainda não há implementação publicada nesta pasta.



acessar o banco via terminal (mais rápido)
docker exec -it controle-de-gastos-api-postgres-tx-1 psql -U postgres -d tx_db
1. Ver tabelas
   \dt
2. 👀 2. Ver estrutura da tabela
   \d transactions
3. 📊 3. Ver dados (admin seed)
   SELECT * FROM transactions;


## Funcionalidades planejadas

- 📌 Listar todas as transações  
- 📌 Criar nova transação  
- 📌 Buscar transação por ID  
- 📌 Atualizar transação  
- 📌 Remover transação  
- 📌 Validação e tratamento de erros  
- 📌 Integração com relatórios em `analytics-service`

## Endpoints (planejados)

http://localhost:8002/swagger-ui/index.html

mais senha 

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/transactions` | Listar todas as transações |
| GET | `/transactions/{id}` | Obter transação por ID |
| POST | `/transactions` | Criar nova transação |
| PUT | `/transactions/{id}` | Atualizar transação |
| DELETE | `/transactions/{id}` | Remover transação |

## Próximos passos

1. Criar entidades e DTOs  
2. Implementar controllers, services e repositórios  
3. Adicionar testes automatizados  
4. Adicionar documentação Swagger  
5. Conectar com `analytics-service`

## Rodando em desenvolvimento

Para evitar problemas de resolução de host entre container e host, adicionamos dois profiles:

- `local` — usa `localhost:5433` para Postgres (útil quando o Postgres está rodando via docker-compose e você executa a aplicação localmente)
- `docker` — usa `postgres:5432` (útil quando a aplicação roda dentro do Docker Compose)

Comandos PowerShell:

# Subir infra (Postgres/Kafka) via docker-compose
docker compose -f .\docker-compose.transactions.yml up -d

# Rodar a aplicação localmente conectando ao Postgres do container (use profile local)
$env:SPRING_PROFILES_ACTIVE = "local"; mvn spring-boot:run

# Ou passar como argumento
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"

# Rodar tudo via Docker Compose (a aplicação usará o profile docker quando for necessário via variáveis de ambiente)
# Nota: o serviço `transaction-service` no docker-compose já referencia jdbc://postgres:5432
docker compose -f .\docker-compose.transactions.yml up --build

Para rodar H2:

mvn spring-boot:run -Dspring-boot.run.profiles=test

Para rodar PostgreSQL (padrão):

mvn spring-boot:run

mvn clean install
mvn spring-boot:run
mvn clean install

{
"amount": 50.00,
"currency": "BRL",
"type": "DEPOSIT",
"category": "FOOD",
"description": "Teste"
}

Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2FvLnRlc3RlQGVtYWlsLmNvbSIsImlkIjo4MDMsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzczNzE4NjY2LCJleHAiOjE3NzM3MjIyNjZ9.JPfprmsl0OQ2J4PzkJNbwDMzJupEpDHD_N_4Iibq_to