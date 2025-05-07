package util;

import controller.AuthController;
import controller.DashboardController;
import controller.RoomController;
import controller.Design2DController;
import controller.Design3DController;
import controller.FurnitureController;

/**
 * A context class to hold all controllers
 * This way we only need to pass one object to views
 */
public class AppContext {
    private AuthController authController;
    private DashboardController dashboardController;
    private RoomController roomController;
    private Design2DController design2DController;
    private Design3DController design3DController;
    private FurnitureController furnitureController;

    public AppContext() {
        // Initialize controllers
        this.authController = new AuthController();
        this.furnitureController = new FurnitureController(); // Initialize this first
        this.dashboardController = new DashboardController(authController);
        this.roomController = new RoomController(authController);
        this.design2DController = new Design2DController(authController, furnitureController);
        this.design3DController = new Design3DController(authController, furnitureController);
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
}