// Design3DController.java - Complete modified class
package controller;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.AmbientLight;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import model.Design;
import model.Furniture;
import model.Room;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Design3DController {
    private AuthController authController;
    private FurnitureController furnitureController;
    private Design currentDesign;

    // JavaFX 3D components
    private Scene scene;
    private Group root3D;
    private Group contentGroup; // Group that will be rotated
    private Group roomGroup;
    private Group furnitureGroup;
    private PerspectiveCamera camera;

    // Rotation and transformation objects
    private Rotate rotateX;
    private Rotate rotateY;
    private double currentRotateX = 30; // Initial X rotation
    private double currentRotateY = 0; // Initial Y rotation

    // Store room components for visibility toggling
    private Map<String, Box> roomComponents = new HashMap<>();

    // Wall visibility settings
    private boolean showFrontWall = false; // Default: hidden for better view
    private boolean showBackWall = true;
    private boolean showLeftWall = false; // Default: hidden for better view
    private boolean showRightWall = true;
    private boolean showCeiling = false; // Default: hidden for better view
    private boolean showFloor = true; // Floor should always be visible by default

    public Design3DController(AuthController authController, FurnitureController furnitureController) {
        this.authController = authController;
        this.furnitureController = furnitureController;
    }

    public void setCurrentDesign(Design design) {
        this.currentDesign = design;
        System.out.println("Design3DController: Setting current design to " +
                (design != null ? design.getName() : "null"));
    }

    public Design getCurrentDesign() {
        return currentDesign;
    }

    public Scene createScene(double width, double height) {
        System.out.println("Creating new 3D scene with dimensions: " + width + "x" + height);

        // Clear any previous scene elements
        if (root3D != null) {
            root3D.getChildren().clear();
        }

        // Create main container
        root3D = new Group();

        // Create content group for rotation
        contentGroup = new Group();

        // Create groups for room and furniture
        roomGroup = new Group();
        furnitureGroup = new Group();

        // Create rotation transforms
        rotateX = new Rotate(currentRotateX, Rotate.X_AXIS);
        rotateY = new Rotate(currentRotateY, Rotate.Y_AXIS);

        // Apply rotation transforms to the content group
        contentGroup.getTransforms().addAll(rotateX, rotateY);

        // Add room and furniture groups to content group
        contentGroup.getChildren().addAll(roomGroup, furnitureGroup);

        // Set up camera
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-1000); // Position camera back from the scene

        // Set a dark gray background
        scene = new Scene(root3D, width, height, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.rgb(50, 50, 50));
        scene.setCamera(camera);

        // Add lighting
        setupLighting();

        // Add content group to root
        root3D.getChildren().add(contentGroup);

        System.out.println("3D Scene created with initial rotation: X=" +
                currentRotateX + ", Y=" + currentRotateY);
        return scene;
    }

    private void setupLighting() {
        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.setOpacity(0.5);

        // Add point lights from different directions for better illumination
        PointLight pointLight1 = new PointLight(Color.WHITE);
        pointLight1.setTranslateX(400);
        pointLight1.setTranslateY(-400);
        pointLight1.setTranslateZ(-400);

        PointLight pointLight2 = new PointLight(Color.WHITE);
        pointLight2.setTranslateX(-400);
        pointLight2.setTranslateY(-200);
        pointLight2.setTranslateZ(400);

        // Add lighting to the root
        root3D.getChildren().addAll(ambientLight, pointLight1, pointLight2);
    }

    public void renderRoom() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::renderRoom);
            return;
        }

        if (currentDesign == null) {
            System.err.println("Error in renderRoom: currentDesign is null");
            return;
        }

        if (roomGroup == null || furnitureGroup == null) {
            System.err.println("Error in renderRoom: roomGroup or furnitureGroup is null");
            return;
        }

        try {
            // Clear previous render
            System.out.println("Starting room rendering...");

            // Clear previous render
            roomGroup.getChildren().clear();
            furnitureGroup.getChildren().clear();
            roomComponents.clear();

            Room room = currentDesign.getRoom();
            if (room == null) {
                System.err.println("Error in renderRoom: Room is null in the design");
                return;
            }

            System.out.println("Rendering room: " + room.getWidth() + "x" +
                    room.getLength() + "x" + room.getHeight());

            // Convert AWT colors to JavaFX colors
            java.awt.Color awtFloorColor = room.getFloorColor();
            java.awt.Color awtWallColor = room.getWallColor();

            Color fxFloorColor = Color.rgb(
                    awtFloorColor.getRed(),
                    awtFloorColor.getGreen(),
                    awtFloorColor.getBlue());

            Color fxWallColor = Color.rgb(
                    awtWallColor.getRed(),
                    awtWallColor.getGreen(),
                    awtWallColor.getBlue());

            // Create materials
            PhongMaterial floorMaterial = new PhongMaterial();
            floorMaterial.setDiffuseColor(fxFloorColor);
            floorMaterial.setSpecularColor(Color.WHITE);
            floorMaterial.setSpecularPower(10);

            PhongMaterial wallMaterial = new PhongMaterial();
            wallMaterial.setDiffuseColor(fxWallColor);
            wallMaterial.setSpecularColor(Color.WHITE);
            wallMaterial.setSpecularPower(5);

            // Scale factors for JavaFX units (100 units = 1 meter)
            double w = Math.max(0.1, room.getWidth()) * 100;
            double l = Math.max(0.1, room.getLength()) * 100;
            double h = Math.max(0.1, room.getHeight()) * 100;

            // Create floor
            Box floor = new Box(w, 5, l);
            floor.setMaterial(floorMaterial);
            floor.setTranslateY(h/2); // Place at bottom of room
            floor.setVisible(showFloor);
            roomComponents.put("floor", floor);

            // Create ceiling
            Box ceiling = new Box(w, 5, l);
            ceiling.setMaterial(wallMaterial);
            ceiling.setTranslateY(-h/2); // Place at top of room
            ceiling.setVisible(showCeiling);
            roomComponents.put("ceiling", ceiling);

            // Create walls
            Box frontWall = new Box(w, h, 5);
            frontWall.setMaterial(wallMaterial);
            frontWall.setTranslateZ(-l/2);
            frontWall.setVisible(showFrontWall);
            roomComponents.put("frontWall", frontWall);

            Box backWall = new Box(w, h, 5);
            backWall.setMaterial(wallMaterial);
            backWall.setTranslateZ(l/2);
            backWall.setVisible(showBackWall);
            roomComponents.put("backWall", backWall);

            Box rightWall = new Box(5, h, l);
            rightWall.setMaterial(wallMaterial);
            rightWall.setTranslateX(w/2);
            rightWall.setVisible(showRightWall);
            roomComponents.put("rightWall", rightWall);

            Box leftWall = new Box(5, h, l);
            leftWall.setMaterial(wallMaterial);
            leftWall.setTranslateX(-w/2);
            leftWall.setVisible(showLeftWall);
            roomComponents.put("leftWall", leftWall);

            // Add room elements to scene
            roomGroup.getChildren().addAll(floor, ceiling, frontWall, backWall, rightWall, leftWall);

            // Add furniture if any exists
            if (currentDesign.getFurnitureList() != null) {
                System.out.println("Adding " + currentDesign.getFurnitureList().size() +
                        " furniture items to 3D scene");

                for (Furniture furniture : currentDesign.getFurnitureList()) {
                    if (furniture != null) {
                        add3DFurniture(furniture);
                    }
                }
            } else {
                System.out.println("No furniture to add to 3D scene (furniture list is null)");
            }

            System.out.println("Room rendered with " + roomGroup.getChildren().size() +
                    " room elements and " + furnitureGroup.getChildren().size() +
                    " furniture elements");
        } catch (Exception e) {
            System.err.println("Error in renderRoom: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void add3DFurniture(Furniture furniture) {
        try {
            // Convert AWT color to JavaFX color
            java.awt.Color awtColor = furniture.getColor();
            Color fxColor = Color.rgb(
                    awtColor.getRed(),
                    awtColor.getGreen(),
                    awtColor.getBlue());

            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(fxColor);
            material.setSpecularColor(Color.WHITE);
            material.setSpecularPower(32.0);

            // Scale factors for JavaFX units (100 units = 1 meter)
            double w = Math.max(0.1, furniture.getWidth()) * 100;
            double l = Math.max(0.1, furniture.getLength()) * 100;
            double h = Math.max(0.1, furniture.getHeight()) * 100;

            // Create 3D furniture based on type
            Box furnitureBox = new Box(w, h, l);
            furnitureBox.setMaterial(material);

            // Position the furniture based on 2D coordinates
            Room room = currentDesign.getRoom();
            double roomWidth = room.getWidth() * 100;
            double roomLength = room.getLength() * 100;

            // Get position from the furniture object
            Point pos = furniture.getPosition();
            if (pos == null) {
                System.out.println("Warning: Furniture position is null, using default position");
                pos = new Point(0, 0);
            }

            // Convert from pixel position to 3D coordinate system
            double x = (pos.getX() / 100.0) * 100 - (roomWidth / 2);
            double z = (pos.getY() / 100.0) * 100 - (roomLength / 2);
            double y = (room.getHeight() * 100 / 2) - (h / 2); // Place on floor

            System.out.println("Adding 3D furniture: " + furniture.getType() +
                    " at (" + x + ", " + y + ", " + z + ")");

            // Set position
            furnitureBox.setTranslateX(x);
            furnitureBox.setTranslateY(y);
            furnitureBox.setTranslateZ(z);

            // Apply rotation around Y axis (vertical)
            Rotate rotation = new Rotate(furniture.getRotation(), Rotate.Y_AXIS);
            furnitureBox.getTransforms().add(rotation);

            // Add to scene
            furnitureGroup.getChildren().add(furnitureBox);
        } catch (Exception e) {
            System.err.println("Error adding furniture: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Wall visibility control methods
    public void setWallVisibility(String wall, boolean visible) {
        switch (wall.toLowerCase()) {
            case "front":
                showFrontWall = visible;
                break;
            case "back":
                showBackWall = visible;
                break;
            case "left":
                showLeftWall = visible;
                break;
            case "right":
                showRightWall = visible;
                break;
            case "ceiling":
                showCeiling = visible;
                break;
            case "floor":
                showFloor = visible;
                break;
        }

        // Update wall visibility if already rendered
        if (wall.equalsIgnoreCase("front") && roomComponents.get("frontWall") != null) {
            roomComponents.get("frontWall").setVisible(visible);
        } else if (wall.equalsIgnoreCase("back") && roomComponents.get("backWall") != null) {
            roomComponents.get("backWall").setVisible(visible);
        } else if (wall.equalsIgnoreCase("left") && roomComponents.get("leftWall") != null) {
            roomComponents.get("leftWall").setVisible(visible);
        } else if (wall.equalsIgnoreCase("right") && roomComponents.get("rightWall") != null) {
            roomComponents.get("rightWall").setVisible(visible);
        } else if (wall.equalsIgnoreCase("ceiling") && roomComponents.get("ceiling") != null) {
            roomComponents.get("ceiling").setVisible(visible);
        } else if (wall.equalsIgnoreCase("floor") && roomComponents.get("floor") != null) {
            roomComponents.get("floor").setVisible(visible);
        }

        System.out.println("Wall visibility updated: " + wall + " = " + visible);
    }

    // Method to check if a wall is visible
    public boolean isWallVisible(String wall) {
        switch (wall.toLowerCase()) {
            case "front": return showFrontWall;
            case "back": return showBackWall;
            case "left": return showLeftWall;
            case "right": return showRightWall;
            case "ceiling": return showCeiling;
            case "floor": return showFloor;
            default: return false;
        }
    }

    // Camera control methods
    public void rotateCamera(double angle) {
        currentRotateY += angle;
        rotateY.setAngle(currentRotateY);
        System.out.println("Rotating Y to: " + currentRotateY);
    }

    public void tiltCamera(double angle) {
        currentRotateX += angle;
        // Limit tilt to avoid flipping
        if (currentRotateX < -60) currentRotateX = -60;
        if (currentRotateX > 60) currentRotateX = 60;
        rotateX.setAngle(currentRotateX);
        System.out.println("Rotating X to: " + currentRotateX);
    }

    public void zoomCamera(double factor) {
        // Adjust camera distance
        double newZ = camera.getTranslateZ() * factor;
        // Limit zoom range
        if (newZ > -3000 && newZ < -100) {
            camera.setTranslateZ(newZ);
            System.out.println("Zooming camera to: " + newZ);
        }
    }

    public void resetCamera() {
        currentRotateX = 30;
        currentRotateY = 0;
        rotateX.setAngle(currentRotateX);
        rotateY.setAngle(currentRotateY);
        camera.setTranslateZ(-1000);
        System.out.println("Camera reset to initial position");
    }
}
