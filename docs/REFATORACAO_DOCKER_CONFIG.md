Resumo das mudanças

Objetivo: remover duplicação de configurações entre `docker-compose.yml` e `application-*.yml` e seguir boas práticas profissionais usando perfis do Spring.

O que foi feito

1. docker-compose.yml
   - Mantido apenas serviços de infraestrutura (Postgres, Kafka, Zookeeper) e build/ports dos microserviços.
   - Definido em cada serviço apenas `SPRING_PROFILES_ACTIVE=docker` e variáveis sensíveis (por exemplo `JWT_SECRET`).
   - Removidas configurações de datasource/kafka/URLs dos environment dos serviços para evitar duplicação.
   - Substituído uso de `localhost` por nomes dos serviços Docker (ex.: `postgres-user`, `kafka`).

2. application-docker.yml em cada serviço
   - Criado `application-docker.yml` (profile `docker`) para `user`, `transaction-service`, `analytics-service`, `api-gateway`.
   - Contém configurações específicas para ambiente Docker: datasource apontando para os serviços Postgres (ex.: `jdbc:postgresql://postgres-user:5432/user_db`), `kafka.bootstrap-servers: kafka:9092`, URLs externas parametrizadas via `${}`.
   - Usa placeholders para credenciais sensíveis (`${SPRING_DATASOURCE_USERNAME}`, `${SPRING_DATASOURCE_PASSWORD}`, `${JWT_SECRET}`), permitindo definição via docker-compose ou secret manager.

3. application.yml (default)
   - Atualizados para conter valores locais (localhost) para desenvolvimento.
   - Mantém configuração para executar o serviço localmente sem Docker.

Motivação técnica das mudanças

- Separação de responsabilidades: o Compose orquestra containers e passa somente variáveis de ambiente; o Spring gerencia configuração por perfil.
- Evita inconsistência: alteração da URL do Postgres precisa ser feita apenas em `application-docker.yml`, não em dois lugares.
- Segurança: variáveis sensíveis (senha do DB, JWT) ficam no Compose ou em secret manager; os arquivos de configuração usam placeholders.
- Portabilidade: serviços dentro do mesmo compose se comunicam por nome de serviço (ex.: `kafka`), não por `localhost`.

Erros comuns e como evitá-los

1. Usar `localhost` dentro de containers
   - Sintoma: serviços não conseguem conectar ao Kafka/Postgres quando em containers.
   - Solução: usar nome do serviço definido no compose (ex.: `postgres-user`, `kafka`).

2. Esquecer `spring.profiles.active=docker`
   - Sintoma: Spring carrega `application.yml` (localhost) e usa `localhost:5432` causando falha de conexão.
   - Solução: definir `SPRING_PROFILES_ACTIVE=docker` no docker-compose ou arquivo `.env`.

3. Não externalizar segredos
   - Sintoma: segredos hardcoded nos commits.
   - Solução: use variáveis de ambiente no compose, Docker secrets, ou soluções externas.

4. Portas expostas desnecessariamente
   - Sintoma: conflitos de porta e exposição desnecessária em produção.
   - Solução: expor portas apenas quando necessário; para desenvolvimento mantenha mapeamentos, em produção use ingress.

5. Kafka advertised.listeners errado
   - Sintoma: clientes externos não conseguem se inscrever no broker.
   - Solução: configure `KAFKA_ADVERTISED_LISTENERS` com o nome do serviço e, se necessário, um host para acesso externo.

Checklist de validação (passos para testar)

- [ ] 1. Subir o ambiente: `docker-compose up --build` (ou via PowerShell: `docker-compose up --build;` )
- [ ] 2. Verificar logs do Kafka e Zookeeper (se levantou corretamente).
- [ ] 3. Verificar se cada serviço iniciou na porta correta (8001, 8002, 8003, 8080).
- [ ] 4. Testar endpoint de saúde: `http://localhost:8001/actuator/health` (ou endpoint implementado).
- [ ] 5. Criar um usuário via API e verificar gravação no banco (`postgres-user` container).
- [ ] 6. Criar uma transação e verificar evento Kafka (topic de transações) e consumidores.

Arquivos alterados/novos

- `docker-compose.yml` (refatorado)
- `user/src/main/resources/application-docker.yml` (novo)
- `transaction-service/src/main/resources/application-docker.yml` (atualizado)
- `analytics-service/src/main/resources/application-docker.yml` (novo)
- `api-gateway/src/main/resources/application-docker.yml` (novo)
- `analytics-service/src/main/resources/application.yml` (ajustado para local)
- `transaction-service/src/main/resources/application.yml` (ajustado para local)

Como reverter

- Use controle de versão (git) para reverter as mudanças, por exemplo `git checkout -- docker-compose.yml`.

Docker secrets (opcional / produção)

- Estrutura no `docker-compose.yml`: um bloco `secrets:` foi adicionado e os serviços referenciam `jwt_secret`, `postgres_password_user`, `postgres_password_tx`, `postgres_password_analytics`.
- Recomenda-se usar Docker Swarm ou orquestrador equivalente para suportar secrets em produção.

Exemplo rápido (Docker Swarm):

1) Inicializar Swarm (se ainda não estiver em modo Swarm):
   docker swarm init

2) Criar os secrets localmente:
   echo -n "my-very-secret-jwt-value" | docker secret create jwt_secret -
   echo -n "postgres_pw_user" | docker secret create postgres_password_user -
   echo -n "postgres_pw_tx" | docker secret create postgres_password_tx -
   echo -n "postgres_pw_analytics" | docker secret create postgres_password_analytics -

3) Fazer deploy do stack (note que `docker stack deploy` utiliza o bloco `secrets` corretamente):
   docker stack deploy -c docker-compose.yml my_stack

Fallback local

- Para desenvolvimento local (sem Swarm) continue usando `.env` com `docker-compose up --build`.
- A configuração atual prioriza Docker secrets (quando presentes) mas mantém fallback para variáveis de ambiente definidas no `.env` para conveniência.

Segurança

- Em ambientes de CI/CD e produção, prefira soluções de secrets gerenciadas (HashiCorp Vault, AWS Secrets Manager, Azure Key Vault) e injete segredos como variables/volumes/secret mounts no pipeline.
