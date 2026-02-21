package com.finance.transaction_service.domain.model;

public enum     TransactionType {

    DEPOSIT("Depósito", Operation.INCOME),
    WITHDRAW("Saque", Operation.EXPENSE),
    TRANSFER("Transferência", Operation.EXPENSE),
    PURCHASE("Compra", Operation.EXPENSE);

    private final String description;
    private final Operation operation;

    TransactionType(String description, Operation operation) {
        this.description = description;
        this.operation = operation;
    }

    public String getDescription() {
        return description;
    }

    public boolean isIncome() {
        return operation == Operation.INCOME;
    }

    public boolean isExpense() {
        return operation == Operation.EXPENSE;
    }

    private enum Operation {
        INCOME,
        EXPENSE
    }
}
