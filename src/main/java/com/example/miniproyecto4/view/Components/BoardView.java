package com.example.miniproyecto4.view.Components;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.view.utils.Colors;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

/**
 * Visual representation of a game board in the Battleship game.
 * Manages a grid of CellView components and provides methods to update
 * cell states for hits, misses, ships, and sunk ships.
 * Extends GridPane to organize cells in a grid layout.
 */
public class BoardView extends GridPane implements IBoardView {

    /**
     * The size of the board (number of cells per side).
     */
    private final int size;

    /**
     * The size of each cell in pixels.
     */
    private final double cellSize;

    /**
     * Two-dimensional array of cell views representing the game board.
     */
    private final CellView[][] cells;

    /**
     * Constructs a BoardView with the specified size and cell dimensions.
     * Initializes all cells and arranges them in a grid layout.
     *
     * @param size the number of cells per side of the board
     * @param cellSize the width and height of each cell in pixels
     */
    public BoardView(int size, double cellSize) {
        this.size = size;
        this.cellSize = cellSize;
        this.cells = new CellView[size][size];
        initializeBoard();
    }

    /**
     * Initializes the board by creating and positioning all cell views.
     * Sets up gaps between cells and adds them to the grid.
     */
    private void initializeBoard() {
        setHgap(2);
        setVgap(2);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                CellView cellView = new CellView(cellSize);
                cells[x][y] = cellView;
                add(cellView, x, y);
            }
        }
    }

    /**
     * Returns the cell view at the specified grid coordinates.
     *
     * @param x the x-coordinate of the cell
     * @param y the y-coordinate of the cell
     * @return the CellView at the specified position, or null if coordinates are invalid
     */
    public CellView getCell(int x, int y) {
        if (x >= 0 && x < size && y >= 0 && y < size) {
            return cells[x][y];
        }
        return null;
    }

    /**
     * Returns the cell view at the specified coordinate.
     *
     * @param coordinate the coordinate of the cell
     * @return the CellView at the specified coordinate
     */
    public CellView getCell(Coordinate coordinate) {
        return getCell(coordinate.getX(), coordinate.getY());
    }

    /**
     * Draws a cell with the specified color.
     *
     * @param coordinate the coordinate of the cell to draw
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
     * Marks a cell as hit, displaying a hit indicator.
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
     * Marks a cell as a miss, displaying a miss indicator.
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
     *
     * @param coordinate the coordinate of the cell to mark
     */
    @Override
    public void markShip(Coordinate coordinate) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.markAsShip();
        }
    }

    /**
     * Marks a cell as part of a sunk ship.
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
     * Clears all cells on the board, resetting them to their initial state.
     */
    @Override
    public void clear() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                cells[x][y].reset();
            }
        }
    }

    /**
     * Refreshes the visual state of all cells on the board.
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