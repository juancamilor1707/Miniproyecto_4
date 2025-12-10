package com.example.miniproyecto4.view.Components;

import com.example.miniproyecto4.view.utils.Colors;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Visual component representing an individual cell on the game board.
 * Displays different states: empty, ship, hit, miss, and sunk.
 * Handles mouse events for user interaction and displays placement previews.
 * Extends StackPane to layer multiple visual elements.
 */
public class CellView extends StackPane {

    private final Rectangle background;
    private final double size;
    private String currentState;
    private boolean isPreviewActive;
    private String originalColor;
    private String shipPartType = "middle"; // "front", "back", "middle", "single"
    private boolean isHorizontal = true;

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

    public void setFill(String color) {
        background.setFill(Color.web(color));
        originalColor = color;
    }

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

    public void clearPreview() {
        if (isPreviewActive && currentState.equals("EMPTY")) {
            isPreviewActive = false;
            background.setFill(Color.web(originalColor));
            background.setOpacity(1.0);
        }
    }

    public void markAsHit() {
        currentState = "HIT";
        isPreviewActive = false;
        background.setFill(Color.web(Colors.HIT));
        background.setOpacity(1.0);
        originalColor = Colors.HIT;

        Group fireSymbol = createFireSymbol(size / 2);
        getChildren().add(fireSymbol);
    }

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
     * Marks the cell as containing part of a ship with shape information.
     * @param partType "front", "back", "middle", or "single"
     * @param horizontal true if ship is horizontal
     */
    public void markAsShip(String partType, boolean horizontal) {
        this.shipPartType = partType;
        this.isHorizontal = horizontal;
        markAsShip();
    }

    public void markAsShip() {
        currentState = "SHIP";
        isPreviewActive = false;
        background.setOpacity(1.0);
        background.setFill(Color.web(Colors.WATER));

        drawShipPart();
        originalColor = Colors.SHIP;
    }

    private void drawShipPart() {
        // Base del barco con gradiente
        LinearGradient shipGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#5D5D5D")),
                new Stop(0.5, Color.web("#8B8B8B")),
                new Stop(1, Color.web("#6E6E6E"))
        );

