# 🚀 QUICK REFERENCE - Parâmetros de Transações

## 📋 Resumo Rápido dos Endpoints

### GET /api/transactions - LISTAGEM
```
Parâmetros de Query:
┌─────────────────┬──────────────┬───────────┬─────────────────────────────┐
│ Parâmetro       │ Tipo         │ Padrão    │ Descrição                   │
├─────────────────┼──────────────┼───────────┼─────────────────────────────┤
│ category        │ String       │ -         │ Filtrar por categoria       │
│ type            │ String       │ -         │ DEPOSIT/WITHDRAW/TRANSFER   │
│ startDate       │ DateTime     │ -         │ Formato: 2026-03-18T00:00   │
│ endDate         │ DateTime     │ -         │ Formato: 2026-03-18T23:59   │
│ page            │ Integer      │ 0         │ Página (0-indexed)          │
│ pageSize        │ Integer      │ 10        │ Itens por página            │
│ sortBy          │ String       │ createdAt │ createdAt ou amount         │
│ sortDirection   │ String       │ DESC      │ ASC ou DESC                 │
└─────────────────┴──────────────┴───────────┴─────────────────────────────┘

Resposta: PagedResponseDTO<TransactionResponseDTO>
Status: 200 OK
```

### PUT /api/transactions/{id} - ATUALIZAÇÃO
```
Parâmetro Path:
┌─────────┬───────┐
│ id      │ UUID  │
└─────────┴───────┘

Request Body (UpdateTransactionDTO):
{
  "description": "String",     // obrigatório
  "amount": 0.00,             // obrigatório, > 0.01
  "currency": "BRL",          // obrigatório
  "type": "PURCHASE",         // obrigatório
  "category": "String"        // obrigatório
}

Resposta: TransactionResponseDTO
Status: 200 OK
```

### DELETE /api/transactions/{id} - DELEÇÃO
```
Parâmetro Path:
┌─────────┬───────┐
│ id      │ UUID  │
└─────────┴───────┘

Parâmetro Query:
┌──────────────────┬─────────┬───────┐
│ confirmDelete    │ Boolean │ true  │
└──────────────────┴─────────┴───────┘

Resposta: (vazio)
Status: 204 No Content
```

---

## 🎯 Exemplos Rápidos

### Listar TUDO
```bash
GET /api/transactions
```

### Listar Despesas do Mês
```bash
GET /api/transactions?type=PURCHASE&startDate=2026-03-01T00:00:00&endDate=2026-03-31T23:59:59
```

### Listar por Categoria (Alimentação)
```bash
GET /api/transactions?category=Alimentação
```

### Listar Página 2 (10 itens por página)
```bash
GET /api/transactions?page=1&pageSize=10
```

### Listar Ordenado por Valor (maior para menor)
```bash
GET /api/transactions?sortBy=amount&sortDirection=DESC
```

### Combinar Filtros
```bash
GET /api/transactions?category=Alimentação&type=PURCHASE&startDate=2026-01-01T00:00:00&endDate=2026-03-31T23:59:59&page=0&pageSize=20&sortBy=amount&sortDirection=DESC
```

### Atualizar Transação
```bash
PUT /api/transactions/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json

{
  "description": "Nova descrição",
  "amount": 200.00,
  "currency": "BRL",
  "type": "PURCHASE",
  "category": "Alimentação"
}
```

### Deletar Transação
```bash
DELETE /api/transactions/550e8400-e29b-41d4-a716-446655440000?confirmDelete=true
```

---

## 💡 Tipos de Transação (type)

```
DEPOSIT    → Depósito     (Aumenta saldo)
WITHDRAW   → Saque        (Diminui saldo)
TRANSFER   → Transferência (Diminui saldo)
PURCHASE   → Compra       (Diminui saldo)
```

---

## 🔑 Headers Obrigatórios

```
Authorization: Bearer {TOKEN}
Content-Type: application/json  (apenas para POST/PUT)
```

---

## 📊 Resposta de Paginação

```json
{
  "content": [ ... ],           // Lista de TransactionResponseDTO
  "pageNumber": 0,              // Página atual (0-indexed)
  "pageSize": 10,               // Itens por página
  "totalElements": 100,         // Total de itens
  "totalPages": 10,             // Total de páginas
  "isFirst": true,              // É a primeira página?
  "isLast": false,              // É a última página?
  "hasNext": true,              // Tem próxima página?
  "hasPrevious": false          // Tem página anterior?
}
```

---

## ⚠️ Status HTTP

```
200 OK                  → Sucesso GET/PUT
201 Created             → Sucesso POST
204 No Content          → Sucesso DELETE
400 Bad Request         → Dados inválidos
401 Unauthorized        → Token ausente/inválido
403 Forbidden           → Sem permissão
404 Not Found           → Recurso não existe
500 Internal Error      → Erro no servidor
```

---

## 🔍 Formato de Data

```
Formato: dd-MM-yyyy

Exemplos:
18-03-2026     → 18 de março de 2026
01-03-2026     → Início do mês (01 de março)
31-03-2026     → Fim do mês (31 de março)
```

---

## 🎓 Dicas de Uso

**Tip 1:** Use `sortBy=amount&sortDirection=DESC` para ver maiores gastos primeiro

**Tip 2:** Combine `category` + `type` para filtros específicos

**Tip 3:** Use `pageSize=50` para mais resultados por página

**Tip 4:** Sempre use `?confirmDelete=true` para deletar (prevenção)

**Tip 5:** As datas são filtros inclusivos (>= startDate e <= endDate)

---

## 🚫 Validações Automáticas

UPDATE rejeita se:
- ❌ description vazia
- ❌ amount ≤ 0.01
- ❌ currency vazia
- ❌ type vazio
- ❌ category vazia

---

## 📁 Arquivo de Referência

Para consultas mais detalhadas, veja:
- `PARAMETRIZACAO_TRANSACOES.md` - Documentação completa
- `EXEMPLOS_TESTES.md` - Exemplos com curl
- `ARQUITETURA_DTOS.md` - Estrutura técnica

