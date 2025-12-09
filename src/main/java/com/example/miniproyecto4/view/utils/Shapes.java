package com.example.miniproyecto4.view.utils;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Utility class for creating visual shapes used in the game interface.
 * Provides factory methods for creating cells, markers, and ship segments
 * with consistent styling.
 * This class cannot be instantiated.
 */
public class Shapes {

    /**
     * Creates a rectangle representing a board cell.
     * The cell is styled with water color and grid border.
     *
     * @param size the width and height of the cell in pixels
     * @return a styled Rectangle representing a cell
     */
    public static Rectangle createCellRectangle(double size) {
        Rectangle rect = new Rectangle(size, size);
        rect.setFill(Color.web(Colors.WATER));
        rect.setStroke(Color.web(Colors.GRID));
        rect.setStrokeWidth(1);
        return rect;
    }

    /**
     * Creates a circular marker indicating a hit.
     * The marker is filled with the hit marker color.
     *
     * @param radius the radius of the circle in pixels
     * @return a Circle representing a hit marker
     */
    public static Circle createHitMarker(double radius) {
        Circle circle = new Circle(radius);
        circle.setFill(Color.web(Colors.HIT_MARKER));
        return circle;
    }

    /**
     * Creates a circular marker indicating a miss.
     * The marker is filled with white and has a grid-colored border.
     *
     * @param radius the radius of the circle in pixels
     * @return a Circle representing a miss marker
     */
    public static Circle createMissMarker(double radius) {
        Circle circle = new Circle(radius);
        circle.setFill(Color.web(Colors.MISS));
        circle.setStroke(Color.web(Colors.GRID));
        circle.setStrokeWidth(2);
        return circle;
    }

    /**
     * Creates a rectangle representing a segment of a ship.
     * The segment is styled with ship color and border.
     *
     * @param size the width and height of the segment in pixels
     * @return a styled Rectangle representing a ship segment
     */
    public static Rectangle createShipSegment(double size) {
        Rectangle rect = new Rectangle(size, size);
        rect.setFill(Color.web(Colors.SHIP));
        rect.setStroke(Color.web(Colors.SHIP_BORDER));
        rect.setStrokeWidth(2);
        return rect;
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Shapes() {
    }
}