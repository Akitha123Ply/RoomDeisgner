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
            System.out.println("JavaFX toolkit already initialized.");
            return;
        }

        System.out.println("Initializing JavaFX toolkit...");

        // Create a new JFXPanel to initialize the JavaFX toolkit
        // This is thread-safe and can be called from any thread
        new JFXPanel();

        // Make sure Platform is running
        Platform.runLater(() -> {
            System.out.println("JavaFX toolkit initialized.");
            initialized = true;
            initLatch.countDown();
        });

        // If we're not on the EDT, wait for initialization to complete
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                initLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("JavaFX initialization interrupted");
            }
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

        // Only wait if not on EDT
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                doneLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}