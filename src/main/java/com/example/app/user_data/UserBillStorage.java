package com.example.app.user_data;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 存储用户账单数据
 * 这个类管理用户账单数据的物理存储
 */
public class UserBillStorage {
    private static final Logger LOGGER = Logger.getLogger(UserBillStorage.class.getName());
    private static final String BILL_FILENAME = "user_bill.dat";
    private static File billFile;
    
    static {
        // 直接使用项目内的文件路径
        String packagePath = "c:\\tmp_financial\\src\\main\\java\\com\\example\\app\\user_data";
        billFile = new File(packagePath, BILL_FILENAME);
        
        // 确保文件存在
        initializeStorage();
    }
    
    /**
     * 初始化存储目录和文件
     */
    private static void initializeStorage() {
        File directory = billFile.getParentFile();
        
        // 创建目录(如果不存在)
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                LOGGER.log(Level.INFO, "创建账单目录在: {0}", directory.getAbsolutePath());
            } else {
                LOGGER.log(Level.SEVERE, "无法创建账单目录在: {0}", directory.getAbsolutePath());
                return;
            }
        }
        
        // 如果文件不存在则创建文件
        if (!billFile.exists()) {
            try {
                if (billFile.createNewFile()) {
                    LOGGER.log(Level.INFO, "创建账单文件在: {0}", billFile.getAbsolutePath());
                    
                    // 初始化一个空列表作为默认内容
                    saveTransactions(new ArrayList<>());
                } else {
                    LOGGER.log(Level.SEVERE, "无法创建账单文件在: {0}", billFile.getAbsolutePath());
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "创建账单文件时出错", e);
            }
        } else {
            LOGGER.log(Level.INFO, "账单文件已存在于: {0}", billFile.getAbsolutePath());
        }
    }
    
    /**
     * 获取账单文件的路径
     * @return 账单文件的路径
     */
    public static String getBillFilePath() {
        return billFile.getAbsolutePath();
    }
    
    /**
     * 从文件加载交易记录
     * @return 交易记录的列表，如果加载失败返回null
     */
    @SuppressWarnings("unchecked")
    public static List<Object[]> loadTransactions() {
        // 确认文件存在
        if (!billFile.exists()) {
            LOGGER.log(Level.WARNING, "账单文件不存在: {0}", billFile.getAbsolutePath());
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(billFile))) {
            List<Object[]> transactions = (List<Object[]>) ois.readObject();
            LOGGER.log(Level.INFO, "成功从以下位置加载交易记录: {0}", billFile.getAbsolutePath());
            LOGGER.log(Level.INFO, "加载了 {0} 条交易记录", transactions.size());
            return transactions;
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "从文件加载交易记录时出错: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 将交易记录保存到文件
     * @param transactions 要保存的交易记录列表
     * @return 如果成功则返回true，否则返回false
     */
    public static boolean saveTransactions(List<Object[]> transactions) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(billFile))) {
            oos.writeObject(transactions);
            LOGGER.log(Level.INFO, "成功将 {0} 条交易记录保存到: {1}", 
                    new Object[]{transactions.size(), billFile.getAbsolutePath()});
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "保存交易记录到文件时出错: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 将新交易记录添加到现有记录中
     * @param newTransactions 要添加的新交易记录列表
     * @return 如果成功则返回true，否则返回false
     */
    public static boolean addTransactions(List<Object[]> newTransactions) {
        // 加载现有交易记录
        List<Object[]> existingTransactions = loadTransactions();
        
        // 添加新交易记录
        existingTransactions.addAll(newTransactions);
        
        // 保存更新后的交易记录
        return saveTransactions(existingTransactions);
    }
}