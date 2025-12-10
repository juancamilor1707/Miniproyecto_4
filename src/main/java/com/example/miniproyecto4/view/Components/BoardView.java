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
 * Includes continuous ship rendering across multiple cells for enhanced visuals.
 */
public class BoardView extends Pane implements IBoardView {

    /**
     * The number of cells in each dimension of the board.
     */
    private final int size;

    /**
     * The pixel size of each individual cell.
     */
    private final double cellSize;

    /**
     * 2D array containing all cell views on the board.
     */
    private final CellView[][] cells;

    /**
     * Layer containing all cell views, drawn first.
     */
    private final Pane cellLayer;

    /**
     * Layer containing ship graphics, drawn above cells.
     */
    private final Pane shipLayer;

    /**
     * Map storing the visual representation of each placed ship.
     */
    private final Map<IShip, Group> shipGraphics;

    /**
     * Constructs a new BoardView with the specified size and cell dimensions.
     * Creates layered panes for cells and ships, initializes the grid.
     *
     * @param size the number of cells in each dimension (width and height)
     * @param cellSize the pixel size of each cell
     */
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

    /**
     * Initializes the board by creating and positioning all cells and layers.
     * Creates the cell layer first, then adds individual cells with gaps,
     * and finally adds the ship layer on top.
     */
    private void initializeBoard() {
        // Add cell layer first
        getChildren().add(cellLayer);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                CellView cellView = new CellView(cellSize);
                cells[x][y] = cellView;

                // Position cells with 2px gaps
                cellView.setLayoutX(x * (cellSize + 2));
                cellView.setLayoutY(y * (cellSize + 2));

                cellLayer.getChildren().add(cellView);
            }
        }

        // Add ship layer on top
        getChildren().add(shipLayer);
    }

    /**
     * Draws a complete ship across multiple cells as one continuous graphic.
     * The ship is rendered as a single visual element spanning its coordinates.
     * Creates different ship designs based on ship size.
     *
     * @param ship the ship to draw on the board
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

    /**
     * Creates a small boat graphic for single-cell ships (frigate).
     * Renders a triangular boat shape with a mast.
     *
     * @param x the x position in pixels
     * @param y the y position in pixels
     * @param horizontal true if the boat is oriented horizontally
     * @return a Group containing the boat graphic elements
     */
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

    /**
     * Creates a destroyer ship graphic spanning two cells.
     * Renders a military vessel with hull, bridge, windows, and gun turret.
     *
     * @param x the starting x position in pixels
     * @param y the starting y position in pixels
     * @param width the total width of the ship in pixels
     * @param height the total height of the ship in pixels
     * @param horizontal true if the ship is oriented horizontally
     * @return a Group containing the destroyer graphic elements
     */
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

    /**
     * Creates a submarine ship graphic spanning three cells.
     * Renders a submarine with elliptical body, tower, and periscope.
     *
     * @param x the starting x position in pixels
     * @param y the starting y position in pixels
     * @param width the total width of the ship in pixels
     * @param height the total height of the ship in pixels
     * @param horizontal true if the ship is oriented horizontally
     * @return a Group containing the submarine graphic elements
     */
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

    /**
     * Creates an aircraft carrier ship graphic spanning four or more cells.
     * Renders a large carrier with hull, flight deck, island/control tower,
     * radar antenna, windows, and deck markings.
     *
     * @param x the starting x position in pixels
     * @param y the starting y position in pixels
     * @param width the total width of the ship in pixels
     * @param height the total height of the ship in pixels
     * @param horizontal true if the ship is oriented horizontally
     * @return a Group containing the carrier graphic elements
     */
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
     * Used when toggling ship visibility or resetting the board.
     *
     * @param ship the ship to remove from visual display
     */
    public void removeShipGraphic(IShip ship) {
        Group graphic = shipGraphics.get(ship);
        if (graphic != null) {
            shipLayer.getChildren().remove(graphic);
            shipGraphics.remove(ship);
        }
    }

    /**
     * Gets the cell view at the specified grid coordinates.
     *
     * @param x the x coordinate in the grid (0-based)
     * @param y the y coordinate in the grid (0-based)
     * @return the CellView at that position, or null if out of bounds
     */
    public CellView getCell(int x, int y) {
        if (x >= 0 && x < size && y >= 0 && y < size) {
            return cells[x][y];
        }
        return null;
    }

    /**
     * Gets the cell view at the specified coordinate object.
     *
     * @param coordinate the coordinate to get the cell from
     * @return the CellView at that coordinate, or null if out of bounds
     */
    public CellView getCell(Coordinate coordinate) {
        return getCell(coordinate.getX(), coordinate.getY());
    }

    /**
     * Draws a cell with a specific color.
     *
     * @param coordinate the coordinate of the cell to color
     * @param color the color to apply in hexadecimal format
     */
    @Override
    public void drawCell(Coordinate coordinate, String color) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.setFill(color);
        }
    }

    /**
     * Marks a cell as hit by a shot.
     * Displays hit visual effects on the cell.
     *
     * @param coordinate the coordinate of the cell to mark
     */
    @Override
    public void markHit(Coordinate coordinate) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.markAsHit();
        }
    }

    /**
     * Marks a cell as a miss (shot but no ship).
     * Displays miss visual indicator on the cell.
     *
     * @param coordinate the coordinate of the cell to mark
     */
    @Override
    public void markMiss(Coordinate coordinate) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.markAsMiss();
        }
    }

    /**
     * Marks a cell as containing a ship.
     * Note: Ships are now drawn as continuous graphics via drawContinuousShip().
     * This method is kept for interface compatibility.
     *
     * @param coordinate the coordinate of the cell to mark
     */
    @Override
    public void markShip(Coordinate coordinate) {
        // Ships are now drawn as continuous graphics
    }

    /**
     * Marks a cell as part of a sunk ship.
     * Displays sunk ship visual effects on the cell.
     *
     * @param coordinate the coordinate of the cell to mark
     */
    @Override
    public void markSunk(Coordinate coordinate) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.markAsSunk();
        }
    }

    /**
     * Clears all cells and ship graphics from the board.
     * Resets the board to its initial empty state.
     */
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

    /**
     * Refreshes all cell visualizations on the board.
     */
    @Override
    public void refresh() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                cells[x][y].refresh();
            }
        }
    }
}