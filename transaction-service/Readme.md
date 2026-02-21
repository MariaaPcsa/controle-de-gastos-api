# 🧾 Transaction Service

Este módulo é responsável pelo **CRUD de transações** (despesas e receitas) do sistema.

⚠️ **Status:** Em desenvolvimento — ainda não há implementação publicada nesta pasta.

## Funcionalidades planejadas

- 📌 Listar todas as transações  
- 📌 Criar nova transação  
- 📌 Buscar transação por ID  
- 📌 Atualizar transação  
- 📌 Remover transação  
- 📌 Validação e tratamento de erros  
- 📌 Integração com relatórios em `analytics-service`

## Endpoints (planejados)

http://localhost:8081/swagger-ui.html

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

Para rodar H2:

mvn spring-boot:run -Dspring-boot.run.profiles=test

Para rodar PostgreSQL (padrão):

mvn spring-boot:run

mvn clean install
mvn spring-boot:run
mvn clean install