package controller;

import model.User;
import util.FileManager;

import java.util.Map;

public class AuthController {
    private Map<String, User> users;
    private User currentUser;

    public AuthController() {
        // Load users from file
        users = FileManager.loadUsers();
    }

    public boolean login(String email, String password) {
        User user = users.get(email);

        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }

        return false;
    }

    public boolean register(String email, String password) {
        // Check if user already exists
        if (users.containsKey(email)) {
            return false;
        }

        // Create new user
        User newUser = new User(email, password);
        users.put(email, newUser);

        // Save users
        FileManager.saveUsers(users);

        return true;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}