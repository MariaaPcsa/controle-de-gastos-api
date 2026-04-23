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
   - POST /api/auth/register -> registrar usuário
   - POST /api/auth/login -> obter token JWT
   - /api/users/** -> CRUD usuários
   - /api/transactions/** -> CRUD transações
   - /api/analytics/** -> relatórios e downloads

5) Swagger/OpenAPI:
   - user: http://localhost:8001/swagger-ui.html (ou via gateway /api/users/swagger-ui.html)
   - transaction: http://localhost:8002/swagger-ui.html
   - analytics: http://localhost:8003/swagger-ui.html

6) Import Excel:
   - Envie via endpoint POST /api/transactions/upload (multipart/form-data)
   - O serviço processará linhas e retornará relatório de importação

7) Observabilidade básica:
   - /actuator/health em cada serviço

8) Repositório contém exemplos de upload: `user/users_upload.xlsx`, `user_upload_test_cases.xlsx`, `examples/transactions_upload_example.csv`

Se você prefere não usar Docker, você pode executar cada serviço via IDE (Spring Boot) apontando as variáveis de ambiente para os bancos locais e para Kafka.

