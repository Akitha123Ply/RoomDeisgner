import util.AppContext;
import view.LoginView;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Application Starting...");

        // Initialize JavaFX early
        //JavaFXManager.getInstance().initialize();

        // Set the look and feel to system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create application context
        AppContext appContext = new AppContext();

        // Launch the application on Swing EDT
        SwingUtilities.invokeLater(() -> {
            // Create and show login view
            JFrame frame = new JFrame("Room Designer");
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