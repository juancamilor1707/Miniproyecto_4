package com.example.miniproyecto4.model.Validation;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Coordinate;

public interface IShotValidator {

    boolean validate(IBoard board, Coordinate coordinate);

    boolean isValidCoordinate(IBoard board, Coordinate coordinate);

    boolean isNotAlreadyShot(IBoard board, Coordinate coordinate);
}