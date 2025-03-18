package com.example.app.ui.dashboard;

import javax.swing.*;
import java.awt.*;

public class OverviewPanel extends JPanel {
    public OverviewPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JLabel contentLabel = new JLabel("Overview Content - Financial summary charts would go here");
        centerPanel.add(contentLabel);
        
        add(centerPanel, BorderLayout.CENTER);
    }
}