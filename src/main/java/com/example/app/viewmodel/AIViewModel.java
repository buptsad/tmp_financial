package com.example.app.viewmodel;

import com.example.app.model.FinancialAdvice;
import com.example.app.ui.dashboard.OverviewPanel;
import com.example.app.ui.pages.AI.getRes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for AI Panel following MVVM pattern.
 * Handles business logic for AI interactions and chat functionality.
 */
public class AIViewModel {
    // Model references
    private final getRes aiService;
    private final String apiKey;
    private final String username;
    
    // Chat message history
    private final List<ChatMessage> messages;
    
    // Listeners for UI updates
    private final List<AIDataChangeListener> listeners;

    /**
     * Represents a chat message with content and sender information
     */
    public static class ChatMessage {
        private final String content;
        private final boolean isFromUser;

        public ChatMessage(String content, boolean isFromUser) {
            this.content = content;
            this.isFromUser = isFromUser;
        }

        public String getContent() {
            return content;
        }

        public boolean isFromUser() {
            return isFromUser;
        }
        
        public String getFormattedMessage() {
            return (isFromUser ? "You: " : "AI: ") + content;
        }
    }

    /**
     * Interface for components that need to be notified of AI data changes
     */
    public interface AIDataChangeListener {
        void onMessageAdded(ChatMessage message);
        void onErrorOccurred(String errorMessage);
        void onAdviceUpdated();
    }

    public AIViewModel(String username) {
        this.username = username;
        this.aiService = new getRes();
        this.apiKey = "sk-fdf26a37926f46ab8d4884c2cd533db8";
        this.messages = new ArrayList<>();
        this.listeners = new ArrayList<>();
        
        // Initialize the shared advice with username
        OverviewPanel.sharedAdvice.initialize(username);
        
        // Add initial welcome message
        addAIMessage("Hello! I can help analyze your finances and provide personalized advice. Ask me anything about your financial data.");
    }

    /**
     * Add a listener for AI data changes
     */
    public void addListener(AIDataChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a listener
     */
    public void removeListener(AIDataChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Send a user message to the AI and get a response
     */
    public void sendMessage(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return;
        }
        
        // Add user message to history
        addUserMessage(userInput);
        
        // Create a new thread to avoid blocking the UI
        new Thread(() -> {
            try {
                // Call the AI service
                String response = aiService.getResponse(apiKey, userInput);
                String parsedResponse = aiService.parseAIResponse(response);
                
                // Add AI response to history
                addAIMessage(parsedResponse);
            } catch (IOException e) {
                // Notify listeners of error
                notifyError("Error communicating with AI: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Regenerate financial advice
     */
    public void regenerateAdvice() {
        // Access shared advice instance and regenerate
        OverviewPanel.sharedAdvice.regenerate();
        
        // Add system message to chat
        addAIMessage("Financial advice has been updated with new AI insights.");
        
        // Notify listeners specifically about advice update
        for (AIDataChangeListener listener : new ArrayList<>(listeners)) {
            listener.onAdviceUpdated();
        }
    }

    /**
     * Get access to the financial advice model
     */
    public FinancialAdvice getFinancialAdvice() {
        return OverviewPanel.sharedAdvice;
    }

    /**
     * Get all chat messages
     */
    public List<ChatMessage> getMessages() {
        return new ArrayList<>(messages); // Return a copy to prevent external modification
    }

    // Private helper methods
    private void addUserMessage(String content) {
        ChatMessage message = new ChatMessage(content, true);
        messages.add(message);
        notifyMessageAdded(message);
    }

    private void addAIMessage(String content) {
        ChatMessage message = new ChatMessage(content, false);
        messages.add(message);
        notifyMessageAdded(message);
    }

    private void notifyMessageAdded(ChatMessage message) {
        for (AIDataChangeListener listener : new ArrayList<>(listeners)) {
            listener.onMessageAdded(message);
        }
    }

    private void notifyError(String errorMessage) {
        for (AIDataChangeListener listener : new ArrayList<>(listeners)) {
            listener.onErrorOccurred(errorMessage);
        }
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        listeners.clear();
    }
}