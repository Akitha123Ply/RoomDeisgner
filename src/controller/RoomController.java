package controller;

import model.Room;
import model.Design;
import model.User;
import util.FileManager;

public class RoomController {
    private AuthController authController;

    public RoomController(AuthController authController) {
        this.authController = authController;
    }

    public Design createDesign(String name, Room room) {
        User currentUser = authController.getCurrentUser();
        if (currentUser == null) {
            return null;
        }

        // Create new design with the room
        Design design = new Design(0, name, currentUser.getEmail(), room);

        // Save the design
        FileManager.addDesign(design);

        // Add design to user's list
        currentUser.addDesignId(design.getId());

        return design;
    }

    public boolean updateRoom(Room room, Design design) {
        if (design == null) {
            return false;
        }

        design.setRoom(room);
        FileManager.updateDesign(design);
        return true;
    }
}