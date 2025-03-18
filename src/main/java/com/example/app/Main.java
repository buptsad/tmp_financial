package com.example.app;

import com.formdev.flatlaf.FlatDarculaLaf;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Set up FlatDarculaLaf (dark theme)
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatDarculaLaf");
        }
        
        // Ensure we're running on the EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
    
    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("FlatLaF Swing Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        // Create a panel with components
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add a toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(new JButton("New"));
        toolBar.add(new JButton("Open"));
        toolBar.add(new JButton("Save"));
        toolBar.addSeparator();
        toolBar.add(new JButton("Settings"));
        
        // Create central content
        JPanel contentPanel = new JPanel(new BorderLayout());
        
        // Add a sidebar with a list
        JList<String> sidebarList = new JList<>(new String[] {
            "Home", "Documents", "Pictures", "Music", "Videos", "Downloads"
        });
        sidebarList.setPreferredSize(new Dimension(150, 0));
        
        // Create a text area for the main content
        JTextArea textArea = new JTextArea();
        textArea.setText("Welcome to your FlatLaF Swing Application!\n\n" +
                         "This is a simple demo showing how to use FlatLaF with Java Swing.\n\n" +
                         "You're currently using the Darcula dark theme!");
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        // Add components to content panel
        contentPanel.add(sidebarList, BorderLayout.WEST);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add a status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusBar.add(new JLabel("Ready"), BorderLayout.WEST);
        
        // Assemble the main panel
        mainPanel.add(toolBar, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        // Set up the frame
        frame.setContentPane(mainPanel);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }
}