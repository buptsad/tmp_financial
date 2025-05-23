package com.example.app.user_data;

import org.junit.jupiter.api.*;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthServiceTest {

    private static final String TEST_USERNAME = "testuser_auth";
    private static final String TEST_PASSWORD = "testpass123";
    private static final String TEST_EMAIL = "testuser@example.com";
    private static final String USER_DIR_PATH = ".\\user_data\\" + TEST_USERNAME;
    private static final File USER_DIR = new File(USER_DIR_PATH);

    @BeforeEach
    void setUp() {
        // Clean up before test
        if (USER_DIR.exists()) {
            for (File file : USER_DIR.listFiles()) {
                file.delete();
            }
            USER_DIR.delete();
        }
    }

    @AfterAll
    static void cleanUp() {
        File userDataDir = new File(".\\user_data");
        if (USER_DIR.exists()) {
            for (File file : USER_DIR.listFiles()) {
                file.delete();
            }
            USER_DIR.delete();
        }
        if (userDataDir.exists() && userDataDir.isDirectory() && userDataDir.list().length == 0) {
            userDataDir.delete();
        }
    }

    @Test
    @DisplayName("Should register user and create files")
    void testRegisterUserCreatesFiles() {
        assertTrue(UserAuthService.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL), "Registration should succeed");
        assertTrue(USER_DIR.exists() && USER_DIR.isDirectory(), "User directory should exist");
        File settingsFile = new File(USER_DIR, "user_settings.properties");
        File billFile = new File(USER_DIR, "user_bill.csv");
        File budgetFile = new File(USER_DIR, "user_budgets.csv");
        assertTrue(settingsFile.exists(), "Settings file should exist");
        assertTrue(billFile.exists(), "Bill file should exist");
        assertTrue(budgetFile.exists(), "Budget file should exist");
    }

    @Test
    @DisplayName("Should not register duplicate user")
    void testRegisterDuplicateUser() {
        assertTrue(UserAuthService.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL));
        assertFalse(UserAuthService.registerUser(TEST_USERNAME, "otherpass", "other@example.com"), "Duplicate registration should fail");
    }

    @Test
    @DisplayName("Should authenticate user with correct password")
    void testAuthenticateUserSuccess() {
        assertTrue(UserAuthService.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL));
        assertTrue(UserAuthService.authenticateUser(TEST_USERNAME, TEST_PASSWORD), "Authentication should succeed");
    }

    @Test
    @DisplayName("Should not authenticate with wrong password")
    void testAuthenticateUserWrongPassword() {
        assertTrue(UserAuthService.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL));
        assertFalse(UserAuthService.authenticateUser(TEST_USERNAME, "wrongpass"), "Authentication should fail with wrong password");
    }

    @Test
    @DisplayName("Should not authenticate non-existent user")
    void testAuthenticateNonExistentUser() {
        assertFalse(UserAuthService.authenticateUser("nonexistent", "nopass"), "Authentication should fail for non-existent user");
    }

    @Test
    @DisplayName("Should check username availability")
    void testIsUsernameAvailable() {
        assertTrue(UserAuthService.isUsernameAvailable(TEST_USERNAME), "Username should be available before registration");
        assertTrue(UserAuthService.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL));
        assertFalse(UserAuthService.isUsernameAvailable(TEST_USERNAME), "Username should not be available after registration");
    }
}