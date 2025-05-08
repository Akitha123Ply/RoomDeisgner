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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Design3DView extends JPanel {
    private AppContext appContext;
    private JFrame parentFrame;
    private Design design;
    private JFXPanel jfxPanel;
    private double mouseOldX, mouseOldY;
    private boolean sceneInitialized = false;

    // Wall visibility checkboxes
    private JCheckBox frontWallCheckbox;
    private JCheckBox backWallCheckbox;
    private JCheckBox leftWallCheckbox;
    private JCheckBox rightWallCheckbox;
    private JCheckBox ceilingCheckbox;

    public Design3DView(AppContext appContext, JFrame parentFrame, Design design) {
        this.appContext = appContext;
        this.parentFrame = parentFrame;
        this.design = design;

        // Make sure JavaFX is initialized
        if (!JavaFXIntegration.isInitialized()) {
            JavaFXIntegration.initializeJavaFX();
        }

        // Set the current design in the 3D controller
        appContext.getDesign3DController().setCurrentDesign(design);

        setLayout(new BorderLayout());

        // Create UI components
        createDesign3DUI();

        // Add resize listener to update scene on resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (sceneInitialized) {
                    Platform.runLater(() -> {
                        appContext.getDesign3DController().renderRoom();
                    });
                }
            }
        });

        // Add initialization message
        System.out.println("Design3DView initialized for design: " +
                (design != null ? design.getName() : "null"));
        if (design != null && design.getRoom() != null) {
            System.out.println("Room dimensions: " +
                    design.getRoom().getWidth() + "x" +
                    design.getRoom().getLength() + "x" +
                    design.getRoom().getHeight());
        }
    }

    private void createDesign3DUI() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);

        // Top panel for navigation
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topPanel.setBackground(Color.WHITE);

        // Back button
        JButton backButton = new JButton("Back to 2D");
        backButton.setPreferredSize(new Dimension(100, 25));
        backButton.addActionListener(e -> {
            // Go back to 2D view with the current design
            parentFrame.getContentPane().removeAll();
            parentFrame.setContentPane(new Design2DView(appContext, parentFrame, design.getRoom(), design));
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        // Title
        JLabel titleLabel = new JLabel("3D View");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Room details
        JLabel roomInfoLabel = new JLabel("Room Size: " +
                design.getRoom().getWidth() + "m x " +
                design.getRoom().getLength() + "m x " +
                design.getRoom().getHeight() + "m");

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(backButton);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        centerPanel.add(titleLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(roomInfoLabel);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(centerPanel, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // Right panel for controls
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("View Controls"));
        controlPanel.setPreferredSize(new Dimension(120, 400));
        controlPanel.setBackground(Color.WHITE);

        // Navigation controls
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createTitledBorder("Navigation"));
        navPanel.setBackground(Color.WHITE);

        JButton zoomInButton = new JButton("Zoom In");
        zoomInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        zoomInButton.setMaximumSize(new Dimension(100, 30));
        zoomInButton.addActionListener(e -> {
            if (sceneInitialized) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().zoomCamera(0.9);
                });
            }
        });

        JButton zoomOutButton = new JButton("Zoom Out");
        zoomOutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        zoomOutButton.setMaximumSize(new Dimension(100, 30));
        zoomOutButton.addActionListener(e -> {
            if (sceneInitialized) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().zoomCamera(1.1);
                });
            }
        });

        JButton rotateLeftButton = new JButton("Rotate Left");
        rotateLeftButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rotateLeftButton.setMaximumSize(new Dimension(100, 30));
        rotateLeftButton.addActionListener(e -> {
            if (sceneInitialized) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().rotateCamera(-15);
                });
            }
        });

        JButton rotateRightButton = new JButton("Rotate Right");
        rotateRightButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rotateRightButton.setMaximumSize(new Dimension(100, 30));
        rotateRightButton.addActionListener(e -> {
            if (sceneInitialized) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().rotateCamera(15);
                });
            }
        });

        JButton resetViewButton = new JButton("Reset View");
        resetViewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetViewButton.setMaximumSize(new Dimension(100, 30));
        resetViewButton.addActionListener(e -> {
            if (sceneInitialized) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().resetCamera();
                });
            }
        });

        navPanel.add(zoomInButton);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(zoomOutButton);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(rotateLeftButton);
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(rotateRightButton);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(resetViewButton);

        // Wall visibility panel
        JPanel wallPanel = new JPanel();
        wallPanel.setLayout(new BoxLayout(wallPanel, BoxLayout.Y_AXIS));
        wallPanel.setBorder(BorderFactory.createTitledBorder("Wall Visibility"));
        wallPanel.setBackground(Color.WHITE);

        frontWallCheckbox = new JCheckBox("Front Wall", false);
        frontWallCheckbox.setBackground(Color.WHITE);
        frontWallCheckbox.addActionListener(e -> {
            if (sceneInitialized) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().setWallVisibility("front",
                            frontWallCheckbox.isSelected());
                });
            }
        });

        backWallCheckbox = new JCheckBox("Back Wall", true);
        backWallCheckbox.setBackground(Color.WHITE);
        backWallCheckbox.addActionListener(e -> {
            if (sceneInitialized) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().setWallVisibility("back",
                            backWallCheckbox.isSelected());
                });
            }
        });

        leftWallCheckbox = new JCheckBox("Left Wall", true);
        leftWallCheckbox.setBackground(Color.WHITE);
        leftWallCheckbox.addActionListener(e -> {
            if (sceneInitialized) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().setWallVisibility("left",
                            leftWallCheckbox.isSelected());
                });
            }
        });

        rightWallCheckbox = new JCheckBox("Right Wall", false);
        rightWallCheckbox.setBackground(Color.WHITE);
        rightWallCheckbox.addActionListener(e -> {
            if (sceneInitialized) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().setWallVisibility("right",
                            rightWallCheckbox.isSelected());
                });
            }
        });

        ceilingCheckbox = new JCheckBox("Ceiling", false);
        ceilingCheckbox.setBackground(Color.WHITE);
        ceilingCheckbox.addActionListener(e -> {
            if (sceneInitialized) {
                Platform.runLater(() -> {
                    appContext.getDesign3DController().setWallVisibility("ceiling",
                            ceilingCheckbox.isSelected());
                });
            }
        });

        wallPanel.add(frontWallCheckbox);
        wallPanel.add(backWallCheckbox);
        wallPanel.add(leftWallCheckbox);
        wallPanel.add(rightWallCheckbox);
        wallPanel.add(ceilingCheckbox);

        // Add mouse instructions
        JLabel mouseLabel = new JLabel("<html>Mouse Controls:<br>• Drag to rotate<br>• Scroll to zoom</html>");
        mouseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add panels to control panel
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(navPanel);
        controlPanel.add(Box.createVerticalStrut(15));
        controlPanel.add(wallPanel);
        controlPanel.add(Box.createVerticalStrut(15));
        controlPanel.add(mouseLabel);
        controlPanel.add(Box.createVerticalGlue());

        // JavaFX panel for 3D view
        jfxPanel = new JFXPanel();

        // Add components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(jfxPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.EAST);

        // Add main panel to this
        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        System.out.println("Design3DView added to hierarchy");

        // Make sure JavaFX is initialized
        if (!JavaFXIntegration.isInitialized()) {
            JavaFXIntegration.initializeJavaFX();
        }

        // Delay initialization to ensure component is fully added
        SwingUtilities.invokeLater(() -> {
            try {
                // Wait a bit to ensure the component is fully laid out
                Thread.sleep(100);
                initializeJavaFX();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void initializeJavaFX() {
        System.out.println("Initializing 3D Scene...");

        // Ensure we have valid dimensions or use defaults
        final int width = jfxPanel.getWidth() > 0 ? jfxPanel.getWidth() : 800;
        final int height = jfxPanel.getHeight() > 0 ? jfxPanel.getHeight() : 600;

        System.out.println("Creating JavaFX scene with size: " + width + "x" + height);

        // Run on JavaFX thread
        Platform.runLater(() -> {
            try {
                // Create 3D scene
                Scene scene = appContext.getDesign3DController().createScene(width, height);

                if (scene == null) {
                    System.err.println("Failed to create scene!");
                    return;
                }

                // Set up mouse event handlers for rotating the view
                scene.setOnMousePressed((MouseEvent me) -> {
                    mouseOldX = me.getSceneX();
                    mouseOldY = me.getSceneY();
                });

                scene.setOnMouseDragged((MouseEvent me) -> {
                    double mousePosX = me.getSceneX();
                    double mousePosY = me.getSceneY();

                    // Calculate rotation based on mouse movement
                    double rotateY = mousePosX - mouseOldX;
                    double rotateX = mousePosY - mouseOldY;

                    // Apply rotation
                    appContext.getDesign3DController().rotateCamera(rotateY * 0.2);
                    appContext.getDesign3DController().tiltCamera(-rotateX * 0.2);

                    mouseOldX = mousePosX;
                    mouseOldY = mousePosY;
                });

                // Add zoom with mouse wheel
                scene.setOnScroll((ScrollEvent se) -> {
                    double delta = se.getDeltaY();
                    if (delta > 0) {
                        // Zoom in
                        appContext.getDesign3DController().zoomCamera(0.95);
                    } else {
                        // Zoom out
                        appContext.getDesign3DController().zoomCamera(1.05);
                    }
                });

                // Set the scene in the JFXPanel
                jfxPanel.setScene(scene);

                // Initialize wall visibility settings
                updateWallVisibilityControls();

                // Mark as initialized
                sceneInitialized = true;

                System.out.println("JavaFX scene set. Rendering room...");

                // Use a separate thread to introduce a delay before rendering
                new Thread(() -> {
                    try {
                        // Wait a bit to ensure scene is fully set up
                        Thread.sleep(300);
                        Platform.runLater(() -> {
                            try {
                                // Render the room
                                appContext.getDesign3DController().renderRoom();
                                System.out.println("Room rendering complete.");
                            } catch (Exception e) {
                                System.err.println("Error rendering room: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

            } catch (Exception e) {
                System.err.println("Error initializing JavaFX: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // Helper method to update wall visibility controls
    private void updateWallVisibilityControls() {
        if (frontWallCheckbox != null && backWallCheckbox != null &&
                leftWallCheckbox != null && rightWallCheckbox != null &&
                ceilingCheckbox != null) {

            frontWallCheckbox.setSelected(appContext.getDesign3DController().isWallVisible("front"));
            backWallCheckbox.setSelected(appContext.getDesign3DController().isWallVisible("back"));
            leftWallCheckbox.setSelected(appContext.getDesign3DController().isWallVisible("left"));
            rightWallCheckbox.setSelected(appContext.getDesign3DController().isWallVisible("right"));
            ceilingCheckbox.setSelected(appContext.getDesign3DController().isWallVisible("ceiling"));
        }
    }

    @Override
    public void removeNotify() {
        // Clean up any resources when being removed from the hierarchy
        sceneInitialized = false;
        super.removeNotify();
    }
}