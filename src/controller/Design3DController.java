package controller;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import model.Design;
import model.Furniture;
import model.Room;

public class Design3DController {
    private AuthController authController;
    private FurnitureController furnitureController;
    private Design currentDesign;

    // JavaFX 3D components
    private Scene scene;
    private Group root3D;
    private PerspectiveCamera camera;

    public Design3DController(AuthController authController, FurnitureController furnitureController) {
        this.authController = authController;
        this.furnitureController = furnitureController;
    }

    public void setCurrentDesign(Design design) {
        this.currentDesign = design;
    }

    public Design getCurrentDesign() {
        return currentDesign;
    }

    public Scene createScene(double width, double height) {
        root3D = new Group();
        scene = new Scene(root3D, width, height, true, SceneAntialiasing.BALANCED);

        // Set up camera
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-1000);
        camera.setRotationAxis(Rotate.Y_AXIS);
        camera.setRotate(45);
        scene.setCamera(camera);

        // Set a dark gray background
        scene.setFill(javafx.scene.paint.Color.rgb(50, 50, 50));

        return scene;
    }

    public void renderRoom() {
        if (currentDesign == null || root3D == null) {
            return;
        }

        // Clear previous render
        root3D.getChildren().clear();

        Room room = currentDesign.getRoom();

        // Convert AWT colors to JavaFX colors
        java.awt.Color awtFloorColor = room.getFloorColor();
        java.awt.Color awtWallColor = room.getWallColor();

        javafx.scene.paint.Color fxFloorColor = javafx.scene.paint.Color.rgb(
                awtFloorColor.getRed(), awtFloorColor.getGreen(), awtFloorColor.getBlue());
        javafx.scene.paint.Color fxWallColor = javafx.scene.paint.Color.rgb(
                awtWallColor.getRed(), awtWallColor.getGreen(), awtWallColor.getBlue());

        // Create materials
        PhongMaterial floorMaterial = new PhongMaterial();
        floorMaterial.setDiffuseColor(fxFloorColor);

        PhongMaterial wallMaterial = new PhongMaterial();
        wallMaterial.setDiffuseColor(fxWallColor);

        // Scale factors for JavaFX units (100 units = 1 meter)
        double w = room.getWidth() * 100;
        double l = room.getLength() * 100;
        double h = room.getHeight() * 100;

        // Create floor
        Box floor = new Box(w, 5, l);
        floor.setMaterial(floorMaterial);
        floor.setTranslateY(h/2);

        // Create walls
        Box wallNorth = new Box(w, h, 5);
        wallNorth.setMaterial(wallMaterial);
        wallNorth.setTranslateZ(-l/2);

        Box wallSouth = new Box(w, h, 5);
        wallSouth.setMaterial(wallMaterial);
        wallSouth.setTranslateZ(l/2);

        Box wallEast = new Box(5, h, l);
        wallEast.setMaterial(wallMaterial);
        wallEast.setTranslateX(w/2);

        Box wallWest = new Box(5, h, l);
        wallWest.setMaterial(wallMaterial);
        wallWest.setTranslateX(-w/2);

        // Add room elements to scene
        root3D.getChildren().addAll(floor, wallNorth, wallSouth, wallEast, wallWest);

        // Add furniture
        for (Furniture furniture : currentDesign.getFurnitureList()) {
            add3DFurniture(furniture);
        }

        // Add lighting
        addLighting();
    }

    private void add3DFurniture(Furniture furniture) {
        // Convert AWT color to JavaFX color
        java.awt.Color awtColor = furniture.getColor();
        javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(
                awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(fxColor);

        // Scale factors for JavaFX units (100 units = 1 meter)
        double w = furniture.getWidth() * 100;
        double l = furniture.getLength() * 100;
        double h = furniture.getHeight() * 100;

        // Create 3D furniture based on type
        Box furnitureBox = new Box(w, h, l);
        furnitureBox.setMaterial(material);

        // Position the furniture based on 2D coordinates
        // (0,0) in 2D is top-left, but in 3D we want to center the room
        Room room = currentDesign.getRoom();
        double roomWidth = room.getWidth() * 100;
        double roomLength = room.getLength() * 100;

        // Convert 2D position to 3D position
        double x = (furniture.getPosition().getX() / 100 * roomWidth) - (roomWidth / 2);
        double z = (furniture.getPosition().getY() / 100 * roomLength) - (roomLength / 2);

        furnitureBox.setTranslateX(x);
        furnitureBox.setTranslateY(0); // Place on floor
        furnitureBox.setTranslateZ(z);

        // Apply rotation
        Rotate rotate = new Rotate(furniture.getRotation(), Rotate.Y_AXIS);
        furnitureBox.getTransforms().add(rotate);

        root3D.getChildren().add(furnitureBox);
    }

    private void addLighting() {
        // Add ambient light
        javafx.scene.AmbientLight ambientLight = new javafx.scene.AmbientLight();
        ambientLight.setColor(javafx.scene.paint.Color.rgb(200, 200, 200, 0.5));

        // Add point light from above
        javafx.scene.PointLight pointLight = new javafx.scene.PointLight();
        pointLight.setColor(javafx.scene.paint.Color.WHITE);
        pointLight.setTranslateY(-500);

        root3D.getChildren().addAll(ambientLight, pointLight);
    }

    // Camera control methods
    public void rotateCamera(double angle) {
        camera.setRotate(camera.getRotate() + angle);
    }

    public void zoomCamera(double factor) {
        camera.setTranslateZ(camera.getTranslateZ() * factor);
    }

    public void resetCamera() {
        camera.setTranslateZ(-1000);
        camera.setRotate(45);
        camera.setRotationAxis(Rotate.Y_AXIS);
    }
}