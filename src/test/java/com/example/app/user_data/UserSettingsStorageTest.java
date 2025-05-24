package com.example.app.user_data;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class UserSettingsStorageTest {

    private static final String TEST_USERNAME = "testuser_settingsstorage";
    private static final String SETTINGS_FILE_PATH = ".\\user_data\\" + TEST_USERNAME + "\\user_settings.properties";
    private static final File SETTINGS_FILE = new File(SETTINGS_FILE_PATH);

    @BeforeEach
    void setUp() {
        // Clean up before test
        if (SETTINGS_FILE.exists()) {
            SETTINGS_FILE.delete();
        }
        File dir = SETTINGS_FILE.getParentFile();
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            dir.delete();
        }
        // Now create the file for the test
        UserSettingsStorage.setUsername(TEST_USERNAME);
    }

    @AfterAll
    static void cleanUp() {
        // Clean up only the test user's directory and files
        File dir = SETTINGS_FILE.getParentFile();
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            dir.delete();
        }
        // Do NOT delete user_data directory!
    }

    @Test
    @DisplayName("Should create settings file and directory if not exist")
    void testInitializeStorageCreatesFileAndDirectory() {
        // File and directory should exist after setUsername
        assertTrue(SETTINGS_FILE.exists(), "Settings file should be created");
        assertTrue(SETTINGS_FILE.getParentFile().exists(), "Settings directory should be created");
    }

    @Test
    @DisplayName("Should write and read settings correctly")
    void testSaveAndLoadSettings() {
        Properties props = new Properties();
        props.setProperty("user.name", "Alice");
        props.setProperty("user.email", "alice@example.com");
        props.setProperty("currency.code", "CNY");
        props.setProperty("theme.dark", "true");

        assertTrue(UserSettingsStorage.saveSettings(props), "Should save settings successfully");

        Properties loaded = UserSettingsStorage.loadSettings();
        assertNotNull(loaded, "Loaded properties should not be null");
        assertEquals("Alice", loaded.getProperty("user.name"));
        assertEquals("alice@example.com", loaded.getProperty("user.email"));
        assertEquals("CNY", loaded.getProperty("currency.code"));
        assertEquals("true", loaded.getProperty("theme.dark"));
    }

    @Test
    @DisplayName("Should load default settings if file does not exist")
    void testLoadDefaultSettingsIfFileMissing() {
        // Delete file and directory to simulate missing file
        if (SETTINGS_FILE.exists()) SETTINGS_FILE.delete();
        File dir = SETTINGS_FILE.getParentFile();
        if (dir.exists()) dir.delete();

        // setUsername should recreate file with defaults
        UserSettingsStorage.setUsername(TEST_USERNAME);
        Properties loaded = UserSettingsStorage.loadSettings();
        assertNotNull(loaded, "Loaded properties should not be null");
        assertEquals(TEST_USERNAME, loaded.getProperty("user.name"));
        assertEquals("USD", loaded.getProperty("currency.code"));
        assertEquals("$", loaded.getProperty("currency.symbol"));
        assertEquals("false", loaded.getProperty("theme.dark"));
        assertEquals("true", loaded.getProperty("notifications.budget.enabled"));
        assertEquals("true", loaded.getProperty("notifications.transaction.enabled"));
        assertEquals("", loaded.getProperty("security.password.hash"));
    }

    @Test
    @DisplayName("Should return null if file is missing after initialization")
    void testLoadSettingsReturnsNullIfFileMissing() {
        // Delete file after initialization
        if (SETTINGS_FILE.exists()) SETTINGS_FILE.delete();
        Properties loaded = UserSettingsStorage.loadSettings();
        assertNull(loaded, "Should return null if file is missing");
    }

    @Test
    @DisplayName("Should persist settings to disk")
    void testSettingsPersistedToDisk() throws Exception {
        Properties props = new Properties();
        props.setProperty("user.name", "PersistedUser");
        assertTrue(UserSettingsStorage.saveSettings(props));

        // Read file directly
        Properties fileProps = new Properties();
        try (FileInputStream fis = new FileInputStream(SETTINGS_FILE)) {
            fileProps.load(fis);
        }
        assertEquals("PersistedUser", fileProps.getProperty("user.name"));
    }
}