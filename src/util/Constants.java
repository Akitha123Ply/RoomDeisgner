package util;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Constants {
    // Application constants
    public static final String APP_NAME = "Furniture Visualizer";
    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;

    // File paths
    public static final String USERS_FILE = "users.dat";
    public static final String DESIGNS_FILE = "designs.dat";

    // Color constants
    public static final Color PRIMARY_COLOR = new Color(0, 102, 204);    // Blue
    public static final Color SECONDARY_COLOR = new Color(51, 51, 51);   // Dark Gray
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Light Gray
    public static final Color ACCENT_COLOR = new Color(204, 0, 0);       // Red

    // Floor and wall color choices
    public static final List<Color> FLOOR_COLORS = Arrays.asList(
            new Color(209, 190, 168),  // Light wood
            new Color(101, 67, 33),    // Dark wood
            new Color(169, 169, 169),  // Gray
            new Color(210, 180, 140),  // Tan
            new Color(245, 245, 220),  // Beige
            new Color(255, 0, 0)       // Red
    );

    public static final List<Color> WALL_COLORS = Arrays.asList(
            Color.WHITE,
            new Color(245, 245, 220),  // Beige
            new Color(230, 230, 250),  // Lavender
            new Color(173, 216, 230),  // Light blue
            new Color(144, 238, 144),  // Light green
            new Color(255, 182, 193)   // Light pink
    );

    // Furniture dimensions (in meters)
    public static final double CHAIR_WIDTH = 0.5;
    public static final double CHAIR_LENGTH = 0.5;
    public static final double CHAIR_HEIGHT = 0.9;

    public static final double TABLE_WIDTH = 1.2;
    public static final double TABLE_LENGTH = 0.8;
    public static final double TABLE_HEIGHT = 0.75;

    public static final double SOFA_WIDTH = 2.0;
    public static final double SOFA_LENGTH = 0.9;
    public static final double SOFA_HEIGHT = 0.9;

    public static final double CABINET_WIDTH = 0.6;
    public static final double CABINET_LENGTH = 0.4;
    public static final double CABINET_HEIGHT = 1.8;

    // Default room dimensions
    public static final double DEFAULT_ROOM_WIDTH = 4.0;
    public static final double DEFAULT_ROOM_LENGTH = 5.0;
    public static final double DEFAULT_ROOM_HEIGHT = 3.0;

    // UI constants
    public static final int PADDING = 20;
    public static final int BUTTON_WIDTH = 120;
    public static final int BUTTON_HEIGHT = 40;
    public static final int TEXT_FIELD_WIDTH = 200;
    public static final int TEXT_FIELD_HEIGHT = 30;
}