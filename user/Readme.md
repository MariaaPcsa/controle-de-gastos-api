# 👤 User Module

Este módulo será responsável pela gestão de **usuários** da aplicação, incluindo:

- 📌 Cadastro de novos usuários
- 🔐 Autenticação (login)
- 👥 Listar usuários
- 🔎 Buscar usuário por ID
- 🛠 Atualizar usuários
- 🗑 Deletar usuário
- 🔒 Utilização de JWT para proteção de rotas


-   | Método | Rota | Descrição |
|--------|------|-----------|

| POST | `/api/users` | Cadastrar usuário |
http://localhost:8080/api/users
| POST | `/api/auth/login` | Autenticar usuário |
http://localhost:8080/api/auth/login
| GET | `/api/users` | Listar usuários |
http://localhost:8080/api/users
| GET | `/api/users/{id}` | Buscar usuário por ID |
http://localhost:8080/api/users/
| PUT | `/api/users/{id}` | Atualizar usuário |
http://localhost:8080/api/users/
{
"name": "Maria Silva",
"email": "maria@email.com"
}
Quando alguém faz um POST ou PUT para criar/atualizar um usuário:

{
"name": "Maria Silva",
"email": "maria@email.com",
"password": "123456",
"type": "USER"
}
| DELETE | `/api/users/{id}` | Remover usuário |


http://localhost:8080/api/excel/upload
file [user_upload_test_cases.xlsx] File

Documentação: http://localhost:8080/swagger-ui/index.html




docker-compose down