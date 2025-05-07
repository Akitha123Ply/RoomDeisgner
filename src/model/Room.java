package model;

import java.awt.Color;
import java.io.Serializable;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Shape {
        RECTANGLE,
        SQUARE,
        L_SHAPED
    }

    private double width;
    private double length;
    private double height;
    private Shape shape;
    private Color floorColor;
    private Color wallColor;

    public Room(double width, double length, double height, Shape shape, Color floorColor, Color wallColor) {
        this.width = width;
        this.length = length;
        this.height = height;
        this.shape = shape;
        this.floorColor = floorColor;
        this.wallColor = wallColor;
    }

    // Default constructor for empty room
    public Room() {
        this(4.0, 5.0, 3.0, Shape.RECTANGLE, Color.LIGHT_GRAY, Color.WHITE);
    }

    // Calculate room area (simple rectangle for now)
    public double calculateArea() {
        return width * length;
    }

    // Getters and setters
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

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Color getFloorColor() {
        return floorColor;
    }

    public void setFloorColor(Color floorColor) {
        this.floorColor = floorColor;
    }

    public Color getWallColor() {
        return wallColor;
    }

    public void setWallColor(Color wallColor) {
        this.wallColor = wallColor;
    }

    @Override
    public String toString() {
        return width + "m x " + length + "m x " + height + "m";
    }
}