package com.example.app.ui.dashboard;

import com.example.app.ui.CurrencyManager;
import com.example.app.ui.CurrencyManager.CurrencyChangeListener;
import com.example.app.viewmodel.dashboard.DashboardBudgetsViewModel;
import com.example.app.viewmodel.dashboard.DashboardBudgetsViewModel.BudgetChangeListener;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.logging.Logger;

public class DashboardBudgetsPanel extends JPanel implements CurrencyChangeListener, BudgetChangeListener {
    private static final Logger LOGGER = Logger.getLogger(DashboardBudgetsPanel.class.getName());
    
    // ViewModel reference
    private final DashboardBudgetsViewModel viewModel;
    
    // UI components
    private final JPanel categoriesPanel;
    private String currencySymbol;
    
    public DashboardBudgetsPanel(String username) {
        // Initialize ViewModel
        this.viewModel = new DashboardBudgetsViewModel(username);
        this.viewModel.addBudgetChangeListener(this);
        
        // Initialize currency symbol
        this.currencySymbol = CurrencyManager.getInstance().getCurrencySymbol();
        
        // Set up UI
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Budget Management");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Overall budget progress
        JPanel overallPanel = new JPanel(new BorderLayout());
        double overallPercentage = viewModel.getOverallBudgetPercentage();
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
        
        // Register as listeners
        CurrencyManager.getInstance().addCurrencyChangeListener(this);
    }
    
    private void updateCategoryPanels() {
        categoriesPanel.removeAll();
        
        Map<String, Double> budgets = viewModel.getCategoryBudgets();
        Map<String, Double> expenses = viewModel.getCategoryExpenses();
        
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
    
    private void editCategory(String category) {
        double currentBudget = viewModel.getCategoryBudget(category);
        BudgetDialog dialog = new BudgetDialog(
                SwingUtilities.getWindowAncestor(this), 
                "Edit Category", 
                category, 
                currentBudget);
        
        if (dialog.showDialog()) {
            double newBudget = dialog.getBudget();
            
            // Update through view model
            viewModel.updateCategoryBudget(category, newBudget);
            
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
            // Delete through view model
            if (viewModel.deleteCategoryBudget(category)) {
                JOptionPane.showMessageDialog(this, 
                        "类别已删除: " + category,
                        "类别已删除", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void addNewCategory() {
        BudgetDialog dialog = new BudgetDialog(
                SwingUtilities.getWindowAncestor(this), 
                "Add Category", 
                "", 
                0.0);
                
        if (dialog.showDialog()) {
            String category = dialog.getCategory();
            double budget = dialog.getBudget();
            
            // Update through view model
            viewModel.updateCategoryBudget(category, budget);
            
            JOptionPane.showMessageDialog(this, 
                    "新类别已添加: " + category + " 预算: " + currencySymbol + budget,
                    "类别已添加", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void onCurrencyChanged(String currencyCode, String currencySymbol) {
        this.currencySymbol = currencySymbol;
        updateCategoryPanels();
    }
    
    @Override
    public void onBudgetDataChanged() {
        // Update UI when view model notifies of budget changes
        updateCategoryPanels();
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        // Clean up when panel is removed from UI
        CurrencyManager.getInstance().removeCurrencyChangeListener(this);
        viewModel.removeBudgetChangeListener(this);
        viewModel.cleanup();
    }
    
    // Public method to update username if needed
    public void setUsername(String username) {
        // In MVVM, we'd create a new ViewModel for the new user
        // For simplicity, we'll replace the entire panel in the parent component
        
        // Let the parent container know that it needs to recreate this panel
        Container parent = getParent();
        if (parent != null) {
            parent.remove(this);
            parent.add(new DashboardBudgetsPanel(username));
            parent.revalidate();
            parent.repaint();
        }
    }
    
    // Add a button to open the full budget panel view
    private void openFullBudgetPanel() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            JFrame frame = (JFrame) window;
            // Code to navigate to the full BudgetsPanel would go here
            JOptionPane.showMessageDialog(frame, 
                "Navigate to full Budget Management", 
                "Navigation", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}