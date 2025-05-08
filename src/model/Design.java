package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Design implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private Date creationDate;
    private String userEmail; // Store the email of the user who created this design
    private Room room;
    private List<Furniture> furnitureList;

    public Design(int id, String name, String userEmail, Room room) {
        this.id = id;
        this.name = name;
        this.creationDate = new Date(); // Current date
        this.userEmail = userEmail;
        this.room = room;
        this.furnitureList = new ArrayList<>();
    }

    // Furniture management methods
    public void addFurniture(Furniture furniture) {
        furnitureList.add(furniture);
    }

    public boolean removeFurniture(Furniture furniture) {
        furnitureList.remove(furniture);
        return false;
    }

    public void removeFurniture(int index) {
        if (index >= 0 && index < furnitureList.size()) {
            furnitureList.remove(index);
        }
    }

    public Furniture getFurniture(int index) {
        if (index >= 0 && index < furnitureList.size()) {
            return furnitureList.get(index);
        }
        return null;
    }

    // Formatted date for display
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd, MMM yyyy");
        return sdf.format(creationDate);
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<Furniture> getFurnitureList() {
        return furnitureList;
    }

    // In the Design class, make sure the furniture list is properly initialized and managed
    public void setFurnitureList(List<Furniture> furnitureList) {
        if (furnitureList == null) {
            this.furnitureList = new ArrayList<>();
        } else {
            // Create a deep copy to avoid reference issues
            this.furnitureList = new ArrayList<>();
            for (Furniture furniture : furnitureList) {
                if (furniture != null) {
                    this.furnitureList.add(furniture.clone());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Design " + id + "\nRoom: " + room + "\nCreated: " + getFormattedDate();
    }
}