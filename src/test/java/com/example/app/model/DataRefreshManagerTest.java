package com.example.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.AfterEach;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for DataRefreshManager
 */
class DataRefreshManagerTest {
    
    private DataRefreshManager refreshManager;
    private TestDataRefreshListener testListener;
    
    @BeforeEach
    void setUp() {
        DataRefreshManager._resetForTests(); // <-- Add this line
        refreshManager = DataRefreshManager.getInstance();
        testListener = new TestDataRefreshListener();
        
        // Clean up any existing listeners to ensure isolated tests
        refreshManager.removeListener(testListener);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up listeners after each test
        if (testListener != null) {
            refreshManager.removeListener(testListener);
        }
    }
    
    @Nested
    @DisplayName("Singleton Pattern Tests")
    class SingletonPatternTests {
        
        @Test
        @DisplayName("Should return same instance for multiple getInstance calls")
        void shouldReturnSameInstanceForMultipleGetInstanceCalls() {
            DataRefreshManager instance1 = DataRefreshManager.getInstance();
            DataRefreshManager instance2 = DataRefreshManager.getInstance();
            DataRefreshManager instance3 = DataRefreshManager.getInstance();
            
            assertSame(instance1, instance2, "getInstance should return the same instance");
            assertSame(instance2, instance3, "getInstance should return the same instance");
            assertSame(instance1, instance3, "getInstance should return the same instance");
        }
        
        @Test
        @DisplayName("Should maintain singleton across different threads")
        void shouldMaintainSingletonAcrossDifferentThreads() throws InterruptedException {
            final int threadCount = 10;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);
            List<DataRefreshManager> instances = Collections.synchronizedList(new ArrayList<>());
            
            // Create multiple threads that get the instance
            for (int i = 0; i < threadCount; i++) {
                Thread thread = new Thread(() -> {
                    try {
                        startLatch.await();
                        instances.add(DataRefreshManager.getInstance());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                });
                thread.start();
            }
            
            startLatch.countDown();
            assertTrue(doneLatch.await(5, TimeUnit.SECONDS), "All threads should complete");
            
            assertEquals(threadCount, instances.size(), "Should have instances from all threads");
            
            // Verify all instances are the same
            DataRefreshManager firstInstance = instances.get(0);
            for (DataRefreshManager instance : instances) {
                assertSame(firstInstance, instance, "All instances should be the same singleton");
            }
        }
    }
    
    @Nested
    @DisplayName("Listener Management Tests")
    class ListenerManagementTests {
        
        @Test
        @DisplayName("Should add listener successfully")
        void shouldAddListenerSuccessfully() {
            refreshManager.addListener(testListener);
            
            // Trigger a refresh to verify listener was added
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.ALL);
            
            assertEquals(1, testListener.getRefreshCount(), "Listener should receive refresh notification");
            assertEquals(DataRefreshManager.RefreshType.ALL, testListener.getLastRefreshType(), 
                "Listener should receive correct refresh type");
        }
        
        @Test
        @DisplayName("Should not add duplicate listeners")
        void shouldNotAddDuplicateListeners() {
            refreshManager.addListener(testListener);
            refreshManager.addListener(testListener); // Add same listener again
            refreshManager.addListener(testListener); // Add same listener again
            
            // Trigger a refresh
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.TRANSACTIONS);
            
