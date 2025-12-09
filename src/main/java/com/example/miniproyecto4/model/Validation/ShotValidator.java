package com.example.miniproyecto4.model.Validation;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Cell;
import com.example.miniproyecto4.model.Cell.Coordinate;

/**
 * Validator for shot legality in the Battleship game.
 * Ensures shots are within board boundaries and target cells
 * that have not been previously shot.
 */
public class ShotValidator implements IShotValidator {

    /**
     * Validates whether a shot at the specified coordinate is legal.
     * Checks for null inputs, coordinate validity, and previous shot status.
     *
     * @param board the game board
     * @param coordinate the coordinate to validate
     * @return true if the shot is valid, false otherwise
     */
    @Override
    public boolean validate(IBoard board, Coordinate coordinate) {
        if (board == null || coordinate == null) {
            return false;
        }

        return isValidCoordinate(board, coordinate) && isNotAlreadyShot(board, coordinate);
    }

    /**
     * Checks if the coordinate is valid within the board boundaries.
     *
     * @param board the game board
     * @param coordinate the coordinate to check
     * @return true if the coordinate is valid, false otherwise
     */
    @Override
    public boolean isValidCoordinate(IBoard board, Coordinate coordinate) {
        return board.isValidCoordinate(coordinate);
    }

    /**
     * Checks if the coordinate has not been shot at before.
     * Ensures the cell exists and is neither hit nor marked as a miss.
     *
     * @param board the game board
     * @param coordinate the coordinate to check
     * @return true if the coordinate has not been shot, false otherwise
     */
    @Override
    public boolean isNotAlreadyShot(IBoard board, Coordinate coordinate) {
        Cell cell = board.getCell(coordinate);

        if (cell == null) {
            return false;
        }

        return !cell.isHit() && !cell.isMiss();
    }
}