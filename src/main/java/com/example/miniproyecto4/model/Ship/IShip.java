package com.example.miniproyecto4.model.Ship;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Validation.Orientation;
import java.io.Serializable;
import java.util.List;

public interface IShip extends Serializable {

    ShipType getType();

    int getSize();

    Orientation getOrientation();

    Coordinate getStartCoordinate();

    List<Coordinate> getCoordinates();

    boolean hit(Coordinate coordinate);

    boolean isSunk();

    boolean isHitAt(Coordinate coordinate);

    int getHitCount();

    void setPosition(Coordinate coordinate, Orientation orientation);
}