package controller;

import model.Design;
import model.User;
import util.FileManager;

import java.util.List;

public class DashboardController {
    private AuthController authController;

    public DashboardController(AuthController authController) {
        this.authController = authController;
    }

    public List<Design> getUserDesigns() {
        User currentUser = authController.getCurrentUser();
        if (currentUser == null) {
            return List.of(); // Empty list if no user is logged in
        }

        return FileManager.getUserDesigns(currentUser.getEmail());
    }

    public void deleteDesign(int designId) {
        FileManager.deleteDesign(designId);

        // Update user's design list
        User currentUser = authController.getCurrentUser();
        if (currentUser != null) {
            currentUser.removeDesignId(designId);
        }
    }

    public void createNewDesign() {
        // Will be implemented when Room Creator is added
    }

    public Design loadDesign(int designId) {
        List<Design> designs = FileManager.loadDesigns();

        for (Design design : designs) {
            if (design.getId() == designId) {
                return design;
            }
        }

        return null;
    }
}