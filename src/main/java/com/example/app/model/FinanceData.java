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
    
    // Expenses and incomes by category
    private Map<String, Double> categoryExpenses;
    private Map<String, Double> categoryIncomes;
    
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
        
        // Default budget values will be set after importing data
        
        // Initialize category expenses and incomes
        categoryExpenses = new LinkedHashMap<>();
        categoryIncomes = new LinkedHashMap<>();
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
        
        // 用于收集所有可能的类别
        Set<String> incomeCategories = new HashSet<>();
        Set<String> expenseCategories = new HashSet<>();
        
        // 导入交易数据并收集类别
        for (Object[] transaction : importedTransactions) {
            String dateStr = (String) transaction[0];
            String description = (String) transaction[1];
            String csvCategory = (String) transaction[2];
            double amount = (Double) transaction[3];
            
            try {
                // 解析日期
                LocalDate date = LocalDate.parse(dateStr);
                
                // 确定交易类型和分类
                String category;
                boolean isIncome = amount >= 0;
                
                // 根据交易描述和CSV类别确定最终类别
                if (isIncome) {
                    category = determineIncomeCategory(description, csvCategory);
                    incomeCategories.add(category);
                } else {
                    category = determineExpenseCategory(description, csvCategory);
                    expenseCategories.add(category);
                }
                
                // 添加到交易列表
                Transaction newTransaction = new Transaction(date, description, category, amount);
                transactions.add(newTransaction);
                
                // 更新日常数据映射
                if (isIncome) {  // 收入
                    dailyIncomes.put(date, dailyIncomes.getOrDefault(date, 0.0) + amount);
                    dailyIncomeDescriptions.put(date, description);
                    
                    // 更新收入类别统计
                    categoryIncomes.put(category, categoryIncomes.getOrDefault(category, 0.0) + amount);
                } else {  // 支出
                    double absAmount = Math.abs(amount);
                    dailyExpenses.put(date, dailyExpenses.getOrDefault(date, 0.0) + absAmount);
                    dailyExpenseDescriptions.put(date, description);
                    dailyExpenseCategories.put(date, category);
                    
                    // 更新支出类别统计
                    categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0.0) + absAmount);
                }
            } catch (Exception e) {
                System.err.println("处理交易记录时出错: " + e.getMessage() + 
                    " (日期: " + dateStr + ", 描述: " + description + ")");
            }
        }
        
        // 根据收集到的类别分配预算
        allocateBudgets(expenseCategories);
    }
    
    // 根据收集到的类别自动分配预算
    private void allocateBudgets(Set<String> expenseCategories) {
        double totalBudget = MONTHLY_BUDGET;
        
        // 清空现有预算
        categoryBudgets.clear();
        
        if (expenseCategories.isEmpty()) {
            // 如果没有导入支出类别，设置默认类别
            categoryBudgets.put("Other", totalBudget);
            return;
        }
        
        // 平均分配预算
        double budgetPerCategory = totalBudget / expenseCategories.size();
        
        // 为每个类别分配预算
        for (String category : expenseCategories) {
            // 对于某些特殊类别可以调整预算分配比例
            double categoryBudget;
            switch (category.toLowerCase()) {
                case "food":
                    categoryBudget = totalBudget * 0.25; // 25% 用于食物
                    break;
                case "housing":
                    categoryBudget = totalBudget * 0.35; // 35% 用于住房
                    break;
                case "transportation":
                    categoryBudget = totalBudget * 0.10; // 10% 用于交通
                    break;
                case "entertainment":
                    categoryBudget = totalBudget * 0.05; // 5% 用于娱乐
                    break;
                default:
                    categoryBudget = budgetPerCategory; // 其他类别均分剩余预算
            }
            categoryBudgets.put(category, categoryBudget);
        }
        
        // 确保预算总和与月度预算相符
        double allocatedBudget = categoryBudgets.values().stream().mapToDouble(Double::doubleValue).sum();
        if (allocatedBudget != totalBudget) {
            // 调整"其他"类别的预算以平衡
            if (categoryBudgets.containsKey("Other")) {
                double otherBudget = categoryBudgets.get("Other") + (totalBudget - allocatedBudget);
                categoryBudgets.put("Other", otherBudget);
            } else {
                categoryBudgets.put("Other", totalBudget - allocatedBudget);
            }
        }
    }
    
    private String determineIncomeCategory(String description, String csvCategory) {
        // 根据描述和CSV类别确定收入类别
        description = description.toLowerCase();
        
        if (description.contains("工资") || description.contains("薪水") || description.contains("salary")) {
            return "Salary";
        } else if (description.contains("转账") && !description.contains("零钱通")) {
            return "Transfer";
        } else if (description.contains("投资") || description.contains("股息") || description.contains("dividend")) {
            return "Investment";
        } else if (description.contains("退款") || description.contains("refund")) {
            return "Refund";
        } else if (description.contains("零钱通")) {
            return "Savings";
        }
        
        // 默认类别
        return "Other Income";
    }
    
    private String determineExpenseCategory(String description, String csvCategory) {
        // 根据描述和CSV类别确定支出类别
        description = description.toLowerCase();
        csvCategory = csvCategory.toLowerCase();
        
        // 不同的分类逻辑
        if (csvCategory.contains("群收款")) {
            return "Entertainment";
        } else if (description.contains("红包")) {
            return "Gift";
        } else if (csvCategory.contains("商户消费")) {
            if (description.contains("超市") || description.contains("食品") || 
                description.contains("饭") || description.contains("餐")) {
                return "Food";
            } else if (description.contains("交通") || description.contains("车")) {
                return "Transportation";
            } else if (description.contains("医") || description.contains("药")) {
                return "Healthcare";
            }
            return "Shopping"; // 默认商户消费归为购物类
        } else if (csvCategory.contains("扫二维码付款")) {
            return "Service";
        }
        
        // 更具体的类别判断
        if (description.contains("房租") || description.contains("水电") || description.contains("物业")) {
            return "Housing";
        } else if (description.contains("电话") || description.contains("网络")) {
            return "Utilities";
        } else if (description.contains("医") || description.contains("药") || 
                   description.contains("hospital") || description.contains("clinic")) {
            return "Healthcare";
        }
        
        // 默认类别
        return "Other";
    }
    
    // 其他方法保持不变...
    
    // 新增获取收入类别数据的方法
    public Map<String, Double> getCategoryIncomes() {
        return categoryIncomes;
    }
    
    // 修改后的 getCategoryBudgets 方法，如果预算为空，初始化默认值
    public Map<String, Double> getCategoryBudgets() {
        if (categoryBudgets.isEmpty()) {
            // 如果没有数据导入，提供默认预算
            categoryBudgets.put("Housing", 1400.00);
            categoryBudgets.put("Food", 800.00);
            categoryBudgets.put("Transportation", 400.00);
            categoryBudgets.put("Utilities", 350.00);
            categoryBudgets.put("Entertainment", 250.00);
            categoryBudgets.put("Healthcare", 300.00);
            categoryBudgets.put("Other", 500.00);
        }
        return categoryBudgets;
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
        public boolean isExpense() { return amount < 0; }
        public boolean isIncome() { return amount >= 0; }
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