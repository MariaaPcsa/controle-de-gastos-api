# 🧪 Testes dos Endpoints - Exemplos Práticos

## 📋 Índice de Exemplos

1. [Autenticação](#autenticação)
2. [Criar Transação](#criar-transação)
3. [Listar com Filtros](#listar-com-filtros)
4. [Atualizar Transação](#atualizar-transação)
5. [Deletar Transação](#deletar-transação)

---

## 🔐 Autenticação

Todos os endpoints requerem um token JWT. Primeiro, obtenha o token do serviço de autenticação:

```bash
# Obter token (assumindo endpoint de login)
curl -X POST "http://localhost:8081/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@email.com",
    "password": "senha"
  }'

# Resposta esperada:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "expires_in": 3600
# }
```

**Armazene o token em uma variável de ambiente:**

```bash
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## ✨ Criar Transação

### Exemplo 1: Criar uma despesa de supermercado
```bash
curl -X POST "http://localhost:8080/api/transactions" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Compras no supermercado",
    "amount": 150.50,
    "currency": "BRL",
    "type": "PURCHASE",
    "category": "Alimentação"
  }'
```

### Exemplo 2: Criar um depósito/renda
```bash
curl -X POST "http://localhost:8080/api/transactions" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Salário do mês",
    "amount": 3000.00,
    "currency": "BRL",
    "type": "DEPOSIT",
    "category": "Renda"
  }'
```

### Exemplo 3: Criar uma transferência internacional (USD)
```bash
curl -X POST "http://localhost:8080/api/transactions" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Transferência internacional",
    "amount": 500.00,
    "currency": "USD",
    "type": "TRANSFER",
    "category": "Transferências"
  }'
```

**Resposta esperada (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "description": "Compras no supermercado",
  "amount": 150.50,
  "originalAmount": 150.50,
  "currency": "BRL",
  "category": "Alimentação",
  "type": "PURCHASE",
  "typeDescription": "Compra",
  "signedAmount": -150.50,
  "createdAt": "18-03-2026"
}
```

---

## 📊 Listar com Filtros

### Exemplo 1: Listar todas as transações (sem filtros)
```bash
curl -X GET "http://localhost:8080/api/transactions" \
  -H "Authorization: Bearer $TOKEN"
```

### Exemplo 2: Listar apenas despesas
```bash
curl -X GET "http://localhost:8080/api/transactions?type=PURCHASE" \
  -H "Authorization: Bearer $TOKEN"
```

### Exemplo 3: Listar apenas receitas (depósitos)
```bash
curl -X GET "http://localhost:8080/api/transactions?type=DEPOSIT" \
  -H "Authorization: Bearer $TOKEN"
```

### Exemplo 4: Listar por categoria
```bash
curl -X GET "http://localhost:8080/api/transactions?category=Alimentação" \
  -H "Authorization: Bearer $TOKEN"
```

### Exemplo 5: Listar por período (último mês)
```bash
curl -X GET "http://localhost:8080/api/transactions?startDate=18-02-2026&endDate=18-03-2026" \
  -H "Authorization: Bearer $TOKEN"
```

### Exemplo 6: Listar com paginação (18-03-2026)
```bash
# Primeira página
curl -X GET "http://localhost:8080/api/transactions?startDate=18-03-2026&endDate=18-03-2026&page=0&pageSize=10" \
  -H "Authorization: Bearer $TOKEN"

# Segunda página
curl -X GET "http://localhost:8080/api/transactions?startDate=18-03-2026&endDate=18-03-2026&page=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN"
```

### Exemplo 7: Listar ordenado por valor (maior para menor)
```bash
curl -X GET "http://localhost:8080/api/transactions?sortBy=amount&sortDirection=DESC" \
  -H "Authorization: Bearer $TOKEN"
```

### Exemplo 8: Combinado - Despesas de "Alimentação" no mês, paginadas por valor
```bash
curl -X GET "http://localhost:8080/api/transactions?type=PURCHASE&category=Alimentação&startDate=01-03-2026&endDate=31-03-2026&sortBy=amount&sortDirection=DESC&page=0&pageSize=20" \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta esperada (200 OK):**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "description": "Compras no supermercado",
      "amount": 150.50,
      "originalAmount": 150.50,
      "currency": "BRL",
      "category": "Alimentação",
      "type": "PURCHASE",
      "typeDescription": "Compra",
      "signedAmount": -150.50,
      "createdAt": "15-03-2026"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "description": "Restaurante",
      "amount": 85.00,
      "originalAmount": 85.00,
      "currency": "BRL",
      "category": "Alimentação",
      "type": "PURCHASE",
      "typeDescription": "Compra",
      "signedAmount": -85.00,
      "createdAt": "10-03-2026"
    }
  ],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 2,
  "totalPages": 1,
  "isFirst": true,
  "isLast": true,
  "hasNext": false,
  "hasPrevious": false
}
```

---

## ✏️ Atualizar Transação

### Exemplo 1: Atualizar descrição e categoria
```bash
curl -X PUT "http://localhost:8080/api/transactions/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Supermercado atualizado",
    "amount": 150.50,
    "currency": "BRL",
    "type": "PURCHASE",
    "category": "Alimentação - Supermercado"
  }'
```

### Exemplo 2: Atualizar valor
```bash
curl -X PUT "http://localhost:8080/api/transactions/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Compras no supermercado",
    "amount": 175.99,
    "currency": "BRL",
    "type": "PURCHASE",
    "category": "Alimentação"
  }'
```

### Exemplo 3: Atualizar tipo de transação
```bash
curl -X PUT "http://localhost:8080/api/transactions/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Transferência para conta poupança",
    "amount": 500.00,
    "currency": "BRL",
    "type": "TRANSFER",
    "category": "Transferências"
  }'
```

**Resposta esperada (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "description": "Supermercado atualizado",
  "amount": 150.50,
  "originalAmount": 150.50,
  "currency": "BRL",
  "category": "Alimentação - Supermercado",
  "type": "PURCHASE",
  "typeDescription": "Compra",
  "signedAmount": -150.50,
  "createdAt": "15-03-2026"
}
```

---

## 🗑️ Deletar Transação

### Exemplo 1: Deletar com confirmação explícita
```bash
curl -X DELETE "http://localhost:8080/api/transactions/550e8400-e29b-41d4-a716-446655440000?confirmDelete=true" \
  -H "Authorization: Bearer $TOKEN"
```

### Exemplo 2: Tentar deletar sem confirmação (erro)
```bash
curl -X DELETE "http://localhost:8080/api/transactions/550e8400-e29b-41d4-a716-446655440000?confirmDelete=false" \
  -H "Authorization: Bearer $TOKEN"

# Resposta esperada (400 Bad Request)
```

### Exemplo 3: Deletar usando valor padrão
```bash
# O padrão é confirmDelete=true, então pode ser omitido
curl -X DELETE "http://localhost:8080/api/transactions/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta esperada (204 No Content):**
```
(sem body - apenas headers)
```

---

## 📝 Resumo de Status HTTP

| Código | Endpoint | Descrição |
|--------|----------|-----------|
| 200 | GET, PUT | Sucesso |
| 201 | POST | Criado com sucesso |
| 204 | DELETE | Deletado com sucesso |
| 400 | Qualquer | Dados inválidos |
| 403 | Qualquer | Sem permissão |
| 404 | GET, PUT, DELETE | Não encontrado |
| 500 | Qualquer | Erro interno do servidor |

---

## 🔬 Dicas de Teste

### Usar Variáveis de Ambiente (Postman/Insomnia)

Crie uma variável global:
```
TOKEN = seu_token_jwt_aqui
BASE_URL = http://localhost:8080
```

Depois use nos URLs:
```
{{BASE_URL}}/api/transactions
Authorization: Bearer {{TOKEN}}
```

### Testar Período Completo

1. **Criar** uma transação
2. **Listar** para confirmar que foi criada
3. **Atualizar** a transação
4. **Listar** para confirmar a atualização
5. **Deletar** a transação
6. **Listar** para confirmar a deleção

---

## ⚠️ Possíveis Erros e Soluções

| Erro | Causa | Solução |
|------|-------|---------|
| 401 Unauthorized | Token ausente ou inválido | Verify se o token está no header Authorization |
| 400 Bad Request | Dados inválidos | Verifique os tipos de dados (String, BigDecimal, Enum) |
| 404 Not Found | Transação não existe | Verifique se o UUID está correto |
| 403 Forbidden | Não é o dono | Só é possível alterar/deletar suas próprias transações |


