package com.example.app.ui.pages;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.example.app.ui.CurrencyManager;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

public class SettingsPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private JButton activeButton; // 当前选中的按钮
    private Dimension originalWindowSize; // 保存原始窗口大小

    public SettingsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // 主面板使用垂直 BoxLayout

        // Title
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 20));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT); // 左对齐
        add(Box.createVerticalStrut(10)); // 添加垂直间距
        add(titleLabel);

        // 导航栏
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS)); // 导航栏使用水平 BoxLayout
        navPanel.setBorder(new EmptyBorder(10, 0, 10, 0)); // 设置内边距
        navPanel.setAlignmentX(LEFT_ALIGNMENT); // 左对齐

        // 添加按钮
        JButton profileButton = createNavButton("Profile", "PROFILE");
        JButton preferencesButton = createNavButton("Preferences", "PREFERENCES");
        JButton notificationsButton = createNavButton("Notifications", "NOTIFICATIONS");
        JButton securityButton = createNavButton("Security", "SECURITY");
        navPanel.add(profileButton);
        navPanel.add(Box.createHorizontalStrut(10)); // 添加水平间距
        navPanel.add(preferencesButton);
        navPanel.add(Box.createHorizontalStrut(10));
        navPanel.add(notificationsButton);
        navPanel.add(Box.createHorizontalStrut(10));
        navPanel.add(securityButton);

        add(Box.createVerticalStrut(10)); // 添加垂直间距
        add(navPanel);

        // 内容面板
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setPreferredSize(new Dimension(600, 400)); // 设置内容面板的大小
        contentPanel.setAlignmentX(LEFT_ALIGNMENT); // 左对齐

        // 添加子面板
        contentPanel.add(createProfilePanel(), "PROFILE");
        contentPanel.add(createPreferencesPanel(), "PREFERENCES");
        contentPanel.add(createNotificationsPanel(), "NOTIFICATIONS");
        contentPanel.add(createSecurityPanel(), "SECURITY");

        add(Box.createVerticalStrut(10)); // 添加垂直间距
        add(contentPanel);

        // 设置默认视图
        cardLayout.show(contentPanel, "PROFILE");
        setActiveButton(profileButton); // 默认选中 Profile 按钮
        
        // 当面板显示时记录原始窗口大小并调整窗口
        addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                    if (isShowing()) {
                        // 面板显示时
                        SwingUtilities.invokeLater(() -> {
                            Window window = SwingUtilities.getWindowAncestor(SettingsPanel.this);
                            if (window != null) {
                                // 保存原始窗口大小
                                originalWindowSize = window.getSize();
                                // 调整窗口大小
                                window.setSize(700, 550);
                                window.setLocationRelativeTo(null); // 居中显示
                            }
                        });
                    } else {
                        // 面板隐藏时恢复原始窗口大小
                        SwingUtilities.invokeLater(() -> {
                            Window window = SwingUtilities.getWindowAncestor(SettingsPanel.this);
                            if (window != null && originalWindowSize != null) {
                                window.setSize(originalWindowSize);
                                window.setLocationRelativeTo(null); // 居中显示
                            }
                        });
                    }
                }
            }
        });
    }
    
    private JButton createNavButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setFocusPainted(false); // 移除焦点边框
        button.setContentAreaFilled(false); // 移除背景填充
        button.setBorderPainted(false); // 移除边框
        button.setFont(new Font("Arial", Font.PLAIN, 14)); // 设置字体
        button.setForeground(Color.WHITE); // 设置默认字体颜色

        button.addActionListener(e -> {
            cardLayout.show(contentPanel, panelName);
            setActiveButton(button); // 设置当前选中的按钮
        });

        return button;
    }

    private void setActiveButton(JButton button) {
        if (activeButton != null) {
            activeButton.setForeground(Color.WHITE); // 重置上一个按钮的颜色
            activeButton.setFont(new Font("Arial", Font.PLAIN, 14)); // 重置字体
        }
        activeButton = button;
        activeButton.setForeground(new Color(70, 130, 180)); // 设置选中按钮的颜色为蓝色
        activeButton.setFont(new Font("Arial", Font.BOLD, 14)); // 设置选中按钮的字体为加粗
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // 子面板使用垂直 BoxLayout
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        panel.add(createLabeledField("Name:", new JTextField()));
        panel.add(Box.createVerticalStrut(8)); // 调整垂直间距
        panel.add(createLabeledField("Email:", new JTextField()));
        panel.add(Box.createVerticalStrut(8)); // 调整垂直间距
        panel.add(createLabeledField("Phone:", new JTextField()));
        
        // 添加提交按钮
        panel.add(Box.createVerticalStrut(15));
        JButton submitButton = new JButton("Save Changes");
        submitButton.setAlignmentX(LEFT_ALIGNMENT);
        submitButton.addActionListener(e -> JOptionPane.showMessageDialog(
            this, "Profile information updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE));
        panel.add(submitButton);
    
        return panel;
    }

    private JPanel createPreferencesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        // 创建货币选择下拉框
        JComboBox<String> currencyComboBox = new JComboBox<>(new String[]{"USD $", "RMB ¥"});
        
        // 创建货币符号文本框
        JTextField currencySymbolField = new JTextField(5);
        
        // 设置当前值
        CurrencyManager currencyManager = CurrencyManager.getInstance();
        if (currencyManager.getCurrencyCode().equals("USD")) {
            currencyComboBox.setSelectedItem("USD $");
            currencySymbolField.setText("$");
        } else if (currencyManager.getCurrencyCode().equals("RMB")) {
            currencyComboBox.setSelectedItem("RMB ¥");
            currencySymbolField.setText("¥");
        }
        
        // 监听货币选择变化
        currencyComboBox.addActionListener(e -> {
            String selected = (String) currencyComboBox.getSelectedItem();
            if ("USD $".equals(selected)) {
                currencySymbolField.setText("$");
            } else if ("RMB ¥".equals(selected)) {
                currencySymbolField.setText("¥");
            }
        });
        
        panel.add(createLabeledField("Default Currency:", currencyComboBox));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createLabeledField("Currency Symbol:", currencySymbolField));
        
        // 添加应用按钮
        panel.add(Box.createVerticalStrut(15));
        JButton applyButton = new JButton("Apply Changes");
        applyButton.setAlignmentX(LEFT_ALIGNMENT);
        applyButton.addActionListener(e -> {
            // 保存货币设置
            String selected = (String) currencyComboBox.getSelectedItem();
            String symbol = currencySymbolField.getText();
            String code = "USD";
            
            if ("USD $".equals(selected)) {
                code = "USD";
            } else if ("RMB ¥".equals(selected)) {
                code = "RMB";
            }
            
            // 更新货币管理器
            CurrencyManager.getInstance().setCurrency(code, symbol);
            
            JOptionPane.showMessageDialog(
                this, "Currency preferences updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        panel.add(applyButton);
    
        return panel;
    }
    
    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // 子面板使用垂直 BoxLayout
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(createLabeledField("Budget Alerts:", new JCheckBox("Enable")));
        panel.add(Box.createVerticalStrut(8)); // 调整垂直间距
        panel.add(createLabeledField("Transaction Alerts:", new JCheckBox("Enable")));
        
        // 添加保存按钮
        panel.add(Box.createVerticalStrut(15));
        JButton saveButton = new JButton("Save Preferences");
        saveButton.setAlignmentX(LEFT_ALIGNMENT);
        saveButton.addActionListener(e -> JOptionPane.showMessageDialog(
            this, "Notification settings saved!", "Success", JOptionPane.INFORMATION_MESSAGE));
        panel.add(saveButton);

        return panel;
    }

    private JPanel createSecurityPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // 子面板使用垂直 BoxLayout
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(createLabeledField("Current Password:", new JPasswordField(20)));
        panel.add(Box.createVerticalStrut(8)); // 调整垂直间距
        panel.add(createLabeledField("New Password:", new JPasswordField(20)));
        panel.add(Box.createVerticalStrut(8)); // 调整垂直间距
        panel.add(createLabeledField("Confirm Password:", new JPasswordField(20)));
        
        // 添加更改密码按钮
        panel.add(Box.createVerticalStrut(15));
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setAlignmentX(LEFT_ALIGNMENT);
        changePasswordButton.addActionListener(e -> JOptionPane.showMessageDialog(
            this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE));
        panel.add(changePasswordButton);

        return panel;
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS)); // 水平排列
        panel.setAlignmentX(LEFT_ALIGNMENT); // 左对齐
    
        // 设置标签，使用固定宽度让所有标签对齐
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        panel.add(label);
    
        // 设置输入框大小
        field.setPreferredSize(new Dimension(200, 25)); // 设置首选尺寸
        field.setMaximumSize(new Dimension(250, 25));   // 调整最大尺寸
        panel.add(field);
    
        return panel;
    }
}