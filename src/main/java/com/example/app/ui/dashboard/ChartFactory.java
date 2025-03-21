package com.example.app.ui.dashboard;

import com.example.app.model.FinanceData;
import com.example.app.ui.CurrencyManager;
import com.example.app.ui.CurrencyManager.CurrencyChangeListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChartFactory extends JPanel implements CurrencyChangeListener {
    
    private final FinanceData financeData;
    private ChartPanel chartPanel;
    
    public ChartFactory(FinanceData financeData) {
        this.financeData = financeData;
        setLayout(new BorderLayout());
        
        // 创建初始图表
        refreshChart();
        
        // 注册货币变化监听器
        CurrencyManager.getInstance().addCurrencyChangeListener(this);
    }
    
    private void refreshChart() {
        // 创建图表
        JFreeChart chart = createFinancialLineChart();
        
        // 如果chartPanel已经存在，移除它
        if (chartPanel != null) {
            remove(chartPanel);
        }
        
        // 创建新的ChartPanel
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        chartPanel.setMouseWheelEnabled(true);
        
        // 添加到面板
        add(chartPanel, BorderLayout.CENTER);
        
        // 重新验证和重绘
        revalidate();
        repaint();
    }
    
    private JFreeChart createFinancialLineChart() {
        // 创建数据集
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        
        // 获取当前货币符号
        String currencySymbol = CurrencyManager.getInstance().getCurrencySymbol();
        
        // 添加收入数据
        TimeSeries incomeSeries = new TimeSeries("Income");
        addDataToSeries(incomeSeries, financeData.getDates(), financeData.getDailyIncomes());
        dataset.addSeries(incomeSeries);
        
        // 添加支出数据
        TimeSeries expenseSeries = new TimeSeries("Expenses");
        addDataToSeries(expenseSeries, financeData.getDates(), financeData.getDailyExpenses());
        dataset.addSeries(expenseSeries);
        
        // 添加预算数据
        TimeSeries budgetSeries = new TimeSeries("Daily Budget");
        double dailyBudget = financeData.getDailyBudget();
        for (LocalDate date : financeData.getDates()) {
            Date javaDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            budgetSeries.add(new Day(javaDate), dailyBudget);
        }
        dataset.addSeries(budgetSeries);
        
        // 创建图表
        DateAxis domainAxis = new DateAxis("Date");
        domainAxis.setDateFormatOverride(new SimpleDateFormat("MMM d")); // 格式化日期显示
        
        // 使用当前货币符号设置Y轴标签
        NumberAxis rangeAxis = new NumberAxis("Amount (" + currencySymbol + ")");
        
        // 创建渲染器
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        // 收入线 (蓝色)
        renderer.setSeriesPaint(0, new Color(65, 105, 225));
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, true);
        
        // 支出线 (红色)
        renderer.setSeriesPaint(1, new Color(178, 34, 34));
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(1, true);
        
        // 预算线 (绿色)
        renderer.setSeriesPaint(2, new Color(46, 139, 87));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
                                10.0f, new float[]{10.0f, 6.0f}, 0.0f));
        renderer.setSeriesShapesVisible(2, false);
        
        // 创建绘图区
        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // 创建图表
        JFreeChart chart = new JFreeChart("Daily Financial Overview", 
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chart.setBackgroundPaint(Color.WHITE);
        
        return chart;
    }
    
    private void addDataToSeries(TimeSeries series, List<LocalDate> dates, Map<LocalDate, Double> dataMap) {
        for (LocalDate date : dates) {
            Double value = dataMap.get(date);
            if (value != null) {
                Date javaDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                series.add(new Day(javaDate), value);
            }
        }
    }
    
    @Override
    public void onCurrencyChanged(String currencyCode, String currencySymbol) {
        // 货币变化时刷新图表
        refreshChart();
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        // 移除组件时取消监听
        CurrencyManager.getInstance().removeCurrencyChangeListener(this);
    }
    
    // 提供静态方法以便兼容现有代码
    public static JFreeChart createFinancialLineChart(FinanceData financeData) {
        ChartFactory factory = new ChartFactory(financeData);
        return factory.createFinancialLineChart();
    }
}