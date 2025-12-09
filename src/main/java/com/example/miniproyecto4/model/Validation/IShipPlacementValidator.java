package com.example.miniproyecto4.model.Validation;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Ship.IShip;

/**
 * Interface defining the contract for ship placement validation in the Battleship game.
 * Provides methods to validate ship placement by checking boundaries and overlap.
 */
public interface IShipPlacementValidator {

    /**
     * Validates whether a ship can be placed on the board.
     * Performs comprehensive validation including bounds and overlap checks.
     *
     * @param board the game board
     * @param ship the ship to validate
     * @return true if the ship placement is valid, false otherwise
     */
    boolean validate(IBoard board, IShip ship);

    /**
     * Checks if all coordinates occupied by the ship are within board boundaries.
     *
     * @param board the game board
     * @param ship the ship to check
     * @return true if the ship is completely within bounds, false otherwise
     */
    boolean isWithinBounds(IBoard board, IShip ship);

    /**
     * Checks if the ship does not overlap with any existing ships on the board.
     *
     * @param board the game board
     * @param ship the ship to check
     * @return true if there is no overlap, false otherwise
     */
    boolean hasNoOverlap(IBoard board, IShip ship);
}