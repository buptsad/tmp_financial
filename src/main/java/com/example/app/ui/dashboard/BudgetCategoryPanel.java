package com.example.app.ui.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class BudgetCategoryPanel extends JPanel {
    private final String category;
    private final double budget;
    private final double expense;
    private final double percentage;
    
    public BudgetCategoryPanel(String category, double budget, double expense, double percentage,
                              ActionListener editAction, ActionListener deleteAction) {
        this.category = category;
        this.budget = budget;
        this.expense = expense;
        this.percentage = percentage;
        
        setupUI(editAction, deleteAction);
    }
    
    private void setupUI(ActionListener editAction, ActionListener deleteAction) {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Category name and amount
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font(categoryLabel.getFont().getName(), Font.BOLD, 14));
        topPanel.add(categoryLabel, BorderLayout.WEST);
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        JLabel amountLabel = new JLabel(String.format("%s / %s (%.1f%%)", 
                currencyFormat.format(expense), 
                currencyFormat.format(budget),
                percentage));
        topPanel.add(amountLabel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Progress bar
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
        
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        
        JButton editButton = new JButton("Edit");
        editButton.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        editButton.addActionListener(editAction);
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.setIcon(UIManager.getIcon("FileChooser.detailsViewIcon"));
        deleteButton.addActionListener(deleteAction);
        
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(progressBar, BorderLayout.CENTER);
        centerPanel.add(buttonsPanel, BorderLayout.EAST);
        
        add(centerPanel, BorderLayout.CENTER);
    }
}