package com.finance.transaction_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.transaction_service.infrastructure.persistence.repository.TransactionRepositoryJpa;
import com.finance.transaction_service.presentation.dto.ImportResultDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@EmbeddedKafka(partitions = 1, topics = {"transaction.created"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionImportIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private TransactionRepositoryJpa repository;

    private KafkaConsumer<String, String> consumer;

    private static final ObjectMapper mapper = new ObjectMapper();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // datasource in-memory
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "false");
    }

    @BeforeAll
    void setupConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of("transaction.created"));
    }

    @AfterAll
    void teardown() {
        if (consumer != null) consumer.close();
    }

    @Test
    void uploadXlsx_shouldPersistTransactionsAndPublishKafkaEvents() throws Exception {
        // build xlsx in memory with 2 rows
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("transactions");
        Row header = sheet.createRow(0);
        String[] cols = new String[]{"userId","occurredAt","type","amount","currency","category","description","targetUserId"};
        for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);

        Row r1 = sheet.createRow(1);
        r1.createCell(0).setCellValue("1");
        r1.createCell(1).setCellValue("2026-04-01T10:00:00");
        r1.createCell(2).setCellValue("DEPOSIT");
        r1.createCell(3).setCellValue(1000.00);
        r1.createCell(4).setCellValue("BRL");
        r1.createCell(5).setCellValue("SALARY");
        r1.createCell(6).setCellValue("Salary April");

        Row r2 = sheet.createRow(2);
        r2.createCell(0).setCellValue("1");
        r2.createCell(1).setCellValue("2026-04-02T12:00:00");
        r2.createCell(2).setCellValue("PURCHASE");
        r2.createCell(3).setCellValue(50.25);
        r2.createCell(4).setCellValue("BRL");
        r2.createCell(5).setCellValue("GROCERIES");
        r2.createCell(6).setCellValue("Supermarket");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile("file", "transactions.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());

        var mvcResult = mockMvc.perform(multipart("/api/transactions/upload").file(file))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        ImportResultDTO result = mapper.readValue(json, ImportResultDTO.class);

        assertThat(result.getProcessed()).isEqualTo(2);
        assertThat(result.getSuccess()).isEqualTo(2);
        assertThat(result.getFailed()).isEqualTo(0);

        // DB assertions
        var list = repository.findByUserId(1L);
        assertThat(list.size()).isGreaterThanOrEqualTo(2);

        // Kafka assertions: poll messages
        var records = consumer.poll(Duration.ofSeconds(5));
        assertThat(records.count()).isGreaterThanOrEqualTo(2);
    }
}
