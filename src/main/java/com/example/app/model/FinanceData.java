package com.example.app.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    
    // Maps to store consistent transaction descriptions for each date
    private Map<LocalDate, String> dailyExpenseDescriptions;
    private Map<LocalDate, String> dailyIncomeDescriptions;
    private Map<LocalDate, String> dailyExpenseCategories;
    
    // List to store all transactions
    private List<Transaction> transactions;
    
    public FinanceData() {
        // Initialize data structures
        initializeEmptyData();
    }
    
    private void initializeEmptyData() {
        // Initialize empty data structures
        dailyIncomes = new HashMap<>();
        dailyExpenses = new HashMap<>();
        dailyExpenseDescriptions = new HashMap<>();
        dailyIncomeDescriptions = new HashMap<>();
        dailyExpenseCategories = new HashMap<>();
        transactions = new ArrayList<>();
        
        // Initialize category budgets
        categoryBudgets = new LinkedHashMap<>();
        categoryBudgets.put("Housing", 1400.00);
        categoryBudgets.put("Food", 800.00);
        categoryBudgets.put("Transportation", 400.00);
        categoryBudgets.put("Utilities", 350.00);
        categoryBudgets.put("Entertainment", 250.00);
        categoryBudgets.put("Healthcare", 300.00);
        categoryBudgets.put("Other", 500.00);
        
        // Initialize category expenses with zeros
        categoryExpenses = new LinkedHashMap<>();
        categoryBudgets.keySet().forEach(category -> categoryExpenses.put(category, 0.0));
    }
    
    // Method to import transactions from CSV
    public void importTransactions(List<Object[]> importedTransactions) {
        for (Object[] transaction : importedTransactions) {
            String dateStr = (String) transaction[0];
            String description = (String) transaction[1];
            String category = (String) transaction[2];
            double amount = (Double) transaction[3];
            
            // Parse date
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // Add to transactions list
            Transaction newTransaction = new Transaction(date, description, category, amount);
            transactions.add(newTransaction);
            
            // Update daily maps
            if (amount >= 0) {
                dailyIncomes.put(date, dailyIncomes.getOrDefault(date, 0.0) + amount);
                dailyIncomeDescriptions.put(date, description);
            } else {
                double absAmount = Math.abs(amount);
                dailyExpenses.put(date, dailyExpenses.getOrDefault(date, 0.0) + absAmount);
                dailyExpenseDescriptions.put(date, description);
                dailyExpenseCategories.put(date, category);
                
                // Update category expenses
                categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0.0) + absAmount);
            }
        }
    }
    
    // Inner class to represent a transaction
    public static class Transaction {
        private LocalDate date;
        private String description;
        private String category;
        private double amount;
        
        public Transaction(LocalDate date, String description, String category, double amount) {
            this.date = date;
            this.description = description;
            this.category = category;
            this.amount = amount;
        }
        
        public LocalDate getDate() { return date; }
        public String getDescription() { return description; }
        public String getCategory() { return category; }
        public double getAmount() { return amount; }
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
        Set<LocalDate> allDates = new HashSet<>();
        allDates.addAll(dailyIncomes.keySet());
        allDates.addAll(dailyExpenses.keySet());
        List<LocalDate> dates = new ArrayList<>(allDates);
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
    
    // Methods to access consistent transaction descriptions
    public String getExpenseDescription(LocalDate date) {
        return dailyExpenseDescriptions.getOrDefault(date, "Unknown expense");
    }
    
    public String getIncomeDescription(LocalDate date) {
        return dailyIncomeDescriptions.getOrDefault(date, "Unknown income");
    }
    
    public String getExpenseCategory(LocalDate date) {
        return dailyExpenseCategories.getOrDefault(date, "Other");
    }
    
    // 修改后的方法，不再依赖随机生成的描述
    public String getRandomExpenseDescription(Random random) {
        return "Expense";
    }
    
    public String getRandomIncomeDescription(Random random) {
        return "Income";
    }
    
    // 修改这些方法返回空数组，而不是样例描述
    public String[] getExpenseDescriptions() {
        return new String[0];
    }
    
    public String[] getIncomeDescriptions() {
        return new String[0];
    }
    
    public List<Transaction> getTransactions() {
        return transactions;
    }
}