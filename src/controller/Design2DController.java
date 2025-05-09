package controller;

import model.Design;
import model.Furniture;
import model.Room;
import model.User;
import util.FileManager;

import java.awt.Point;
import java.awt.Color;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

public class Design2DController {
    private AuthController authController;
    private FurnitureController furnitureController;
    private Design currentDesign;
    private boolean designChanged = false;
    private boolean isNewUnsavedDesign = false;

    // For undo/redo functionality
    private Stack<List<Furniture>> undoStack;
    private Stack<List<Furniture>> redoStack;

    public Design2DController(AuthController authController, FurnitureController furnitureController) {
        this.authController = authController;
        this.furnitureController = furnitureController;
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    public void setCurrentDesign(Design design) {
        this.currentDesign = design;
        // Clear undo/redo stacks when setting a new design
        undoStack.clear();
        redoStack.clear();
        // Save the initial state for undo
        saveState();
        // Reset the design changed flag
        this.designChanged = false;
        // Reset the new unsaved design flag if we're loading an existing design
        this.isNewUnsavedDesign = (design.getId() == 0);
    }

    public Design getCurrentDesign() {
        return currentDesign;
    }

    public boolean isDesignChanged() {
        return designChanged;
    }

    public Design createNewDesign(Room room) {
        User currentUser = authController.getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        // Create a new design with a default name
        String designName = "Design " + (FileManager.loadDesigns().size() + 1);

        // Create design but don't save it yet - temporary ID of 0
        Design design = new Design(0, designName, currentUser.getEmail(), room);

        // Set as current design
        currentDesign = design;

        // Initialize undo/redo stacks
        undoStack.clear();
        redoStack.clear();

        // Mark design as changed and new unsaved design
        this.designChanged = true;
        this.isNewUnsavedDesign = true;

        return design;
    }

    private void saveState() {
        if (currentDesign == null) return;

        // Create a deep copy of the current furniture list
        List<Furniture> currentState = new ArrayList<>();
        for (Furniture furniture : currentDesign.getFurnitureList()) {
            currentState.add(furniture.clone());
        }

        undoStack.push(currentState);
        // Clear redo stack when a new action is performed
        redoStack.clear();

        // Mark that the design has been changed but not saved
        this.designChanged = true;
    }

    public void addFurniture(Furniture furniture, Point position) {
        if (currentDesign == null) {
            System.out.println("Cannot add furniture: currentDesign is null");
            return;
        }

        // Clone the furniture to avoid modifying the original
        Furniture newFurniture = furniture.clone();
        newFurniture.setPosition(new Point(position));

        System.out.println("Adding furniture: " + newFurniture.getType() +
                " at position (" + position.x + "," + position.y + ")");

        // Add to the design
        currentDesign.addFurniture(newFurniture);

        // Save the state for undo
        saveState();

        // Set design as changed
        this.designChanged = true;
    }

    public void removeFurniture(Furniture furniture) {
        if (currentDesign == null) {
            return;
        }

        currentDesign.removeFurniture(furniture);

        saveState();
    }

    public void moveFurniture(Furniture furniture, Point newPosition) {
        if (currentDesign == null) {
            return;
        }

        furniture.setPosition(newPosition);

        // We don't save state on every move as it would create too many undo steps
        // Instead, we'll save state when mouse is released

        // Just mark the design as changed
        this.designChanged = true;
    }

    public void changeFurnitureColor(Furniture furniture, Color color) {
        if (currentDesign == null) {
            return;
        }

        furniture.setColor(color);

        saveState();
    }

    /**
     * Explicitly save the current design to the file system.
     * This should be called when the user chooses to save the design.
     */
    public Design saveDesign() {
        if (currentDesign != null) {
            User currentUser = authController.getCurrentUser();

            if (isNewUnsavedDesign) {

                // Create a clean copy of the design to save
                Design designToSave = new Design(
                        0,
                        currentDesign.getName(),
                        currentDesign.getUserEmail(),
                        currentDesign.getRoom()
                );

                // Copy the furniture list (deep copy to prevent reference issues)
                for (Furniture furniture : currentDesign.getFurnitureList()) {
                    designToSave.addFurniture(furniture.clone());
                }

                // Save the design
                FileManager.addDesign(designToSave);

                // Update the current design with the ID that was assigned
                currentDesign.setId(designToSave.getId());

                // Add design to user's list only on first save
                if (currentUser != null) {
                    currentUser.addDesignId(currentDesign.getId());
                }

                isNewUnsavedDesign = false;
            } else {
                // This is an existing design being updated
                FileManager.updateDesign(currentDesign);
            }

            // Reset the design changed flag
            this.designChanged = false;
        }

        return currentDesign;
    }
}