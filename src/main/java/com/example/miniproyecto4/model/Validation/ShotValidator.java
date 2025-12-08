package com.example.miniproyecto4.model.Validation;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Cell;
import com.example.miniproyecto4.model.Cell.Coordinate;

public class ShotValidator implements IShotValidator {

    @Override
    public boolean validate(IBoard board, Coordinate coordinate) {
        if (board == null || coordinate == null) {
            return false;
        }

        return isValidCoordinate(board, coordinate) && isNotAlreadyShot(board, coordinate);
    }

    @Override
    public boolean isValidCoordinate(IBoard board, Coordinate coordinate) {
        return board.isValidCoordinate(coordinate);
    }

    @Override
    public boolean isNotAlreadyShot(IBoard board, Coordinate coordinate) {
        Cell cell = board.getCell(coordinate);

        if (cell == null) {
            return false;
        }

        return !cell.isHit() && !cell.isMiss();
    }
}