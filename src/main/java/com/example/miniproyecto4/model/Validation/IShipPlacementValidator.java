package com.example.miniproyecto4.model.Validation;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Ship.IShip;

public interface IShipPlacementValidator {

    boolean validate(IBoard board, IShip ship);

    boolean isWithinBounds(IBoard board, IShip ship);

    boolean hasNoOverlap(IBoard board, IShip ship);
}