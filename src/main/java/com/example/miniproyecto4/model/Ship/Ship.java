package com.example.miniproyecto4.model.Ship;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Validation.Orientation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a ship in the Battleship game.
 * Manages ship position, orientation, hit tracking, and sunk status.
 * Implements Serializable to support game state persistence.
 */
public class Ship implements IShip, Serializable {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The type of this ship (Carrier, Submarine, Destroyer, or Frigate).
     */
    private final ShipType type;

    /**
     * The size of the ship in grid cells.
     */
    private final int size;

    /**
     * The starting coordinate of the ship on the board.
     */
    private Coordinate startCoordinate;

    /**
     * The orientation of the ship (horizontal or vertical).
     */
    private Orientation orientation;

    /**
     * Set of coordinates where the ship has been hit.
     */
    private final Set<Coordinate> hitCoordinates;

    /**
     * List of all coordinates occupied by the ship.
     */
    private final List<Coordinate> coordinates;

    /**
     * Constructs a Ship of the specified type without position information.
     * Initializes empty hit coordinates and coordinates lists.
     *
     * @param type the type of ship to create
     */
    public Ship(ShipType type) {
        this.type = type;
        this.size = type.getSize();
        this.hitCoordinates = new HashSet<>();
        this.coordinates = new ArrayList<>();
    }

    /**
     * Constructs a Ship of the specified type with a starting position and orientation.
     * Sets the ship's position and calculates all occupied coordinates.
     *
     * @param type the type of ship to create
     * @param startCoordinate the starting coordinate for the ship
     * @param orientation the orientation of the ship (horizontal or vertical)
     */
    public Ship(ShipType type, Coordinate startCoordinate, Orientation orientation) {
        this(type);
        setPosition(startCoordinate, orientation);
    }

    /**
     * Returns the type of this ship.
     *
     * @return the ship type
     */
    @Override
    public ShipType getType() {
        return type;
    }

    /**
     * Returns the size of the ship in cells.
     *
     * @return the ship size
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Returns the orientation of the ship.
     *
     * @return the ship orientation (horizontal or vertical)
     */
    @Override
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Returns the starting coordinate of the ship.
     *
     * @return the starting coordinate
     */
    @Override
    public Coordinate getStartCoordinate() {
        return startCoordinate;
    }

    /**
     * Returns a copy of all coordinates occupied by the ship.
     *
     * @return a list of all ship coordinates
     */
    @Override
    public List<Coordinate> getCoordinates() {
        return new ArrayList<>(coordinates);
    }

    /**
     * Registers a hit at the specified coordinate if it is part of the ship
     * and has not been hit before.
     *
     * @param coordinate the coordinate where the hit occurred
     * @return true if the hit was successful, false otherwise
     */
    @Override
    public boolean hit(Coordinate coordinate) {
        if (coordinates.contains(coordinate) && !hitCoordinates.contains(coordinate)) {
            hitCoordinates.add(coordinate);
            return true;
        }
        return false;
    }

    /**
     * Checks if the ship has been sunk.
     * A ship is sunk when all of its coordinates have been hit.
     *
     * @return true if the ship is sunk, false otherwise
     */
    @Override
    public boolean isSunk() {
        return hitCoordinates.size() == size;
    }

    /**
     * Checks if the ship has been hit at the specified coordinate.
     *
     * @param coordinate the coordinate to check
     * @return true if the ship has been hit at this coordinate, false otherwise
     */
    @Override
    public boolean isHitAt(Coordinate coordinate) {
        return hitCoordinates.contains(coordinate);
    }

    /**
     * Returns the number of times the ship has been hit.
     *
     * @return the hit count
     */
    @Override
    public int getHitCount() {
        return hitCoordinates.size();
    }

    /**
     * Sets the position and orientation of the ship.
     * Calculates all coordinates occupied by the ship based on its size and orientation.
     *
     * @param coordinate the starting coordinate for the ship
     * @param orientation the orientation of the ship (horizontal or vertical)
     */
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