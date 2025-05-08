package util;

import model.Design;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {
    private static final String USERS_FILE_PATH = "data/" + Constants.USERS_FILE;
    private static final String DESIGNS_FILE_PATH = "data/" + Constants.DESIGNS_FILE;

    // Create data directory if it doesn't exist
    static {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
    }

    // User methods
    public static void saveUsers(Map<String, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(USERS_FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, User> loadUsers() {
        File file = new File(USERS_FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(USERS_FILE_PATH))) {
            return (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // Design methods
    public static void saveDesigns(List<Design> designs) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DESIGNS_FILE_PATH))) {
            oos.writeObject(designs);
        } catch (IOException e) {
            System.err.println("Error saving designs: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Design> loadDesigns() {
        File file = new File(DESIGNS_FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DESIGNS_FILE_PATH))) {
            return (List<Design>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading designs: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get designs for a specific user
    public static List<Design> getUserDesigns(String userEmail) {
        List<Design> allDesigns = loadDesigns();
        List<Design> userDesigns = new ArrayList<>();

        for (Design design : allDesigns) {
            if (design.getUserEmail().equals(userEmail)) {
                userDesigns.add(design);
            }
        }

        return userDesigns;
    }

    // Add a new design
    public static void addDesign(Design design) {
        List<Design> designs = loadDesigns();

        // Find the next available ID
        int maxId = 0;
        for (Design d : designs) {
            if (d.getId() > maxId) {
                maxId = d.getId();
            }
        }
        design.setId(maxId + 1);

        designs.add(design);
        saveDesigns(designs);
    }

    // Update an existing design
    public static void updateDesign(Design design) {
        List<Design> designs = loadDesigns();

        for (int i = 0; i < designs.size(); i++) {
            if (designs.get(i).getId() == design.getId()) {
                designs.set(i, design);
                break;
            }
        }

        saveDesigns(designs);
    }

    // Delete a design
    public static void deleteDesign(int designId) {
        List<Design> designs = loadDesigns();
        designs.removeIf(d -> d.getId() == designId);
        saveDesigns(designs);
    }
}
