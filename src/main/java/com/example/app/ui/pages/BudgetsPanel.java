package com.example.app.ui.pages;

import com.example.app.ui.CurrencyManager;
import com.example.app.ui.CurrencyManager.CurrencyChangeListener;
import com.example.app.ui.dashboard.BudgetCategoryPanel;
import com.example.app.ui.dashboard.BudgetDialog;
import com.example.app.viewmodel.BudgetViewModel;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;

public class BudgetsPanel extends JPanel implements CurrencyChangeListener, BudgetViewModel.BudgetChangeListener {
    private final BudgetViewModel viewModel;
    private final JPanel userBudgetsPanel;
    private final JPanel aiSuggestedPanel;
    private Map<String, Double> currentSuggestedBudgets; // Store last generated suggestions

    public BudgetsPanel(String username) {
        // Initialize ViewModel
        this.viewModel = new BudgetViewModel(username);
        this.viewModel.addBudgetChangeListener(this);
        
        setLayout(new BorderLayout(20, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Budget Management");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Overall budget progress
        JPanel overallPanel = createOverallBudgetPanel();
        headerPanel.add(overallPanel, BorderLayout.SOUTH);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel - split into left and right
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        // Left panel - Current budget allocation
        JPanel userPanel = new JPanel(new BorderLayout());
        TitledBorder userBorder = BorderFactory.createTitledBorder("Your Budget Allocation");
        userBorder.setTitleFont(new Font(getFont().getName(), Font.BOLD, 16));
        userPanel.setBorder(BorderFactory.createCompoundBorder(
                userBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        userBudgetsPanel = new JPanel();
        userBudgetsPanel.setLayout(new BoxLayout(userBudgetsPanel, BoxLayout.Y_AXIS));
        updateUserCategoryPanels();

        JScrollPane userScrollPane = new JScrollPane(userBudgetsPanel);
        userScrollPane.setBorder(BorderFactory.createEmptyBorder());
        userScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        userPanel.add(userScrollPane, BorderLayout.CENTER);

        // Add category button
        JPanel userButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Category");
        addButton.setIcon(UIManager.getIcon("Tree.addIcon"));
        addButton.addActionListener(e -> addNewCategory());
        userButtonPanel.add(addButton);
        userPanel.add(userButtonPanel, BorderLayout.SOUTH);

        // Right panel - AI suggested allocation
        JPanel aiPanel = new JPanel(new BorderLayout());
        TitledBorder aiBorder = BorderFactory.createTitledBorder("AI Suggested Budget");
        aiBorder.setTitleFont(new Font(getFont().getName(), Font.BOLD, 16));
        aiPanel.setBorder(BorderFactory.createCompoundBorder(
                aiBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        aiSuggestedPanel = new JPanel();
        aiSuggestedPanel.setLayout(new BoxLayout(aiSuggestedPanel, BoxLayout.Y_AXIS));
        updateAISuggestedPanels();

        JScrollPane aiScrollPane = new JScrollPane(aiSuggestedPanel);
        aiScrollPane.setBorder(BorderFactory.createEmptyBorder());
        aiScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        aiPanel.add(aiScrollPane, BorderLayout.CENTER);

        // Shuffle button for AI suggestions
        JPanel aiButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton shuffleButton = new JButton("Shuffle Suggestions");
        shuffleButton.setIcon(UIManager.getIcon("Table.descendingSortIcon"));
        shuffleButton.addActionListener(e -> shuffleAISuggestions());
        
        JButton applyButton = new JButton("Apply Suggestions");
        applyButton.setIcon(UIManager.getIcon("FileView.fileIcon"));
        applyButton.addActionListener(e -> applyAISuggestions());
        
        aiButtonPanel.add(shuffleButton);
        aiButtonPanel.add(applyButton);
        aiPanel.add(aiButtonPanel, BorderLayout.SOUTH);

        // Add both panels to the content area
        contentPanel.add(userPanel);
        contentPanel.add(aiPanel);
        add(contentPanel, BorderLayout.CENTER);
        
        // Register as currency change listener
        CurrencyManager.getInstance().addCurrencyChangeListener(this);
    }

    private JPanel createOverallBudgetPanel() {
        JPanel overallPanel = new JPanel(new BorderLayout());
        double overallPercentage = viewModel.getOverallBudgetPercentage();
        JLabel overallLabel = new JLabel(String.format("Overall Budget: %.2f%% used", overallPercentage));
        overallLabel.setFont(new Font(overallLabel.getFont().getName(), Font.BOLD, 14));
        overallPanel.add(overallLabel, BorderLayout.NORTH);
        
        JProgressBar overallProgressBar = createProgressBar(overallPercentage);
        overallProgressBar.setPreferredSize(new Dimension(getWidth(), 15));
        overallPanel.add(overallProgressBar, BorderLayout.CENTER);
        
        return overallPanel;
    }

    private void updateUserCategoryPanels() {
        userBudgetsPanel.removeAll();
        
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
            
            userBudgetsPanel.add(categoryPanel);
            userBudgetsPanel.add(Box.createVerticalStrut(10));
        }
        
        // Add total
        double totalBudget = budgets.values().stream().mapToDouble(Double::doubleValue).sum();
        
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        
        String currencySymbol = CurrencyManager.getInstance().getCurrencySymbol();
        JLabel totalLabel = new JLabel(String.format("<html><b>Total: %s%.2f</b></html>", currencySymbol, totalBudget));
        totalLabel.setFont(new Font(totalLabel.getFont().getName(), Font.BOLD, 14));
        totalPanel.add(totalLabel, BorderLayout.WEST);
        
        userBudgetsPanel.add(Box.createVerticalStrut(10));
        userBudgetsPanel.add(totalPanel);
        
        revalidate();
        repaint();
    }
    
    private void updateAISuggestedPanels() {
        aiSuggestedPanel.removeAll();
        
        // Get actual budgets
        Map<String, Double> actualBudgets = viewModel.getCategoryBudgets();
        double totalBudget = actualBudgets.values().stream().mapToDouble(Double::doubleValue).sum();
        
        // Create or use existing AI suggestions
        if (currentSuggestedBudgets == null) {
            currentSuggestedBudgets = viewModel.generateSuggestedBudgets();
        }
        
        // Display each category with comparison to actual budget
        for (String category : currentSuggestedBudgets.keySet()) {
            double suggestedBudget = currentSuggestedBudgets.get(category);
            double actualBudget = actualBudgets.getOrDefault(category, 0.0);
            double difference = suggestedBudget - actualBudget;
            
            JPanel categoryPanel = createAISuggestionPanel(category, suggestedBudget, difference);
            aiSuggestedPanel.add(categoryPanel);
            aiSuggestedPanel.add(Box.createVerticalStrut(10));
        }
        
        // Add total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        
        String currencySymbol = CurrencyManager.getInstance().getCurrencySymbol();
        JLabel totalLabel = new JLabel(String.format("<html><b>Total: %s%.2f</b></html>", currencySymbol, totalBudget));
        totalLabel.setFont(new Font(totalLabel.getFont().getName(), Font.BOLD, 14));
        totalPanel.add(totalLabel, BorderLayout.WEST);
        
        aiSuggestedPanel.add(Box.createVerticalStrut(10));
        aiSuggestedPanel.add(totalPanel);
        
        revalidate();
        repaint();
    }
    
    private JPanel createAISuggestionPanel(String category, double budget, double difference) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        String currencySymbol = CurrencyManager.getInstance().getCurrencySymbol();
        
        // Category name and budget amount
        JPanel leftPanel = new JPanel(new BorderLayout());
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font(categoryLabel.getFont().getName(), Font.BOLD, 14));
        leftPanel.add(categoryLabel, BorderLayout.NORTH);
        
        JLabel budgetLabel = new JLabel(String.format("%s%.2f", currencySymbol, budget));
        budgetLabel.setFont(new Font(budgetLabel.getFont().getName(), Font.PLAIN, 14));
        leftPanel.add(budgetLabel, BorderLayout.SOUTH);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        // Difference indicator
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        // Format difference display
        String diffText;
        Color diffColor;
        
        if (Math.abs(difference) < 0.01) {
            diffText = "No change";
            diffColor = Color.GRAY;
        } else if (difference > 0) {
            diffText = String.format("+%s%.2f", currencySymbol, difference);
            diffColor = new Color(46, 204, 113); // Green
        } else {
            diffText = String.format("-%s%.2f", currencySymbol, Math.abs(difference));
            diffColor = new Color(231, 76, 60); // Red
        }
        
        JLabel diffLabel = new JLabel(diffText);
        diffLabel.setForeground(diffColor);
        diffLabel.setFont(new Font(diffLabel.getFont().getName(), Font.BOLD, 14));
        rightPanel.add(diffLabel, BorderLayout.CENTER);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
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
        String currencySymbol = CurrencyManager.getInstance().getCurrencySymbol();

        BudgetDialog dialog = new BudgetDialog(SwingUtilities.getWindowAncestor(this), "Add Category", "", 0.0);
        if (dialog.showDialog()) {
            String category = dialog.getCategory();
            double budget = dialog.getBudget();
            
            // Update through view model
            viewModel.updateCategoryBudget(category, budget);
            
            JOptionPane.showMessageDialog(this, 
                    "已添加新类别: " + category + " 预算为: " + currencySymbol + budget,
                    "类别已添加", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void editCategory(String category) {
        String currencySymbol = CurrencyManager.getInstance().getCurrencySymbol();

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
                    "已更新类别: " + category + " 的预算为: " + currencySymbol + newBudget,
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
    
    private void shuffleAISuggestions() {
        // Generate new suggestions through view model
        currentSuggestedBudgets = viewModel.generateSuggestedBudgets();
        updateAISuggestedPanels();
        
        JOptionPane.showMessageDialog(this,
                "New AI budget suggestions generated!",
                "Suggestions Updated",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void applyAISuggestions() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "确定要应用AI建议的预算分配到你的预算中吗?",
                "应用AI建议",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Apply suggestions through view model
            viewModel.applySuggestedBudgets(currentSuggestedBudgets);
            
            JOptionPane.showMessageDialog(this,
                    "AI建议的预算已应用到你的预算分配中!",
                    "已应用建议",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void onCurrencyChanged(String currencyCode, String currencySymbol) {
        // Refresh panels when currency changes
        updateUserCategoryPanels();
        updateAISuggestedPanels();
    }

    @Override
    public void onBudgetDataChanged() {
        // Update UI when view model notifies of data changes
        updateUserCategoryPanels();
        updateAISuggestedPanels();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        // Clean up when panel is removed
        CurrencyManager.getInstance().removeCurrencyChangeListener(this);
        viewModel.removeBudgetChangeListener(this);
        viewModel.cleanup();
    }
}