            assertEquals(1, testListener.getRefreshCount(), 
                "Listener should only receive one notification despite being added multiple times");
        }
        
        @Test
        @DisplayName("Should remove listener successfully")
        void shouldRemoveListenerSuccessfully() {
            refreshManager.addListener(testListener);
            refreshManager.removeListener(testListener);
            
            // Trigger a refresh
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.BUDGETS);
            
            assertEquals(0, testListener.getRefreshCount(), 
                "Removed listener should not receive refresh notifications");
        }
        
        @Test
        @DisplayName("Should handle removing non-existent listener gracefully")
        void shouldHandleRemovingNonExistentListenerGracefully() {
            assertDoesNotThrow(() -> {
                refreshManager.removeListener(testListener);
            }, "Removing non-existent listener should not throw exception");
        }
        
        @Test
        @DisplayName("Should handle multiple listeners")
        void shouldHandleMultipleListeners() {
            TestDataRefreshListener listener1 = new TestDataRefreshListener();
            TestDataRefreshListener listener2 = new TestDataRefreshListener();
            TestDataRefreshListener listener3 = new TestDataRefreshListener();
            
            try {
                refreshManager.addListener(listener1);
                refreshManager.addListener(listener2);
                refreshManager.addListener(listener3);
                
                // Trigger a refresh
                refreshManager.notifyRefresh(DataRefreshManager.RefreshType.CURRENCY);
                
                assertEquals(1, listener1.getRefreshCount(), "Listener 1 should receive notification");
                assertEquals(1, listener2.getRefreshCount(), "Listener 2 should receive notification");
                assertEquals(1, listener3.getRefreshCount(), "Listener 3 should receive notification");
                
                assertEquals(DataRefreshManager.RefreshType.CURRENCY, listener1.getLastRefreshType());
                assertEquals(DataRefreshManager.RefreshType.CURRENCY, listener2.getLastRefreshType());
                assertEquals(DataRefreshManager.RefreshType.CURRENCY, listener3.getLastRefreshType());
                
            } finally {
                refreshManager.removeListener(listener1);
                refreshManager.removeListener(listener2);
                refreshManager.removeListener(listener3);
            }
        }
    }
    
    @Nested
    @DisplayName("Refresh Notification Tests")
    class RefreshNotificationTests {
        
        @Test
        @DisplayName("Should notify listeners of TRANSACTIONS refresh")
        void shouldNotifyListenersOfTransactionsRefresh() {
            refreshManager.addListener(testListener);
            
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.TRANSACTIONS);
            
            assertEquals(1, testListener.getRefreshCount());
            assertEquals(DataRefreshManager.RefreshType.TRANSACTIONS, testListener.getLastRefreshType());
        }
        
        @Test
        @DisplayName("Should notify listeners of BUDGETS refresh")
        void shouldNotifyListenersOfBudgetsRefresh() {
            refreshManager.addListener(testListener);
            
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.BUDGETS);
            
            assertEquals(1, testListener.getRefreshCount());
            assertEquals(DataRefreshManager.RefreshType.BUDGETS, testListener.getLastRefreshType());
        }
        
        @Test
        @DisplayName("Should notify listeners of CURRENCY refresh")
        void shouldNotifyListenersOfCurrencyRefresh() {
            refreshManager.addListener(testListener);
            
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.CURRENCY);
            
            assertEquals(1, testListener.getRefreshCount());
            assertEquals(DataRefreshManager.RefreshType.CURRENCY, testListener.getLastRefreshType());
        }
        
        @Test
        @DisplayName("Should notify listeners of SETTINGS refresh")
        void shouldNotifyListenersOfSettingsRefresh() {
            refreshManager.addListener(testListener);
            
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.SETTINGS);
            
            assertEquals(1, testListener.getRefreshCount());
            assertEquals(DataRefreshManager.RefreshType.SETTINGS, testListener.getLastRefreshType());
        }
        
        @Test
        @DisplayName("Should notify listeners of ADVICE refresh")
        void shouldNotifyListenersOfAdviceRefresh() {
            refreshManager.addListener(testListener);
            
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.ADVICE);
            
            assertEquals(1, testListener.getRefreshCount());
            assertEquals(DataRefreshManager.RefreshType.ADVICE, testListener.getLastRefreshType());
        }
        
        @Test
        @DisplayName("Should notify listeners of ALL refresh")
        void shouldNotifyListenersOfAllRefresh() {
            refreshManager.addListener(testListener);
            
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.ALL);
            
            assertEquals(1, testListener.getRefreshCount());
            assertEquals(DataRefreshManager.RefreshType.ALL, testListener.getLastRefreshType());
        }
        
        @Test
        @DisplayName("Should handle multiple sequential refresh notifications")
        void shouldHandleMultipleSequentialRefreshNotifications() {
            refreshManager.addListener(testListener);
            
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.TRANSACTIONS);
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.BUDGETS);
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.ALL);
            
            assertEquals(3, testListener.getRefreshCount(), "Should receive all three notifications");
            assertEquals(DataRefreshManager.RefreshType.ALL, testListener.getLastRefreshType(), 
                "Should remember the last refresh type");
        }
    }
    
    @Nested
    @DisplayName("Convenience Method Tests")
    class ConvenienceMethodTests {
        
        @Test
        @DisplayName("Should trigger TRANSACTIONS refresh via convenience method")
        void shouldTriggerTransactionsRefreshViaConvenienceMethod() {
            refreshManager.addListener(testListener);
            
            refreshManager.refreshTransactions();
            
            assertEquals(1, testListener.getRefreshCount());
            assertEquals(DataRefreshManager.RefreshType.TRANSACTIONS, testListener.getLastRefreshType());
        }
        
        @Test
        @DisplayName("Should trigger BUDGETS refresh via convenience method")
        void shouldTriggerBudgetsRefreshViaConvenienceMethod() {
            refreshManager.addListener(testListener);
            
            refreshManager.refreshBudgets();
            
            assertEquals(1, testListener.getRefreshCount());
            assertEquals(DataRefreshManager.RefreshType.BUDGETS, testListener.getLastRefreshType());
        }
        
        @Test
        @DisplayName("Should trigger ALL refresh via convenience method")
        void shouldTriggerAllRefreshViaConvenienceMethod() {
            refreshManager.addListener(testListener);
            
            refreshManager.refreshAll();
            
            assertEquals(1, testListener.getRefreshCount());
            assertEquals(DataRefreshManager.RefreshType.ALL, testListener.getLastRefreshType());
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle listener exceptions gracefully")
        void shouldHandleListenerExceptionsGracefully() {
            ExceptionThrowingListener exceptionListener = new ExceptionThrowingListener();
            TestDataRefreshListener normalListener = new TestDataRefreshListener();
            
            try {
                refreshManager.addListener(exceptionListener);
                refreshManager.addListener(normalListener);
                
                // This should not throw an exception even though one listener throws
                assertDoesNotThrow(() -> {
                    refreshManager.notifyRefresh(DataRefreshManager.RefreshType.ALL);
                }, "Should handle listener exceptions gracefully");
                
                // Normal listener should still receive the notification
                assertEquals(1, normalListener.getRefreshCount(), 
                    "Normal listener should receive notification despite other listener throwing exception");
                
            } finally {
                refreshManager.removeListener(exceptionListener);
                refreshManager.removeListener(normalListener);
            }
        }
        
        @Test
        @DisplayName("Should prevent recursive refresh calls")
        void shouldPreventRecursiveRefreshCalls() {
            RecursiveRefreshListener recursiveListener = new RecursiveRefreshListener(refreshManager);
            
            try {
                refreshManager.addListener(recursiveListener);
                
                // This should not cause infinite recursion
                assertDoesNotThrow(() -> {
                    refreshManager.notifyRefresh(DataRefreshManager.RefreshType.ALL);
                }, "Should prevent recursive refresh calls");
                
                // Should only be called once (recursive call should be prevented)
                assertEquals(1, recursiveListener.getCallCount(), 
                    "Recursive calls should be prevented");
                
            } finally {
                refreshManager.removeListener(recursiveListener);
            }
        }
    }
    
    @Nested
    @DisplayName("Concurrency Tests")
    class ConcurrencyTests {
        
        @Test
        @DisplayName("Should handle concurrent listener addition and removal")
        void shouldHandleConcurrentListenerAdditionAndRemoval() throws InterruptedException {
            final int threadCount = 5;
            final int operationsPerThread = 100;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);
            AtomicInteger errorCount = new AtomicInteger(0);
            
            for (int i = 0; i < threadCount; i++) {
                Thread thread = new Thread(() -> {
                    try {
                        startLatch.await();
                        
                        for (int j = 0; j < operationsPerThread; j++) {
                            TestDataRefreshListener listener = new TestDataRefreshListener();
                            refreshManager.addListener(listener);
                            refreshManager.removeListener(listener);
                        }
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                    } finally {
                        doneLatch.countDown();
                    }
                });
                thread.start();
            }
            
            startLatch.countDown();
            assertTrue(doneLatch.await(10, TimeUnit.SECONDS), "All threads should complete");
            assertEquals(0, errorCount.get(), "No errors should occur during concurrent operations");
        }
        
        @Test
        @DisplayName("Should handle concurrent refresh notifications")
        void shouldHandleConcurrentRefreshNotifications() throws InterruptedException {
            final int threadCount = 5;
            final int refreshesPerThread = 50;
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);
            AtomicInteger totalRefreshes = new AtomicInteger(0);
            
            TestDataRefreshListener concurrentListener = new TestDataRefreshListener();
            refreshManager.addListener(concurrentListener);
            
            try {
                for (int i = 0; i < threadCount; i++) {
                    final int threadIndex = i;
                    Thread thread = new Thread(() -> {
                        try {
                            startLatch.await();
                            
                            for (int j = 0; j < refreshesPerThread; j++) {
                                DataRefreshManager.RefreshType type = 
                                    DataRefreshManager.RefreshType.values()[(threadIndex + j) % DataRefreshManager.RefreshType.values().length];
                                refreshManager.notifyRefresh(type);
                                totalRefreshes.incrementAndGet();
                            }
                        } catch (Exception e) {
                            // Error handling if needed
                        } finally {
                            doneLatch.countDown();
                        }
                    });
                    thread.start();
                }
                
                startLatch.countDown();
                assertTrue(doneLatch.await(10, TimeUnit.SECONDS), "All threads should complete");
                
                // Give some time for all notifications to be processed
                Thread.sleep(100);
                
                assertEquals(threadCount * refreshesPerThread, totalRefreshes.get(), 
                    "All refresh notifications should have been sent");
                assertTrue(concurrentListener.getRefreshCount() > 0, 
                    "Listener should have received some notifications");
                
            } finally {
                refreshManager.removeListener(concurrentListener);
            }
        }
    }
    
    @Nested
    @DisplayName("RefreshType Enum Tests")
    class RefreshTypeEnumTests {
        
        @Test
        @DisplayName("Should have all expected RefreshType values")
        void shouldHaveAllExpectedRefreshTypeValues() {
            DataRefreshManager.RefreshType[] types = DataRefreshManager.RefreshType.values();
            
            assertEquals(6, types.length, "Should have 6 RefreshType values");
            
            // Verify all expected types exist
            assertTrue(containsType(types, DataRefreshManager.RefreshType.TRANSACTIONS));
            assertTrue(containsType(types, DataRefreshManager.RefreshType.BUDGETS));
            assertTrue(containsType(types, DataRefreshManager.RefreshType.CURRENCY));
            assertTrue(containsType(types, DataRefreshManager.RefreshType.SETTINGS));
            assertTrue(containsType(types, DataRefreshManager.RefreshType.ADVICE));
            assertTrue(containsType(types, DataRefreshManager.RefreshType.ALL));
        }
        
        @Test
        @DisplayName("Should handle all RefreshType enum values")
        void shouldHandleAllRefreshTypeEnumValues() {
            refreshManager.addListener(testListener);
            
            for (DataRefreshManager.RefreshType type : DataRefreshManager.RefreshType.values()) {
                testListener.reset();
                
                refreshManager.notifyRefresh(type);
                
                assertEquals(1, testListener.getRefreshCount(), 
                    "Should handle RefreshType: " + type);
                assertEquals(type, testListener.getLastRefreshType(), 
                    "Should pass correct RefreshType: " + type);
            }
        }
        
        private boolean containsType(DataRefreshManager.RefreshType[] types, DataRefreshManager.RefreshType target) {
            for (DataRefreshManager.RefreshType type : types) {
                if (type == target) {
                    return true;
                }
            }
            return false;
        }
    }
    
    // Test helper classes
    private static class TestDataRefreshListener implements DataRefreshListener {
        private final AtomicInteger refreshCount = new AtomicInteger(0);
        private final AtomicReference<DataRefreshManager.RefreshType> lastRefreshType = new AtomicReference<>();
        
        @Override
        public void onDataRefresh(DataRefreshManager.RefreshType type) {
            refreshCount.incrementAndGet();
            lastRefreshType.set(type);
        }
        
        public int getRefreshCount() {
            return refreshCount.get();
        }
        
        public DataRefreshManager.RefreshType getLastRefreshType() {
            return lastRefreshType.get();
        }
        
        public void reset() {
            refreshCount.set(0);
            lastRefreshType.set(null);
        }
    }
    
    private static class ExceptionThrowingListener implements DataRefreshListener {
        @Override
        public void onDataRefresh(DataRefreshManager.RefreshType type) {
            throw new RuntimeException("Test exception from listener");
        }
    }
    
    private static class RecursiveRefreshListener implements DataRefreshListener {
        private final DataRefreshManager refreshManager;
        private final AtomicInteger callCount = new AtomicInteger(0);
        
        public RecursiveRefreshListener(DataRefreshManager refreshManager) {
            this.refreshManager = refreshManager;
        }
        
        @Override
        public void onDataRefresh(DataRefreshManager.RefreshType type) {
            callCount.incrementAndGet();
            // Try to trigger another refresh (should be prevented)
            refreshManager.notifyRefresh(DataRefreshManager.RefreshType.TRANSACTIONS);
        }
        
        public int getCallCount() {
            return callCount.get();
        }
    }
}