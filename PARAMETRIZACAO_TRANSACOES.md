# Parametrização de Transações - Documentação de Implementação

## 🎯 Resumo das Mudanças

Foram implementados parâmetros e DTOs para melhorar a gestão de transações nos endpoints de listagem, atualização e deleção.

---

## 📦 DTOs Criados

### 1. **FilterTransactionDTO**
Novo DTO para filtrar transações com suporte a paginação e ordenação.

**Parâmetros disponíveis:**
- `category` (String, opcional) - Filtrar por categoria
- `type` (TransactionType, opcional) - Filtrar por tipo (DEPOSIT, WITHDRAW, TRANSFER, PURCHASE)
- `startDate` (LocalDateTime, opcional) - Data inicial do período
- `endDate` (LocalDateTime, opcional) - Data final do período
- `page` (Integer, padrão: 0) - Número da página (0-indexed)
- `pageSize` (Integer, padrão: 10) - Quantidade de registros por página
- `sortBy` (String, padrão: "createdAt") - Campo para ordenação (createdAt, amount)
- `sortDirection` (String, padrão: "DESC") - Direção de ordenação (ASC ou DESC)

**Métodos auxiliares:**
- `hasCategory()` - Verifica se categoria foi informada
- `hasType()` - Verifica se tipo foi informado
- `hasDateRange()` - Verifica se período foi informado
- `hasFilters()` - Verifica se há algum filtro ativo

---

### 2. **UpdateTransactionDTO**
DTO específico para atualizar transações com validações.

**Parâmetros obrigatórios:**
- `description` (String) - Descrição da transação
- `amount` (BigDecimal) - Valor da transação (> 0)
- `currency` (String) - Código de moeda (ex: USD, EUR, BRL)
- `type` (TransactionType) - Tipo de transação
- `category` (String) - Categoria da transação

**Validações aplicadas:**
- Descrição não pode estar em branco
- Valor deve ser maior que 0.01
- Tipo é obrigatório
- Categoria é obrigatória
- Moeda é obrigatória

---

### 3. **PagedResponseDTO<T>**
DTO genérico para respostas paginadas.

**Parâmetros retornados:**
- `content` (List<T>) - Lista de itens da página
- `pageNumber` (Integer) - Número da página atual (0-indexed)
- `pageSize` (Integer) - Quantidade de itens por página
- `totalElements` (Long) - Total de elementos
- `totalPages` (Integer) - Total de páginas
- `isFirst` (Boolean) - Indica se é a primeira página
- `isLast` (Boolean) - Indica se é a última página
- `hasNext` (Boolean) - Indica se há próxima página
- `hasPrevious` (Boolean) - Indica se há página anterior

**Factory method:**
```java
PagedResponseDTO<T> of(List<T> content, Integer pageNumber, Integer pageSize, Long totalElements)
```

---

### 4. **DeleteTransactionDTO**
DTO para deleção com confirmação.

**Parâmetros:**
- `transactionId` (UUID) - ID da transação
- `confirmDelete` (Boolean, padrão: false) - Confirmação de deleção
- `reason` (String, opcional) - Motivo da deleção

**Método de validação:**
```java
boolean isValid() - Valida se transactionId e confirmDelete estão preenchidos
```

---

## 🔄 Endpoints Atualizados

### 1. **GET /api/transactions** - Listar com Filtros e Paginação

#### Query Parameters:
```
GET /api/transactions?category=Alimentação&type=EXPENSE&startDate=01-01-2024&endDate=31-12-2024&page=0&pageSize=10&sortBy=createdAt&sortDirection=DESC
```

| Parâmetro | Tipo | Obrigatório | Padrão | Descrição |
|-----------|------|-------------|--------|-----------|
| `category` | String | Não | - | Filtrar por categoria |
| `type` | String | Não | - | Filtrar por tipo (DEPOSIT, WITHDRAW, TRANSFER, PURCHASE) |
| `startDate` | LocalDateTime | Não | - | Data inicial (formato dd-MM-yyyy: 01-01-2024) |
| `endDate` | LocalDateTime | Não | - | Data final (formato dd-MM-yyyy: 31-12-2024) |
| `page` | Integer | Não | 0 | Número da página (0-indexed) |
| `pageSize` | Integer | Não | 10 | Registros por página |
| `sortBy` | String | Não | createdAt | Campo para ordenação (createdAt, amount) |
| `sortDirection` | String | Não | DESC | Direção: ASC ou DESC |

#### Exemplo de Requisição:
```bash
curl -X GET "http://localhost:8080/api/transactions?category=Alimentação&type=EXPENSE&page=0&pageSize=10&sortBy=createdAt&sortDirection=DESC" \
  -H "Authorization: Bearer <token>"
```

