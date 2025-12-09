package com.example.miniproyecto4.view.Components;

import com.example.miniproyecto4.view.utils.Colors;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Visual component representing an individual cell on the game board.
 * Displays different states: empty, ship, hit, miss, and sunk.
 * Handles mouse events for user interaction and displays placement previews.
 * Extends StackPane to layer multiple visual elements.
 */
public class CellView extends StackPane {

    /**
     * The background rectangle of the cell.
     */
    private final Rectangle background;

    /**
     * The size of the cell in pixels (width and height).
     */
    private final double size;

    /**
     * The current state of the cell (EMPTY, SHIP, HIT, MISS, SUNK).
     */
    private String currentState;

    /**
     * Flag indicating whether a preview is currently active.
     */
    private boolean isPreviewActive;

    /**
     * The original color of the cell before any preview or hover effects.
     */
    private String originalColor;

    /**
     * Constructs a CellView with the specified size.
     * Initializes the cell in empty state with water color and sets up mouse event handlers.
     *
     * @param size the size in pixels of the cell (width and height)
     */
    public CellView(double size) {
        this.size = size;
        this.currentState = "EMPTY";
        this.isPreviewActive = false;

        background = new Rectangle(size, size);
        background.setFill(Color.web(Colors.WATER));
        background.setStroke(Color.web(Colors.GRID));
        background.setStrokeWidth(1);

        originalColor = Colors.WATER;

        getChildren().add(background);

        setOnMouseEntered(e -> {
            if (currentState.equals("EMPTY") && !isPreviewActive) {
                background.setFill(Color.web(Colors.HOVER));
            }
        });

        setOnMouseExited(e -> {
            if (currentState.equals("EMPTY") && !isPreviewActive) {
                background.setFill(Color.web(originalColor));
            }
        });
    }

    /**
     * Sets the fill color of the cell's background.
     *
     * @param color the color in hexadecimal format
     */
    public void setFill(String color) {
        background.setFill(Color.web(color));
        originalColor = color;
    }

    /**
     * Displays a visual preview of where a ship will be placed.
     * Shows green for valid placement or red for invalid placement.
     *
     * @param isValid true if the position is valid (green/dark), false if invalid (red)
     */
    public void showPreview(boolean isValid) {
        if (currentState.equals("EMPTY")) {
            isPreviewActive = true;
            if (isValid) {
                background.setFill(Color.web("#5A9F5A"));
                background.setOpacity(0.7);
            } else {
                background.setFill(Color.web("#E74C3C"));
                background.setOpacity(0.5);
            }
        }
    }

    /**
     * Clears the visual preview and restores the original color.
     */
    public void clearPreview() {
        if (isPreviewActive && currentState.equals("EMPTY")) {
            isPreviewActive = false;
            background.setFill(Color.web(originalColor));
            background.setOpacity(1.0);
        }
    }

    /**
     * Marks the cell as hit.
     * Changes the color and adds a visual hit marker.
     */
    public void markAsHit() {
        currentState = "HIT";
        isPreviewActive = false;
        background.setFill(Color.web(Colors.HIT));
        background.setOpacity(1.0);
        originalColor = Colors.HIT;

        Circle hitMarker = new Circle(size / 4);
        hitMarker.setFill(Color.web(Colors.HIT_MARKER));
        getChildren().add(hitMarker);
    }

    /**
     * Marks the cell as water (missed shot).
     * Adds an X as a visual indicator.
     */
    public void markAsMiss() {
        currentState = "MISS";
        isPreviewActive = false;
        background.setFill(Color.web(Colors.WATER));
        background.setOpacity(1.0);
        originalColor = Colors.WATER;

        Text missMarker = new Text("X");
        missMarker.setStyle("-fx-font-size: " + (size / 2) + "px; -fx-font-weight: bold;");
        missMarker.setFill(Color.web(Colors.MISS));
        getChildren().add(missMarker);
    }

    /**
     * Marks the cell as containing part of a ship.
     * Changes the color to display the ship position.
     */
    public void markAsShip() {
        currentState = "SHIP";
        isPreviewActive = false;
        background.setFill(Color.web(Colors.SHIP));
        background.setOpacity(1.0);
        originalColor = Colors.SHIP;
    }

    /**
     * Marks the cell as part of a sunk ship.
     * Changes the color and adds a sunk marker.
     */
    public void markAsSunk() {
        currentState = "SUNK";
        isPreviewActive = false;
        background.setFill(Color.web(Colors.SUNK));
        background.setOpacity(1.0);
        originalColor = Colors.SUNK;

        Circle sunkMarker = new Circle(size / 3);
        sunkMarker.setFill(Color.web(Colors.SUNK_MARKER));
        getChildren().add(sunkMarker);
    }

    /**
     * Resets the cell to its initial empty state.
     * Clears all visual markers and restores water color.
     */
    public void reset() {
        currentState = "EMPTY";
        isPreviewActive = false;
        getChildren().clear();
        getChildren().add(background);
        background.setFill(Color.web(Colors.WATER));
        background.setOpacity(1.0);
        originalColor = Colors.WATER;
    }

    /**
     * Refreshes the cell's visualization.
     * Provided for compatibility; can be extended for future updates.
     */
    public void refresh() {
        // Method for compatibility, can be used for future updates
    }

    /**
     * Returns the current state of the cell.
     *
     * @return the current state (EMPTY, SHIP, HIT, MISS, or SUNK)
     */
    public String getCurrentState() {
        return currentState;
    }
}