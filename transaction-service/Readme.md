🧱 VISÃO GERAL – TRANSACTION SERVICE
Responsabilidades

Registrar transações financeiras

Converter moeda (BrasilAPI)

Publicar eventos no Kafka

Servir dados para o analysis-service


📁 ESTRUTURA FINAL
transaction-service/
│
├── domain/
│   ├── model/
│   │   ├── Transaction.java
│   │   └── TransactionType.java
│   ├── repository/
│   │   └── TransactionRepository.java
│   └── usecase/
│       ├── CreateTransactionUseCase.java
│       ├── ListTransactionsUseCase.java
│       └── DeleteTransactionUseCase.java
│
├── application/
│   └── service/
│       └── TransactionApplicationService.java
│
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/TransactionEntity.java
│   │   ├── mapper/TransactionMapper.java
│   │   └── repository/TransactionRepositoryJpa.java
│   ├── kafka/
│   │   └── TransactionProducer.java
│   └── external/
│       └── ExchangeRateClient.java
│
├── presentation/
│   ├── controller/
│   │   └── TransactionController.java
│   └── dto/
│       ├── TransactionRequestDTO.java
│       └── TransactionResponseDTO.java
│
└── Dockerfile

🧠 DOMAIN (NEGÓCIO PURO)
Transaction.java
package domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private TransactionType type;
    private String category;
    private LocalDateTime createdAt;

    public Transaction(Long userId, BigDecimal amount, String currency,
                       TransactionType type, String category) {
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.category = category;
        this.createdAt = LocalDateTime.now();
    }

    // getters
}

TransactionType.java
package domain.model;

public enum TransactionType {
DEPOSIT,
WITHDRAW,
TRANSFER,
PURCHASE
}

TransactionRepository (PORTA)
package domain.repository;

import domain.model.Transaction;

import java.util.List;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    List<Transaction> findByUserId(Long userId);

    void delete(Long id);
}

🧩 USE CASES (REGRAS DO DESAFIO)
CreateTransactionUseCase
package domain.usecase;

import domain.model.Transaction;
import domain.repository.TransactionRepository;

public class CreateTransactionUseCase {

    private final TransactionRepository repository;

    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction execute(Transaction transaction) {
        return repository.save(transaction);
    }
}

ListTransactionsUseCase
package domain.usecase;

import domain.model.Transaction;
import domain.repository.TransactionRepository;

import java.util.List;

public class ListTransactionsUseCase {

    private final TransactionRepository repository;

    public ListTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> execute(Long userId) {
        return repository.findByUserId(userId);
    }
}

⚙️ APPLICATION SERVICE
package application.service;

import domain.model.Transaction;
import domain.repository.TransactionRepository;
import domain.usecase.*;

import java.util.List;

public class TransactionApplicationService {

    private final CreateTransactionUseCase create;
    private final ListTransactionsUseCase list;

    public TransactionApplicationService(TransactionRepository repository) {
        this.create = new CreateTransactionUseCase(repository);
        this.list = new ListTransactionsUseCase(repository);
    }

    public Transaction create(Transaction transaction) {
        return create.execute(transaction);
    }

    public List<Transaction> list(Long userId) {
        return list.execute(userId);
    }
}

🏗 INFRASTRUCTURE
TransactionEntity
@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id @GeneratedValue
    private Long id;

    private Long userId;
    private BigDecimal amount;
    private String currency;
    private String category;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private LocalDateTime createdAt;
}

TransactionRepositoryJpa
@Repository
public interface TransactionRepositoryJpa
extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByUserId(Long userId);
}

TransactionMapper
public class TransactionMapper {

    public static Transaction toDomain(TransactionEntity e) {
        return new Transaction(
                e.getUserId(),
                e.getAmount(),
                e.getCurrency(),
                e.getType(),
                e.getCategory()
        );
    }

    public static TransactionEntity toEntity(Transaction t) {
        TransactionEntity e = new TransactionEntity();
        e.setUserId(t.getUserId());
        e.setAmount(t.getAmount());
        e.setCurrency(t.getCurrency());
        e.setType(t.getType());
        e.setCategory(t.getCategory());
        e.setCreatedAt(t.getCreatedAt());
        return e;
    }
}

🔌 KAFKA (EVENTO DE TRANSAÇÃO)
@Service
public class TransactionProducer {

    private final KafkaTemplate<String, TransactionResponseDTO> kafka;

    public TransactionProducer(KafkaTemplate<String, TransactionResponseDTO> kafka) {
        this.kafka = kafka;
    }

    public void publish(TransactionResponseDTO dto) {
        kafka.send("transactions-topic", dto);
    }
}

🌍 BRASILAPI – CÂMBIO
@Component
public class ExchangeRateClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal convert(BigDecimal value, String from, String to) {
        String url = "https://brasilapi.com.br/api/cambio/v1/converter"
                   + "?valor=" + value + "&moedaOrigem=" + from + "&moedaDestino=" + to;

        Map response = restTemplate.getForObject(url, Map.class);
        return new BigDecimal(response.get("valorConvertido").toString());
    }
}

🌐 CONTROLLER
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionApplicationService service;
    private final TransactionProducer producer;

    @PostMapping
    public TransactionResponseDTO create(@RequestBody TransactionRequestDTO dto,
                                         @AuthenticationPrincipal User user) {

        Transaction transaction = dto.toDomain(user.getId());
        Transaction saved = service.create(transaction);

        TransactionResponseDTO response = TransactionResponseDTO.fromDomain(saved);
        producer.publish(response);

        return response;
    }

    @GetMapping
    public List<TransactionResponseDTO> list(@AuthenticationPrincipal User user) {
        return service.list(user.getId())
                .stream()
                .map(TransactionResponseDTO::fromDomain)
                .toList();
    }
}

🧪 TESTES (EXEMPLO DE BANCA)
@Test
void should_create_transaction() {
TransactionRepository repo = mock(TransactionRepository.class);
CreateTransactionUseCase useCase = new CreateTransactionUseCase(repo);

    Transaction t = new Transaction(1L, BigDecimal.TEN, "BRL", TransactionType.DEPOSIT, "SALARIO");
    when(repo.save(any())).thenReturn(t);

    Transaction result = useCase.execute(t);

    assertEquals(TransactionType.DEPOSIT, result.getType());
}