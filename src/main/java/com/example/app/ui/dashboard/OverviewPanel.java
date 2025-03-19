package com.example.app.ui.dashboard;

import com.example.app.model.FinanceData;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OverviewPanel extends JPanel {
    private FinanceData financeData;
    private JPanel chartPanel;
    
    public OverviewPanel() {
        // Initialize data model
        financeData = new FinanceData();
        
        // Set up panel
        setLayout(new BorderLayout(15, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Create a split panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6); // Left panel gets 60% of space
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        
        // Create the left panel for the chart
        chartPanel = createChartPanel();
        splitPane.setLeftComponent(chartPanel);
        
        // Create the right panel for additional details
        FinancialDetailsPanel detailsPanel = new FinancialDetailsPanel(financeData);
        splitPane.setRightComponent(detailsPanel);
        
        // Add to main panel
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Create chart
        JFreeChart chart = ChartFactory.createFinancialLineChart(financeData);
        
        // Create chart panel with chart
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
}