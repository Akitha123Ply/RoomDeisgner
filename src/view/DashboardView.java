package view;

import controller.AuthController;
import controller.DashboardController;
import controller.ViewManager;
import model.Design;
import model.User;
import util.AppContext;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DashboardView extends JPanel {
    private AppContext appContext;
    private JFrame parentFrame;
    private User currentUser;

    // UI Components
    private JPanel contentPanel;
    private JPanel designCardsPanel;

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

        backgroundPanel.setLayout(null); // Use absolute positioning

        // Create dashboard content
        createDashboardContent(backgroundPanel);

        add(backgroundPanel, BorderLayout.CENTER);
    }

    private void createDashboardContent(JPanel backgroundPanel) {
        // Top-left: Username
        String username = currentUser != null ? currentUser.getEmail().split("@")[0] : "Admin";
        JLabel usernameLabel = new JLabel("Hello, " + username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        usernameLabel.setBounds(parentFrame.getWidth() - 200, 40, 200, 30);
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
        dashboardLabel.setFont(new Font("Arial", Font.BOLD, 32));
        dashboardLabel.setBounds(50, 35, 200, 40);
        backgroundPanel.add(dashboardLabel);

        // Quick Actions label
        JLabel quickActionsLabel = new JLabel("Quick Actions");
        quickActionsLabel.setFont(new Font("Arial", Font.BOLD, 22));
        quickActionsLabel.setBounds(50, 85, 200, 30);
        backgroundPanel.add(quickActionsLabel);

        // Create Design button - card style
        JPanel createDesignCard = createActionCard("Create Design", "src/resources/icons/design.png");
        createDesignCard.setBounds(50, 120, 150, 80);
        createDesignCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createDesignCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Navigate to room creator view
                parentFrame.getContentPane().removeAll();
                parentFrame.setContentPane(new RoomCreatorView(appContext, parentFrame));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
        backgroundPanel.add(createDesignCard);

        // Load Design button - card style
        JPanel loadDesignCard = createActionCard("Load Design", "src/resources/icons/load.png");
        loadDesignCard.setBounds(220, 120, 150, 80);
        loadDesignCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadDesignCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Get all user designs
                List<Design> userDesigns = appContext.getDashboardController().getUserDesigns();

                if (userDesigns.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "No saved designs found. Please create a new design first.",
                            "No Designs Available",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Create a dialog to select a design
                String[] designNames = userDesigns.stream()
                        .map(d -> d.getName() + " (" + d.getRoom().toString() + ")")
                        .toArray(String[]::new);

                String selectedDesign = (String) JOptionPane.showInputDialog(
                        parentFrame,
                        "Select a design to load:",
                        "Load Design",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        designNames,
                        designNames[0]);

                if (selectedDesign != null) {
                    // Find the selected design
                    int index = -1;
                    for (int i = 0; i < designNames.length; i++) {
                        if (designNames[i].equals(selectedDesign)) {
                            index = i;
                            break;
                        }
                    }

                    if (index >= 0) {
                        Design design = userDesigns.get(index);
                        // Load the selected design
                        parentFrame.getContentPane().removeAll();
                        parentFrame.setContentPane(new Design2DView(appContext, parentFrame, design.getRoom(), design));
                        parentFrame.revalidate();
                        parentFrame.repaint();
                    }
                }
            }
        });
        backgroundPanel.add(loadDesignCard);

        // Saved Designs label
        JLabel savedDesignsLabel = new JLabel("Saved Designs");
        savedDesignsLabel.setFont(new Font("Arial", Font.BOLD, 22));
        savedDesignsLabel.setBounds(50, 240, 200, 30);
        backgroundPanel.add(savedDesignsLabel);

        // Add design cards
        displayDesignCards(backgroundPanel);
    }

    private JPanel createActionCard(String text, String iconPath) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // Icon
        JLabel iconLabel = new JLabel();
        ImageIcon originalIcon = new ImageIcon(iconPath);
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        iconLabel.setIcon(new ImageIcon(resizedImage));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);

        // Text
        JLabel textLabel = new JLabel(text);
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        card.add(iconLabel, BorderLayout.CENTER);
        card.add(textLabel, BorderLayout.SOUTH);

        // Add rounded corners and hover effect
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        return card;
    }

    private void displayDesignCards(JPanel backgroundPanel) {
        // Get user designs
        List<Design> userDesigns = appContext.getDashboardController().getUserDesigns();

        // No saved designs message
        JLabel noDesignsLabel = new JLabel("No saved designs. Create a new design to get started.");
        noDesignsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        noDesignsLabel.setBounds(50, 280, 400, 30);
        backgroundPanel.add(noDesignsLabel);

        if (userDesigns.isEmpty()) {
            noDesignsLabel.setVisible(true);
            return;
        }

        // Hide the "no designs" message if there are designs
        noDesignsLabel.setVisible(false);

        // Create a grid layout for design cards (2x2)
        int xOffset = 50;
        int yOffset = 280;
        int cardWidth = 250;
        int cardHeight = 150;
        int horizontalGap = 30;
        int verticalGap = 30;
        int cardsPerRow = 2;

        // SimpleDateFormat for formatting dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        // Display at most 4 designs in a 2x2 grid
        int displayCount = Math.min(userDesigns.size(), 4);

        for (int i = 0; i < displayCount; i++) {
            Design design = userDesigns.get(i);

            // Calculate position in grid
            int row = i / cardsPerRow;
            int col = i % cardsPerRow;

            int x = xOffset + col * (cardWidth + horizontalGap);
            int y = yOffset + row * (cardHeight + verticalGap);

            // Create design card
            JPanel designCard = createDesignCard(design, dateFormat);
            designCard.setBounds(x, y, cardWidth, cardHeight);

            // Add card to background panel
            backgroundPanel.add(designCard);
        }
    }

    private JPanel createDesignCard(Design design, SimpleDateFormat dateFormat) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(Color.WHITE);

        // Add rounded corners
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Top bar with design name
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.LIGHT_GRAY);
        topBar.setBounds(10, 10, 230, 30);

        JLabel designNameLabel = new JLabel("  " + design.getName());
        designNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topBar.add(designNameLabel, BorderLayout.WEST);

        // Add dots icon to top bar
        JLabel dotsLabel = new JLabel("•••");
        dotsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dotsLabel.setHorizontalAlignment(JLabel.RIGHT);
        dotsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        topBar.add(dotsLabel, BorderLayout.EAST);

        card.add(topBar);

        // Home icon
        JLabel homeIconLabel = new JLabel();
        ImageIcon homeIcon = new ImageIcon("src/resources/icons/home.png");
        Image homeImage = homeIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        homeIconLabel.setIcon(new ImageIcon(homeImage));
        homeIconLabel.setBounds(20, 50, 50, 50);
        card.add(homeIconLabel);

        // Room information
        JLabel roomInfoLabel = new JLabel("<html>Room: " + design.getRoom().getWidth() + "m x " +
                design.getRoom().getLength() + "m x " +
                design.getRoom().getHeight() + "m<br>" +
                "Created: " + dateFormat.format(new Date()) + "</html>");
        roomInfoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        roomInfoLabel.setBounds(40, 50, 150, 50);
        card.add(roomInfoLabel);

        // Edit button
        JButton editButton = new JButton();
        ImageIcon editIcon = new ImageIcon("src/resources/icons/edit.png");
        Image editImage = editIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        editButton.setIcon(new ImageIcon(editImage));
        editButton.setBounds(200, 50, 30, 30);
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open design in editor
                parentFrame.getContentPane().removeAll();
                parentFrame.setContentPane(new Design2DView(appContext, parentFrame, design.getRoom(), design));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
        card.add(editButton);

        // Delete button
        JButton deleteButton = new JButton();
        ImageIcon deleteIcon = new ImageIcon("src/resources/icons/delete.png");
        Image deleteImage = deleteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        deleteButton.setIcon(new ImageIcon(deleteImage));
        deleteButton.setBounds(200, 90, 30, 30);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Confirm deletion
                int result = JOptionPane.showConfirmDialog(
                        parentFrame,
                        "Are you sure you want to delete this design?",
                        "Delete Design",
                        JOptionPane.YES_NO_OPTION
                );

                if (result == JOptionPane.YES_OPTION) {
                    // Delete design
                    appContext.getDashboardController().deleteDesign(design.getId());

                    // Refresh dashboard
                    parentFrame.getContentPane().removeAll();
                    parentFrame.setContentPane(new DashboardView(appContext, parentFrame));
                    parentFrame.revalidate();
                    parentFrame.repaint();
                }
            }
        });
        card.add(deleteButton);

        // Make whole card clickable to edit the design
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Ignore clicks on buttons
                if (evt.getSource() != editButton && evt.getSource() != deleteButton) {
                    // Open design in editor
//                    parentFrame.getContentPane().removeAll();
//                    parentFrame.setContentPane(new Design2DView(appContext, parentFrame, design.getRoom(), design));
//                    parentFrame.revalidate();
//                    parentFrame.repaint();
                }
            }
        });

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return card;
    }

    // Inner class for rounded borders
    private class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}