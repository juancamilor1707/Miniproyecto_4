package com.example.miniproyecto4.model.Cell;

import java.io.Serializable;

/**
 * Implementation of a cell on the game board.
 * Represents a single position with a coordinate and status (empty, ship, hit, miss, sunk).
 * Implements Serializable to support game state persistence.
 */
public class Cell implements ICell, Serializable {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The coordinate position of this cell on the board.
     */
    private final Coordinate coordinate;

    /**
     * The current status of this cell (EMPTY, SHIP, HIT, MISS, or SUNK).
     */
    private CellStatus status;

    /**
     * Constructs a new cell with the given coordinate.
     * The cell is initialized with EMPTY status.
     *
     * @param coordinate the cell's coordinate position
     */
    public Cell(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.status = CellStatus.EMPTY;
    }

    /**
     * Constructs a new cell with the given x and y coordinates.
     * The cell is initialized with EMPTY status.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Cell(int x, int y) {
        this(new Coordinate(x, y));
    }

    /**
     * Returns the coordinate of this cell.
     *
     * @return the cell's coordinate
     */
    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Returns the current status of this cell.
     *
     * @return the cell status
     */
    @Override
    public CellStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of this cell.
     *
     * @param status the new status to set
     */
    @Override
    public void setStatus(CellStatus status) {
        this.status = status;
    }

    /**
     * Checks if this cell is empty (contains no ship and has not been shot).
     *
     * @return true if the cell is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return status == CellStatus.EMPTY;
    }

    /**
     * Checks if this cell contains a ship that has not been hit.
     *
     * @return true if the cell contains an intact ship, false otherwise
     */
    @Override
    public boolean hasShip() {
        return status == CellStatus.SHIP;
    }

    /**
     * Checks if this cell has been hit (contains a ship that was shot).
     *
     * @return true if the cell has been hit, false otherwise
     */
    @Override
    public boolean isHit() {
        return status == CellStatus.HIT;
    }

    /**
     * Checks if this cell is a miss (was shot but contained no ship).
     *
     * @return true if the cell is a miss, false otherwise
     */
    @Override
    public boolean isMiss() {
        return status == CellStatus.MISS;
    }
}