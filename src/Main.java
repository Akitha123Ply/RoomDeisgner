import controller.AuthController;
import controller.DashboardController;
import util.AppContext;
import view.LoginView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Furniture Visualizer Application Starting...");

        // Initialize JavaFX for later use
        //JavaFXIntegration.initializeJavaFX();

        // Set the look and feel to system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppContext appContext = new AppContext();

        // Launch the application on Swing EDT
        SwingUtilities.invokeLater(() -> {
            // Create and show login view
            JFrame frame = new JFrame("Furniture Visualizer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1024, 768);
            frame.setLocationRelativeTo(null);

            // Set icon for the application
            ImageIcon appIcon = new ImageIcon("src/resources/icons/design.png");
            frame.setIconImage(appIcon.getImage());

            // Set login view as the content pane
            LoginView loginView = new LoginView(appContext, frame);
            frame.setContentPane(loginView);

            // Display the window
            frame.setVisible(true);
        });
    }
}