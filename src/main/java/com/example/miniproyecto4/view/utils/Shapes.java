package com.example.miniproyecto4.view.utils;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Shapes {

    public static Rectangle createCellRectangle(double size) {
        Rectangle rect = new Rectangle(size, size);
        rect.setFill(Color.web(Colors.WATER));
        rect.setStroke(Color.web(Colors.GRID));
        rect.setStrokeWidth(1);
        return rect;
    }

    public static Circle createHitMarker(double radius) {
        Circle circle = new Circle(radius);
        circle.setFill(Color.web(Colors.HIT_MARKER));
        return circle;
    }

    public static Circle createMissMarker(double radius) {
        Circle circle = new Circle(radius);
        circle.setFill(Color.web(Colors.MISS));
        circle.setStroke(Color.web(Colors.GRID));
        circle.setStrokeWidth(2);
        return circle;
    }

    public static Rectangle createShipSegment(double size) {
        Rectangle rect = new Rectangle(size, size);
        rect.setFill(Color.web(Colors.SHIP));
        rect.setStroke(Color.web(Colors.SHIP_BORDER));
        rect.setStrokeWidth(2);
        return rect;
    }

    private Shapes() {
    }
}