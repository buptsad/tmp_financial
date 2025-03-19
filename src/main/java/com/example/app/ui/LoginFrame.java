package com.example.app.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel loginPanel;
    private JPanel registerPanel;
    private JButton loginToggle;
    private JButton registerToggle;
    
    // Colors and borders for navigation highlighting (matched from MainFrame)
    private static final Color SELECTED_COLOR = new Color(70, 130, 180);
    private static final Border SELECTED_BORDER = new MatteBorder(0, 0, 2, 0, SELECTED_COLOR);
    private static final Border DEFAULT_BORDER = new EmptyBorder(0, 0, 2, 0);
    
    public LoginFrame() {
        setTitle("Finance Manager - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        
        // Create main container with card layout
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        
        // Create toggle switch panel with FlowLayout centered
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        togglePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        // Create toggle buttons
        loginToggle = createStyledButton("Login", "login");
        registerToggle = createStyledButton("Register", "register");
        
        // Add buttons to the panel
        togglePanel.add(loginToggle);
        togglePanel.add(registerToggle);
        
        // Create login panel
        createLoginPanel();
        
        // Create register panel
        createRegisterPanel();
        
        // Add panels to card layout
        cardPanel.add(loginPanel, "login");
        cardPanel.add(registerPanel, "register");
        
        // Set up main layout
        setLayout(new BorderLayout());
        add(togglePanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        
        // Set active page initially
        setActiveToggle("login");
        
        setVisible(true);
    }
    
    private JButton createStyledButton(String text, String pageKey) {
        JButton button = new JButton(text);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(DEFAULT_BORDER);
        button.setToolTipText("Switch to " + text + " page");
        
        button.addActionListener(e -> {
            cardLayout.show(cardPanel, pageKey);
            setActiveToggle(pageKey);
        });
        
        return button;
    }
    
    private void setActiveToggle(String pageKey) {
        // Reset all button styles
        loginToggle.setBorder(DEFAULT_BORDER);
        loginToggle.setForeground(UIManager.getColor("Button.foreground"));
        registerToggle.setBorder(DEFAULT_BORDER);
        registerToggle.setForeground(UIManager.getColor("Button.foreground"));
        
        // Highlight the active button
        if (pageKey.equals("login")) {
            loginToggle.setBorder(SELECTED_BORDER);
            loginToggle.setForeground(SELECTED_COLOR);
        } else {
            registerToggle.setBorder(SELECTED_BORDER);
            registerToggle.setForeground(SELECTED_COLOR);
        }
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title label
        JLabel titleLabel = new JLabel("Finance Manager");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        loginPanel.add(titleLabel, gbc);
        
        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(usernameField, gbc);
        
        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);
        
        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setToolTipText("Click to login to your account");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);
        
        // Add action listener to login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // For demo purposes, any login is successful
                // In a real app, you would validate credentials here
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                dispose(); // Close the login window
            }
        });
    }
    
    private void createRegisterPanel() {
        registerPanel = new JPanel();
        registerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title label
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        registerPanel.add(titleLabel, gbc);
        
        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        registerPanel.add(usernameLabel, gbc);
        
        JTextField regUsernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        registerPanel.add(regUsernameField, gbc);
        
        // Email label and field
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        registerPanel.add(emailLabel, gbc);
        
        JTextField emailField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        registerPanel.add(emailField, gbc);
        
        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        registerPanel.add(passwordLabel, gbc);
        
        JPasswordField regPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        registerPanel.add(regPasswordField, gbc);
        
        // Confirm Password label and field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        registerPanel.add(confirmPasswordLabel, gbc);
        
        JPasswordField confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 4;
        registerPanel.add(confirmPasswordField, gbc);
        
        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.setToolTipText("Click to create a new account");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        registerPanel.add(registerButton, gbc);
        
        // Add action listener to register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // For demo purposes, show a successful registration message
                // In a real app, you would validate and store the user data
                JOptionPane.showMessageDialog(
                    LoginFrame.this,
                    "Registration successful! You can now login.",
                    "Account Created",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                // Switch to login panel
                setActiveToggle("login");
                cardLayout.show(cardPanel, "login");
            }
        });
    }
    
    // Main method for testing
    public static void main(String[] args) {
        // Set FlatLaf look and feel
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
        
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}