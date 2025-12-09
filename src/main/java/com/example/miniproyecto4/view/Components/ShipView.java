package com.example.miniproyecto4.view.Components;

import com.example.miniproyecto4.model.Ship.ShipType;
import com.example.miniproyecto4.view.utils.Colors;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Visual representation of a ship in the Battleship game.
 * Displays a ship as a series of connected rectangular segments
 * in either horizontal or vertical orientation.
 * Extends Group to contain multiple shape elements.
 */
public class ShipView extends Group {

    /**
     * The type of ship being displayed.
     */
    private final ShipType type;

    /**
     * The size of each cell segment in pixels.
     */
    private final double cellSize;

    /**
     * Flag indicating whether the ship is oriented horizontally.
     */
    private final boolean horizontal;

    /**
     * Constructs a ShipView with the specified type, cell size, and orientation.
     * Automatically draws the ship segments upon creation.
     *
     * @param type the type of ship to display
     * @param cellSize the size of each ship segment in pixels
     * @param horizontal true for horizontal orientation, false for vertical
     */
    public ShipView(ShipType type, double cellSize, boolean horizontal) {
        this.type = type;
        this.cellSize = cellSize;
        this.horizontal = horizontal;
        draw();
    }

    /**
     * Draws the ship as a series of rectangular segments.
     * Positions segments based on the ship's size and orientation.
     * Each segment is styled with ship color and border.
     */
    private void draw() {
        int size = type.getSize();

        for (int i = 0; i < size; i++) {
            Rectangle segment = new Rectangle(cellSize, cellSize);
            segment.setFill(Color.web(Colors.SHIP));
            segment.setStroke(Color.web(Colors.SHIP_BORDER));
            segment.setStrokeWidth(2);

            if (horizontal) {
                segment.setX(i * (cellSize + 2));
                segment.setY(0);
            } else {
                segment.setX(0);
                segment.setY(i * (cellSize + 2));
            }

            getChildren().add(segment);
        }
    }
}