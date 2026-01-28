🧱 ANALYSIS-SERVICE — VISÃO GERAL
Responsabilidades

✔ Consumir eventos do Kafka (transações)
✔ Persistir dados de análise
✔ Gerar resumo financeiro
✔ Exportar relatórios
✔ NÃO conhece user-service nem transaction-service diretamente

📁 ESTRUTURA FINAL
analysis-service/
│
├── domain/
│   ├── model/
│   │   └── ExpenseSummary.java
│   ├── repository/
│   │   └── ExpenseRepository.java
│   └── usecase/
│       ├── ProcessTransactionUseCase.java
│       └── GenerateReportUseCase.java
│
├── application/
│   └── service/
│       └── AnalysisApplicationService.java
│
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/ExpenseEntity.java
│   │   ├── mapper/ExpenseMapper.java
│   │   └── repository/ExpenseRepositoryJpa.java
│   ├── kafka/
│   │   └── TransactionConsumer.java
│   └── report/
│       ├── ExcelReportGenerator.java
│       └── PdfReportGenerator.java
│
├── presentation/
│   └── controller/
│       └── AnalysisController.java
│
└── Dockerfile

✅ PROJETO FINAL — CHECKLIST DO DESAFIO

✔ Clean Architecture real
✔ 3 microserviços
✔ Docker + PostgreSQL + Kafka
✔ Segurança (JWT)
✔ Upload Excel
✔ API pública (BrasilAPI)
✔ API Mock (saldo)
✔ Relatórios
✔ Testes unitários
✔ OpenAPI


🧠 DOMAIN (NEGÓCIO PURO)
ExpenseSummary
package domain.model;

import java.math.BigDecimal;

public class ExpenseSummary {

    private String category;
    private BigDecimal total;

    public ExpenseSummary(String category, BigDecimal total) {
        this.category = category;
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getTotal() {
        return total;
    }
}

ExpenseRepository (PORTA)
package domain.repository;

import domain.model.ExpenseSummary;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository {

    void save(Long userId, String category, String type, 
              double amount, LocalDate date);

    List<ExpenseSummary> summaryByCategory(Long userId);

    List<ExpenseSummary> summaryByMonth(Long userId, int year, int month);
}

🧩 USE CASES
ProcessTransactionUseCase
package domain.usecase;

import domain.repository.ExpenseRepository;

import java.time.LocalDate;

public class ProcessTransactionUseCase {

    private final ExpenseRepository repository;

    public ProcessTransactionUseCase(ExpenseRepository repository) {
        this.repository = repository;
    }

    public void execute(Long userId, String category, String type,
                        double amount, LocalDate date) {

        if (type.equals("DEPOSIT")) return; // regra de negócio

        repository.save(userId, category, type, amount, date);
    }
}

GenerateReportUseCase
package domain.usecase;

import domain.model.ExpenseSummary;
import domain.repository.ExpenseRepository;

import java.util.List;

public class GenerateReportUseCase {

    private final ExpenseRepository repository;

    public GenerateReportUseCase(ExpenseRepository repository) {
        this.repository = repository;
    }

    public List<ExpenseSummary> byCategory(Long userId) {
        return repository.summaryByCategory(userId);
    }

    public List<ExpenseSummary> byMonth(Long userId, int year, int month) {
        return repository.summaryByMonth(userId, year, month);
    }
}

⚙️ APPLICATION SERVICE
package application.service;

import domain.usecase.*;

import java.time.LocalDate;

public class AnalysisApplicationService {

    private final ProcessTransactionUseCase process;
    private final GenerateReportUseCase report;

    public AnalysisApplicationService(ProcessTransactionUseCase process,
                                      GenerateReportUseCase report) {
        this.process = process;
        this.report = report;
    }

    public void processTransaction(Long userId, String category,
                                   String type, double amount,
                                   LocalDate date) {
        process.execute(userId, category, type, amount, date);
    }

    public GenerateReportUseCase report() {
        return report;
    }
}

🏗 INFRASTRUCTURE
ExpenseEntity
@Entity
@Table(name = "expenses")
public class ExpenseEntity {

    @Id @GeneratedValue
    private Long id;

    private Long userId;
    private String category;
    private String type;
    private Double amount;
    private LocalDate date;
}

ExpenseRepositoryJpa
@Repository
public interface ExpenseRepositoryJpa
extends JpaRepository<ExpenseEntity, Long> {

    @Query("""
      SELECT new domain.model.ExpenseSummary(e.category, SUM(e.amount))
      FROM ExpenseEntity e
      WHERE e.userId = :userId
      GROUP BY e.category
    """)
    List<ExpenseSummary> summaryByCategory(Long userId);
}

🔌 KAFKA CONSUMER
@Service
public class TransactionConsumer {

    private final AnalysisApplicationService service;

    @KafkaListener(topics = "transactions-topic", groupId = "analysis-group")
    public void consume(TransactionResponseDTO dto) {
        service.processTransaction(
                dto.getUserId(),
                dto.getCategory(),
                dto.getType(),
                dto.getAmount().doubleValue(),
                dto.getDate().toLocalDate()
        );
    }
}

📊 RELATÓRIOS
Excel
public class ExcelReportGenerator {

    public static byte[] generate(List<ExpenseSummary> data) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Resumo");

        int row = 0;
        for (ExpenseSummary e : data) {
            Row r = sheet.createRow(row++);
            r.createCell(0).setCellValue(e.getCategory());
            r.createCell(1).setCellValue(e.getTotal().doubleValue());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        return out.toByteArray();
    }
}

🌐 CONTROLLER
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisApplicationService service;

    @GetMapping("/summary/{userId}")
    public List<ExpenseSummary> summary(@PathVariable Long userId) {
        return service.report().byCategory(userId);
    }

    @GetMapping("/report/excel/{userId}")
    public ResponseEntity<byte[]> excel(@PathVariable Long userId) {

        var data = service.report().byCategory(userId);
        byte[] file = ExcelReportGenerator.generate(data);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=report.xlsx")
                .body(file);
    }
}