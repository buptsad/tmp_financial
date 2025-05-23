package com.example.app.user_data;

import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserBudgetStorageTest {

    private static final String TEST_USERNAME = "testuser_budgetstorage";
    private static final String BUDGET_FILE_PATH = ".\\user_data\\" + TEST_USERNAME + "\\user_budgets.csv";
    private static final File BUDGET_FILE = new File(BUDGET_FILE_PATH);

    @BeforeEach
    void setUp() {
        // Clean up before test
        if (BUDGET_FILE.exists()) {
            BUDGET_FILE.delete();
        }
        File dir = BUDGET_FILE.getParentFile();
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            dir.delete();
        }
        // Now create the file for the test
        UserBudgetStorage.setUsername(TEST_USERNAME);
    }

    @AfterAll
    static void cleanUp() {
        // Clean up budget file and parent directory after all tests
        if (BUDGET_FILE.exists()) {
            BUDGET_FILE.delete();
        }
        File dir = BUDGET_FILE.getParentFile();
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            dir.delete();
        }
        // Clean up user_data directory if empty
        File userDataDir = new File(".\\user_data");
        if (userDataDir.exists() && userDataDir.isDirectory() && userDataDir.list().length == 0) {
            userDataDir.delete();
        }
    }

    @Test
    @DisplayName("Should create budget file and directory if not exist")
    void testInitializeStorageCreatesFileAndDirectory() {
        assertTrue(BUDGET_FILE.exists(), "Budget file should be created");
        assertTrue(BUDGET_FILE.getParentFile().exists(), "Budget directory should be created");
    }

    @Test
    @DisplayName("Should write and read budgets correctly")
    void testSaveAndLoadBudgets() {
        List<Object[]> budgets = new ArrayList<>();
        budgets.add(new Object[]{"Food", 500.0, LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30)});
        budgets.add(new Object[]{"Housing", 1200.0, null, null});
        assertTrue(UserBudgetStorage.saveBudgets(budgets), "Should save budgets successfully");

        List<Object[]> loaded = UserBudgetStorage.loadBudgets();
        assertEquals(2, loaded.size());
        assertEquals("Food", loaded.get(0)[0]);
        assertEquals(500.0, (Double) loaded.get(0)[1], 0.01);
        assertEquals(LocalDate.of(2024, 6, 1), loaded.get(0)[2]);
        assertEquals(LocalDate.of(2024, 6, 30), loaded.get(0)[3]);
        assertEquals("Housing", loaded.get(1)[0]);
        assertEquals(1200.0, (Double) loaded.get(1)[1], 0.01);
        assertNull(loaded.get(1)[2]);
        assertNull(loaded.get(1)[3]);
    }

    @Test
    @DisplayName("Should load empty list if file does not exist")
    void testLoadBudgetsFileNotExist() {
        if (BUDGET_FILE.exists()) BUDGET_FILE.delete();
        File dir = BUDGET_FILE.getParentFile();
        if (dir.exists()) dir.delete();
        UserBudgetStorage.setUsername(TEST_USERNAME);
        if (BUDGET_FILE.exists()) BUDGET_FILE.delete();
        List<Object[]> loaded = UserBudgetStorage.loadBudgets();
        assertNotNull(loaded);
        assertTrue(loaded.isEmpty());
    }

    @Test
    @DisplayName("Should persist budgets to disk")
    void testBudgetsPersistedToDisk() throws Exception {
        List<Object[]> budgets = new ArrayList<>();
        budgets.add(new Object[]{"Persisted", 999.0, LocalDate.of(2025, 1, 1), null});
        assertTrue(UserBudgetStorage.saveBudgets(budgets));

        // Read file directly
        List<String> lines = java.nio.file.Files.readAllLines(BUDGET_FILE.toPath());
        assertTrue(lines.stream().anyMatch(line -> line.contains("Persisted")));
    }
}