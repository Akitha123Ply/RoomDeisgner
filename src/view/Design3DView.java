package view;

import controller.Design3DController;
import model.Design;
import util.AppContext;
import util.JavaFXIntegration;

import javax.swing.*;
import javafx.embed.swing.JFXPanel;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Design3DView extends JPanel {
    private AppContext appContext;
    private JFrame parentFrame;
    private Design design;
    private JFXPanel jfxPanel;
    private double mouseOldX, mouseOldY;
    private double mousePosX, mousePosY;

    public Design3DView(AppContext appContext, JFrame parentFrame, Design design) {
        this.appContext = appContext;
        this.parentFrame = parentFrame;
        this.design = design;

        // Set the current design in the 3D controller
        appContext.getDesign3DController().setCurrentDesign(design);

        setLayout(new BorderLayout());

        // Create UI components
        createDesign3DUI();

        // Initialize JavaFX scene
        initializeJavaFX();
    }

    private void createDesign3DUI() {
        // Main container panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header panel with back button and title
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center panel for 3D visualization
        jfxPanel = new JFXPanel();
        jfxPanel.setPreferredSize(new Dimension(800, 600));
        mainPanel.add(jfxPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Back button
        JButton backButton = new JButton();
        ImageIcon originalBackIcon = new ImageIcon("src/resources/icons/back.png");
        Image originalBackImage = originalBackIcon.getImage();
        Image resizedBackImage = originalBackImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        backButton.setIcon(new ImageIcon(resizedBackImage));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Go back to 2D view
                parentFrame.getContentPane().removeAll();
                parentFrame.setContentPane(new Design2DView(appContext, parentFrame, design.getRoom()));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });

        // Title label
        JLabel titleLabel = new JLabel("3D");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Room info label
        JLabel roomInfoLabel = new JLabel("Room Size: " + design.getRoom().toString());
        roomInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Control buttons panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);

        // Zoom in button
        JButton zoomInButton = createIconButton("zoom.png", "Zoom In");
        zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().zoomCamera(0.9); // Zoom in
                });
            }
        });

        // Zoom out button
        JButton zoomOutButton = createIconButton("zoom_out.png", "Zoom Out");
        zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().zoomCamera(1.1); // Zoom out
                });
            }
        });

        // Reset view button
        JButton resetButton = createIconButton("reset.png", "Reset View");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().resetCamera();
                });
            }
        });

        controlPanel.add(zoomInButton);
        controlPanel.add(zoomOutButton);
        controlPanel.add(resetButton);

        // Add components to header
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(backButton);
        leftPanel.add(titleLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(roomInfoLabel);
        rightPanel.add(controlPanel);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JButton createIconButton(String iconName, String tooltip) {
        JButton button = new JButton();
        ImageIcon originalIcon = new ImageIcon("src/resources/icons/" + iconName);
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(resizedImage));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        return button;
    }

    private void initializeJavaFX() {
        // Initialize JavaFX components on the JavaFX Application Thread
        Platform.runLater(() -> {
            // Create 3D scene
            Scene scene = appContext.getDesign3DController().createScene(
                    jfxPanel.getWidth(), jfxPanel.getHeight());

            // Setup mouse event handlers for rotating the view
            scene.setOnMousePressed((MouseEvent me) -> {
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            });

            scene.setOnMouseDragged((MouseEvent me) -> {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();

                // Calculate rotation based on mouse movement
                double rotateY = mousePosX - mouseOldX;

                // Apply rotation
                appContext.getDesign3DController().rotateCamera(rotateY * 0.2);

                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            });

            // Render the room
            appContext.getDesign3DController().renderRoom();

            // Set the scene in the JFXPanel
            jfxPanel.setScene(scene);
        });
    }
}