package com.example.app.ui.dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardTransactionsPanel extends JPanel {
    private JTable transactionsTable;
    
    public DashboardTransactionsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JLabel titleLabel = new JLabel("Recent Transactions");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);
        
        // Create transactions table
        createTransactionsTable();
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewAllButton = new JButton("View All Transactions");
        buttonPanel.add(viewAllButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void createTransactionsTable() {
        // Define table columns
        String[] columns = {"Date", "Description", "Category", "Amount"};
        
        // Sample data
        Object[][] data = {
            {"2025-03-15", "Grocery Shopping", "Food", -125.45},
            {"2025-03-14", "Salary", "Income", 2500.00},
            {"2025-03-12", "Restaurant", "Dining", -78.50},
            {"2025-03-10", "Gas Station", "Transportation", -45.75},
            {"2025-03-08", "Online Store", "Shopping", -156.80},
            {"2025-03-05", "Freelance Work", "Income", 350.00},
            {"2025-03-01", "Rent Payment", "Housing", -1200.00}
        };
        
        // Create table model
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3) return Double.class; // For proper sorting of amounts
                return String.class;
            }
        };
        
        // Create and configure table
        transactionsTable = new JTable(model);
        transactionsTable.setRowHeight(30);
        transactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionsTable.setShowGrid(true);
        transactionsTable.setGridColor(Color.LIGHT_GRAY);
    }
}