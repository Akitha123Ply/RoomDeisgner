package controller;

import model.Design;
import model.Furniture;
import model.Room;
import model.User;
import util.FileManager;

import java.awt.Point;
import java.util.List;

public class Design2DController {
    private AuthController authController;
    private FurnitureController furnitureController;
    private Design currentDesign;

    public Design2DController(AuthController authController, FurnitureController furnitureController) {
        this.authController = authController;
        this.furnitureController = furnitureController;
    }

    public void setCurrentDesign(Design design) {
        this.currentDesign = design;
    }

    public Design getCurrentDesign() {
        return currentDesign;
    }

    public Design createNewDesign(Room room) {
        User currentUser = authController.getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        // Create a new design with a default name
        String designName = "Design " + (FileManager.loadDesigns().size() + 1);
        Design design = new Design(0, designName, currentUser.getEmail(), room);

        // Save the design
        FileManager.addDesign(design);

        // Add design to user's list
        currentUser.addDesignId(design.getId());

        currentDesign = design;
        return design;
    }

    public void addFurniture(Furniture furniture, Point position) {
        if (currentDesign == null) {
            return;
        }

        // Clone the furniture to avoid modifying the original
        Furniture newFurniture = furniture.clone();
        newFurniture.setPosition(position);

        currentDesign.addFurniture(newFurniture);
        saveDesign();
    }

    public void removeFurniture(Furniture furniture) {
        if (currentDesign == null) {
            return;
        }

        currentDesign.removeFurniture(furniture);
        saveDesign();
    }

    public void moveFurniture(Furniture furniture, Point newPosition) {
        if (currentDesign == null) {
            return;
        }

        furniture.setPosition(newPosition);
        saveDesign();
    }

    public void rotateFurniture(Furniture furniture, double degrees) {
        if (currentDesign == null) {
            return;
        }

        furniture.setRotation(furniture.getRotation() + degrees);
        saveDesign();
    }

    public void changeFurnitureColor(Furniture furniture, java.awt.Color color) {
        if (currentDesign == null) {
            return;
        }

        furniture.setColor(color);
        saveDesign();
    }

    private void saveDesign() {
        if (currentDesign != null) {
            FileManager.updateDesign(currentDesign);
        }
    }
}