package com.example.app.ui.dashboard.report;

import com.example.app.model.FinanceData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CategorySpendingChartPanel extends JPanel {
    
    private final FinanceData financeData = new FinanceData();
    
    public CategorySpendingChartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Spending by Category"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        // Create the chart
        JFreeChart chart = createChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setMouseWheelEnabled(true);
        
        add(chartPanel, BorderLayout.CENTER);
    }
    
    private JFreeChart createChart() {
        // Create dataset for the chart
        DefaultCategoryDataset dataset = createDataset();
        
        // Create the chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Budget vs. Actual Spending",
                "Category",
                "Amount ($)",
                dataset,
                PlotOrientation.VERTICAL,
                true,  // Legend
                true,  // Tooltips
                false  // URLs
        );
        
        // Customize the plot
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        
        // Customize domain axis
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.2);
        
        // Customize range axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        // Customize the renderer
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.1);
        
        // Budget bars (blue)
        renderer.setSeriesPaint(0, new Color(65, 105, 225));
        
        // Expense bars (orange)
        renderer.setSeriesPaint(1, new Color(255, 140, 0));
        
        return chart;
    }
    
    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Get data from the model
        Map<String, Double> categoryBudgets = financeData.getCategoryBudgets();
        Map<String, Double> categoryExpenses = financeData.getCategoryExpenses();
        
        // Add budget and expense data for each category
        for (String category : categoryBudgets.keySet()) {
            double budget = categoryBudgets.get(category);
            double expense = categoryExpenses.getOrDefault(category, 0.0);
            
            dataset.addValue(budget, "Budget", category);
            dataset.addValue(expense, "Actual", category);
        }
        
        return dataset;
    }
}