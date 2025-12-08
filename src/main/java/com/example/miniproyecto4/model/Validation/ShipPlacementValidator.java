package com.example.miniproyecto4.model.Validation;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.IShip;
import java.util.List;

public class ShipPlacementValidator implements IShipPlacementValidator {

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