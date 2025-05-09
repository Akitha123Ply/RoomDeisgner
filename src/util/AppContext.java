package util;

import controller.*;
import javafx.scene.Scene;
import model.Design;

public class AppContext {
    // Controllers
    private final AuthController authController;
    private final DashboardController dashboardController;
    private final RoomController roomController;
    private final Design2DController design2DController;
    private final Design3DController design3DController;
    private final FurnitureController furnitureController;

    // JavaFX manager
    private final JavaFXManager javaFXManager;

    // State for 3D scenes
    private boolean design3DInitialized = false;
    private Scene current3DScene = null;

    public AppContext() {
        // Initialize JavaFX first
        this.javaFXManager = JavaFXManager.getInstance();
        this.javaFXManager.initialize();

        // Initialize controllers
        this.furnitureController = new FurnitureController();
        this.authController = new AuthController();
        this.dashboardController = new DashboardController(authController);
        this.roomController = new RoomController(authController);
        this.design2DController = new Design2DController(authController, furnitureController);
        this.design3DController = new Design3DController(authController, furnitureController);

        System.out.println("[AppContext] Context initialized with all controllers");
    }

    // Getters for controllers
    public AuthController getAuthController() {
        return authController;
    }

    public DashboardController getDashboardController() {
        return dashboardController;
    }

    public RoomController getRoomController() {
        return roomController;
    }

    public Design2DController getDesign2DController() {
        return design2DController;
    }

    public Design3DController getDesign3DController() {
        return design3DController;
    }

    public FurnitureController getFurnitureController() {
        return furnitureController;
    }

    // JavaFX state management
    public JavaFXManager getJavaFXManager() {
        return javaFXManager;
    }

    public boolean isDesign3DInitialized() {
        return design3DInitialized;
    }

    public void setDesign3DInitialized(boolean initialized) {
        this.design3DInitialized = initialized;
    }

    public Scene getCurrent3DScene() {
        return current3DScene;
    }

    public void setCurrent3DScene(Scene scene) {
        this.current3DScene = scene;
    }

    // Method to ensure clean transition between views
    public void prepareForViewChange(String targetView) {
        System.out.println("[AppContext] Preparing for view change to: " + targetView);

        // Save current state if needed
        if (design2DController.isDesignChanged()) {
            System.out.println("[AppContext] Saving design changes before view change");
            design2DController.saveDesign();
        }

        // Specific preparations for each view type
        if ("3D".equals(targetView)) {
            // Ensure design is properly set in 3D controller
            Design currentDesign = design2DController.getCurrentDesign();
            if (currentDesign != null) {
                System.out.println("[AppContext] Setting current design for 3D view: " + currentDesign.getName());
                design3DController.setCurrentDesign(currentDesign);
            }
        }
    }
}