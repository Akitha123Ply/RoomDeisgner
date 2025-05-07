package view;

import controller.Design2DController;
import model.Design;
import model.Furniture;
import model.Room;
import util.AppContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class Design2DView extends JPanel {
    private AppContext appContext;
    private JFrame parentFrame;
    private Room room;
    private Design design;

    // UI Components
    private JPanel roomPanel;
    private JPanel furniturePanel;
    private JPanel selectedFurniturePanel;
    private JPanel colorPanel;
    private Furniture selectedFurniture;
    private Furniture draggingFurniture;
    private Point dragStartPoint;
    private Color selectedColor = Color.RED;

    public Design2DView(AppContext appContext, JFrame parentFrame, Room room) {
        this.appContext = appContext;
        this.parentFrame = parentFrame;
        this.room = room;

        // Create or load a design with this room
        design = appContext.getDesign2DController().createNewDesign(room);
        appContext.getDesign2DController().setCurrentDesign(design);

        setLayout(new BorderLayout());

        // Create UI components
        createDesign2DUI();
    }

    private void createDesign2DUI() {
        // Main container panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header panel with back button and title
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Left panel for furniture selection
        JPanel leftPanel = createFurnitureSelectionPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Center panel for room visualization
        roomPanel = createRoomPanel();
        mainPanel.add(new JScrollPane(roomPanel), BorderLayout.CENTER);

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
                // Go back to room creation
                parentFrame.getContentPane().removeAll();
                parentFrame.setContentPane(new RoomCreatorView(appContext, parentFrame));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });

        // Title label
        JLabel titleLabel = new JLabel("Add Furniture & 2D");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // View 3D button
        JButton view3DButton = new JButton("3D");
        view3DButton.setBackground(Color.BLACK);
        view3DButton.setForeground(Color.WHITE);
        view3DButton.setFocusPainted(false);
        view3DButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        view3DButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch to 3D view
                parentFrame.getContentPane().removeAll();
                parentFrame.setContentPane(new Design3DView(appContext, parentFrame, design));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });

        // Room info label
        JLabel roomInfoLabel = new JLabel("Room Size: " + room.toString());
        roomInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add components to header
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(backButton);
        leftPanel.add(titleLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(roomInfoLabel);
        rightPanel.add(view3DButton);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFurnitureSelectionPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.LIGHT_GRAY);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setPreferredSize(new Dimension(200, 600));

        // Furniture Types Label
        JLabel furnitureTypesLabel = new JLabel("Furniture Types");
        furnitureTypesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        furnitureTypesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Furniture selection grid
        furniturePanel = new JPanel(new GridLayout(0, 2, 5, 5));
        furniturePanel.setOpaque(false);

        // Add furniture icons
        addFurnitureIcons(furniturePanel);

        // Color selection
        JLabel colorLabel = new JLabel("Color");
        colorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        colorPanel = new JPanel();
        colorPanel.setBackground(selectedColor);
        colorPanel.setPreferredSize(new Dimension(30, 30));
        colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        colorPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        colorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                        leftPanel,
                        "Choose Furniture Color",
                        selectedColor);
                if (newColor != null) {
                    selectedColor = newColor;
                    colorPanel.setBackground(newColor);

                    // Apply color to selected furniture if any
                    if (selectedFurniture != null) {
                        appContext.getDesign2DController().changeFurnitureColor(selectedFurniture, newColor);
                        roomPanel.repaint();
                    }
                }
            }
        });

        // Add button
        JButton addButton = new JButton("Add");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setBackground(Color.BLACK);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setEnabled(false); // Disabled until a furniture is selected

        // Selected furniture panel
        selectedFurniturePanel = new JPanel();
        selectedFurniturePanel.setBackground(Color.WHITE);
        selectedFurniturePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        selectedFurniturePanel.setPreferredSize(new Dimension(80, 80));
        selectedFurniturePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Room information at bottom
        JLabel roomSizeLabel = new JLabel("Room Size:");
        roomSizeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        roomSizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roomDimensionsLabel = new JLabel(room.toString());
        roomDimensionsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        roomDimensionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Control buttons
        JPanel controlButtonsPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        controlButtonsPanel.setOpaque(false);

        JButton saveButton = createIconButton("save.png", "Save");
        JButton undoButton = createIconButton("undo.png", "Undo");
        JButton resetButton = createIconButton("reset.png", "Reset");

        controlButtonsPanel.add(saveButton);
        controlButtonsPanel.add(undoButton);
        controlButtonsPanel.add(resetButton);

        // Add components to left panel
        leftPanel.add(furnitureTypesLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(furniturePanel);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(colorLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(colorPanel);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(selectedFurniturePanel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(addButton);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(controlButtonsPanel);
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(roomSizeLabel);
        leftPanel.add(roomDimensionsLabel);

        return leftPanel;
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

    private void addFurnitureIcons(JPanel panel) {
        // Chair icon
        JButton chairButton = createFurnitureButton("chair.png", Furniture.Type.CHAIR);
        panel.add(chairButton);

        // Table icon
        JButton tableButton = createFurnitureButton("table.png", Furniture.Type.TABLE);
        panel.add(tableButton);

        // Bed icon
        JButton bedButton = createFurnitureButton("bed.png", Furniture.Type.SOFA);
        panel.add(bedButton);

        // Cupboard icon
        JButton cupboardButton = createFurnitureButton("cupboard.png", Furniture.Type.CABINET);
        panel.add(cupboardButton);
    }

    private JButton createFurnitureButton(String iconName, Furniture.Type type) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(60, 60));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        ImageIcon originalIcon = new ImageIcon("src/resources/icons/" + iconName);
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(resizedImage));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get furniture from controller
                List<Furniture> furnitureOfType = appContext.getFurnitureController().getFurnitureByType(type);
                if (!furnitureOfType.isEmpty()) {
                    // Get the first furniture of this type as a template
                    Furniture furniture = furnitureOfType.get(0).clone();
                    furniture.setColor(selectedColor);

                    // Update selected furniture
                    selectedFurniture = furniture;

                    // Update selected furniture panel
                    selectedFurniturePanel.removeAll();
                    ImageIcon icon = new ImageIcon("src/resources/icons/" + iconName);
                    JLabel iconLabel = new JLabel(icon);
                    selectedFurniturePanel.add(iconLabel);
                    selectedFurniturePanel.revalidate();
                    selectedFurniturePanel.repaint();
                }
            }
        });

        return button;
    }

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw room background
                g.setColor(room.getFloorColor());
                g.fillRect(0, 0, getWidth(), getHeight());

                // Draw room walls
                g.setColor(room.getWallColor());
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

                // Draw furniture
                if (design != null) {
                    for (Furniture furniture : design.getFurnitureList()) {
                        drawFurniture(g, furniture);
                    }
                }

                // Draw selected furniture if being dragged
                if (draggingFurniture != null) {
                    drawFurniture(g, draggingFurniture);
                }
            }
        };

        // Set preferred size based on room dimensions
        int pixelsPerMeter = 100; // Scale: 1 meter = 100 pixels
        int width = (int) (room.getWidth() * pixelsPerMeter);
        int height = (int) (room.getLength() * pixelsPerMeter);
        panel.setPreferredSize(new Dimension(width, height));

        // Add mouse listeners for furniture placement and manipulation
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Check if clicking on existing furniture
                Point clickPoint = e.getPoint();
                Furniture clickedFurniture = getFurnitureAtPoint(clickPoint);

                if (clickedFurniture != null) {
                    // Select the furniture
                    selectedFurniture = clickedFurniture;

                    // Start dragging
                    draggingFurniture = clickedFurniture;
                    dragStartPoint = clickPoint;
                } else if (selectedFurniture != null) {
                    // Add new furniture at click position
                    Furniture newFurniture = selectedFurniture.clone();

                    // Adjust position to center furniture at click point
                    int furnitureWidth = (int) (newFurniture.getWidth() * pixelsPerMeter);
                    int furnitureLength = (int) (newFurniture.getLength() * pixelsPerMeter);

                    Point adjustedPoint = new Point(
                            clickPoint.x - furnitureWidth / 2,
                            clickPoint.y - furnitureLength / 2);

                    appContext.getDesign2DController().addFurniture(newFurniture, adjustedPoint);
                    panel.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggingFurniture != null) {
                    // End dragging
                    draggingFurniture = null;
                    dragStartPoint = null;
                    panel.repaint();
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggingFurniture != null && dragStartPoint != null) {
                    // Calculate new position
                    Point newPoint = new Point(
                            draggingFurniture.getPosition().x + (e.getX() - dragStartPoint.x),
                            draggingFurniture.getPosition().y + (e.getY() - dragStartPoint.y));

                    // Update furniture position
                    appContext.getDesign2DController().moveFurniture(draggingFurniture, newPoint);

                    // Update drag start point
                    dragStartPoint = e.getPoint();

                    panel.repaint();
                }
            }
        });

        return panel;
    }

    private void drawFurniture(Graphics g, Furniture furniture) {
        g.setColor(furniture.getColor());

        int pixelsPerMeter = 100; // Scale: 1 meter = 100 pixels
        int width = (int) (furniture.getWidth() * pixelsPerMeter);
        int height = (int) (furniture.getLength() * pixelsPerMeter);

        Point position = furniture.getPosition();

        // For simplicity, just draw rectangles for all furniture types
        g.fillRect(position.x, position.y, width, height);

        // Draw border
        g.setColor(Color.BLACK);
        g.drawRect(position.x, position.y, width, height);
    }

    private Furniture getFurnitureAtPoint(Point point) {
        if (design == null) {
            return null;
        }

        int pixelsPerMeter = 100; // Scale: 1 meter = 100 pixels

        for (Furniture furniture : design.getFurnitureList()) {
            Point position = furniture.getPosition();
            int width = (int) (furniture.getWidth() * pixelsPerMeter);
            int height = (int) (furniture.getLength() * pixelsPerMeter);

            Rectangle bounds = new Rectangle(position.x, position.y, width, height);
            if (bounds.contains(point)) {
                return furniture;
            }
        }

        return null;
    }
}