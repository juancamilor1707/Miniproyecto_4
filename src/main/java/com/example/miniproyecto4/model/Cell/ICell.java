package com.example.miniproyecto4.model.Cell;

import java.io.Serializable;

/**
 * Interface for a cell on the game board.
 * Defines operations for managing cell state and checking cell conditions.
 */
public interface ICell extends Serializable {

    /**
     * Gets the coordinate of this cell.
     *
     * @return The cell's coordinate
     */
    Coordinate getCoordinate();

    /**
     * Gets the current status of this cell.
     *
     * @return The cell status
     */
    CellStatus getStatus();

    /**
     * Sets the status of this cell.
     *
     * @param status The new status
     */
    void setStatus(CellStatus status);

    /**
     * Checks if this cell is empty.
     *
     * @return true if the cell is empty, false otherwise
     */
    boolean isEmpty();

    /**
     * Checks if this cell has a ship.
     *
     * @return true if the cell contains a ship, false otherwise
     */
    boolean hasShip();

    /**
     * Checks if this cell has been hit.
     *
     * @return true if the cell has been hit, false otherwise
     */
    boolean isHit();

    /**
     * Checks if this cell is a miss (shot but no ship).
     *
     * @return true if the cell is a miss, false otherwise
     */
    boolean isMiss();
}