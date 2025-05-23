package com.example.app.viewmodel.dashboard;

import com.example.app.model.FinanceData;
import com.example.app.model.FinancialAdvice;
import com.example.app.model.DataRefreshListener;
import com.example.app.model.DataRefreshManager;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * ViewModel for FinancialDetailsPanel following the MVVM pattern.
 * Acts as an intermediary between the FinancialDetailsPanel (View) and the data models.
 * <p>
 * Features:
 * <ul>
 *   <li>Provides access to financial summary and advice data for the view</li>
 *   <li>Listens for data refresh events and notifies listeners</li>
 *   <li>Supports registration and removal of change listeners</li>
 *   <li>Handles cleanup of listeners when no longer needed</li>
 * </ul>
 * </p>
 */
public class FinancialDetailsViewModel implements DataRefreshListener {
    private final FinanceData financeData;
    private final FinancialAdvice financialAdvice;
    private final List<FinancialDetailsChangeListener> listeners = new ArrayList<>();

    /**
     * Listener interface for components that need to be notified of financial details changes.
     */
    public interface FinancialDetailsChangeListener {
        /**
         * Called when financial data has changed and the view should be refreshed.
         */
        void onFinancialDataChanged();

        /**
         * Called when financial advice has changed and the view should be refreshed.
         */
        void onAdviceChanged();
    }

    /**
     * Constructs a FinancialDetailsViewModel with the given FinanceData and FinancialAdvice.
     * Registers for data refresh events.
     *
     * @param financeData      the finance data model
     * @param financialAdvice  the financial advice model
     */
    public FinancialDetailsViewModel(FinanceData financeData, FinancialAdvice financialAdvice) {
        this.financeData = financeData;
        this.financialAdvice = financialAdvice;

        // Register for data refresh events
        DataRefreshManager.getInstance().addListener(this);
    }

    /**
     * Adds a listener for financial details changes.
     *
     * @param listener the listener to add
     */
    public void addChangeListener(FinancialDetailsChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener for financial details changes.
     *
     * @param listener the listener to remove
     */
    public void removeChangeListener(FinancialDetailsChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all listeners that financial data has changed.
     */
    private void notifyFinancialDataChanged() {
        for (FinancialDetailsChangeListener listener : new ArrayList<>(listeners)) {
            listener.onFinancialDataChanged();
        }
    }

    /**
     * Notifies all listeners that advice has changed.
     */
    private void notifyAdviceChanged() {
        for (FinancialDetailsChangeListener listener : new ArrayList<>(listeners)) {
            listener.onAdviceChanged();
        }
    }

    // Implement DataRefreshListener method
    @Override
    public void onDataRefresh(DataRefreshManager.RefreshType type) {
        if (type == DataRefreshManager.RefreshType.ADVICE) {
            notifyAdviceChanged();
        }
        if (type == DataRefreshManager.RefreshType.TRANSACTIONS || 
            type == DataRefreshManager.RefreshType.BUDGETS || 
            type == DataRefreshManager.RefreshType.ALL) {
            notifyFinancialDataChanged();
        }
    }
    
    // Methods to get financial data for the view
    public double getMonthlyBudget() {
        return financeData.getMonthlyBudget();
    }
    
    public double getTotalIncome() {
        return financeData.getTotalIncome();
    }
    
    public double getTotalExpenses() {
        return financeData.getTotalExpenses();
    }
    
    public double getTotalSavings() {
        return financeData.getTotalSavings();
    }
    
    public double getOverallBudgetPercentage() {
        return financeData.getOverallBudgetPercentage();
    }
    
    public Map<String, Double> getCategoryBudgets() {
        return financeData.getCategoryBudgets();
    }
    
    public Map<String, Double> getCategoryExpenses() {
        return financeData.getCategoryExpenses();
    }
    
    // Get financial advice data
    public String getAdvice() {
        return financialAdvice.getAdvice();
    }
    
    public String getFormattedGenerationTime() {
        return financialAdvice.getFormattedGenerationTime();
    }
    
    /**
     * Clean up when no longer needed
     */
    public void cleanup() {
        DataRefreshManager.getInstance().removeListener(this);
        listeners.clear();
    }
}