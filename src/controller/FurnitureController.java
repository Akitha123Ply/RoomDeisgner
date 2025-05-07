package controller;

import model.Furniture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FurnitureController {
    private Map<Furniture.Type, List<Furniture>> furnitureLibrary;

    public FurnitureController() {
        initializeFurnitureLibrary();
    }

    private void initializeFurnitureLibrary() {
        furnitureLibrary = new HashMap<>();

        // Create standard furniture items for each type
        List<Furniture> chairs = new ArrayList<>();
        chairs.add(Furniture.createStandardChair());

        List<Furniture> tables = new ArrayList<>();
        tables.add(Furniture.createStandardTable());

        List<Furniture> sofas = new ArrayList<>();
        sofas.add(Furniture.createStandardSofa());

        List<Furniture> cabinets = new ArrayList<>();
        cabinets.add(Furniture.createStandardCabinet());

        // Add more furniture types and items as needed

        // Populate the library
        furnitureLibrary.put(Furniture.Type.CHAIR, chairs);
        furnitureLibrary.put(Furniture.Type.TABLE, tables);
        furnitureLibrary.put(Furniture.Type.SOFA, sofas);
        furnitureLibrary.put(Furniture.Type.CABINET, cabinets);
    }

    public List<Furniture.Type> getAvailableFurnitureTypes() {
        return new ArrayList<>(furnitureLibrary.keySet());
    }

    public List<Furniture> getFurnitureByType(Furniture.Type type) {
        return furnitureLibrary.getOrDefault(type, new ArrayList<>());
    }

    public Furniture getFurnitureById(int id) {
        for (List<Furniture> furnitureList : furnitureLibrary.values()) {
            for (Furniture furniture : furnitureList) {
                if (furniture.getId() == id) {
                    return furniture.clone(); // Return a clone to avoid modifying the original
                }
            }
        }
        return null;
    }
}