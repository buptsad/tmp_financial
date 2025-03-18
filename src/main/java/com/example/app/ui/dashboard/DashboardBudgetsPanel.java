package com.example.app.ui.dashboard;

import javax.swing.*;
import java.awt.*;

public class DashboardBudgetsPanel extends JPanel {
    public DashboardBudgetsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JLabel titleLabel = new JLabel("Budget Overview");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JLabel contentLabel = new JLabel("Budget summary and progress bars would go here");
        centerPanel.add(contentLabel);
        
        add(centerPanel, BorderLayout.CENTER);
    }
}