        if (shipPartType.equals("single")) {
            // Barco pequeño completo (fragata)
            drawCompleteSmallShip(shipGradient);
        } else if (shipPartType.equals("front")) {
            drawFrontPart(shipGradient);
        } else if (shipPartType.equals("back")) {
            drawBackPart(shipGradient);
        } else {
            drawMiddlePart(shipGradient);
        }
    }

    private void drawCompleteSmallShip(LinearGradient gradient) {
        if (isHorizontal) {
            Polygon boat = new Polygon();
            boat.getPoints().addAll(
                    size * 0.15, size * 0.35,
                    size * 0.85, size * 0.25,
                    size * 0.85, size * 0.75,
                    size * 0.15, size * 0.65
            );
            boat.setFill(gradient);
            boat.setStroke(Color.web("#3A3A3A"));
            boat.setStrokeWidth(1.5);


            Circle window = new Circle(size * 0.5, size * 0.5, size * 0.1);
            window.setFill(Color.web("#87CEEB"));
            window.setStroke(Color.web("#2C3E50"));

            getChildren().addAll(boat, window);
        } else {
            Polygon boat = new Polygon();
            boat.getPoints().addAll(
                    size * 0.35, size * 0.15,
                    size * 0.25, size * 0.85,
                    size * 0.75, size * 0.85,
                    size * 0.65, size * 0.15
            );
            boat.setFill(gradient);
            boat.setStroke(Color.web("#3A3A3A"));
            boat.setStrokeWidth(1.5);

            Circle window = new Circle(size * 0.5, size * 0.5, size * 0.1);
            window.setFill(Color.web("#87CEEB"));
            window.setStroke(Color.web("#2C3E50"));

            getChildren().addAll(boat, window);
        }
    }

    private void drawFrontPart(LinearGradient gradient) {
        if (isHorizontal) {

            Polygon front = new Polygon();
            front.getPoints().addAll(
                    size * 0.1, size * 0.5,
                    size * 0.9, size * 0.25,
                    size * 0.9, size * 0.75,
                    size * 0.1, size * 0.5
            );
            front.setFill(gradient);
            front.setStroke(Color.web("#3A3A3A"));
            front.setStrokeWidth(1.5);

            Circle window = new Circle(size * 0.6, size * 0.5, size * 0.12);
            window.setFill(Color.web("#87CEEB"));
            window.setStroke(Color.web("#2C3E50"));
            window.setStrokeWidth(1);

            getChildren().addAll(front, window);
        } else {

            Polygon front = new Polygon();
            front.getPoints().addAll(
                    size * 0.5, size * 0.1,
                    size * 0.25, size * 0.9,
                    size * 0.75, size * 0.9
            );
            front.setFill(gradient);
            front.setStroke(Color.web("#3A3A3A"));
            front.setStrokeWidth(1.5);

            Circle window = new Circle(size * 0.5, size * 0.6, size * 0.12);
            window.setFill(Color.web("#87CEEB"));
            window.setStroke(Color.web("#2C3E50"));
            window.setStrokeWidth(1);

            getChildren().addAll(front, window);
        }
    }

    private void drawBackPart(LinearGradient gradient) {
        if (isHorizontal) {
            Rectangle back = new Rectangle(size * 0.1, size * 0.25, size * 0.8, size * 0.5);
            back.setFill(gradient);
            back.setStroke(Color.web("#3A3A3A"));
            back.setStrokeWidth(1.5);

            Rectangle chimney = new Rectangle(size * 0.7, size * 0.15, size * 0.15, size * 0.3);
            chimney.setFill(Color.web("#C0392B"));
            chimney.setStroke(Color.web("#7F1D1D"));
            chimney.setStrokeWidth(1);

            Circle smoke = new Circle(size * 0.77, size * 0.1, size * 0.08);
            smoke.setFill(Color.web("#CCCCCC"));
            smoke.setOpacity(0.6);

            getChildren().addAll(back, chimney, smoke);
        } else {
            Rectangle back = new Rectangle(size * 0.25, size * 0.1, size * 0.5, size * 0.8);
            back.setFill(gradient);
            back.setStroke(Color.web("#3A3A3A"));
            back.setStrokeWidth(1.5);

            Rectangle chimney = new Rectangle(size * 0.35, size * 0.7, size * 0.3, size * 0.15);
            chimney.setFill(Color.web("#C0392B"));
            chimney.setStroke(Color.web("#7F1D1D"));
            chimney.setStrokeWidth(1);

            Circle smoke = new Circle(size * 0.5, size * 0.88, size * 0.08);
            smoke.setFill(Color.web("#CCCCCC"));
            smoke.setOpacity(0.6);

            getChildren().addAll(back, chimney, smoke);
        }
    }

    private void drawMiddlePart(LinearGradient gradient) {
        Rectangle middle = new Rectangle(size * 0.15, size * 0.25, size * 0.7, size * 0.5);
        middle.setFill(gradient);
        middle.setStroke(Color.web("#3A3A3A"));
        middle.setStrokeWidth(1.5);

        double window1X = isHorizontal ? size * 0.35 : size * 0.5;
        double window1Y = isHorizontal ? size * 0.5 : size * 0.35;
        Circle window1 = new Circle(window1X, window1Y, size * 0.1);
        window1.setFill(Color.web("#87CEEB"));
        window1.setStroke(Color.web("#2C3E50"));
        window1.setStrokeWidth(1);

        double window2X = isHorizontal ? size * 0.65 : size * 0.5;
        double window2Y = isHorizontal ? size * 0.5 : size * 0.65;
        Circle window2 = new Circle(window2X, window2Y, size * 0.1);
        window2.setFill(Color.web("#87CEEB"));
        window2.setStroke(Color.web("#2C3E50"));
        window2.setStrokeWidth(1);

        getChildren().addAll(middle, window1, window2);
    }

    public void markAsSunk() {
        currentState = "SUNK";
        isPreviewActive = false;
        background.setOpacity(1.0);

        LinearGradient sunkGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#2C3E50")),
                new Stop(0.5, Color.web("#34495E")),
                new Stop(1, Color.web("#1C2833"))
        );
        background.setFill(sunkGradient);

        Group fireSymbol = createFireSymbol(size * 0.6);

        Rectangle line1 = new Rectangle(size * 0.5, size * 0.08);
        line1.setFill(Color.web("#E74C3C"));
        line1.setRotate(45);

        Rectangle line2 = new Rectangle(size * 0.5, size * 0.08);
        line2.setFill(Color.web("#E74C3C"));
        line2.setRotate(-45);

        getChildren().addAll(fireSymbol, line1, line2);
        originalColor = Colors.SUNK;
    }


    private Group createFireSymbol(double fireSize) {
        Group fire = new Group();

        Polygon flame1 = new Polygon(
                0, fireSize * 0.3,
                -fireSize * 0.5, -fireSize * 0.2,
                -fireSize * 0.3, -fireSize * 0.8,
                0, -fireSize * 1,
                fireSize * 0.3, -fireSize * 0.8,
                fireSize * 0.5, -fireSize * 0.2
        );
        flame1.setFill(Color.web("#FF6B35"));
        flame1.setStroke(Color.web("#E74C3C"));
        flame1.setStrokeWidth(1.5);

        Polygon flame2 = new Polygon(
                0, fireSize * 0.2,
                -fireSize * 0.30, 0,
                -fireSize * 0.016, -fireSize * 0.5,
                0, -fireSize * 0.5,
                fireSize * 0.16, -fireSize * 0.5,
                fireSize * 0.3, 0
        );
        flame2.setFill(Color.web("#FFC914"));

        Circle core = new Circle(0, 0, fireSize * 0.08);
        core.setFill(Color.web("#FFF8DC"));

        fire.getChildren().addAll(flame1, flame2, core);

        return fire;
    }

    public void reset() {
        currentState = "EMPTY";
        isPreviewActive = false;
        shipPartType = "middle";
        isHorizontal = true;
        getChildren().clear();
        getChildren().add(background);
        background.setFill(Color.web(Colors.WATER));
        background.setOpacity(1.0);
        originalColor = Colors.WATER;
    }

    public void refresh() {
        // Method for compatibility
    }

    public String getCurrentState() {
        return currentState;
    }
}