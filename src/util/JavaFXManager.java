package util;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaFXManager {
    // Singleton instance
    private static JavaFXManager instance;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    // Private constructor for singleton
    private JavaFXManager() {
        // Private to prevent instantiation
    }

    // Get the singleton instance
    public static synchronized JavaFXManager getInstance() {
        if (instance == null) {
            instance = new JavaFXManager();
        }
        return instance;
    }

    public boolean isInitialized() {
        return initialized.get();
    }

    /**
     * Initialize JavaFX toolkit
     */
    public void initialize() {
        // Only initialize once
        if (initialized.get()) {
            System.out.println("[JavaFXManager] JavaFX toolkit already initialized.");
            return;
        }

        System.out.println("[JavaFXManager] Initializing JavaFX toolkit...");

        // Use a latch to ensure initialization completes
        final CountDownLatch latch = new CountDownLatch(1);

        try {
            // Create JFXPanel to initialize JavaFX thread
            SwingUtilities.invokeAndWait(() -> {
                try {
                    new JFXPanel(); // This initializes JavaFX environment

                    // Now that we have initialized the JavaFX thread, we can use Platform.runLater
                    Platform.runLater(() -> {
                        try {
                            // Additional initialization if needed
                            System.out.println("[JavaFXManager] JavaFX toolkit initialized successfully.");
                            initialized.set(true);
                        } finally {
                            latch.countDown();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("[JavaFXManager] Error in JavaFX initialization: " + e.getMessage());
                    e.printStackTrace();
                    latch.countDown();
                }
            });

            // Wait for initialization to complete
            latch.await();
        } catch (Exception e) {
            System.err.println("[JavaFXManager] Failed to initialize JavaFX: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Run a task on the JavaFX thread and wait for completion
     */
    public void runAndWait(Runnable task) {
        if (!initialized.get()) {
            initialize();
        }

        if (Platform.isFxApplicationThread()) {
            task.run();
            return;
        }

        final CountDownLatch doneLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                task.run();
            } finally {
                doneLatch.countDown();
            }
        });

        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[JavaFXManager] Interrupted while waiting for JavaFX task: " + e.getMessage());
        }
    }

    /**
     * Run a task on the JavaFX thread without waiting
     */
    public void runLater(Runnable task) {
        if (!initialized.get()) {
            initialize();
        }

        Platform.runLater(task);
    }
}