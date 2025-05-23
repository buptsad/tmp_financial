package com.example.app.model;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BudgetManager class.
 */
class BudgetManagerTest {

    private static Path tempDir;

    @BeforeAll
    static void setupClass() throws IOException {
        tempDir = Files.createTempDirectory("budget_manager_test");
    }

    @AfterAll
    static void cleanupClass() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    @Test
    @DisplayName("Should save and load budgets correctly")
    void testSaveAndLoadBudgets() {
        Map<String, Double> budgets = new LinkedHashMap<>();
        budgets.put("Food", 500.0);
        budgets.put("Housing", 1200.0);
        budgets.put("Entertainment", 300.0);

        // Save budgets
        BudgetManager.saveBudgetsToCSV(budgets, tempDir.toString());

        // Load budgets
        Map<String, Double> loaded = BudgetManager.loadBudgetsFromCSV(tempDir.toString());

        assertEquals(budgets.size(), loaded.size());
        for (String key : budgets.keySet()) {
            assertTrue(loaded.containsKey(key));
            assertEquals(budgets.get(key), loaded.get(key), 0.01);
        }
    }

    @Test
    @DisplayName("Should return empty map if file does not exist")
    void testLoadBudgetsFileNotExist() {
        Path nonExistentDir = tempDir.resolve("nonexistent");
        Map<String, Double> loaded = BudgetManager.loadBudgetsFromCSV(nonExistentDir.toString());
        assertNotNull(loaded);
        assertTrue(loaded.isEmpty());
    }

    @Test
    @DisplayName("Should skip invalid lines and parse valid ones")
    void testLoadBudgetsWithInvalidLines() throws IOException {
        Path filePath = tempDir.resolve("user_budgets.csv");
        List<String> lines = Arrays.asList(
                "Category,Budget",
                "Food,500",
                "InvalidLine",
                "Housing,notanumber",
                "Entertainment,300"
        );
        Files.write(filePath, lines);

        Map<String, Double> loaded = BudgetManager.loadBudgetsFromCSV(tempDir.toString());
        assertEquals(2, loaded.size());
        assertEquals(500.0, loaded.get("Food"), 0.01);
        assertEquals(300.0, loaded.get("Entertainment"), 0.01);
        assertFalse(loaded.containsKey("Housing"));
    }

    @Test
    @DisplayName("Should overwrite existing file on save")
    void testOverwriteBudgetsFile() {
        Map<String, Double> budgets1 = new LinkedHashMap<>();
        budgets1.put("A", 100.0);
        BudgetManager.saveBudgetsToCSV(budgets1, tempDir.toString());

        Map<String, Double> budgets2 = new LinkedHashMap<>();
        budgets2.put("B", 200.0);
        BudgetManager.saveBudgetsToCSV(budgets2, tempDir.toString());

        Map<String, Double> loaded = BudgetManager.loadBudgetsFromCSV(tempDir.toString());
        assertEquals(1, loaded.size());
        assertTrue(loaded.containsKey("B"));
        assertEquals(200.0, loaded.get("B"), 0.01);
    }
}