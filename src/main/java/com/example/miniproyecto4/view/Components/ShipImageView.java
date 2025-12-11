package com.example.miniproyecto4.view.Components;

import com.example.miniproyecto4.model.Ship.ShipType;
import com.example.miniproyecto4.model.Validation.Orientation;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Component for displaying ship images on the board.
 * Handles image loading, rotation, and scaling based on ship type and orientation.
 */
public class ShipImageView extends StackPane {

    // ========== RUTAS DE LAS IMÁGENES - MODIFICAR AQUÍ ==========
    private static final String CARRIER_IMAGE_PATH = "/com/example/miniproyecto4/Images/carrier.png";
    private static final String SUBMARINE_IMAGE_PATH = "/com/example/miniproyecto4/Images/submarine.png";
    private static final String DESTROYER_IMAGE_PATH = "/com/example/miniproyecto4/Images/destroyer.png";
    private static final String FRIGATE_IMAGE_PATH = "/com/example/miniproyecto4/Images/frigate.png";
    // =============================================================

    private final ImageView imageView;
    private final ShipType shipType;
    private final double cellSize;
    private Orientation currentOrientation;

    /**
     * Constructs a new ShipImageView.
     *
     * @param shipType The type of ship to display
     * @param cellSize The size of each cell
     * @param orientation The initial orientation
     */
    public ShipImageView(ShipType shipType, double cellSize, Orientation orientation) {
        this.shipType = shipType;
        this.cellSize = cellSize;
        this.currentOrientation = orientation;
        this.imageView = new ImageView();

        // Hacer que no interfiera con eventos del mouse
        setPickOnBounds(false);
        setMouseTransparent(true);

        loadAndDisplayImage();
        getChildren().add(imageView);
    }

    /**
     * Loads the appropriate image based on ship type and applies rotation.
     */
    private void loadAndDisplayImage() {
        String imagePath = getImagePath();

        try {
            java.io.InputStream imageStream = getClass().getResourceAsStream(imagePath);

            if (imageStream != null) {
                Image image = new Image(imageStream);
                imageView.setImage(image);
                configureImageView();
            } else {
                createFallbackView();
            }

        } catch (Exception e) {
            System.err.println("Error loading ship image: " + imagePath);
            createFallbackView();
        }
    }

    /**
     * Configures the image view with proper sizing and rotation.
     */
    private void configureImageView() {
        int shipSize = shipType.getSize();

        if (currentOrientation == Orientation.HORIZONTAL) {
            imageView.setFitWidth(cellSize * shipSize);
            imageView.setFitHeight(cellSize);
            imageView.setRotate(0);
        } else {
            imageView.setFitWidth(cellSize);
            imageView.setFitHeight(cellSize * shipSize);
            imageView.setRotate(90);
        }

        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
    }

    /**
     * Gets the image path based on ship type.
     */
    private String getImagePath() {
        switch (shipType) {
            case CARRIER:
                return CARRIER_IMAGE_PATH;
            case SUBMARINE:
                return SUBMARINE_IMAGE_PATH;
            case DESTROYER:
                return DESTROYER_IMAGE_PATH;
            case FRIGATE:
                return FRIGATE_IMAGE_PATH;
            default:
                return CARRIER_IMAGE_PATH;
        }
    }

    /**
     * Creates a fallback visual representation if image loading fails.
     */
    private void createFallbackView() {
        javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle();
        int shipSize = shipType.getSize();

        if (currentOrientation == Orientation.HORIZONTAL) {
            rect.setWidth((cellSize + 2) * shipSize - 2);
            rect.setHeight(cellSize);
        } else {
            rect.setWidth(cellSize);
            rect.setHeight((cellSize + 2) * shipSize - 2);
        }

        rect.setFill(javafx.scene.paint.Color.rgb(100, 100, 100, 0.7));
        rect.setStroke(javafx.scene.paint.Color.BLACK);
        rect.setStrokeWidth(2);
        rect.setArcWidth(8);
        rect.setArcHeight(8);

        getChildren().clear();
        getChildren().add(rect);
    }

    /**
     * Rotates the ship image to the new orientation.
     */
    public void rotate(Orientation newOrientation) {
        this.currentOrientation = newOrientation;
        getChildren().clear();
        this.imageView.setImage(null);
        loadAndDisplayImage();
    }

    /**
     * Sets the opacity of the image for preview effects.
     */
    public void setImageOpacity(double opacity) {
        this.setOpacity(opacity);
    }

    /**
     * Gets the current orientation.
     */
    public Orientation getOrientation() {
        return currentOrientation;
    }
}


