package com.example.miniproyecto4.model.Board;

import com.example.miniproyecto4.model.Cell.Cell;
import com.example.miniproyecto4.model.Cell.CellStatus;
import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.IShip;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the game board for Battleship.
 * Manages a grid of cells, ship placements, and game state tracking.
 * Implements Serializable to support game state persistence.
 */
public class Board implements IBoard, Serializable {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default size of the board (10x10).
     */
    private static final int DEFAULT_SIZE = 10;

    /**
     * The size of the board (width and height).
     */
    private final int size;

    /**
     * Two-dimensional array representing the grid of cells.
     */
    private final Cell[][] grid;

    /**
     * List of all ships placed on the board.
     */
    private final List<IShip> ships;

    /**
     * Map linking coordinates to the ships occupying them.
     */
    private final Map<Coordinate, IShip> shipPositions;

    /**
     * Constructs a board with the default size (10x10).
     * Initializes an empty grid with no ships.
     */
    public Board() {
        this(DEFAULT_SIZE);
    }

    /**
     * Constructs a board with a custom size.
     * Initializes an empty grid with the specified dimensions.
     *
     * @param size the size of the board (width and height)
     */
    public Board(int size) {
        this.size = size;
        this.grid = new Cell[size][size];
        this.ships = new ArrayList<>();
        this.shipPositions = new HashMap<>();
        initializeGrid();
    }

    /**
     * Initializes the board grid with empty cells.
     * Creates a Cell instance for each position in the grid.
     */
    private void initializeGrid() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                grid[x][y] = new Cell(x, y);
            }
        }
    }

    /**
     * Returns the size of the board.
     *
     * @return the board size (width and height)
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Returns the cell at the specified coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the cell at the given position, or null if coordinates are invalid
     */
    @Override
    public Cell getCell(int x, int y) {
        if (!isValidCoordinate(new Coordinate(x, y))) {
            return null;
        }
        return grid[x][y];
    }

    /**
     * Returns the cell at the specified coordinate.
     *
     * @param coordinate the coordinate
     * @return the cell at the given position, or null if coordinate is invalid
     */
    @Override
    public Cell getCell(Coordinate coordinate) {
        return getCell(coordinate.getX(), coordinate.getY());
    }

    /**
     * Places a ship on the board.
     * Validates that all ship coordinates are within bounds and not occupied.
     *
     * @param ship the ship to place
     * @return true if the ship was placed successfully, false otherwise
     */
    @Override
    public boolean placeShip(IShip ship) {
        if (ship == null || ship.getStartCoordinate() == null) {
            return false;
        }

        List<Coordinate> coordinates = ship.getCoordinates();

        for (Coordinate coord : coordinates) {
            if (!isValidCoordinate(coord) || hasShipAt(coord)) {
                return false;
            }
        }

        for (Coordinate coord : coordinates) {
            Cell cell = getCell(coord);
            cell.setStatus(CellStatus.SHIP);
            shipPositions.put(coord, ship);
        }

        ships.add(ship);
        return true;
    }

    /**
     * Removes a ship from the board.
     * Clears all cells occupied by the ship and removes it from the ships list.
     *
     * @param ship the ship to remove
     * @return true if the ship was removed successfully, false if ship not found
     */
    @Override
    public boolean removeShip(IShip ship) {
        if (!ships.contains(ship)) {
            return false;
        }

        for (Coordinate coord : ship.getCoordinates()) {
            Cell cell = getCell(coord);
            if (cell != null) {
                cell.setStatus(CellStatus.EMPTY);
            }
            shipPositions.remove(coord);
        }

        ships.remove(ship);
        return true;
    }

    /**
     * Returns a copy of the list of all ships on the board.
     *
     * @return a new list containing all ships
     */
    @Override
    public List<IShip> getShips() {
        return new ArrayList<>(ships);
    }

    /**
     * Returns the ship at the specified coordinate.
     *
     * @param coordinate the coordinate to check
     * @return the ship at that coordinate, or null if no ship exists
     */
    @Override
    public IShip getShipAt(Coordinate coordinate) {
        return shipPositions.get(coordinate);
    }

    /**
     * Checks if there is a ship at the specified coordinate.
     *
     * @param coordinate the coordinate to check
     * @return true if a ship exists at that coordinate, false otherwise
     */
    @Override
    public boolean hasShipAt(Coordinate coordinate) {
        return shipPositions.containsKey(coordinate);
    }

    /**
     * Validates if a coordinate is within the board bounds.
     *
     * @param coordinate the coordinate to validate
     * @return true if the coordinate is valid, false otherwise
     */
    @Override
    public boolean isValidCoordinate(Coordinate coordinate) {
        int x = coordinate.getX();
        int y = coordinate.getY();
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    /**
     * Returns the count of sunk ships on the board.
     *
     * @return the number of ships that have been completely sunk
     */
    @Override
    public int getSunkShipsCount() {
        int count = 0;
        for (IShip ship : ships) {
            if (ship.isSunk()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if all ships on the board have been sunk.
     *
     * @return true if all ships are sunk, false otherwise
     */
    @Override
    public boolean allShipsSunk() {
        return !ships.isEmpty() && getSunkShipsCount() == ships.size();
    }

    /**
     * Resets the board to its initial empty state.
     * Clears all ships and reinitializes the grid with empty cells.
     */
    @Override
    public void reset() {
        ships.clear();
        shipPositions.clear();
        initializeGrid();
    }
}