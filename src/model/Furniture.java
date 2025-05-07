package model;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;

public class Furniture implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        CHAIR,
        SOFA,
        TABLE,
        BED,
        CABINET,
        BOOKSHELF
    }

    private int id;
    private Type type;
    private double width;
    private double length;
    private double height;
    private Color color;
    private Point position; // 2D position (x, y)
    private double rotation; // Rotation in degrees

    public Furniture(int id, Type type, double width, double length, double height) {
        this.id = id;
        this.type = type;
        this.width = width;
        this.length = length;
        this.height = height;
        this.color = Color.DARK_GRAY; // Default color
        this.position = new Point(0, 0);
        this.rotation = 0.0;
    }

    // Create standard furniture items
    public static Furniture createStandardChair() {
        return new Furniture(1, Type.CHAIR, 0.5, 0.5, 0.9);
    }

    public static Furniture createStandardTable() {
        return new Furniture(2, Type.TABLE, 1.2, 0.8, 0.75);
    }

    public static Furniture createStandardSofa() {
        return new Furniture(3, Type.SOFA, 2.0, 0.9, 0.9);
    }

    public static Furniture createStandardCabinet() {
        return new Furniture(4, Type.CABINET, 0.6, 0.4, 1.8);
    }

    // Scale furniture
    public void scale(double factor) {
        this.width *= factor;
        this.length *= factor;
        this.height *= factor;
    }

    // Clone method for creating copies
    @Override
    public Furniture clone() {
        try {
            Furniture clone = (Furniture) super.clone();
            clone.position = new Point(position);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning not supported", e);
        }
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    @Override
    public String toString() {
        return type + " (" + width + "m x " + length + "m x " + height + "m)";
    }
}