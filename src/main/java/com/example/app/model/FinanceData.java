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
        // 首先清除现有数据
        dailyIncomes.clear();
        dailyExpenses.clear();
        dailyIncomeDescriptions.clear();
        dailyExpenseDescriptions.clear();
        dailyExpenseCategories.clear();
        transactions.clear();
        
        // 重置类别支出
        categoryBudgets.keySet().forEach(category -> categoryExpenses.put(category, 0.0));
        
        // 处理类别映射，将CSV中的类别映射到我们的标准类别
        Map<String, String> categoryMapping = new HashMap<>();
        categoryMapping.put("Income", "Income");
        categoryMapping.put("Expense", "Other");
        categoryMapping.put("/", "Income");  // 修改这里，将 "/" 映射到 "Income"
        
        // 导入交易数据
        for (Object[] transaction : importedTransactions) {
            String dateStr = (String) transaction[0];
            String description = (String) transaction[1];
            String csvCategory = (String) transaction[2];
            double amount = (Double) transaction[3];
            
            // 映射类别
            String category = categoryMapping.getOrDefault(csvCategory, "Other");
            
            try {
                // 解析日期
                LocalDate date = LocalDate.parse(dateStr);
                
                // 添加到交易列表
                Transaction newTransaction = new Transaction(date, description, category, amount);
                transactions.add(newTransaction);
                
                // 更新日常数据映射
                if ("Income".equals(category) || amount >= 0) {  // 收入
                    dailyIncomes.put(date, dailyIncomes.getOrDefault(date, 0.0) + amount);
                    dailyIncomeDescriptions.put(date, description);
                } else {  // 支出
                    double absAmount = Math.abs(amount);
                    dailyExpenses.put(date, dailyExpenses.getOrDefault(date, 0.0) + absAmount);
                    dailyExpenseDescriptions.put(date, description);
                    
                    // 根据描述进一步分类支出
                    String mappedCategory = mapDescriptionToCategory(description);
                    dailyExpenseCategories.put(date, mappedCategory);
                    
                    // 更新类别支出
                    categoryExpenses.put(mappedCategory, 
                        categoryExpenses.getOrDefault(mappedCategory, 0.0) + absAmount);
                }
            } catch (Exception e) {
                System.err.println("处理交易记录时出错: " + e.getMessage() + 
                    " (日期: " + dateStr + ", 描述: " + description + ")");
            }
        }
    }

    private String mapDescriptionToCategory(String description) {
        // 根据描述文本判断交易类别
        description = description.toLowerCase();
        
        if (description.contains("转账") || description.contains("红包")) {
            return "Other";
        } else if (description.contains("群收款")) {
            return "Entertainment";
        } else if (description.contains("商户消费")) {
            return "Food";  // 假设大多数商户消费是食物
        } else if (description.contains("扫二维码付款")) {
            return "Other";
        } else if (description.contains("零钱通")) {
            return "Other";
        }
        
        return "Other";  // 默认类别
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