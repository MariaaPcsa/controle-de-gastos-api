# 💰 Controle de Gastos API

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Maven](https://img.shields.io/badge/Maven-3.9-blue)
![Docker](https://img.shields.io/badge/Docker-ready-blue)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-green)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow)

> ⚠️ **Este projeto ainda está em desenvolvimento.**  
> Algumas funcionalidades, endpoints e integrações podem sofrer alterações.

API REST para **gestão financeira pessoal**, permitindo que usuários controlem seus gastos e receitas, visualizem relatórios e importem dados.

Projeto desenvolvido como **desafio final de curso**, utilizando boas práticas de arquitetura, segurança e conteinerização com Docker.

---

## 🚀 Tecnologias

- ☕ Java 21  
- Spring Boot  
- 🔐 Spring Security + JWT  
- 🐳 Docker + Docker Compose  
- 📦 Maven  
- 🗄️ PostgreSQL  
- 📑 Swagger / OpenAPI  
- 📊 Importação via Excel  

---

## 📁 Estrutura do Projeto

controle-de-gastos-api
┣ 📂 analytics-service # Relatórios e análises financeiras
┣ 📂 transaction-service # CRUD de transações (receitas e despesas)
┣ 📂 user # Cadastro e autenticação de usuários
┣ 📜 docker-compose.yml
┣ 📜 pom.xml
┣ 📜 users_upload.test.xlsx
┗ 📜 README.md


---

## 🔐 Funcionalidades (Em evolução 🚧)

- ✅ Cadastro de usuários  
- ✅ Login com JWT  
- ✅ CRUD de transações (receitas e despesas)  
- 🟡 Relatórios financeiros *(em desenvolvimento)*  
- 🟡 Importação de usuários via Excel *(em desenvolvimento)*  
- ✅ Proteção de rotas com Spring Security  
- ✅ Documentação automática com Swagger  

---

## ▶️ Como Rodar o Projeto

### 📌 Pré-requisitos

- Java 21  
- Maven  
- Docker e Docker Compose  

---

### 🐳 Subir tudo com Docker (Recomendado)

```bash
docker-compose up -d --build
A API estará disponível em:

http://localhost:8080
💻 Rodar Local (Sem Docker)
mvn clean install
mvn spring-boot:run
⚙️ Configuração (application.yml / application.properties)
spring.datasource.url=jdbc:postgresql://localhost:5432/controle_gastos
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update

jwt.secret=segredo_super_secreto
📑 Documentação Swagger
Após subir o projeto, acesse:

http://localhost:8080/swagger-ui.html
ou

http://localhost:8080/swagger-ui/index.html
Você poderá testar todos os endpoints direto pelo navegador 😎

📌 Endpoints Principais (Podem mudar 🚧)
🔑 Autenticação
Método	Rota	Descrição
POST	/api/auth/register	Cadastro de usuário
POST	/api/auth/login	Login e geração do JWT

👤 Usuários
Método	Rota	Descrição
GET	/api/users	Lista usuários
GET	/api/users/{id}	Buscar por ID
DELETE	/api/users/{id}	Deletar usuário

💸 Transações
Método	Rota	Descrição
POST	/api/transactions	Criar transação
GET	/api/transactions	Listar transações
PUT	/api/transactions/{id}	Atualizar
DELETE	/api/transactions/{id}	Remover

📊 Relatórios (Em desenvolvimento 🚧)
Método	Rota	Descrição
GET	/api/analytics/summary	Resumo financeiro
GET	/api/analytics/monthly	Gastos por mês

📥 Importação de Excel (Em desenvolvimento 🚧)
Arquivo de exemplo:

users_upload.test.xlsx
Endpoint:

POST /api/users/import
Formato esperado:

Nome	Email	Senha
Maria	maria@email.com	123456

🔒 Segurança
Autenticação via JWT

Rotas protegidas com Spring Security

Enviar token no header:

Authorization: Bearer SEU_TOKEN_AQUI

🧪 Testes
mvn test

🚀 Deploy (Sugestão)
Você pode publicar no:

Render

Railway

Fly.io

EC2

Configure corretamente:

Variáveis de ambiente

Banco de dados

Porta da aplicação

Segredo JWT

🧠 Boas Práticas Utilizadas
Clean Architecture

DTOs

Separação de camadas

Tratamento de exceções

Validações

Logs

Segurança JWT

Docker

👩‍💻 Contribuição
Contribuições são bem-vindas!

Faça um fork

Crie uma branch

Faça commit

Abra um Pull Request

📄 Licença
Este projeto está sob a licença MIT.


---

Se quiser, eu te entrego agora:
- ✅ `docker-compose.yml` pronto  
- ✅ `application.yml` configurado  
- ✅ Configuração completa do Swagger  
- ✅ Segurança JWT revisada  
- ✅ Checklist do que falta pra considerar o projeto “finalizado” 🚀
