package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private List<Integer> designIds; // Store IDs of designs created by this user

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.designIds = new ArrayList<>();
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Integer> getDesignIds() {
        return designIds;
    }

    public void addDesignId(Integer designId) {
        if (!designIds.contains(designId)) {
            designIds.add(designId);
        }
    }

    public void removeDesignId(Integer designId) {
        designIds.remove(designId);
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", designIds=" + designIds +
                '}';
    }
}