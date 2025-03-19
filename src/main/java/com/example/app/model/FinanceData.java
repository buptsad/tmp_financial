package com.example.app.model;

import java.time.LocalDate;
import java.util.*;

public class FinanceData {
    // Static sample data for demonstration
    private static final double MONTHLY_BUDGET = 4000.00;
    private static final double DAILY_BUDGET = MONTHLY_BUDGET / 30;
    
    // Budget allocation by category
    private Map<String, Double> categoryBudgets;
    
    // Data maps for daily records
    private Map<LocalDate, Double> dailyIncomes;
    private Map<LocalDate, Double> dailyExpenses;
    
    // Expenses by category
    private Map<String, Double> categoryExpenses;
    
    public FinanceData() {
        // Initialize data
        initializeData();
    }
    
    private void initializeData() {
        // Create sample data for the past 30 days
        LocalDate today = LocalDate.now();
        dailyIncomes = new HashMap<>();
        dailyExpenses = new HashMap<>();
        
        // Initialize category budgets
        categoryBudgets = new LinkedHashMap<>();
        categoryBudgets.put("Housing", 1400.00);
        categoryBudgets.put("Food", 800.00);
        categoryBudgets.put("Transportation", 400.00);
        categoryBudgets.put("Utilities", 350.00);
        categoryBudgets.put("Entertainment", 250.00);
        categoryBudgets.put("Healthcare", 300.00);
        categoryBudgets.put("Other", 500.00);
        
        // Initialize category expenses
        categoryExpenses = new LinkedHashMap<>();
        categoryExpenses.put("Housing", 1350.00);
        categoryExpenses.put("Food", 720.00);
        categoryExpenses.put("Transportation", 315.00);
        categoryExpenses.put("Utilities", 285.00);
        categoryExpenses.put("Entertainment", 310.00);  // Over budget
        categoryExpenses.put("Healthcare", 175.00);
        categoryExpenses.put("Other", 420.00);
        
        // Generate some sample data
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            
            // Income varies between 150-200 with some peaks
            double income = 150 + Math.random() * 50;
            if (date.getDayOfMonth() == 15 || date.getDayOfMonth() == 1) {
                // Salary days - higher income
                income = 2500 + Math.random() * 200;
            }
            
            // Expenses vary between 100-180 with some peaks
            double expense = 100 + Math.random() * 80;
            if (date.getDayOfWeek().getValue() >= 5) { // Weekend
                expense += 50 + Math.random() * 30; // Higher weekend expenses
            }
            if (date.getDayOfMonth() == 10 || date.getDayOfMonth() == 25) {
                // Bill payment days - higher expenses
                expense += 500 + Math.random() * 100;
            }
            
            dailyIncomes.put(date, income);
            dailyExpenses.put(date, expense);
        }
    }
    
    public double getTotalBalance() {
        return 12587.43;
    }
    
    public double getTotalIncome() {
        return dailyIncomes.values().stream().mapToDouble(Double::doubleValue).sum();
    }
    
    public double getTotalExpenses() {
        return dailyExpenses.values().stream().mapToDouble(Double::doubleValue).sum();
    }
    
    public double getTotalSavings() {
        return getTotalIncome() - getTotalExpenses();
    }
    
    public double getMonthlyBudget() {
        return MONTHLY_BUDGET;
    }
    
    public double getDailyBudget() {
        return DAILY_BUDGET;
    }
    
    // Methods to get data for charts
    public List<LocalDate> getDates() {
        List<LocalDate> dates = new ArrayList<>(dailyIncomes.keySet());
        dates.sort(LocalDate::compareTo);
        return dates;
    }
    
    public Map<LocalDate, Double> getDailyIncomes() {
        return dailyIncomes;
    }
    
    public Map<LocalDate, Double> getDailyExpenses() {
        return dailyExpenses;
    }
    
    // New methods to support category tracking
    public Map<String, Double> getCategoryBudgets() {
        return categoryBudgets;
    }
    
    public Map<String, Double> getCategoryExpenses() {
        return categoryExpenses;
    }
    
    public double getCategoryBudget(String category) {
        return categoryBudgets.getOrDefault(category, 0.0);
    }
    
    public double getCategoryExpense(String category) {
        return categoryExpenses.getOrDefault(category, 0.0);
    }
    
    public double getCategoryPercentage(String category) {
        double budget = getCategoryBudget(category);
        double expense = getCategoryExpense(category);
        return budget > 0 ? (expense / budget) * 100 : 0;
    }
    
    public double getOverallBudgetPercentage() {
        return (getTotalExpenses() / getMonthlyBudget()) * 100;
    }
}