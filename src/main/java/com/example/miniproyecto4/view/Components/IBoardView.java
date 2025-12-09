package com.example.miniproyecto4.view.Components;

import com.example.miniproyecto4.model.Cell.Coordinate;

/**
 * Interface defining the contract for visual board representation in the Battleship game.
 * Provides methods for drawing cells and marking different cell states
 * such as hits, misses, ships, and sunk ships.
 */
public interface IBoardView {

    /**
     * Draws a cell with the specified color.
     *
     * @param coordinate the coordinate of the cell to draw
     * @param color the color to apply in hexadecimal format
     */
    void drawCell(Coordinate coordinate, String color);

    /**
     * Marks a cell as hit, displaying a hit indicator.
     *
     * @param coordinate the coordinate of the cell to mark
     */
    void markHit(Coordinate coordinate);

    /**
     * Marks a cell as a miss, displaying a miss indicator.
     *
     * @param coordinate the coordinate of the cell to mark
     */
    void markMiss(Coordinate coordinate);

    /**
     * Marks a cell as containing a ship.
     *
     * @param coordinate the coordinate of the cell to mark
     */
    void markShip(Coordinate coordinate);

    /**
     * Marks a cell as part of a sunk ship.
     *
     * @param coordinate the coordinate of the cell to mark
     */
    void markSunk(Coordinate coordinate);

    /**
     * Clears all cells on the board, resetting them to their initial state.
     */
    void clear();

    /**
     * Refreshes the visual state of all cells on the board.
     */
    void refresh();
}