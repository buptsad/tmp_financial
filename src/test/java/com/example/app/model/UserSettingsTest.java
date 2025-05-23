package com.example.app.model;

import com.example.app.user_data.UserSettingsStorage;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class UserSettingsTest {

    private static final String TEST_USERNAME = "testuser_settings";
    private static final String SETTINGS_FILE_PATH = ".\\user_data\\" + TEST_USERNAME + "\\user_settings.properties";

    @AfterAll
    static void cleanUp() {
        // Clean up the advice file after all tests
        File file = new File(SETTINGS_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        File directory = file.getParentFile();
        if (directory.exists()) {
            directory.delete();
        }
    }

    @BeforeEach
    void setUp() {
        // Set username for storage and remove any existing settings file for isolation
        UserSettingsStorage.setUsername(TEST_USERNAME);
        File file = new File(SETTINGS_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        // Reset singleton for isolation
        resetUserSettingsSingleton();
    }

    @AfterEach
    void tearDown() {
        // Clean up settings file after test
        File file = new File(SETTINGS_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        resetUserSettingsSingleton();
    }

    private void resetUserSettingsSingleton() {
        try {
            java.lang.reflect.Field instanceField = UserSettings.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            // Ignore
        }
    }

    @Test
    @DisplayName("Should initialize with default values if no file exists")
    void testDefaultInitialization() {
        UserSettings settings = UserSettings.getInstance();
        assertEquals("", settings.getName());
        assertEquals("", settings.getEmail());
        assertEquals("", settings.getPhone());
        assertEquals("USD", settings.getCurrencyCode());
        assertEquals("$", settings.getCurrencySymbol());
        assertFalse(settings.isDarkTheme());
        assertTrue(settings.isBudgetAlertsEnabled());
        assertTrue(settings.isTransactionAlertsEnabled());
        assertEquals("", settings.getPasswordHash());
    }

    @Test
    @DisplayName("Should save and load settings correctly")
    void testSaveAndLoadSettings() {
        UserSettings settings = UserSettings.getInstance();
        settings.setName("Alice");
        settings.setEmail("alice@example.com");
        settings.setPhone("1234567890");
        settings.setCurrencyCode("CNY");
        settings.setCurrencySymbol("¥");
        settings.setDarkTheme(true);
        settings.setBudgetAlertsEnabled(false);
        settings.setTransactionAlertsEnabled(false);
        settings.setPasswordHash("hash123");
        settings.saveSettings();

        // Reset singleton and reload
        resetUserSettingsSingleton();
        UserSettings settings2 = UserSettings.getInstance();

        assertEquals("Alice", settings2.getName());
        assertEquals("alice@example.com", settings2.getEmail());
        assertEquals("1234567890", settings2.getPhone());
        assertEquals("CNY", settings2.getCurrencyCode());
        assertEquals("¥", settings2.getCurrencySymbol());
        assertTrue(settings2.isDarkTheme());
        assertFalse(settings2.isBudgetAlertsEnabled());
        assertFalse(settings2.isTransactionAlertsEnabled());
        assertEquals("hash123", settings2.getPasswordHash());
    }

    @Test
    @DisplayName("Should reset to default values")
    void testResetToDefaults() {
        UserSettings settings = UserSettings.getInstance();
        settings.setName("Bob");
        settings.setEmail("bob@example.com");
        settings.setPhone("9876543210");
        settings.setCurrencyCode("EUR");
        settings.setCurrencySymbol("€");
        settings.setDarkTheme(true);
        settings.setBudgetAlertsEnabled(false);
        settings.setTransactionAlertsEnabled(false);
        settings.setPasswordHash("hash456");
        settings.saveSettings();

        settings.resetToDefaults();

        assertEquals("", settings.getName());
        assertEquals("", settings.getEmail());
        assertEquals("", settings.getPhone());
        assertEquals("USD", settings.getCurrencyCode());
        assertEquals("$", settings.getCurrencySymbol());
        assertFalse(settings.isDarkTheme());
        assertTrue(settings.isBudgetAlertsEnabled());
        assertTrue(settings.isTransactionAlertsEnabled());
        assertEquals("", settings.getPasswordHash());
    }

    @Test
    @DisplayName("Should persist changes to settings file")
    void testSettingsFilePersistence() {
        UserSettings settings = UserSettings.getInstance();
        settings.setName("PersistedUser");
        settings.saveSettings();

        // Read file directly
        File file = new File(SETTINGS_FILE_PATH);
        assertTrue(file.exists());

        Properties props = new Properties();
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            props.load(fis);
        } catch (Exception e) {
            fail("Failed to read settings file: " + e.getMessage());
        }
        assertEquals("PersistedUser", props.getProperty("user.name"));
    }
}