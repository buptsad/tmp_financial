package com.example.app.model;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Class responsible for managing budget data saving and loading operations
 */
public class BudgetManager {
    
    private static final String BUDGETS_FILE_NAME = "user_budgets.csv";
    
    /**
     * Saves budget data to a CSV file
     * @param categoryBudgets Map of category budget allocations
     * @param directory Directory where the CSV file will be saved
     */
    public static void saveBudgetsToCSV(Map<String, Double> categoryBudgets, String directory) {
        Path filePath = Paths.get(directory, BUDGETS_FILE_NAME);
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            // Write CSV header
            writer.write("Category,Budget");
            writer.newLine();
            
            // Write each category and budget
            for (Map.Entry<String, Double> entry : categoryBudgets.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
            
            System.out.println("Budget data successfully saved to: " + filePath);
            
        } catch (IOException e) {
            System.err.println("Error saving budget data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads budget data from a CSV file
     * @param directory Directory where the CSV file is located
     * @return Map of category budget allocations
     */
    public static Map<String, Double> loadBudgetsFromCSV(String directory) {
        Map<String, Double> categoryBudgets = new LinkedHashMap<>();
        Path filePath = Paths.get(directory, BUDGETS_FILE_NAME);
        
        // Check if file exists
        if (!Files.exists(filePath)) {
            System.out.println("Budget file does not exist, using default budgets");
            return categoryBudgets; // Return empty map, default values will be added later
        }
        
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            // Skip CSV header
            String line = reader.readLine();
            
            // Read each line of data
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("//")) {
                    continue; // Skip empty lines and comments
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String category = parts[0].trim();
                    try {
                        double budget = Double.parseDouble(parts[1].trim());
                        categoryBudgets.put(category, budget);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing budget value: " + parts[1]);
                    }
                }
            }
            
            System.out.println("Successfully loaded " + categoryBudgets.size() + " budget categories from " + filePath);
            
        } catch (IOException e) {
            System.err.println("Error loading budget data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categoryBudgets;
    }
}