#### Resposta Esperada:
```json
{
  "content": [
    {
      "id": "uuid-123",
      "description": "Supermercado",
      "amount": 150.50,
      "originalAmount": 150.50,
      "currency": "BRL",
      "category": "Alimentação",
      "type": "EXPENSE",
      "typeDescription": "Compra",
      "signedAmount": -150.50,
      "createdAt": "15-01-2024"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 25,
  "totalPages": 3,
  "isFirst": true,
  "isLast": false,
  "hasNext": true,
  "hasPrevious": false
}
```

---

### 2. **PUT /api/transactions/{id}** - Atualizar Transação

#### Request Body (UpdateTransactionDTO):
```json
{
  "description": "Descrição atualizada",
  "amount": 200.00,
  "currency": "BRL",
  "type": "EXPENSE",
  "category": "Categoria atualizada"
}
```

#### Parâmetros:
- `id` (UUID, path) - ID da transação a ser atualizada
- `body` (UpdateTransactionDTO) - Dados da transação

#### Validações:
- Descrição não pode estar em branco
- Valor deve ser > 0.01
- Tipo é obrigatório
- Categoria é obrigatória

#### Resposta Esperada (200 OK):
```json
{
  "id": "uuid-123",
  "description": "Descrição atualizada",
  "amount": 200.00,
  "originalAmount": 200.00,
  "currency": "BRL",
  "category": "Categoria atualizada",
  "type": "EXPENSE",
  "typeDescription": "Compra",
  "signedAmount": -200.00,
  "createdAt": "15-01-2024"
}
```

---

### 3. **DELETE /api/transactions/{id}** - Deletar Transação

#### Query Parameters:
```
DELETE /api/transactions/{id}?confirmDelete=true
```

| Parâmetro | Tipo | Obrigatório | Padrão | Descrição |
|-----------|------|-------------|--------|-----------|
| `id` | UUID | Sim | - | ID da transação |
| `confirmDelete` | Boolean | Não | true | Confirmação de deleção |

#### Exemplo de Requisição:
```bash
curl -X DELETE "http://localhost:8080/api/transactions/uuid-123?confirmDelete=true" \
  -H "Authorization: Bearer <token>"
```

#### Respostas:
- **204 No Content** - Transação deletada com sucesso
- **400 Bad Request** - Confirmação não fornecida
- **404 Not Found** - Transação não encontrada
- **403 Forbidden** - Sem permissão para deletar

---

## 🔐 Segurança

Todos os endpoints requerem autenticação via Bearer Token:

```bash
Authorization: Bearer <token_jwt>
```

Os dados da transação são associados ao usuário autenticado (CustomUserDetails).

---

## 📊 Exemplos de Uso Completos

### Exemplo 1: Listar todas as despesas do mês
```bash
curl -X GET "http://localhost:8080/api/transactions?type=EXPENSE&startDate=01-03-2024&endDate=31-03-2024&page=0&pageSize=20" \
  -H "Authorization: Bearer eyJhbGc..."
```

### Exemplo 2: Listar transações de uma categoria, ordenadas por valor decrescente
```bash
curl -X GET "http://localhost:8080/api/transactions?category=Alimentação&sortBy=amount&sortDirection=DESC&page=0&pageSize=10" \
  -H "Authorization: Bearer eyJhbGc..."
```

### Exemplo 3: Atualizar uma transação
```bash
curl -X PUT "http://localhost:8080/api/transactions/550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Compra no supermercado atualizada",
    "amount": 175.50,
    "currency": "BRL",
    "type": "EXPENSE",
    "category": "Alimentação"
  }'
```

### Exemplo 4: Deletar uma transação
```bash
curl -X DELETE "http://localhost:8080/api/transactions/550e8400-e29b-41d4-a716-446655440000?confirmDelete=true" \
  -H "Authorization: Bearer eyJhbGc..."
```

---

## 📝 Notas Importantes

1. **Filtros são opcionais** - Pode-se fazer requisições sem nenhum filtro
2. **Paginação** - Por padrão, retorna 10 itens por página
3. **Conversão de moeda** - Valores são convertidos para BRL automaticamente
4. **Ordenação** - Suporta "createdAt" e "amount"
5. **Segurança** - Usuários só podem ver/editar/deletar suas próprias transações

---

## 🚀 Status da Implementação

✅ FilterTransactionDTO - Criado e funcional
✅ UpdateTransactionDTO - Criado e funcional
✅ PagedResponseDTO - Criado e funcional
✅ DeleteTransactionDTO - Criado e funcional
✅ Endpoint GET com filtros - Implementado e testado
✅ Endpoint PUT com validações - Implementado e testado
✅ Endpoint DELETE com confirmação - Implementado e testado
✅ Compilação - BUILD SUCCESS

