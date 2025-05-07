package view;

import controller.AuthController;
import controller.DashboardController;
import controller.RoomController;
import model.Room;
import util.AppContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomCreatorView extends JPanel {
    private AppContext appContext;
    private JFrame parentFrame;

    // UI Components
    private JComboBox<String> shapeComboBox;
    private JTextField widthField;
    private JTextField lengthField;
    private JTextField heightField;
    private JPanel floorColorPanel;
    private JPanel wallColorPanel;
    private Color selectedFloorColor = Color.RED;
    private Color selectedWallColor = Color.CYAN;

    public RoomCreatorView(AppContext appContext, JFrame parentFrame) {
        this.appContext = appContext;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout());

        // Load background image
        ImageIcon backgroundImage = new ImageIcon("src/resources/backgrounds/bg.jpg");

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

        // Create room creator content
        createRoomCreatorContent(backgroundPanel);

        add(backgroundPanel, BorderLayout.CENTER);
    }

    private void createRoomCreatorContent(JPanel backgroundPanel) {
        // Back button (top left)
        JButton backButton = new JButton();
        ImageIcon originalBackIcon = new ImageIcon("src/resources/icons/back.png");
        Image originalBackImage = originalBackIcon.getImage();
        Image resizedBackImage = originalBackImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        backButton.setIcon(new ImageIcon(resizedBackImage));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setBounds(20, 20, 40, 40);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Navigate back to dashboard
                parentFrame.getContentPane().removeAll();
                parentFrame.setContentPane(new DashboardView(appContext, parentFrame));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
        backgroundPanel.add(backButton);

        // Title: Create Room
        JLabel titleLabel = new JLabel("Create Room");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(400, 20, 200, 30);
        backgroundPanel.add(titleLabel);

        // Create a white panel for the room creation form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBounds(parentFrame.getWidth()/2 - 175, 70, 350, 400);

        // Shape dropdown
        JLabel shapeLabel = new JLabel("Shape");
        shapeLabel.setBounds(30, 20, 100, 25);
        formPanel.add(shapeLabel);

        shapeComboBox = new JComboBox<>(new String[]{"Rectangle", "Square", "L-Shaped"});
        shapeComboBox.setBounds(150, 20, 170, 25);
        shapeComboBox.setBackground(Color.WHITE);
        formPanel.add(shapeComboBox);

        // Width field
        JLabel widthLabel = new JLabel("Width");
        widthLabel.setBounds(30, 60, 100, 25);
        formPanel.add(widthLabel);

        widthField = new JTextField("4.0");
        widthField.setBounds(150, 60, 170, 25);
        formPanel.add(widthField);

        // Length field
        JLabel lengthLabel = new JLabel("Length");
        lengthLabel.setBounds(30, 100, 100, 25);
        formPanel.add(lengthLabel);

        lengthField = new JTextField("5.0");
        lengthField.setBounds(150, 100, 170, 25);
        formPanel.add(lengthField);

        // Height field
        JLabel heightLabel = new JLabel("Height");
        heightLabel.setBounds(30, 140, 100, 25);
        formPanel.add(heightLabel);

        heightField = new JTextField("3.0");
        heightField.setBounds(150, 140, 170, 25);
        formPanel.add(heightField);

        // Colors section
        JLabel colorsLabel = new JLabel("Colors");
        colorsLabel.setBounds(30, 180, 100, 25);
        formPanel.add(colorsLabel);

        // Floor color
        JLabel floorLabel = new JLabel("Floor");
        floorLabel.setBounds(80, 210, 50, 25);
        formPanel.add(floorLabel);

        floorColorPanel = new JPanel();
        floorColorPanel.setBackground(selectedFloorColor);
        floorColorPanel.setBounds(150, 210, 30, 30);
        floorColorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        floorColorPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        floorColorPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Color newColor = JColorChooser.showDialog(
                        formPanel,
                        "Choose Floor Color",
                        floorColorPanel.getBackground());
                if (newColor != null) {
                    selectedFloorColor = newColor;
                    floorColorPanel.setBackground(newColor);
                }
            }
        });
        formPanel.add(floorColorPanel);

        // Wall color
        JLabel wallsLabel = new JLabel("Walls");
        wallsLabel.setBounds(80, 250, 50, 25);
        formPanel.add(wallsLabel);

        wallColorPanel = new JPanel();
        wallColorPanel.setBackground(selectedWallColor);
        wallColorPanel.setBounds(150, 250, 30, 30);
        wallColorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        wallColorPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        wallColorPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Color newColor = JColorChooser.showDialog(
                        formPanel,
                        "Choose Wall Color",
                        wallColorPanel.getBackground());
                if (newColor != null) {
                    selectedWallColor = newColor;
                    wallColorPanel.setBackground(newColor);
                }
            }
        });
        formPanel.add(wallColorPanel);

        // Create button
        JButton createButton = new JButton("Create");
        createButton.setBounds(125, 310, 100, 40);
        createButton.setBackground(Color.BLACK);
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Parse dimensions
                    double width = Double.parseDouble(widthField.getText());
                    double length = Double.parseDouble(lengthField.getText());
                    double height = Double.parseDouble(heightField.getText());

                    // Validate dimensions
                    if (width <= 0 || length <= 0 || height <= 0) {
                        JOptionPane.showMessageDialog(parentFrame,
                                "Dimensions must be positive numbers",
                                "Invalid Input",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Get shape
                    Room.Shape shape;
                    switch (shapeComboBox.getSelectedIndex()) {
                        case 0:
                            shape = Room.Shape.RECTANGLE;
                            break;
                        case 1:
                            shape = Room.Shape.SQUARE;
                            break;
                        case 2:
                            shape = Room.Shape.L_SHAPED;
                            break;
                        default:
                            shape = Room.Shape.RECTANGLE;
                    }

                    // Create room
                    Room room = new Room(width, length, height, shape, selectedFloorColor, selectedWallColor);

                    // Navigate to furniture placement view (2D)
                    parentFrame.getContentPane().removeAll();
                    parentFrame.setContentPane(new Design2DView(appContext, parentFrame, room));
                    parentFrame.revalidate();
                    parentFrame.repaint();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Please enter valid numbers for dimensions",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        formPanel.add(createButton);

        backgroundPanel.add(formPanel);
    }
}