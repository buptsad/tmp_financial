package com.example.app.ui.dashboard;

import com.example.app.model.CSVDataImporter;
import com.example.app.model.FinanceData;
import com.example.app.ui.CurrencyManager;
import com.example.app.ui.CurrencyManager.CurrencyChangeListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class DashboardBudgetsPanel extends JPanel implements CurrencyManager.CurrencyChangeListener {
    private FinanceData financeData;
    private final JPanel categoriesPanel;
    private String currencySymbol = CurrencyManager.getInstance().getCurrencySymbol();
    
    public DashboardBudgetsPanel() {
        // 初始化财务数据
        financeData = new FinanceData();
        
        // 设置数据目录并加载预算和交易数据
        String dataDirectory = "c:\\tmp_financial\\src\\main\\java\\com\\example\\app\\user_data";
        financeData.setDataDirectory(dataDirectory);
        
        // 先加载交易数据
        loadTransactionData();
        
        // 再加载预算数据
        financeData.loadBudgets();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Budget Management");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Overall budget progress
        JPanel overallPanel = new JPanel(new BorderLayout());
        double overallPercentage = financeData.getOverallBudgetPercentage();
        JLabel overallLabel = new JLabel(String.format("Overall Budget: %.2f%% used", overallPercentage));
        overallLabel.setFont(new Font(overallLabel.getFont().getName(), Font.BOLD, 14));
        overallPanel.add(overallLabel, BorderLayout.NORTH);
        
        JProgressBar overallProgressBar = createProgressBar(overallPercentage);
        overallProgressBar.setPreferredSize(new Dimension(getWidth(), 15));
        overallPanel.add(overallProgressBar, BorderLayout.CENTER);
        
        headerPanel.add(overallPanel, BorderLayout.SOUTH);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(headerPanel, BorderLayout.NORTH);
        
        // Categories panel
        categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        
        // Add category panels
        updateCategoryPanels();
        
        JScrollPane scrollPane = new JScrollPane(categoriesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add button panel
        //JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //JButton addButton = new JButton("Add Category");
        //addButton.setFont(new Font(addButton.getFont().getName(), Font.BOLD, 14));
        //addButton.setIcon(UIManager.getIcon("Tree.addIcon"));
        //addButton.addActionListener(e -> addNewCategory());
        //addButtonPanel.add(addButton);
        
        // 添加查看详细预算按钮
        //JButton viewDetailsButton = new JButton("View Budget Details");
        //viewDetailsButton.setFont(new Font(viewDetailsButton.getFont().getName(), Font.BOLD, 14));
        //viewDetailsButton.addActionListener(e -> openFullBudgetPanel());
        //addButtonPanel.add(viewDetailsButton);
        
        //add(addButtonPanel, BorderLayout.SOUTH);
        
        // 注册货币变化监听器
        CurrencyManager.getInstance().addCurrencyChangeListener(this);
    }
    
    /**
     * 从CSV文件加载交易数据
     */
    private void loadTransactionData() {
        String csvFilePath = "c:\\tmp_financial\\src\\main\\java\\com\\example\\app\\user_data\\user_bill.csv";
        List<Object[]> transactions = CSVDataImporter.importTransactionsFromCSV(csvFilePath);
        
        if (!transactions.isEmpty()) {
            financeData.importTransactions(transactions);
            System.out.println("DashboardBudgetsPanel: 成功导入 " + transactions.size() + " 条交易记录");
        } else {
            System.err.println("DashboardBudgetsPanel: 没有交易记录被导入");
        }
    }
    
    /**
     * 打开完整预算面板
     */
    private void openFullBudgetPanel() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            JFrame frame = (JFrame) window;
            // 这里可以添加导航到完整BudgetsPanel的代码
            JOptionPane.showMessageDialog(frame, 
                "查看完整预算管理", 
                "导航", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void updateCategoryPanels() {
        categoriesPanel.removeAll();
        
        Map<String, Double> budgets = financeData.getCategoryBudgets();
        Map<String, Double> expenses = financeData.getCategoryExpenses();
        
        for (String category : budgets.keySet()) {
            double budget = budgets.get(category);
            double expense = expenses.getOrDefault(category, 0.0);
            double percentage = budget > 0 ? (expense / budget) * 100 : 0;
            
            BudgetCategoryPanel categoryPanel = new BudgetCategoryPanel(
                    category, budget, expense, percentage,
                    e -> editCategory(category),
                    e -> deleteCategory(category)
            );
            
            categoriesPanel.add(categoryPanel);
            categoriesPanel.add(Box.createVerticalStrut(10));
        }
        
        // 添加总计面板
        //if (!budgets.isEmpty()) {
            //double totalBudget = budgets.values().stream().mapToDouble(Double::doubleValue).sum();
            //double totalExpense = expenses.values().stream().mapToDouble(Double::doubleValue).sum();
            //double totalPercentage = totalBudget > 0 ? (totalExpense / totalBudget) * 100 : 0;
            
            //JPanel totalPanel = new JPanel(new BorderLayout());
            //totalPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
            
            //JLabel totalLabel = new JLabel(String.format("<html><b>总预算: %s%.2f</b></html>", currencySymbol, totalBudget));
            //totalLabel.setFont(new Font(totalLabel.getFont().getName(), Font.BOLD, 14));
            //totalPanel.add(totalLabel, BorderLayout.WEST);
            
            //JProgressBar totalProgressBar = createProgressBar(totalPercentage);
            //totalProgressBar.setPreferredSize(new Dimension(150, 15));
            //JPanel progressPanel = new JPanel(new BorderLayout());
            //progressPanel.add(totalProgressBar, BorderLayout.CENTER);
            //totalPanel.add(progressPanel, BorderLayout.EAST);
            
            //categoriesPanel.add(Box.createVerticalStrut(10));
            //categoriesPanel.add(totalPanel);
        //}
        
        revalidate();
        repaint();
    }
    
    private JProgressBar createProgressBar(double percentage) {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) percentage);
        progressBar.setStringPainted(true);
        
        // Set color based on percentage
        if (percentage < 80) {
            progressBar.setForeground(new Color(46, 204, 113)); // Green
        } else if (percentage < 100) {
            progressBar.setForeground(new Color(241, 196, 15)); // Yellow
        } else {
            progressBar.setForeground(new Color(231, 76, 60)); // Red
        }
        
        return progressBar;
    }
    
    private void addNewCategory() {
        BudgetDialog dialog = new BudgetDialog(SwingUtilities.getWindowAncestor(this), "Add Category", "", 0.0);
        if (dialog.showDialog()) {
            String category = dialog.getCategory();
            double budget = dialog.getBudget();
            
            // 更新财务数据并保存到CSV文件
            financeData.updateCategoryBudget(category, budget);
            
            // 更新UI
            updateCategoryPanels();
            
            JOptionPane.showMessageDialog(this, 
                    "新类别已添加: " + category + " 预算: " + currencySymbol + budget,
                    "类别已添加", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void editCategory(String category) {
        double currentBudget = financeData.getCategoryBudget(category);
        BudgetDialog dialog = new BudgetDialog(
                SwingUtilities.getWindowAncestor(this), 
                "Edit Category", 
                category, 
                currentBudget);
        
        if (dialog.showDialog()) {
            double newBudget = dialog.getBudget();
            
            // 更新财务数据并保存到CSV文件
            financeData.updateCategoryBudget(category, newBudget);
            
            // 更新UI
            updateCategoryPanels();
            
            JOptionPane.showMessageDialog(this, 
                    "类别已更新: " + category + " 新预算: " + currencySymbol + newBudget,
                    "类别已更新", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void deleteCategory(String category) {
        int result = JOptionPane.showConfirmDialog(
                this,
                "确定要删除类别: " + category + " 吗?",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // 从财务数据中删除类别并保存到CSV文件
            if (financeData.deleteCategoryBudget(category)) {
                // 更新UI
                updateCategoryPanels();
                
                JOptionPane.showMessageDialog(this, 
                        "类别已删除: " + category,
                        "类别已删除", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Override
    public void onCurrencyChanged(String currencyCode, String currencySymbol) {
        this.currencySymbol = currencySymbol;
        categoriesPanel.removeAll();
        updateCategoryPanels();
        revalidate();
        repaint();
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        // 移除组件时取消监听
        CurrencyManager.getInstance().removeCurrencyChangeListener(this);
    }
}