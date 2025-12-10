package com.example.miniproyecto4.view.Components;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.IShip;
import com.example.miniproyecto4.model.Validation.Orientation;
import com.example.miniproyecto4.view.utils.Colors;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Visual representation of a game board in the Battleship game.
 * Manages a grid of CellView components and provides methods to update
 * cell states for hits, misses, ships, and sunk ships.
 * Now includes continuous ship rendering across multiple cells.
 */
public class BoardView extends Pane implements IBoardView {

    private final int size;
    private final double cellSize;
    private final CellView[][] cells;
    private final Pane cellLayer;
    private final Pane shipLayer;
    private final Map<IShip, Group> shipGraphics;

    public BoardView(int size, double cellSize) {
        this.size = size;
        this.cellSize = cellSize;
        this.cells = new CellView[size][size];
        this.shipGraphics = new HashMap<>();

        // Create layers in proper order
        this.cellLayer = new Pane();
        this.shipLayer = new Pane();
        shipLayer.setMouseTransparent(true);

        initializeBoard();

        // Set proper size for the board
        double totalSize = size * cellSize + (size - 1) * 2;
        setPrefSize(totalSize, totalSize);
        setMinSize(totalSize, totalSize);
        setMaxSize(totalSize, totalSize);
    }

    private void initializeBoard() {
        // Add cell layer first
        getChildren().add(cellLayer);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                CellView cellView = new CellView(cellSize);
                cells[x][y] = cellView;

                // Position cells with gaps
                cellView.setLayoutX(x * (cellSize + 2));
                cellView.setLayoutY(y * (cellSize + 2));

                cellLayer.getChildren().add(cellView);
            }
        }

        // Add ship layer on top
        getChildren().add(shipLayer);
    }

    /**
     * Draws a complete ship across multiple cells as one continuous graphic
     */
    public void drawContinuousShip(IShip ship) {
        List<Coordinate> coords = ship.getCoordinates();
        if (coords.isEmpty()) return;

        boolean isHorizontal = ship.getOrientation() == Orientation.HORIZONTAL;
        int shipSize = coords.size();

        Group shipGroup = new Group();

        // Calculate ship position based on first cell
        Coordinate start = coords.get(0);
        double startX = start.getX() * (cellSize + 2);
        double startY = start.getY() * (cellSize + 2);

        double shipWidth = isHorizontal ? (shipSize * cellSize + (shipSize - 1) * 2) : cellSize;
        double shipHeight = isHorizontal ? cellSize : (shipSize * cellSize + (shipSize - 1) * 2);

        // Create ship based on size with improved designs
        if (shipSize == 1) {
            shipGroup.getChildren().addAll(createSmallBoat(startX, startY, isHorizontal));
        } else if (shipSize == 2) {
            shipGroup.getChildren().addAll(createDestroyer(startX, startY, shipWidth, shipHeight, isHorizontal));
        } else if (shipSize == 3) {
            shipGroup.getChildren().addAll(createSubmarine(startX, startY, shipWidth, shipHeight, isHorizontal));
        } else if (shipSize >= 4) {
            shipGroup.getChildren().addAll(createCarrier(startX, startY, shipWidth, shipHeight, isHorizontal));
        }

        shipGraphics.put(ship, shipGroup);
        shipLayer.getChildren().add(shipGroup);
    }

    private Group createSmallBoat(double x, double y, boolean horizontal) {
        Group boat = new Group();

        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#7F8C8D")),
                new Stop(0.5, Color.web("#95A5A6")),
                new Stop(1, Color.web("#7F8C8D"))
        );

        double padding = 6;

        if (horizontal) {
            Polygon triangle = new Polygon(
                    x + padding, y + cellSize * 0.3,
                    x + padding, y + cellSize * 0.7,
                    x + cellSize - padding, y + cellSize * 0.5
            );
            triangle.setFill(gradient);
            triangle.setStroke(Color.web("#34495E"));
            triangle.setStrokeWidth(2.5);

            Rectangle mast = new Rectangle(x + cellSize * 0.35, y + cellSize * 0.4, cellSize * 0.12, cellSize * 0.2);
            mast.setFill(Color.web("#34495E"));
            mast.setStroke(Color.web("#2C3E50"));
            mast.setStrokeWidth(1.5);

            boat.getChildren().addAll(triangle, mast);
        } else {
            Polygon triangle = new Polygon(
                    x + cellSize * 0.3, y + padding,
                    x + cellSize * 0.7, y + padding,
                    x + cellSize * 0.5, y + cellSize - padding
            );
            triangle.setFill(gradient);
            triangle.setStroke(Color.web("#34495E"));
            triangle.setStrokeWidth(2.5);

            Rectangle mast = new Rectangle(x + cellSize * 0.4, y + cellSize * 0.35, cellSize * 0.2, cellSize * 0.12);
            mast.setFill(Color.web("#34495E"));
            mast.setStroke(Color.web("#2C3E50"));
            mast.setStrokeWidth(1.5);

            boat.getChildren().addAll(triangle, mast);
        }

        return boat;
    }

    private Group createDestroyer(double x, double y, double width, double height, boolean horizontal) {
        Group destroyer = new Group();

        LinearGradient bodyGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#5D5D5D")),
                new Stop(0.5, Color.web("#95A5A6")),
                new Stop(1, Color.web("#7F8C8D"))
        );

        double padding = 3;

        if (horizontal) {
            // Main hull
            Rectangle hull = new Rectangle(x + padding, y + height * 0.25, width - padding * 2, height * 0.5);
            hull.setFill(bodyGradient);
            hull.setStroke(Color.web("#2C3E50"));
            hull.setStrokeWidth(2);
            hull.setArcWidth(8);
            hull.setArcHeight(8);

            // Bow (front point)
            Polygon bow = new Polygon(
                    x + padding, y + height * 0.35,
                    x + padding, y + height * 0.65,
                    x - 2, y + height * 0.5
            );
            bow.setFill(bodyGradient);
            bow.setStroke(Color.web("#2C3E50"));
            bow.setStrokeWidth(2);

            // Bridge
            Rectangle bridge = new Rectangle(x + width * 0.6, y + height * 0.1, width * 0.28, height * 0.4);
            bridge.setFill(Color.web("#34495E"));
            bridge.setStroke(Color.web("#1C2833"));
            bridge.setStrokeWidth(1.5);
            bridge.setArcWidth(4);
            bridge.setArcHeight(4);

            // Windows
            Rectangle window1 = new Rectangle(x + width * 0.65, y + height * 0.2, width * 0.08, height * 0.15);
            window1.setFill(Color.web("#5DADE2"));
            window1.setStroke(Color.web("#2980B9"));

            Rectangle window2 = new Rectangle(x + width * 0.75, y + height * 0.2, width * 0.08, height * 0.15);
            window2.setFill(Color.web("#5DADE2"));
            window2.setStroke(Color.web("#2980B9"));

            // Gun turret
            Circle turret = new Circle(x + width * 0.35, y + height * 0.5, height * 0.18);
            turret.setFill(Color.web("#566573"));
            turret.setStroke(Color.web("#2C3E50"));
            turret.setStrokeWidth(2);

            Rectangle barrel = new Rectangle(x + width * 0.25, y + height * 0.45, width * 0.15, height * 0.1);
            barrel.setFill(Color.web("#34495E"));
            barrel.setStroke(Color.web("#2C3E50"));

            destroyer.getChildren().addAll(hull, bow, bridge, window1, window2, turret, barrel);

        } else {
            // Vertical destroyer
            Rectangle hull = new Rectangle(x + width * 0.25, y + padding, width * 0.5, height - padding * 2);
            hull.setFill(bodyGradient);
            hull.setStroke(Color.web("#2C3E50"));
            hull.setStrokeWidth(2);
            hull.setArcWidth(8);
            hull.setArcHeight(8);

            Polygon bow = new Polygon(
                    x + width * 0.35, y + padding,
                    x + width * 0.65, y + padding,
                    x + width * 0.5, y - 2
            );
            bow.setFill(bodyGradient);
            bow.setStroke(Color.web("#2C3E50"));
            bow.setStrokeWidth(2);

            Rectangle bridge = new Rectangle(x + width * 0.1, y + height * 0.6, width * 0.4, height * 0.28);
            bridge.setFill(Color.web("#34495E"));
            bridge.setStroke(Color.web("#1C2833"));
            bridge.setStrokeWidth(1.5);
            bridge.setArcWidth(4);
            bridge.setArcHeight(4);

            Rectangle window1 = new Rectangle(x + width * 0.2, y + height * 0.65, width * 0.15, height * 0.08);
            window1.setFill(Color.web("#5DADE2"));
            window1.setStroke(Color.web("#2980B9"));

            Rectangle window2 = new Rectangle(x + width * 0.2, y + height * 0.75, width * 0.15, height * 0.08);
            window2.setFill(Color.web("#5DADE2"));
            window2.setStroke(Color.web("#2980B9"));

            Circle turret = new Circle(x + width * 0.5, y + height * 0.35, width * 0.18);
            turret.setFill(Color.web("#566573"));
            turret.setStroke(Color.web("#2C3E50"));
            turret.setStrokeWidth(2);

            Rectangle barrel = new Rectangle(x + width * 0.45, y + height * 0.25, width * 0.1, height * 0.15);
            barrel.setFill(Color.web("#34495E"));
            barrel.setStroke(Color.web("#2C3E50"));

            destroyer.getChildren().addAll(hull, bow, bridge, window1, window2, turret, barrel);
        }

        return destroyer;
    }

    private Group createSubmarine(double x, double y, double width, double height, boolean horizontal) {
        Group submarine = new Group();

        LinearGradient bodyGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#2C3E50")),
                new Stop(0.5, Color.web("#34495E")),
                new Stop(1, Color.web("#2C3E50"))
        );

        double padding = 3;

        if (horizontal) {
            Ellipse body = new Ellipse(x + width * 0.5, y + height * 0.5, width * 0.46, height * 0.3);
            body.setFill(bodyGradient);
            body.setStroke(Color.web("#1C2833"));
            body.setStrokeWidth(2.5);

            Ellipse tower = new Ellipse(x + width * 0.5, y + height * 0.5, width * 0.15, height * 0.22);
            tower.setFill(Color.web("#34495E"));
            tower.setStroke(Color.web("#1C2833"));
            tower.setStrokeWidth(2);

            Line periscope = new Line(x + width * 0.5, y + height * 0.28, x + width * 0.5, y + height * 0.12);
            periscope.setStroke(Color.web("#95A5A6"));
            periscope.setStrokeWidth(3);

            Circle periscopeTop = new Circle(x + width * 0.5, y + height * 0.12, 3.5);
            periscopeTop.setFill(Color.web("#E74C3C"));
            periscopeTop.setStroke(Color.web("#C0392B"));
            periscopeTop.setStrokeWidth(1.5);

            Line detailLine1 = new Line(x + width * 0.25, y + height * 0.5, x + width * 0.4, y + height * 0.5);
            detailLine1.setStroke(Color.web("#1C2833"));
            detailLine1.setStrokeWidth(2);

            Line detailLine2 = new Line(x + width * 0.6, y + height * 0.5, x + width * 0.75, y + height * 0.5);
            detailLine2.setStroke(Color.web("#1C2833"));
            detailLine2.setStrokeWidth(2);

            submarine.getChildren().addAll(body, detailLine1, detailLine2, tower, periscope, periscopeTop);

        } else {
            Ellipse body = new Ellipse(x + width * 0.5, y + height * 0.5, width * 0.3, height * 0.46);
            body.setFill(bodyGradient);
            body.setStroke(Color.web("#1C2833"));
            body.setStrokeWidth(2.5);

            Ellipse tower = new Ellipse(x + width * 0.5, y + height * 0.5, width * 0.22, height * 0.15);
            tower.setFill(Color.web("#34495E"));
            tower.setStroke(Color.web("#1C2833"));
            tower.setStrokeWidth(2);

            Line periscope = new Line(x + width * 0.72, y + height * 0.5, x + width * 0.88, y + height * 0.5);
            periscope.setStroke(Color.web("#95A5A6"));
            periscope.setStrokeWidth(3);

            Circle periscopeTop = new Circle(x + width * 0.88, y + height * 0.5, 3.5);
            periscopeTop.setFill(Color.web("#E74C3C"));
            periscopeTop.setStroke(Color.web("#C0392B"));
            periscopeTop.setStrokeWidth(1.5);

            Line detailLine1 = new Line(x + width * 0.5, y + height * 0.25, x + width * 0.5, y + height * 0.4);
            detailLine1.setStroke(Color.web("#1C2833"));
            detailLine1.setStrokeWidth(2);

            Line detailLine2 = new Line(x + width * 0.5, y + height * 0.6, x + width * 0.5, y + height * 0.75);
            detailLine2.setStroke(Color.web("#1C2833"));
            detailLine2.setStrokeWidth(2);

            submarine.getChildren().addAll(body, detailLine1, detailLine2, tower, periscope, periscopeTop);
        }

        return submarine;
    }

    private Group createCarrier(double x, double y, double width, double height, boolean horizontal) {
        Group carrier = new Group();

        LinearGradient bodyGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#5D5D5D")),
                new Stop(0.3, Color.web("#7F8C8D")),
                new Stop(0.7, Color.web("#95A5A6")),
                new Stop(1, Color.web("#7F8C8D"))
        );

        double padding = 2;

        if (horizontal) {
            // Main hull
            Rectangle hull = new Rectangle(x + padding, y + height * 0.3, width - padding * 2, height * 0.5);
            hull.setFill(bodyGradient);
            hull.setStroke(Color.web("#2C3E50"));
            hull.setStrokeWidth(2.5);
            hull.setArcWidth(10);
            hull.setArcHeight(10);

            // Bow
            Polygon bow = new Polygon(
                    x + padding, y + height * 0.4,
                    x + padding, y + height * 0.7,
                    x - 3, y + height * 0.55
            );
            bow.setFill(bodyGradient);
            bow.setStroke(Color.web("#2C3E50"));
            bow.setStrokeWidth(2.5);

            // Flight deck
            Rectangle deck = new Rectangle(x + width * 0.1, y + height * 0.12, width * 0.85, height * 0.28);
            deck.setFill(Color.web("#566573"));
            deck.setStroke(Color.web("#34495E"));
            deck.setStrokeWidth(1.5);

            // Island (control tower)
            Rectangle island = new Rectangle(x + width * 0.65, y + height * 0.02, width * 0.18, height * 0.38);
            island.setFill(Color.web("#34495E"));
            island.setStroke(Color.web("#1C2833"));
            island.setStrokeWidth(2);
            island.setArcWidth(5);
            island.setArcHeight(5);

            // Radar antenna
            Line antenna = new Line(x + width * 0.74, y + height * 0.02, x + width * 0.74, y - height * 0.08);
            antenna.setStroke(Color.web("#E74C3C"));
            antenna.setStrokeWidth(3);

            Circle radarDish = new Circle(x + width * 0.74, y - height * 0.08, 4);
            radarDish.setFill(Color.web("#E74C3C"));
            radarDish.setStroke(Color.web("#C0392B"));
            radarDish.setStrokeWidth(1.5);

            // Windows on island
            for (int i = 0; i < 2; i++) {
                Rectangle window = new Rectangle(x + width * (0.68 + i * 0.08), y + height * 0.15, width * 0.06, height * 0.15);
                window.setFill(Color.web("#5DADE2"));
                window.setStroke(Color.web("#2980B9"));
                window.setStrokeWidth(1);
                carrier.getChildren().add(window);
            }

            // Deck markings
            for (int i = 0; i < 2; i++) {
                Line marking = new Line(x + width * (0.25 + i * 0.25), y + height * 0.15,
                        x + width * (0.25 + i * 0.25), y + height * 0.35);
                marking.setStroke(Color.web("#F39C12"));
                marking.setStrokeWidth(2.5);
                marking.getStrokeDashArray().addAll(4.0, 4.0);
                carrier.getChildren().add(marking);
            }

            carrier.getChildren().addAll(hull, bow, deck, island, antenna, radarDish);

        } else {
            // Vertical carrier
            Rectangle hull = new Rectangle(x + width * 0.3, y + padding, width * 0.5, height - padding * 2);
            hull.setFill(bodyGradient);
            hull.setStroke(Color.web("#2C3E50"));
            hull.setStrokeWidth(2.5);
            hull.setArcWidth(10);
            hull.setArcHeight(10);

            Polygon bow = new Polygon(
                    x + width * 0.4, y + padding,
                    x + width * 0.7, y + padding,
                    x + width * 0.55, y - 3
            );
            bow.setFill(bodyGradient);
            bow.setStroke(Color.web("#2C3E50"));
            bow.setStrokeWidth(2.5);

            Rectangle deck = new Rectangle(x + width * 0.12, y + height * 0.1, width * 0.28, height * 0.85);
            deck.setFill(Color.web("#566573"));
            deck.setStroke(Color.web("#34495E"));
            deck.setStrokeWidth(1.5);

            Rectangle island = new Rectangle(x + width * 0.02, y + height * 0.65, width * 0.38, height * 0.18);
            island.setFill(Color.web("#34495E"));
            island.setStroke(Color.web("#1C2833"));
            island.setStrokeWidth(2);
            island.setArcWidth(5);
            island.setArcHeight(5);

            Line antenna = new Line(x + width * 0.02, y + height * 0.74, x - width * 0.08, y + height * 0.74);
            antenna.setStroke(Color.web("#E74C3C"));
            antenna.setStrokeWidth(3);

            Circle radarDish = new Circle(x - width * 0.08, y + height * 0.74, 4);
            radarDish.setFill(Color.web("#E74C3C"));
            radarDish.setStroke(Color.web("#C0392B"));
            radarDish.setStrokeWidth(1.5);

            for (int i = 0; i < 2; i++) {
                Rectangle window = new Rectangle(x + width * 0.15, y + height * (0.68 + i * 0.08), width * 0.15, height * 0.06);
                window.setFill(Color.web("#5DADE2"));
                window.setStroke(Color.web("#2980B9"));
                window.setStrokeWidth(1);
                carrier.getChildren().add(window);
            }

            for (int i = 0; i < 2; i++) {
                Line marking = new Line(x + width * 0.15, y + height * (0.25 + i * 0.25),
                        x + width * 0.35, y + height * (0.25 + i * 0.25));
                marking.setStroke(Color.web("#F39C12"));
                marking.setStrokeWidth(2.5);
                marking.getStrokeDashArray().addAll(4.0, 4.0);
                carrier.getChildren().add(marking);
            }

            carrier.getChildren().addAll(hull, bow, deck, island, antenna, radarDish);
        }

        return carrier;
    }

    /**
     * Removes the continuous ship graphics from the board.
     * Clears ship visuals while preserving hits, misses, and sunk states.
     *
     * @param ship The ship to remove from visual display
     */
    public void removeShipGraphic(IShip ship) {
        Group graphic = shipGraphics.get(ship);
        if (graphic != null) {
            shipLayer.getChildren().remove(graphic);
            shipGraphics.remove(ship);
        }
    }

    public CellView getCell(int x, int y) {
        if (x >= 0 && x < size && y >= 0 && y < size) {
            return cells[x][y];
        }
        return null;
    }

    public CellView getCell(Coordinate coordinate) {
        return getCell(coordinate.getX(), coordinate.getY());
    }

    @Override
    public void drawCell(Coordinate coordinate, String color) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.setFill(color);
        }
    }

    @Override
    public void markHit(Coordinate coordinate) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.markAsHit();
        }
    }

    @Override
    public void markMiss(Coordinate coordinate) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.markAsMiss();
        }
    }

    @Override
    public void markShip(Coordinate coordinate) {
        // Ships are now drawn as continuous graphics
    }

    @Override
    public void markSunk(Coordinate coordinate) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.markAsSunk();
        }
    }

    @Override
    public void clear() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                cells[x][y].reset();
            }
        }
        shipLayer.getChildren().clear();
        shipGraphics.clear();
    }

    @Override
    public void refresh() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                cells[x][y].refresh();
            }
        }
    }


}