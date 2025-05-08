package view;

import controller.Design2DController;
import model.Design;
import model.Furniture;
import model.Room;
import util.AppContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
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
    private JButton addButton;

    private Furniture selectedFurniture;
    private Furniture draggingFurniture;
    private Point dragStartPoint;
    private Color selectedColor = Color.YELLOW; // Default color changed to yellow to match your screenshot

    // For undo/redo functionality
    private List<List<Furniture>> undoHistory = new ArrayList<>();
    private List<List<Furniture>> redoHistory = new ArrayList<>();
    private int currentHistoryIndex = -1;

    // Add to the Design2DView constructor to debug furniture loading
    public Design2DView(AppContext appContext, JFrame parentFrame, Room room, Design existingDesign) {
        this.appContext = appContext;
        this.parentFrame = parentFrame;
        this.room = room;

        // Create or load a design with this room
        if (existingDesign != null) {
            System.out.println("Loading existing design: " + existingDesign.getId()
                    + ", Name: " + existingDesign.getName());

            if (existingDesign.getFurnitureList() != null) {
                System.out.println("Design has " + existingDesign.getFurnitureList().size()
                        + " furniture items");
                for (Furniture f : existingDesign.getFurnitureList()) {
                    System.out.println("  - " + f.getType() + " at " + f.getPosition());
                }
            } else {
                System.out.println("WARNING: Existing design has null furniture list!");
            }

            // Use the existing design
            design = existingDesign;
        } else {
            System.out.println("Creating new design for room");
            // Create a new design
            design = appContext.getDesign2DController().createNewDesign(room);
        }

        // Set current design in controller
        appContext.getDesign2DController().setCurrentDesign(design);

        setLayout(new BorderLayout());

        // Create UI components
        createDesign2DUI();

        // Initialize history with current state
        saveHistory();

        // Force a repaint immediately after initialization
        SwingUtilities.invokeLater(() -> {
            if (roomPanel != null) {
                System.out.println("Forcing repaint of roomPanel");
                roomPanel.repaint();
            }
        });
    }

    // Add an overloaded constructor to maintain backward compatibility
    public Design2DView(AppContext appContext, JFrame parentFrame, Room room) {
        this(appContext, parentFrame, room, null);
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

        // Center panel for room visualization - wrapped in a container to center it
        roomPanel = createRoomPanel();
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.setBackground(Color.LIGHT_GRAY);
        centeringPanel.add(roomPanel); // This will center the roomPanel in the available space

        JScrollPane scrollPane = new JScrollPane(centeringPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

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
        view3DButton.setBorderPainted(false);
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
        JLabel roomInfoLabel = new JLabel("Room Size: " + room.getWidth() + "m x " +
                room.getLength() + "m x " +
                room.getHeight() + "m");
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
        colorPanel.setPreferredSize(new Dimension(180, 50));
        colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        colorPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        colorPanel.setMaximumSize(new Dimension(180, 50));
        colorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

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
                        selectedFurniture.setColor(newColor); // Update the selected furniture's color
                        appContext.getDesign2DController().changeFurnitureColor(selectedFurniture, newColor);
                        roomPanel.repaint();
                        saveHistory();
                    }
                }
            }
        });

        // Selected furniture panel
        selectedFurniturePanel = new JPanel();
        selectedFurniturePanel.setBackground(Color.WHITE);
        selectedFurniturePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        selectedFurniturePanel.setPreferredSize(new Dimension(180, 80));
        selectedFurniturePanel.setMaximumSize(new Dimension(180, 80));
        selectedFurniturePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add button
        addButton = new JButton("Add");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setBackground(Color.BLACK);
        addButton.setForeground(Color.white);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setEnabled(false); // Disabled until a furniture is selected
        addButton.setMaximumSize(new Dimension(100, 30));

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFurniture != null) {
                    // Place furniture in center of the room
                    int roomWidth = roomPanel.getWidth();
                    int roomHeight = roomPanel.getHeight();

                    Point center = new Point(roomWidth / 2, roomHeight / 2);

                    // Adjust to center the furniture
                    int pixelsPerMeter = 100; // Use consistent scaling
                    int furnitureWidth = (int) (selectedFurniture.getWidth() * pixelsPerMeter);
                    int furnitureLength = (int) (selectedFurniture.getLength() * pixelsPerMeter);
                    center.x -= furnitureWidth / 2;
                    center.y -= furnitureLength / 2;

                    // Clone the furniture and set its color to the selected color
                    Furniture newFurniture = selectedFurniture.clone();
                    newFurniture.setColor(selectedColor);

                    appContext.getDesign2DController().addFurniture(newFurniture, center);
                    roomPanel.repaint();
                    saveHistory();
                }
            }
        });

        // Control buttons
        JPanel controlButtonsPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        controlButtonsPanel.setOpaque(false);
        controlButtonsPanel.setMaximumSize(new Dimension(180, 30));
        controlButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton saveButton = createIconButton("save.png", "Save");
        saveButton.addActionListener(e -> {
            try {
                Design currentDesign = appContext.getDesign2DController().getCurrentDesign();
                if (currentDesign != null) {
                    // Prompt for a new name
                    String newName = JOptionPane.showInputDialog(this,
                            "Enter a name for this design:",
                            currentDesign.getName());

                    // If user cancels, do nothing
                    if (newName == null) {
                        return;
                    }

                    // Check if name is empty
                    if (newName.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "Design name cannot be empty.",
                                "Invalid Name",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Update the design name and save
                    currentDesign.setName(newName);
                    appContext.getDesign2DController().saveDesign();

                    JOptionPane.showMessageDialog(this,
                            "Design \"" + newName + "\" saved successfully!",
                            "Save Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // If no design is loaded, show error
                    JOptionPane.showMessageDialog(this,
                            "No design to save.",
                            "Save Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving design: " + ex.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JButton undoButton = createIconButton("undo.png", "Undo");
        undoButton.addActionListener(e -> {
            undo();
            roomPanel.repaint();
        });

        JButton redoButton = createIconButton("reset.png", "Redo");
        redoButton.addActionListener(e -> {
            redo();
            roomPanel.repaint();
        });

        controlButtonsPanel.add(saveButton);
        controlButtonsPanel.add(undoButton);
        controlButtonsPanel.add(redoButton);

        // Room information at bottom
        JLabel roomSizeLabel = new JLabel("Room Size:");
        roomSizeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        roomSizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roomDimensionsLabel = new JLabel(room.getWidth() + "m x " +
                room.getLength() + "m x " +
                room.getHeight() + "m");
        roomDimensionsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        roomDimensionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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
                    furniture.setColor(selectedColor); // Use the current selected color

                    // Update selected furniture
                    selectedFurniture = furniture;

                    // Enable the add button
                    addButton.setEnabled(true);

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
                Graphics2D g2d = (Graphics2D) g;

                // Enable anti-aliasing for smoother rendering
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Calculate the room dimensions in pixels
                // Maintain aspect ratio of the room
                double roomWidthMeters = room.getWidth();
                double roomLengthMeters = room.getLength();

                int pixelsPerMeter = 100;  // Fixed scale: 1 meter = 100 pixels
                int roomWidthPixels = (int)(roomWidthMeters * pixelsPerMeter);
                int roomLengthPixels = (int)(roomLengthMeters * pixelsPerMeter);

                // Draw room background (floor)
                g2d.setColor(room.getFloorColor());
                g2d.fillRect(0, 0, roomWidthPixels, roomLengthPixels);

                // Draw room walls
                g2d.setColor(room.getWallColor());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(0, 0, roomWidthPixels - 1, roomLengthPixels - 1);

                // Draw furniture
                if (design != null && design.getFurnitureList() != null) {
                    System.out.println("Drawing " + design.getFurnitureList().size() + " furniture items");

                    // Draw furniture
                    for (Furniture furniture : design.getFurnitureList()) {
                        drawFurniture(g2d, furniture, pixelsPerMeter);
                    }
                } else {
                    System.out.println("No furniture to draw - design or furniture list is null");
                }

                // Draw selected furniture if being dragged
                if (draggingFurniture != null) {
                    drawFurniture(g2d, draggingFurniture, pixelsPerMeter);
                }
            }

            @Override
            public Dimension getPreferredSize() {
                // Set panel size based on room dimensions with consistent scale
                int pixelsPerMeter = 100;
                return new Dimension(
                        (int)(room.getWidth() * pixelsPerMeter),
                        (int)(room.getLength() * pixelsPerMeter)
                );
            }
        };

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
                } else if (selectedFurniture != null && addButton.isEnabled()) {
                    // Add new furniture at click position
                    Furniture newFurniture = selectedFurniture.clone();
                    newFurniture.setColor(selectedColor); // Ensure correct color is set

                    // Adjust position to center furniture at click point
                    int pixelsPerMeter = 100;
                    int furnitureWidth = (int) (newFurniture.getWidth() * pixelsPerMeter);
                    int furnitureLength = (int) (newFurniture.getLength() * pixelsPerMeter);

                    Point adjustedPoint = new Point(
                            clickPoint.x - furnitureWidth / 2,
                            clickPoint.y - furnitureLength / 2);

                    appContext.getDesign2DController().addFurniture(newFurniture, adjustedPoint);
                    panel.repaint();
                    saveHistory();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggingFurniture != null) {
                    // End dragging
                    draggingFurniture = null;
                    dragStartPoint = null;
                    panel.repaint();
                    saveHistory();
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

                    // Keep furniture within room boundaries
                    int pixelsPerMeter = 100;
                    int roomWidth = (int)(room.getWidth() * pixelsPerMeter);
                    int roomHeight = (int)(room.getLength() * pixelsPerMeter);
                    int furnitureWidth = (int)(draggingFurniture.getWidth() * pixelsPerMeter);
                    int furnitureHeight = (int)(draggingFurniture.getLength() * pixelsPerMeter);

                    newPoint.x = Math.max(0, Math.min(newPoint.x, roomWidth - furnitureWidth));
                    newPoint.y = Math.max(0, Math.min(newPoint.y, roomHeight - furnitureHeight));

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

    // Update the drawFurniture method in Design2DView to ensure proper rendering
    private void drawFurniture(Graphics2D g2d, Furniture furniture, int pixelsPerMeter) {
        // Log furniture details for debugging
        System.out.println("Drawing furniture: " + furniture.getType()
                + " at position (" + furniture.getPosition().x
                + "," + furniture.getPosition().y
                + ") with color " + furniture.getColor());

        // Save the original transform
        AffineTransform oldTransform = g2d.getTransform();

        int width = (int) (furniture.getWidth() * pixelsPerMeter);
        int height = (int) (furniture.getLength() * pixelsPerMeter);
        Point position = furniture.getPosition();

        // Check if position is null (defensive programming)
        if (position == null) {
            System.out.println("Error: Furniture position is null!");
            position = new Point(0, 0);
        }

        // Create transform for rotation (around center of furniture)
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(furniture.getRotation()),
                position.x + width/2,
                position.y + height/2);
        g2d.setTransform(transform);

        // Draw the furniture
        g2d.setColor(furniture.getColor());
        g2d.fillRect(position.x, position.y, width, height);

        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(position.x, position.y, width, height);

        // Draw label on furniture
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        String label = getFurnitureTypeLabel(furniture.getType());
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(label);
        int textHeight = fm.getHeight();

        // Center the text on the furniture
        int textX = position.x + (width - textWidth) / 2;
        int textY = position.y + (height + textHeight) / 2 - 5;
        g2d.drawString(label, textX, textY);

        // Restore original transform
        g2d.setTransform(oldTransform);
    }

    private String getFurnitureTypeLabel(Furniture.Type type) {
        switch (type) {
            case CHAIR: return "Chair";
            case TABLE: return "Table";
            case SOFA: return "Sofa";
            case CABINET: return "Cabinet";
            case BED: return "Bed";
            case BOOKSHELF: return "Shelf";
            default: return "Item";
        }
    }

    private Furniture getFurnitureAtPoint(Point point) {
        if (design == null) {
            return null;
        }

        int pixelsPerMeter = 100;

        for (Furniture furniture : design.getFurnitureList()) {
            Point position = furniture.getPosition();
            int width = (int) (furniture.getWidth() * pixelsPerMeter);
            int height = (int) (furniture.getLength() * pixelsPerMeter);

            // Using rectangle bounds for simplicity (ignoring rotation for now)
            Rectangle bounds = new Rectangle(position.x, position.y, width, height);

            if (bounds.contains(point)) {
                return furniture;
            }
        }

        return null;
    }

    // Save current state to history for undo/redo
    private void saveHistory() {
        if (design == null) return;

        // Create a deep copy of the current furniture list
        List<Furniture> currentState = new ArrayList<>();
        for (Furniture furniture : design.getFurnitureList()) {
            currentState.add(furniture.clone());
        }

        // If we're in the middle of the history, remove everything after current index
        if (currentHistoryIndex < undoHistory.size() - 1) {
            undoHistory = new ArrayList<>(undoHistory.subList(0, currentHistoryIndex + 1));
            redoHistory.clear();
        }

        // Add current state to history
        undoHistory.add(currentState);
        currentHistoryIndex = undoHistory.size() - 1;
    }

    private void undo() {
        if (currentHistoryIndex <= 0 || undoHistory.isEmpty()) return;

        // Save current state to redo
        currentHistoryIndex--;

        // Restore previous state
        List<Furniture> previousState = undoHistory.get(currentHistoryIndex);

        // Update design with previous state
        design.setFurnitureList(new ArrayList<>());
        for (Furniture furniture : previousState) {
            design.addFurniture(furniture.clone());
        }
    }

    private void redo() {
        if (currentHistoryIndex >= undoHistory.size() - 1) return;

        // Move to next state
        currentHistoryIndex++;

        // Restore next state
        List<Furniture> nextState = undoHistory.get(currentHistoryIndex);

        // Update design with next state
        design.setFurnitureList(new ArrayList<>());
        for (Furniture furniture : nextState) {
            design.addFurniture(furniture.clone());
        }
    }
}