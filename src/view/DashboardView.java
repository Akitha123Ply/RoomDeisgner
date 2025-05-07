package view;

import controller.AuthController;
import controller.DashboardController;
import model.Design;
import model.User;
import util.AppContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DashboardView extends JPanel {
    private AppContext appContext;
    private JFrame parentFrame;
    private User currentUser;

    // UI Components
    private JPanel contentPanel;

    public DashboardView(AppContext appContext, JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.currentUser = appContext.getAuthController().getCurrentUser();
        this.appContext = appContext;

        setLayout(new BorderLayout());

        // Load background image
        ImageIcon backgroundImage = new ImageIcon("src/resources/backgrounds/dashboard.jpg");

        // Create a custom panel with background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage.getImage() != null) {
                    g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(240, 240, 240));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        backgroundPanel.setLayout(new BorderLayout());

        // Create dashboard content
        createDashboardContent(backgroundPanel);

        add(backgroundPanel, BorderLayout.CENTER);
    }

    private void createDashboardContent(JPanel backgroundPanel) {
        // Add components directly to the background panel

        // Top-left: Username
        String username = currentUser != null ? currentUser.getEmail().split("@")[0] : "john";
        JLabel usernameLabel = new JLabel("Hello, " + username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        usernameLabel.setBounds(30, 30, 200, 30);
        backgroundPanel.setLayout(null); // Use absolute positioning
        backgroundPanel.add(usernameLabel);

        // Top-right: Logout button
        JButton logoutButton = new JButton();
        ImageIcon originalIcon = new ImageIcon("src/resources/icons/logout.png");
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        logoutButton.setIcon(new ImageIcon(resizedImage));
        logoutButton.setBorderPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setBounds(parentFrame.getWidth() - 80, 30, 50, 50);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setToolTipText("Logout");

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appContext.getAuthController().logout();

                // Navigate back to login view
                parentFrame.getContentPane().removeAll();
                parentFrame.setContentPane(new LoginView(appContext, parentFrame));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
        backgroundPanel.add(logoutButton);

        // Dashboard title
        JLabel dashboardLabel = new JLabel("Dashboard");
        dashboardLabel.setFont(new Font("Arial", Font.BOLD, 24));
        dashboardLabel.setBounds(500, 30, 200, 30);
        backgroundPanel.add(dashboardLabel);

        // Quick Actions label
        JLabel quickActionsLabel = new JLabel("Quick Actions");
        quickActionsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        quickActionsLabel.setBounds(500, 70, 200, 30);
        backgroundPanel.add(quickActionsLabel);

        // Create Design button
        JButton createDesignButton = new JButton();
        createDesignButton.setLayout(new BorderLayout());
        createDesignButton.setBounds(30, 120, 100, 100);
        createDesignButton.setBackground(Color.WHITE);
        createDesignButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // Icon
        JLabel createIconLabel = new JLabel();
        ImageIcon createOriginalIcon = new ImageIcon("src/resources/icons/design.png");
        Image createOriginalImage = createOriginalIcon.getImage();
        Image createResizedImage = createOriginalImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        createIconLabel.setIcon(new ImageIcon(createResizedImage));
        createIconLabel.setHorizontalAlignment(JLabel.CENTER);

        // Text
        JLabel createTextLabel = new JLabel("Create Design");
        createTextLabel.setHorizontalAlignment(JLabel.CENTER);
        createTextLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        createDesignButton.add(createIconLabel, BorderLayout.CENTER);
        createDesignButton.add(createTextLabel, BorderLayout.SOUTH);

        createDesignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Navigate to room creator view
                parentFrame.getContentPane().removeAll();
                // TODO: Replace with actual RoomCreatorView once implemented
                parentFrame.setContentPane(new RoomCreatorView(appContext, parentFrame));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
        backgroundPanel.add(createDesignButton);

        // Load Design button
        JButton loadDesignButton = new JButton();
        loadDesignButton.setLayout(new BorderLayout());
        loadDesignButton.setBounds(150, 120, 100, 100);
        loadDesignButton.setBackground(Color.WHITE);
        loadDesignButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // Icon
        JLabel loadIconLabel = new JLabel();
        ImageIcon loadOriginalIcon = new ImageIcon("src/resources/icons/load.png");
        Image loadOriginalImage = loadOriginalIcon.getImage();
        Image loadResizedImage = loadOriginalImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        loadIconLabel.setIcon(new ImageIcon(loadResizedImage));
        loadIconLabel.setHorizontalAlignment(JLabel.CENTER);

        // Text
        JLabel loadTextLabel = new JLabel("Load Design");
        loadTextLabel.setHorizontalAlignment(JLabel.CENTER);
        loadTextLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        loadDesignButton.add(loadIconLabel, BorderLayout.CENTER);
        loadDesignButton.add(loadTextLabel, BorderLayout.SOUTH);

        loadDesignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Implement design loading functionality
                JOptionPane.showMessageDialog(parentFrame,
                        "Please select a design from the list below.",
                        "Load Design",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        backgroundPanel.add(loadDesignButton);

        // Saved Designs label
        JLabel savedDesignsLabel = new JLabel("Saved Designs");
        savedDesignsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        savedDesignsLabel.setBounds(500, 250, 200, 30);
        backgroundPanel.add(savedDesignsLabel);

        // No saved designs message
        JLabel noDesignsLabel = new JLabel("No saved designs. Create a new design to get started.");
        noDesignsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        noDesignsLabel.setBounds(30, 300, 400, 30);
        backgroundPanel.add(noDesignsLabel);

        // Check if user has any designs and display them
        List<Design> userDesigns = appContext.getDashboardController().getUserDesigns();
        if (!userDesigns.isEmpty()) {
            // Hide the "no designs" message if there are designs
            noDesignsLabel.setVisible(false);
        }
    }
}