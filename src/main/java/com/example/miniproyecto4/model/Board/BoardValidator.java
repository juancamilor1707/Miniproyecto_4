package com.example.miniproyecto4.model.Board;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.IShip;
import java.util.List;

public class BoardValidator {

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

    public static boolean isWithinBounds(IBoard board, Coordinate coordinate) {
        return board.isValidCoordinate(coordinate);
    }

    public static boolean hasNoOverlap(IBoard board, IShip ship) {
        for (Coordinate coord : ship.getCoordinates()) {
            if (board.hasShipAt(coord)) {
                return false;
            }
        }
        return true;
    }
}