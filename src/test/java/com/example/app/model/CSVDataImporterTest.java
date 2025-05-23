package com.example.app.model;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CSVDataImporterTest {

    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("transactions", ".csv");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    @DisplayName("Should import valid transactions from CSV")
    void testImportValidTransactions() throws IOException {
        List<String> lines = Arrays.asList(
                "Date,Description,Category,Amount",
                "2024-06-01,Salary,Income,5000.0",
                "2024-06-02,Groceries,Food,-200.0"
        );
        Files.write(tempFile, lines);

        List<Object[]> transactions = CSVDataImporter.importTransactionsFromCSV(tempFile.toString());
        assertEquals(2, transactions.size());
        assertEquals("2024-06-01", transactions.get(0)[0]);
        assertEquals("Salary", transactions.get(0)[1]);
        assertEquals("Income", transactions.get(0)[2]);
        assertEquals(5000.0, (Double) transactions.get(0)[3], 0.01);
    }

    @Test
    @DisplayName("Should skip invalid lines and handle duplicates")
    void testSkipInvalidAndDuplicateLines() throws IOException {
        List<String> lines = Arrays.asList(
                "Date,Description,Category,Amount",
                "2024-06-01,Salary,Income,5000.0",
                "2024-06-01,Salary,Income,5000.0", // duplicate
                "2024-06-02,Groceries,Food,-200.0",
                "InvalidLine",
                "2024-06-03,Bus Ticket,Transportation,notanumber"
        );
        Files.write(tempFile, lines);

        List<Object[]> transactions = CSVDataImporter.importTransactionsFromCSV(tempFile.toString());
        assertEquals(2, transactions.size());
        assertEquals("2024-06-01", transactions.get(0)[0]);
        assertEquals("2024-06-02", transactions.get(1)[0]);
    }

    @Test
    @DisplayName("Should return empty list if file does not exist")
    void testFileNotExist() {
        List<Object[]> transactions = CSVDataImporter.importTransactionsFromCSV("nonexistent_file.csv");
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }
}