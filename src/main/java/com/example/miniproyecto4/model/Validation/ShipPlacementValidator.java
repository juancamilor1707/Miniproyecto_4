package com.example.miniproyecto4.model.Validation;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.IShip;
import java.util.List;

/**
 * Validator for ship placement in the Battleship game.
 * Ensures ships are placed within board boundaries and do not overlap
 * with existing ships.
 */
public class ShipPlacementValidator implements IShipPlacementValidator {

    /**
     * Validates whether a ship can be placed on the board.
     * Checks for null inputs, valid start coordinate, boundary constraints,
     * and overlap with existing ships.
     *
     * @param board the game board
     * @param ship the ship to validate
     * @return true if the ship placement is valid, false otherwise
     */
    @Override
    public boolean validate(IBoard board, IShip ship) {
        if (ship == null || board == null) {
            return false;
        }

        if (ship.getStartCoordinate() == null) {
            return false;
        }

        return isWithinBounds(board, ship) && hasNoOverlap(board, ship);
    }

    /**
     * Checks if all coordinates occupied by the ship are within board boundaries.
     *
     * @param board the game board
     * @param ship the ship to check
     * @return true if the ship is completely within bounds, false otherwise
     */
    @Override
    public boolean isWithinBounds(IBoard board, IShip ship) {
        List<Coordinate> coordinates = ship.getCoordinates();

        for (Coordinate coord : coordinates) {
            if (!board.isValidCoordinate(coord)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the ship does not overlap with any existing ships on the board.
     *
     * @param board the game board
     * @param ship the ship to check
     * @return true if there is no overlap, false otherwise
     */
    @Override
    public boolean hasNoOverlap(IBoard board, IShip ship) {
        List<Coordinate> coordinates = ship.getCoordinates();

        for (Coordinate coord : coordinates) {
            if (board.hasShipAt(coord)) {
                return false;
            }
        }

        return true;
    }
}