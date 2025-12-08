package com.example.miniproyecto4.model.Board;

import com.example.miniproyecto4.model.Cell.Cell;
import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.IShip;
import java.io.Serializable;
import java.util.List;

public interface IBoard extends Serializable {

    int getSize();

    Cell getCell(int x, int y);

    Cell getCell(Coordinate coordinate);

    boolean placeShip(IShip ship);

    boolean removeShip(IShip ship);

    List<IShip> getShips();

    IShip getShipAt(Coordinate coordinate);

    boolean hasShipAt(Coordinate coordinate);

    boolean isValidCoordinate(Coordinate coordinate);

    int getSunkShipsCount();

    boolean allShipsSunk();

    void reset();
}