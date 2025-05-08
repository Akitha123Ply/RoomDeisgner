package util;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

public class JavaFXIntegration {
    private static boolean initialized = false;
    private static final CountDownLatch initLatch = new CountDownLatch(1);

    public static synchronized void initializeJavaFX() {
        if (initialized) {
            return;
        }

        System.out.println("Initializing JavaFX toolkit...");

        // Create a new JFXPanel to initialize the JavaFX toolkit
        SwingUtilities.invokeLater(() -> {
            try {
                new JFXPanel();
                Platform.runLater(() -> {
                    System.out.println("JavaFX toolkit initialized.");
                    initialized = true;
                    initLatch.countDown();
                });
            } catch (Exception e) {
                System.err.println("Error initializing JavaFX toolkit: " + e.getMessage());
                e.printStackTrace();
                initLatch.countDown();
            }
        });

        // Wait for initialization to complete
        try {
            initLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }

    // Helper method to execute on JavaFX thread and wait for completion
    public static void runAndWait(Runnable action) {
        if (!initialized) {
            initializeJavaFX();
        }

        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }

        final CountDownLatch doneLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                doneLatch.countDown();
            }
        });

        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}