package com.finance.transaction_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.transaction_service.infrastructure.persistence.repository.TransactionRepositoryJpa;
import com.finance.transaction_service.presentation.dto.ImportResultDTO;
import com.finance.transaction_service.security.CustomUserDetails;

import org.apache.kafka.clients.consumer.ConsumerConfig;
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

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = { "transaction.created" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class TransactionImportIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private TransactionRepositoryJpa repository;

    private KafkaConsumer<String, String> consumer;

    private static final ObjectMapper mapper = new ObjectMapper();

    /*
     * UUID utilizado pelo teste.
     *
     * IMPORTANTE:
     * Não utilizar mais Long/1L.
     */
    private static final UUID USER_ID = UUID.fromString(
            "11111111-1111-1111-1111-111111111111");

    @DynamicPropertySource
    static void setProperties(
            DynamicPropertyRegistry registry) {

        registry.add(
                "spring.datasource.url",
                () -> "jdbc:h2:mem:testdb;" +
                        "DB_CLOSE_DELAY=-1;" +
                        "MODE=PostgreSQL");

        registry.add(
                "spring.datasource.driver-class-name",
                () -> "org.h2.Driver");

        registry.add(
                "spring.jpa.hibernate.ddl-auto",
                () -> "create-drop");

        registry.add(
                "spring.jpa.show-sql",
                () -> "false");
    }

    @BeforeAll
    void setupConsumer() {

        Properties props = new Properties();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                embeddedKafka.getBrokersAsString());

        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "test-group");

        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

        props.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest");

        consumer = new KafkaConsumer<>(props);

        consumer.subscribe(
                List.of("transaction.created"));
    }

    @AfterAll
    void teardown() {

        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void uploadXlsx_shouldPersistTransactionsAndPublishKafkaEvents()
            throws Exception {

        // =====================================================
        // CRIA PLANILHA
        // =====================================================

        XSSFWorkbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("transactions");

        Row header = sheet.createRow(0);

        String[] cols = {
                "occurredAt",
                "type",
                "amount",
                "currency",
                "category",
                "description"
        };

        for (int i = 0; i < cols.length; i++) {
            header.createCell(i)
                    .setCellValue(cols[i]);
        }

        // =====================================================
        // LINHA 1
        // =====================================================

        Row r1 = sheet.createRow(1);

        r1.createCell(0)
                .setCellValue(
                        "2026-04-01T10:00:00");

        r1.createCell(1)
                .setCellValue("DEPOSIT");

        r1.createCell(2)
                .setCellValue(1000.00);

        r1.createCell(3)
                .setCellValue("BRL");

        r1.createCell(4)
                .setCellValue("SALARY");

        r1.createCell(5)
                .setCellValue("Salary April");

        // =====================================================
        // LINHA 2
        // =====================================================

        Row r2 = sheet.createRow(2);

        r2.createCell(0)
                .setCellValue(
                        "2026-04-02T12:00:00");

        r2.createCell(1)
                .setCellValue("WITHDRAW");

        r2.createCell(2)
                .setCellValue(50.25);

        r2.createCell(3)
                .setCellValue("BRL");

        r2.createCell(4)
                .setCellValue("GROCERIES");

        r2.createCell(5)
                .setCellValue("Supermarket");

        // =====================================================
        // CONVERTE XLSX PARA BYTES
        // =====================================================

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        workbook.write(out);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "transactions.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                out.toByteArray());

        // =====================================================
        // UPLOAD
        // =====================================================

        CustomUserDetails testUser = new CustomUserDetails(
                USER_ID,
                "test-user",
                "USER");

        var mvcResult = mockMvc.perform(
                multipart(
                        "/api/transactions/upload")
                        .file(file)
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andReturn();

        // =====================================================
        // VALIDA RESULTADO
        // =====================================================

        String json = mvcResult
                .getResponse()
                .getContentAsString();

        ImportResultDTO result = mapper.readValue(
                json,
                ImportResultDTO.class);

        assertThat(result.getProcessed())
                .isEqualTo(2);

        assertThat(result.getSuccess())
                .isEqualTo(2);

        assertThat(result.getFailed())
                .isEqualTo(0);

        // =====================================================
        // VALIDA BANCO
        // =====================================================

        var list = repository.findByUserId(USER_ID);

        assertThat(list)
                .hasSizeGreaterThanOrEqualTo(2);

        // =====================================================
        // VALIDA KAFKA
        // =====================================================

        var records = consumer.poll(
                Duration.ofSeconds(5));

        assertThat(records.count())
                .isGreaterThanOrEqualTo(2);
    }
}
