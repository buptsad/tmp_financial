package com.example.app.ui.dashboard;

import com.example.app.model.FinanceData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

public class FinancialDetailsPanel extends JPanel {
    private FinanceData financeData;
    
    public FinancialDetailsPanel(FinanceData financeData) {
        this.financeData = financeData;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Add all panel sections
        add(createSummaryPanel());
        add(Box.createVerticalStrut(15));
        add(createCategoryProgressPanel());
        add(Box.createVerticalStrut(15));
        add(createTipsPanel());
        add(Box.createVerticalGlue()); // Push everything to the top
    }
    
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Financial Summary",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14)
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add summary items
        summaryPanel.add(createLabelPanel("Monthly Budget:", 
            String.format("$%.2f", financeData.getMonthlyBudget())));
        summaryPanel.add(Box.createVerticalStrut(10));
        
        summaryPanel.add(createLabelPanel("Total Income:", 
            String.format("$%.2f", financeData.getTotalIncome())));
        summaryPanel.add(Box.createVerticalStrut(10));
        
        summaryPanel.add(createLabelPanel("Total Expenses:", 
            String.format("$%.2f", financeData.getTotalExpenses())));
        summaryPanel.add(Box.createVerticalStrut(10));
        
        summaryPanel.add(createLabelPanel("Net Savings:", 
            String.format("$%.2f", financeData.getTotalSavings())));
        
        return summaryPanel;
    }
    
    private JPanel createCategoryProgressPanel() {
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Budget Progress by Category",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14)
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
        progressPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // First, add the overall budget progress
        double overallPercentage = financeData.getOverallBudgetPercentage();
        JPanel overallPanel = new JPanel();
        overallPanel.setLayout(new BoxLayout(overallPanel, BoxLayout.Y_AXIS));
        overallPanel.setBorder(BorderFactory.createTitledBorder("Overall"));
        overallPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel overallLabel = new JLabel(String.format("$%.2f of $%.2f (%.1f%%)", 
            financeData.getTotalExpenses(), 
            financeData.getMonthlyBudget(),
            overallPercentage));
        overallLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JProgressBar overallProgressBar = createProgressBar(overallPercentage);
        overallProgressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        overallProgressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        overallPanel.add(overallLabel);
        overallPanel.add(Box.createVerticalStrut(5));
        overallPanel.add(overallProgressBar);
        
        progressPanel.add(overallPanel);
        progressPanel.add(Box.createVerticalStrut(15));
        
        // Add a panel for category progress bars
        JPanel categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setBorder(BorderFactory.createTitledBorder("Categories"));
        categoriesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add progress bars for each category
        Map<String, Double> categoryBudgets = financeData.getCategoryBudgets();
        Map<String, Double> categoryExpenses = financeData.getCategoryExpenses();
        
        for (String category : categoryBudgets.keySet()) {
            double budget = categoryBudgets.get(category);
            double expense = categoryExpenses.getOrDefault(category, 0.0);
            double percentage = (expense / budget) * 100;
            
            JPanel categoryPanel = new JPanel(new GridBagLayout());
            categoryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            categoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0, 0, 0, 5);  // Small spacing between components
            
            // Label for category
            JLabel categoryLabel = new JLabel(category);
            categoryLabel.setPreferredSize(new Dimension(100, 20));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0.0;
            categoryPanel.add(categoryLabel, gbc);
            
            // Progress bar
            JProgressBar progressBar = createProgressBar(percentage);
            gbc.gridx = 1;
            gbc.weightx = 1.0;  // Progress bar takes up all available space
            categoryPanel.add(progressBar, gbc);
            
            // Value label
            JLabel valueLabel = new JLabel(String.format("$%.0f / $%.0f", expense, budget));
            valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            valueLabel.setPreferredSize(new Dimension(90, 20));
            gbc.gridx = 2;
            gbc.weightx = 0.0;
            categoryPanel.add(valueLabel, gbc);
            
            categoriesPanel.add(categoryPanel);
            categoriesPanel.add(Box.createVerticalStrut(5));
        }
        
        progressPanel.add(categoriesPanel);
        
        return progressPanel;
    }
    
    private JProgressBar createProgressBar(double percentage) {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) percentage);
        progressBar.setStringPainted(true);
        
        // Set color based on percentage
        if (percentage < 70) {
            progressBar.setForeground(new Color(46, 139, 87)); // Green
        } else if (percentage < 90) {
            progressBar.setForeground(new Color(255, 165, 0)); // Orange
        } else {
            progressBar.setForeground(new Color(178, 34, 34)); // Red
        }
        
        return progressBar;
    }
    
    private JPanel createTipsPanel() {
        JPanel tipsPanel = new JPanel();
        tipsPanel.setLayout(new BoxLayout(tipsPanel, BoxLayout.Y_AXIS));
        tipsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Financial Tips",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14)
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
        tipsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Check for categories that are over budget
        StringBuilder tipText = new StringBuilder();
        Map<String, Double> categoryBudgets = financeData.getCategoryBudgets();
        Map<String, Double> categoryExpenses = financeData.getCategoryExpenses();
        
        boolean foundOverBudget = false;
        for (String category : categoryBudgets.keySet()) {
            double budget = categoryBudgets.get(category);
            double expense = categoryExpenses.getOrDefault(category, 0.0);
            
            if (expense > budget) {
                if (!foundOverBudget) {
                    tipText.append("Attention needed in these categories:\n");
                    foundOverBudget = true;
                }
                double overage = expense - budget;
                tipText.append(String.format("• %s is $%.2f over budget\n", category, overage));
            }
        }
        
        // Add general tip based on overall budget
        double percentUsed = financeData.getOverallBudgetPercentage();
        if (!foundOverBudget) {
            if (percentUsed > 90) {
                tipText.append("You're close to exceeding your overall budget. Consider reducing non-essential expenses.");
            } else if (percentUsed > 70) {
                tipText.append("You've used most of your budget. Monitor your spending carefully.");
            } else {
                tipText.append("You're managing your budget well. Consider saving the surplus!");
            }
        } else {
            tipText.append("\nConsider adjusting your spending in the categories above.");
        }
        
        JTextArea tipArea = new JTextArea(tipText.toString());
        tipArea.setWrapStyleWord(true);
        tipArea.setLineWrap(true);
        tipArea.setEditable(false);
        tipArea.setBackground(UIManager.getColor("Panel.background"));
        tipArea.setFont(UIManager.getFont("Label.font"));
        tipArea.setBorder(null);
        tipArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        tipsPanel.add(tipArea);
        
        return tipsPanel;
    }
    
    private JPanel createLabelPanel(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font(labelComponent.getFont().getName(), Font.PLAIN, 14));
        panel.add(labelComponent, BorderLayout.WEST);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font(valueComponent.getFont().getName(), Font.BOLD, 14));
        panel.add(valueComponent, BorderLayout.EAST);
        
        return panel;
    }
}