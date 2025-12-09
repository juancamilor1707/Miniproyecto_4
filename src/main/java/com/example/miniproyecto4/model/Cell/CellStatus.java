
package com.example.miniproyecto4.model.Cell;

/**
 * Enumeration of possible cell states in the game board.
 */
public enum CellStatus {
    /** Cell is empty with no ship */
    EMPTY,
    /** Cell contains a ship that has not been hit */
    SHIP,
    /** Cell contains a ship that has been hit */
    HIT,
    /** Cell has been shot but contained no ship */
    MISS,
    /** Cell is part of a sunk ship */
    SUNK
}