package com.example.miniproyecto4.model.Board;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.IShip;
import java.util.List;

/**
 * Utility class for validating ship placements and board operations.
 * Provides static methods for validation logic.
 */
public class BoardValidator {

    /**
     * Validates if a ship can be placed on the board.
     * Checks that the ship is not null, has a start coordinate, is within bounds,
     * and does not overlap with existing ships.
     *
     * @param board The board to validate against
     * @param ship The ship to validate
     * @return true if the placement is valid, false otherwise
     */
    public static boolean isValidPlacement(IBoard board, IShip ship) {
        if (ship == null || ship.getStartCoordinate() == null) {
            return false;
        }

        List<Coordinate> coordinates = ship.getCoordinates();

        for (Coordinate coord : coordinates) {
            if (!board.isValidCoordinate(coord)) {
                return false;
            }

            if (board.hasShipAt(coord)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a coordinate is within the board bounds.
     *
     * @param board The board to check against
     * @param coordinate The coordinate to validate
     * @return true if the coordinate is within bounds, false otherwise
     */
    public static boolean isWithinBounds(IBoard board, Coordinate coordinate) {
        return board.isValidCoordinate(coordinate);
    }

    /**
     * Checks if a ship placement would overlap with existing ships.
     *
     * @param board The board to check against
     * @param ship The ship to check
     * @return true if there is no overlap, false if overlap exists
     */
    public static boolean hasNoOverlap(IBoard board, IShip ship) {
        for (Coordinate coord : ship.getCoordinates()) {
            if (board.hasShipAt(coord)) {
                return false;
            }
        }
        return true;
    }
}