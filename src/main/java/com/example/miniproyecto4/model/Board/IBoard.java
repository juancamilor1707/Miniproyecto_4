package com.example.miniproyecto4.model.Board;

import com.example.miniproyecto4.model.Cell.Cell;
import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.IShip;
import java.io.Serializable;
import java.util.List;

/**
 * Interface for the game board.
 * Defines operations for managing cells, ships, and game state on the board.
 */
public interface IBoard extends Serializable {

    /**
     * Gets the size of the board.
     *
     * @return The board size (width and height)
     */
    int getSize();

    /**
     * Gets the cell at the specified coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The cell at the given position, or null if invalid
     */
    Cell getCell(int x, int y);

    /**
     * Gets the cell at the specified coordinate.
     *
     * @param coordinate The coordinate
     * @return The cell at the given position, or null if invalid
     */
    Cell getCell(Coordinate coordinate);

    /**
     * Places a ship on the board.
     *
     * @param ship The ship to place
     * @return true if the ship was placed successfully, false otherwise
     */
    boolean placeShip(IShip ship);

    /**
     * Removes a ship from the board.
     *
     * @param ship The ship to remove
     * @return true if the ship was removed successfully, false otherwise
     */
    boolean removeShip(IShip ship);

    /**
     * Gets the list of all ships on the board.
     *
     * @return A copy of the ships list
     */
    List<IShip> getShips();

    /**
     * Gets the ship at the specified coordinate.
     *
     * @param coordinate The coordinate to check
     * @return The ship at that coordinate, or null if no ship exists
     */
    IShip getShipAt(Coordinate coordinate);

    /**
     * Checks if there is a ship at the specified coordinate.
     *
     * @param coordinate The coordinate to check
     * @return true if a ship exists at that coordinate, false otherwise
     */
    boolean hasShipAt(Coordinate coordinate);

    /**
     * Validates if a coordinate is within the board bounds.
     *
     * @param coordinate The coordinate to validate
     * @return true if the coordinate is valid, false otherwise
     */
    boolean isValidCoordinate(Coordinate coordinate);

    /**
     * Gets the count of sunk ships on the board.
     *
     * @return The number of sunk ships
     */
    int getSunkShipsCount();

    /**
     * Checks if all ships on the board are sunk.
     *
     * @return true if all ships are sunk, false otherwise
     */
    boolean allShipsSunk();

    /**
     * Resets the board to its initial empty state.
     */
    void reset();
}
