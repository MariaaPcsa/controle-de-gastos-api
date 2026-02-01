# 👤 User Module

Este módulo será responsável pela gestão de **usuários** da aplicação, incluindo:

- 📌 Cadastro de novos usuários
- 🔐 Autenticação (login)
- 👥 Listar usuários
- 🔎 Buscar usuário por ID
- 🛠 Atualizar usuários
- 🗑 Deletar usuário
- 🔒 Utilização de JWT para proteção de rotas
- | Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/users` | Cadastrar usuário |
| POST | `/api/auth/login` | Autenticar usuário |
| GET | `/api/users` | Listar usuários |
| GET | `/api/users/{id}` | Buscar usuário por ID |
| PUT | `/api/users/{id}` | Atualizar usuário |
| DELETE | `/api/users/{id}` | Remover usuário |

Documentação: http://localhost:8080/swagger-ui/index.html
