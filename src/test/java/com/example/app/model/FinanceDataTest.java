package com.example.app.model;

import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FinanceData class.
 */
class FinanceDataTest {

    private FinanceData financeData;

    @BeforeEach
    void setUp() {
        financeData = new FinanceData();
    }

    @Test
    @DisplayName("Initial state should have empty data structures")
    void testInitialState() {
        assertNotNull(financeData.getCategoryBudgets());
        assertNotNull(financeData.getCategoryExpenses());
        assertNotNull(financeData.getCategoryIncomes());
        assertNotNull(financeData.getDailyExpenses());
        assertNotNull(financeData.getDailyIncomes());
        assertNotNull(financeData.getTransactions());
    }

    @Test
    @DisplayName("Importing transactions updates all relevant data")
    void testImportTransactions() {
        List<Object[]> transactions = new ArrayList<>();
        transactions.add(new Object[]{"2024-06-01", "Salary", "Income", 5000.0});
        transactions.add(new Object[]{"2024-06-02", "Groceries", "Food", -200.0});
        transactions.add(new Object[]{"2024-06-03", "Bus Ticket", "Transportation", -20.0});
        transactions.add(new Object[]{"2024-06-03", "Gift Received", "Gift", 100.0});

        financeData.importTransactions(transactions);

        // Check transactions list
        assertEquals(4, financeData.getTransactions().size());

        // Daily incomes and expenses
        assertEquals(2, financeData.getDailyIncomes().size());
        assertEquals(2, financeData.getDailyExpenses().size());

        // Category incomes and expenses
        assertTrue(financeData.getCategoryIncomes().containsKey("Other"));
        assertTrue(financeData.getCategoryExpenses().containsKey("Food"));
        assertTrue(financeData.getCategoryExpenses().containsKey("Transportation"));

        // Totals
        assertEquals(5100.0, financeData.getTotalIncome(), 0.01);
        assertEquals(220.0, financeData.getTotalExpenses(), 0.01);
        assertEquals(4880.0, financeData.getTotalSavings(), 0.01);
    }

    @Test
    @DisplayName("Budget allocation and retrieval works")
    void testBudgetAllocation() {
        List<Object[]> transactions = new ArrayList<>();
        transactions.add(new Object[]{"2024-06-01", "Groceries", "Food", -100.0});
        transactions.add(new Object[]{"2024-06-02", "Rent", "Housing", -1200.0});
        financeData.importTransactions(transactions);

        Map<String, Double> budgets = financeData.getCategoryBudgets();
        assertTrue(budgets.containsKey("Food"));
        assertTrue(budgets.containsKey("Housing"));
        assertTrue(budgets.containsKey("Other"));
        assertEquals(4000.0, budgets.values().stream().mapToDouble(Double::doubleValue).sum(), 0.1);
    }

    @Test
    @DisplayName("Category percentage calculation is correct")
    void testCategoryPercentage() {
        List<Object[]> transactions = new ArrayList<>();
        transactions.add(new Object[]{"2024-06-01", "Groceries", "Food", -400.0});
        financeData.importTransactions(transactions);
        double percent = financeData.getCategoryPercentage("Food");
        assertTrue(percent > 0 && percent <= 100);
    }

    @Test
    @DisplayName("Expense and income descriptions and categories are correct")
    void testDescriptionsAndCategories() {
        List<Object[]> transactions = new ArrayList<>();
        transactions.add(new Object[]{"2024-06-04", "Dinner", "Food", -50.0});
        transactions.add(new Object[]{"2024-06-04", "Salary", "Income", 3000.0});
        financeData.importTransactions(transactions);

        LocalDate date = LocalDate.parse("2024-06-04");
        assertEquals("Dinner", financeData.getExpenseDescription(date));
        assertEquals("Salary", financeData.getIncomeDescription(date));
        assertEquals("Food", financeData.getExpenseCategory(date));
    }

    @Test
    @DisplayName("Budget update and deletion works")
    void testBudgetUpdateAndDelete() {
        financeData.updateCategoryBudget("TestCat", 123.45);
        assertEquals(123.45, financeData.getCategoryBudget("TestCat"), 0.01);

        boolean deleted = financeData.deleteCategoryBudget("TestCat");
        assertTrue(deleted);
        assertEquals(0.0, financeData.getCategoryBudget("TestCat"), 0.01);
    }

    @Test
    @DisplayName("getOverallBudgetPercentage returns correct ratio")
    void testOverallBudgetPercentage() {
        List<Object[]> transactions = new ArrayList<>();
        transactions.add(new Object[]{"2024-06-01", "Rent", "Housing", -1400.0});
        transactions.add(new Object[]{"2024-06-02", "Groceries", "Food", -800.0});
        financeData.importTransactions(transactions);

        double percent = financeData.getOverallBudgetPercentage();
        assertTrue(percent > 0 && percent <= 100);
    }

    @Test
    @DisplayName("getDates returns sorted unique dates")
    void testGetDates() {
        List<Object[]> transactions = new ArrayList<>();
        transactions.add(new Object[]{"2024-06-02", "Groceries", "Food", -100.0});
        transactions.add(new Object[]{"2024-06-01", "Salary", "Income", 3000.0});
        financeData.importTransactions(transactions);

        List<LocalDate> dates = financeData.getDates();
        assertEquals(2, dates.size());
        assertEquals(LocalDate.parse("2024-06-01"), dates.get(0));
        assertEquals(LocalDate.parse("2024-06-02"), dates.get(1));
    }
}