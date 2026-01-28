//package com.finance.transaction_service;
//
//import com.finance.transaction_service.domain.model.Transaction;
//import com.finance.transaction_service.domain.model.TransactionType;
//import com.finance.transaction_service.domain.repository.TransactionRepository;
//import com.finance.transaction_service.domain.usecase.CreateTransactionUseCase;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//
//@Test
//void should_create_transaction() {
//	TransactionRepository repo = mock(TransactionRepository.class);
//	CreateTransactionUseCase useCase = new CreateTransactionUseCase(repo);
//
//	Transaction t = new Transaction(1L, BigDecimal.TEN, "BRL", TransactionType.DEPOSIT, "SALARIO");
//	when(repo.save(any())).thenReturn(t);
//
//	Transaction result = useCase.execute(t);
//
//	assertEquals(TransactionType.DEPOSIT, result.getType());
}

