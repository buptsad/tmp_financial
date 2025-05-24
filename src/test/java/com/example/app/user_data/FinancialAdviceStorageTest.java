package com.example.app.user_data;

import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class FinancialAdviceStorageTest {

    private static final String TEST_USERNAME = "testuser_advice";
    private static final String ADVICE_FILE_PATH = ".\\user_data\\" + TEST_USERNAME + "\\user_advice.txt";
    private static final File ADVICE_FILE = new File(ADVICE_FILE_PATH);

    @BeforeEach
    void setUp() {
        // Clean up before test
        if (ADVICE_FILE.exists()) {
            ADVICE_FILE.delete();
        }
        File dir = ADVICE_FILE.getParentFile();
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            dir.delete();
        }
        FinancialAdviceStorage.setUsername(TEST_USERNAME);
    }

    @AfterAll
    static void cleanUp() {
        File dir = ADVICE_FILE.getParentFile();
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            dir.delete();
        }
        // Do NOT delete user_data directory!
    }

    @Test
    @DisplayName("Should create advice file and directory if not exist")
    void testInitializeStorageCreatesFileAndDirectory() {
        assertTrue(ADVICE_FILE.exists(), "Advice file should be created");
        assertTrue(ADVICE_FILE.getParentFile().exists(), "Advice directory should be created");
    }

    @Test
    @DisplayName("Should write and read advice correctly")
    void testSaveAndLoadAdvice() {
        String advice = "Test advice for user";
        LocalDateTime now = LocalDateTime.now().withNano(0);
        assertTrue(FinancialAdviceStorage.saveAdvice(advice, now), "Should save advice successfully");

        Object[] loaded = FinancialAdviceStorage.loadAdvice();
        assertNotNull(loaded, "Loaded advice should not be null");
        assertEquals(advice, loaded[0]);
        assertEquals(now, ((LocalDateTime) loaded[1]).withNano(0));
    }

    @Test
    @DisplayName("Should load default advice if file does not exist")
    void testLoadDefaultAdviceIfFileMissing() {
        if (ADVICE_FILE.exists()) ADVICE_FILE.delete();
        File dir = ADVICE_FILE.getParentFile();
        if (dir.exists()) dir.delete();

        FinancialAdviceStorage.setUsername(TEST_USERNAME);
        Object[] loaded = FinancialAdviceStorage.loadAdvice();
        assertNotNull(loaded, "Loaded advice should not be null");
        assertTrue(((String) loaded[0]).contains("Welcome to your financial assistant!"));
        assertNotNull(loaded[1]);
    }

    @Test
    @DisplayName("Should return null if file is missing after initialization")
    void testLoadAdviceReturnsNullIfFileMissing() {
        if (ADVICE_FILE.exists()) ADVICE_FILE.delete();
        Object[] loaded = FinancialAdviceStorage.loadAdvice();
        assertNull(loaded, "Should return null if file is missing");
    }

    @Test
    @DisplayName("Should persist advice to disk")
    void testAdvicePersistedToDisk() throws Exception {
        String advice = "Persisted advice";
        LocalDateTime now = LocalDateTime.now().withNano(0);
        assertTrue(FinancialAdviceStorage.saveAdvice(advice, now));

        // Read file directly
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(ADVICE_FILE))) {
            String dateStr = reader.readLine();
            String fileAdvice = reader.readLine();
            assertNotNull(dateStr);
            assertNotNull(fileAdvice);
            assertEquals(advice, fileAdvice);
            // Check date format
            LocalDateTime fileTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            assertEquals(now, fileTime.withNano(0));
        }
    }
}