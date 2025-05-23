package com.example.app.user_data;

import org.junit.jupiter.api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserBillStorageTest {

    private static final String TEST_USERNAME = "testuser_billstorage";
    private static final String BILL_FILE_PATH = ".\\user_data\\" + TEST_USERNAME + "\\user_bill.csv";
    private static final File BILL_FILE = new File(BILL_FILE_PATH);

    @BeforeEach
    void setUp() {
        // Clean up before test
        if (BILL_FILE.exists()) {
            BILL_FILE.delete();
        }
        File dir = BILL_FILE.getParentFile();
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            dir.delete();
        }
        // Now create the file for the test
        UserBillStorage.setUsername(TEST_USERNAME);
    }

    @AfterAll
    static void cleanUp() {
        // Clean up bill file and parent directory after all tests
        if (BILL_FILE.exists()) {
            BILL_FILE.delete();
        }
        File dir = BILL_FILE.getParentFile();
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
    @DisplayName("Should create bill file and directory if not exist")
    void testInitializeStorageCreatesFileAndDirectory() {
        assertTrue(BILL_FILE.exists(), "Bill file should be created");
        assertTrue(BILL_FILE.getParentFile().exists(), "Bill directory should be created");
    }

    @Test
    @DisplayName("Should write and read transactions correctly")
    void testSaveAndLoadTransactions() {
        List<Object[]> transactions = new ArrayList<>();
        transactions.add(new Object[]{"2024-06-01 12:00", "Lunch", "Food", -20.0, true});
        transactions.add(new Object[]{"2024-06-02 08:30", "Bus", "Transportation", -2.5, false});
        assertTrue(UserBillStorage.saveTransactions(transactions), "Should save transactions successfully");

        List<Object[]> loaded = UserBillStorage.loadTransactions();
        assertEquals(2, loaded.size());
        assertEquals("2024-06-01 12:00", loaded.get(0)[0]);
        assertEquals("Lunch", loaded.get(0)[1]);
        assertNotNull(loaded.get(0)[2]);
        assertFalse(((String) loaded.get(0)[2]).isEmpty());
        assertEquals(-20.0, (Double) loaded.get(0)[3], 0.01);
        assertEquals(true, loaded.get(0)[4]);
        assertEquals("2024-06-02 08:30", loaded.get(1)[0]);
        assertEquals("Bus", loaded.get(1)[1]);
        assertNotNull(loaded.get(1)[2]);
        assertFalse(((String) loaded.get(1)[2]).isEmpty());
        assertEquals(-2.5, (Double) loaded.get(1)[3], 0.01);
        assertEquals(false, loaded.get(1)[4]);
    }

    @Test
    @DisplayName("Should load empty list if file does not exist")
    void testLoadTransactionsFileNotExist() {
        if (BILL_FILE.exists()) BILL_FILE.delete();
        File dir = BILL_FILE.getParentFile();
        if (dir.exists()) dir.delete();
        UserBillStorage.setUsername(TEST_USERNAME);
        if (BILL_FILE.exists()) BILL_FILE.delete();
        List<Object[]> loaded = UserBillStorage.loadTransactions();
        assertNotNull(loaded);
        assertTrue(loaded.isEmpty());
    }

    @Test
    @DisplayName("Should persist transactions to disk")
    void testTransactionsPersistedToDisk() throws Exception {
        List<Object[]> transactions = new ArrayList<>();
        transactions.add(new Object[]{"2025-01-01 09:00", "Persisted", "Other", 123.45, true});
        assertTrue(UserBillStorage.saveTransactions(transactions));

        // Read file directly
        List<String> lines = java.nio.file.Files.readAllLines(BILL_FILE.toPath());
        assertTrue(lines.stream().anyMatch(line -> line.contains("Persisted")));
    }
}