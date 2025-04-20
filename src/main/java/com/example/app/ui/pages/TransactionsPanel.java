package com.example.app.ui.pages;

import com.example.app.model.FinanceData;
import com.example.app.ui.dialogs.CSVImportDialog;
import com.example.app.user_data.UserBillStorage;
import com.example.app.model.DataRefreshListener;
import com.example.app.model.DataRefreshManager;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionsPanel extends JPanel implements DataRefreshListener {
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilterComboBox;
    private FinanceData financeData;
    private JButton addButton, deleteButton, saveButton, cancelButton, askAIButton;
    private boolean hasUnsavedChanges = false;

    private String username;
    private static final Logger LOGGER = Logger.getLogger(TransactionsPanel.class.getName());
    
    public TransactionsPanel(String username) {
        this.username = username;
        financeData = new FinanceData();
        setLayout(new BorderLayout());
        
        // Create the title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Transactions", JLabel.LEFT);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Create search and filter panel
        JPanel searchPanel = createSearchFilterPanel();
        titlePanel.add(searchPanel, BorderLayout.EAST);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Create transactions table
        createTransactionsTable();
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create bottom panel with buttons
        JPanel bottomPanel = createButtonPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Register as listener for data refresh events
        DataRefreshManager.getInstance().addListener(this);
        
        // Load saved transactions
        loadSavedTransactions();
    }
    
    private JPanel createSearchFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 15));
        
        // Search field
        JLabel searchLabel = new JLabel("Search: ");
        searchField = new JTextField(15);
        searchField.addActionListener(e -> applyFilters());
        
        // Category filter
        JLabel categoryLabel = new JLabel("Category: ");
        categoryFilterComboBox = new JComboBox<>();
        categoryFilterComboBox.setEditable(true);
        
        // Add empty option as default (show all)
        categoryFilterComboBox.addItem("");
        
        // Add categories from finance data
        Set<String> categories = financeData.getCategoryBudgets().keySet();
        for (String category : categories) {
            categoryFilterComboBox.addItem(category);
        }
        
        categoryFilterComboBox.addActionListener(e -> applyFilters());
        
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(categoryLabel);
        panel.add(categoryFilterComboBox);
        
        return panel;
    }
    
    private void createTransactionsTable() {
        // Define table columns
        String[] columns = {"Date", "Description", "Category", "Amount", "Delete"};
        
        // Create table model that supports boolean for checkbox column
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) return Boolean.class; // Checkbox column
                if (column == 3) return Double.class;  // Amount column
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 4 || tableModel.getValueAt(row, 4) instanceof Boolean; // Make all columns editable except checkbox column
            }
        };
        
        // Add sample data from FinanceData
        populateTableWithSampleData();
        
        // Create and configure table
        transactionsTable = new JTable(tableModel);
        transactionsTable.setRowHeight(30);
        transactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionsTable.setShowGrid(true);
        transactionsTable.setGridColor(Color.LIGHT_GRAY);
        
        // Add table cell edit listener to track changes
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                setHasUnsavedChanges(true);
            }
        });

        // Set column widths
        transactionsTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
        transactionsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Description
        transactionsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Category
        transactionsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Amount
        transactionsTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Delete checkbox
        
        // Make the table sortable
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        transactionsTable.setRowSorter(sorter);
    }
    
    private void populateTableWithSampleData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<LocalDate, Double> expenses = financeData.getDailyExpenses();
        Map<LocalDate, Double> incomes = financeData.getDailyIncomes();
        
        // Process and add all dates
        List<LocalDate> dates = financeData.getDates();
        
        for (LocalDate date : dates) {
            Double expense = expenses.get(date);
            Double income = incomes.get(date);
            
            // Format date
            String dateStr = date.format(formatter);
            
            // Add expense transaction
            if (expense != null && expense > 0) {
                String category = financeData.getExpenseCategory(date);
                String description = financeData.getExpenseDescription(date);
                
                // Add negative expense
                Object[] rowData = {dateStr, description, category, -expense, false};
                tableModel.addRow(rowData);
            }
            
            // Add income transaction
            if (income != null && income > 0) {
                // For incomes, use "Income" category
                String description = financeData.getIncomeDescription(date);
                
                // Add positive income
                Object[] rowData = {dateStr, description, "Income", income, false};
                tableModel.addRow(rowData);
            }
        }
    }
    
    private String getRandomCategory(Set<String> categories, Random random) {
        List<String> categoryList = new ArrayList<>(categories);
        return categoryList.get(random.nextInt(categoryList.size()));
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Left side buttons
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = new JButton("Add Transaction");
        deleteButton = new JButton("Delete Selected");
        JButton loadFromCSVButton = new JButton("Load from CSV");
        
        addButton.addActionListener(e -> addNewTransaction());
        deleteButton.addActionListener(e -> deleteSelectedTransactions());
        loadFromCSVButton.addActionListener(e -> openCSVImportDialog());
        
        leftPanel.add(addButton);
        leftPanel.add(deleteButton);
        leftPanel.add(loadFromCSVButton);
        
        // Right side buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        askAIButton = new JButton("Ask AI");
        saveButton = new JButton("Save Changes");
        cancelButton = new JButton("Cancel");
        
        askAIButton.addActionListener(e -> askAI());
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> cancelChanges());
        
        // Disable save/cancel buttons initially
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
        
        rightPanel.add(askAIButton);
        rightPanel.add(saveButton);
        rightPanel.add(cancelButton);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void openCSVImportDialog() {
        CSVImportDialog dialog = new CSVImportDialog(SwingUtilities.getWindowAncestor(this), this, financeData);
        dialog.setVisible(true);
    }

    // Method to add transactions from CSV import
    public void addTransactionsFromCSV(List<Object[]> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return;
        }
        
        for (Object[] transaction : transactions) {
            tableModel.insertRow(0, transaction);
        }
        
        setHasUnsavedChanges(true);
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = (String) categoryFilterComboBox.getSelectedItem();
        
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) transactionsTable.getRowSorter();
        
        // If both filters are empty, show all rows
        if (searchText.isEmpty() && (selectedCategory == null || selectedCategory.isEmpty())) {
            sorter.setRowFilter(null);
            return;
        }
        
        // Create a filter that checks both the search text and category
        RowFilter<DefaultTableModel, Integer> filter = new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                int row = entry.getIdentifier();
                
                // Check category filter if selected
                if (selectedCategory != null && !selectedCategory.isEmpty()) {
                    String rowCategory = (String) entry.getModel().getValueAt(row, 2);
                    if (!rowCategory.equals(selectedCategory)) {
                        return false;
                    }
                }
                
                // Check search text if not empty
                if (!searchText.isEmpty()) {
                    boolean matchFound = false;
                    // Check columns 0, 1, 2 (Date, Description, Category)
                    for (int i = 0; i < 3; i++) {
                        String value = entry.getModel().getValueAt(row, i).toString().toLowerCase();
                        if (value.contains(searchText)) {
                            matchFound = true;
                            break;
                        }
                    }
                    return matchFound;
                }
                
                return true;
            }
        };
        
        sorter.setRowFilter(filter);
    }
    
    private void addNewTransaction() {
        // Create a sample transaction
        LocalDate today = LocalDate.now();
        Random random = new Random();
        
        // Randomly choose if it's income or expense
        boolean isIncome = random.nextBoolean();
        
        String description = isIncome ? 
                             financeData.getRandomIncomeDescription(random) : 
                             financeData.getRandomExpenseDescription(random);
        String category = isIncome ? "Income" : getRandomCategory(financeData.getCategoryExpenses().keySet(), random);
        double amount = isIncome ? 
                        100 + random.nextDouble() * 400 : 
                        -(50 + random.nextDouble() * 200);
        
        // Add new row at the top of the table
        Object[] rowData = {today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), 
                           description, category, amount, false};
        tableModel.insertRow(0, rowData);
        
        // Mark that we have unsaved changes
        setHasUnsavedChanges(true);
    }
    
    private void deleteSelectedTransactions() {
        // Collect indices of rows to remove (in reverse order to avoid index shifting problems)
        List<Integer> rowsToRemove = new ArrayList<>();
        
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            Boolean isChecked = (Boolean) tableModel.getValueAt(i, 4);
            if (isChecked != null && isChecked) {
                rowsToRemove.add(i);
            }
        }
        
        // Remove the rows
        for (int row : rowsToRemove) {
            tableModel.removeRow(row);
        }
        
        if (!rowsToRemove.isEmpty()) {
            setHasUnsavedChanges(true);
        }
    }
    
    private void askAI() {
        // No implementation needed as per requirements
        JOptionPane.showMessageDialog(this, 
                "AI assistant would be available here to help with your transactions.", 
                "Ask AI", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void saveChanges() {
        try {
            // Collect all table data to save
            List<Object[]> transactionsToSave = new ArrayList<>();
            
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String date = tableModel.getValueAt(i, 0).toString();
                String description = tableModel.getValueAt(i, 1).toString();
                String category = tableModel.getValueAt(i, 2).toString();
                Double amount = (Double) tableModel.getValueAt(i, 3);
                
                // Create transaction data array (matches CSV format)
                Object[] transactionData = {date, description, category, amount, false};
                transactionsToSave.add(transactionData);
            }
            
            // Save to storage
            UserBillStorage.setUsername(username);
            UserBillStorage.saveTransactions(transactionsToSave);
            LOGGER.log(Level.INFO, "Saved {0} transactions to storage", transactionsToSave.size());
            
            // Update the finance data model
            List<Object[]> formattedTransactions = new ArrayList<>(transactionsToSave);
            financeData.importTransactionsAndNotify(formattedTransactions);
            
            // Show success message
            JOptionPane.showMessageDialog(this, 
                    "Changes saved successfully", 
                    "Changes Saved", JOptionPane.INFORMATION_MESSAGE);
            
            setHasUnsavedChanges(false);
            
            // Manually trigger a refresh for all components
            DataRefreshManager.getInstance().refreshTransactions();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to save transactions", ex);
            JOptionPane.showMessageDialog(this, 
                    "Error saving transactions: " + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelChanges() {
        // Reset the table with original data
        tableModel.setRowCount(0);
        populateTableWithSampleData();
        
        setHasUnsavedChanges(false);
    }
    
    private void setHasUnsavedChanges(boolean hasChanges) {
        this.hasUnsavedChanges = hasChanges;
        saveButton.setEnabled(hasChanges);
        cancelButton.setEnabled(hasChanges);
    }

    private void loadSavedTransactions() {
        try {
            // Ensure UserBillStorage is initialized
            if (username != null && !username.isEmpty()) {
                if (UserBillStorage.getBillFilePath() == null) {
                    UserBillStorage.setUsername(username);
                }
            }
            
            // Load transactions from storage
            List<Object[]> savedTransactions = UserBillStorage.loadTransactions();
            
            if (savedTransactions != null && !savedTransactions.isEmpty()) {
                // Clear existing table data
                tableModel.setRowCount(0);
                
                // Add loaded transactions to table
                for (Object[] transaction : savedTransactions) {
                    if (transaction.length >= 4) {
                        String date = transaction[0].toString();
                        String description = transaction[1].toString();
                        String category = transaction[2].toString();
                        Double amount = 0.0;
                        
                        try {
                            if (transaction[3] instanceof Double) {
                                amount = (Double) transaction[3];
                            } else {
                                amount = Double.parseDouble(transaction[3].toString());
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.WARNING, "Invalid amount format: " + transaction[3], e);
                        }
                        
                        // Add to table
                        tableModel.addRow(new Object[] {date, description, category, amount, false});
                    }
                }
                
                // IMPORTANT: Update financeData with loaded transactions
                // This ensures consistency between displayed data and model
                financeData.importTransactions(savedTransactions);
                
                LOGGER.log(Level.INFO, "Successfully loaded " + savedTransactions.size() + " transactions");
            } else {
                LOGGER.log(Level.WARNING, "No transactions loaded or empty transaction list");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading transactions", e);
            JOptionPane.showMessageDialog(this, 
                "Failed to load transactions: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onDataRefresh(DataRefreshManager.RefreshType type) {
        if (type == DataRefreshManager.RefreshType.TRANSACTIONS || 
            type == DataRefreshManager.RefreshType.ALL) {
            // Clear the table
            tableModel.setRowCount(0);
            
            // Load transactions directly from storage
            // This ensures we see what's actually saved on disk
            loadSavedTransactions();
            
            // Don't call populateTableWithSampleData() - it gets data from financeData
            // which may not reflect what's in storage
            
            // Log the refresh for debugging
            LOGGER.log(Level.INFO, "Refreshed transactions table with {0} rows", tableModel.getRowCount());
        }
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        // Unregister when component is removed from UI
        DataRefreshManager.getInstance().removeListener(this);
    }
}