# Room Designer Application

A Java application for designing and customizing room layouts with furniture in both 2D and 3D views.

## Setting Up JavaFX 3D in IntelliJ IDEA

### Method 1: Using Maven (Recommended)

1. **Open the project** in IntelliJ IDEA
2. **Ensure Maven is properly configured** in IntelliJ
3. **Add JavaFX dependencies** to your `pom.xml`:

```xml
<dependencies>
    <!-- JavaFX Base -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17.0.2</version>
    </dependency>
    <!-- JavaFX 3D Support -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-graphics</artifactId>
        <version>17.0.2</version>
    </dependency>
</dependencies>
```

4. **Reload Maven** to update dependencies

### Method 2: Using JavaFX SDK

1. **Download JavaFX SDK** from [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/)
2. **Extract the SDK** to a location on your computer
3. **Set up in IntelliJ**:
   - Go to File > Project Structure > Libraries
   - Click + and select "Java"
   - Navigate to your JavaFX SDK's `lib` folder and select it
   - Apply and OK
4. **Configure Run Configuration**:
   - Go to Run > Edit Configurations
   - Select your run configuration (or create a new Application configuration)
   - In the "VM options" field, add:
     ```
     --module-path "C:/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.graphics --enable-native-access=javafx.graphics
     ```
   - Replace the path with your actual JavaFX SDK location
   - Click Apply and OK

5. **Run the application** using the green "Run" button

## Common Issues with JavaFX 3D

If 3D elements are not rendering properly:

1. **Check graphics driver** - Make sure your graphics driver is up to date
2. **Try software rendering** - Add this VM option if hardware rendering fails:
   ```
   -Dprism.forceGPU=false
   ```
3. **Increase memory** - For complex 3D scenes, add:
   ```
   -Xmx2g
   ```

## Important Classes

- `Design2DController` - Manages the 2D design view with undo/redo functionality
- `Design3DView` - Renders the 3D representation of your room design

## Features

- Design rooms in 2D top-down view
- View designs in 3D perspective
- Save and load your designs
- Undo/redo functionality
- Customize furniture placement and properties
