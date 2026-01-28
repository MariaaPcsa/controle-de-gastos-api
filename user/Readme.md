

📌 API de Gestão Financeira – Microserviço User
🧱 VISÃO GERAL – ARQUITETURA LIMPA
Princípios que vamos seguir

✔ Regra de negócio não depende de Spring
✔ Domínio não conhece banco, web, segurança, Excel
✔ Frameworks ficam na borda
✔ Testável sem subir aplicação

📁 ESTRUTURA FINAL DO user-service
user-service/
│
├── domain/
│   ├── model/
│   │   ├── User.java
│   │   └── UserType.java
│   ├── repository/
│   │   └── UserRepository.java        (INTERFACE)
│   └── usecase/
│       ├── CreateUserUseCase.java
│       ├── UpdateUserUseCase.java
│       ├── DeleteUserUseCase.java
│       └── ListUsersUseCase.java
│
├── application/
│   └── service/
│       └── UserApplicationService.java
│
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/UserEntity.java
│   │   ├── mapper/UserMapper.java
│   │   └── repository/UserRepositoryJpa.java
│   ├── security/
│   │   ├── JwtService.java
│   │   └── SecurityConfig.java
│   └── excel/
│       └── UserExcelImporter.java
│
├── presentation/
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   └── UploadController.java
│   └── dto/
│       ├── UserRequestDTO.java
│       ├── UserResponseDTO.java
│       └── LoginDTO.java
│
├── Dockerfile
├── docker-compose.yml
└── users_upload.xlsx


Arquitetura Limpa, alinhado com o user-service, com:

✅ CRUD de transações
✅ Regras de negócio isoladas (Use Cases)
✅ Integração com Kafka (producer)
✅ Consumo da BrasilAPI (câmbio)
✅ Preparado para análise de despesas
✅ Pronto para testes unitários

✔️ Separação correta de responsabilidades
✔️ Pronto para escalar para outros microserviços (transactions, auth…)

👤 2. Funcionalidades de Usuário implementadas
✅ Casos de uso criados

Criar usuário

Listar usuários

Deletar usuário (com validação de permissão)

Tudo isso fora do controller, como manda o Clean Code.

🔄 3. UserApplicationService

Você já tem:

Orquestração dos casos de uso

Injeção correta do UserRepository

Código compilando (BUILD SUCCESS) ✅

Exemplo:

public User create(User user) {
return create.execute(user);
}

🌐 4. Controller REST funcionando

Endpoints REST expostos

Comunicação correta com UserApplicationService

Pronto para ser consumido por outros microserviços

🗄️ 5. Persistência

Banco configurado

Repository JPA implementado

Entidade User mapeada corretamente

IDs gerados automaticamente

🔐 6. Spring Security ativo

Security configurado

Swagger liberado sem senha 🎉

Aplicação protegida para endpoints sensíveis

📚 7. Swagger / OpenAPI funcionando

Você já tem:

Swagger configurado

/swagger-ui.html acessível

Documentação automática da API

Isso é ponto positivo forte no desafio.

🐳 8. Base preparada para Docker

Mesmo que ainda não esteja final:

Projeto está estruturado

Separação por microserviço

Pronto para Dockerfile e docker-compose

✅ 9. Build estável
BUILD SUCCESS


✔️ Sem erro de package
✔️ Sem erro de porta
✔️ Sem erro de dependência

OpenAPI/Swagger

Adicione dependência:

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>2.2.0</version>
</dependency>


Banco para teste

http://localhost:8080/h2-console


4️⃣ Preencha o formulário do H2
Campo	Valor
JDBC URL	jdbc:h2:mem:transactiondb
User Name	sa
Password	(em branco)
Driver Class	org.h2.Driver

URL da documentação:

http://localhost:8080/swagger-ui.html

http://localhost:8080/swagger-ui/index.html

No pom.xml do transaction-service (ou outro microserviço):

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>


👉 Sem isso, Swagger não aparece.

Depois de adicionar:

mvn clean install

