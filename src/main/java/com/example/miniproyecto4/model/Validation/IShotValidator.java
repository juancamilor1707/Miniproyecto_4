package com.example.miniproyecto4.model.Validation;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Coordinate;

/**
 * Interface defining the contract for shot validation in the Battleship game.
 * Provides methods to validate shots by checking coordinate validity
 * and ensuring the target has not been previously shot.
 */
public interface IShotValidator {

    /**
     * Validates whether a shot at the specified coordinate is legal.
     * Checks that the coordinate is valid and has not been shot before.
     *
     * @param board the game board
     * @param coordinate the coordinate to validate
     * @return true if the shot is valid, false otherwise
     */
    boolean validate(IBoard board, Coordinate coordinate);

    /**
     * Checks if the coordinate is valid within the board boundaries.
     *
     * @param board the game board
     * @param coordinate the coordinate to check
     * @return true if the coordinate is valid, false otherwise
     */
    boolean isValidCoordinate(IBoard board, Coordinate coordinate);

    /**
     * Checks if the coordinate has not been shot at before.
     * Ensures the cell is neither hit nor marked as a miss.
     *
     * @param board the game board
     * @param coordinate the coordinate to check
     * @return true if the coordinate has not been shot, false otherwise
     */
    boolean isNotAlreadyShot(IBoard board, Coordinate coordinate);
}