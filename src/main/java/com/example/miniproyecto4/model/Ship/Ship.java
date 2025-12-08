package com.example.miniproyecto4.model.Ship;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Validation.Orientation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ship implements IShip, Serializable {

    private static final long serialVersionUID = 1L;

    private final ShipType type;
    private final int size;
    private Coordinate startCoordinate;
    private Orientation orientation;
    private final Set<Coordinate> hitCoordinates;
    private final List<Coordinate> coordinates;

    public Ship(ShipType type) {
        this.type = type;
        this.size = type.getSize();
        this.hitCoordinates = new HashSet<>();
        this.coordinates = new ArrayList<>();
    }

    public Ship(ShipType type, Coordinate startCoordinate, Orientation orientation) {
        this(type);
        setPosition(startCoordinate, orientation);
    }

    @Override
    public ShipType getType() {
        return type;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public Coordinate getStartCoordinate() {
        return startCoordinate;
    }

    @Override
    public List<Coordinate> getCoordinates() {
        return new ArrayList<>(coordinates);
    }

    @Override
    public boolean hit(Coordinate coordinate) {
        if (coordinates.contains(coordinate) && !hitCoordinates.contains(coordinate)) {
            hitCoordinates.add(coordinate);
            return true;
        }
        return false;
    }

    @Override
    public boolean isSunk() {
        return hitCoordinates.size() == size;
    }

    @Override
    public boolean isHitAt(Coordinate coordinate) {
        return hitCoordinates.contains(coordinate);
    }

    @Override
    public int getHitCount() {
        return hitCoordinates.size();
    }

    @Override
    public void setPosition(Coordinate coordinate, Orientation orientation) {
        this.startCoordinate = coordinate;
        this.orientation = orientation;
        this.coordinates.clear();

        for (int i = 0; i < size; i++) {
            if (orientation == Orientation.HORIZONTAL) {
                coordinates.add(new Coordinate(coordinate.getX() + i, coordinate.getY()));
            } else {
                coordinates.add(new Coordinate(coordinate.getX(), coordinate.getY() + i));
            }
        }
    }
}