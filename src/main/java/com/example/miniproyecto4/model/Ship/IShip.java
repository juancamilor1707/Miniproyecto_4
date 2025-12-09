package com.example.miniproyecto4.model.Ship;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Validation.Orientation;
import java.io.Serializable;
import java.util.List;

/**
 * Interface defining the contract for ships in the Battleship game.
 * Extends Serializable to support game state persistence.
 * Provides methods for managing ship position, orientation, hit tracking, and status.
 */
public interface IShip extends Serializable {

    /**
     * Returns the type of this ship.
     *
     * @return the ship type
     */
    ShipType getType();

    /**
     * Returns the size of the ship in cells.
     *
     * @return the ship size
     */
    int getSize();

    /**
     * Returns the orientation of the ship.
     *
     * @return the ship orientation (horizontal or vertical)
     */
    Orientation getOrientation();

    /**
     * Returns the starting coordinate of the ship.
     *
     * @return the starting coordinate
     */
    Coordinate getStartCoordinate();

    /**
     * Returns all coordinates occupied by the ship.
     *
     * @return a list of all ship coordinates
     */
    List<Coordinate> getCoordinates();

    /**
     * Registers a hit at the specified coordinate.
     *
     * @param coordinate the coordinate where the hit occurred
     * @return true if the hit was successful, false otherwise
     */
    boolean hit(Coordinate coordinate);

    /**
     * Checks if the ship has been sunk.
     * A ship is sunk when all of its coordinates have been hit.
     *
     * @return true if the ship is sunk, false otherwise
     */
    boolean isSunk();

    /**
     * Checks if the ship has been hit at the specified coordinate.
     *
     * @param coordinate the coordinate to check
     * @return true if the ship has been hit at this coordinate, false otherwise
     */
    boolean isHitAt(Coordinate coordinate);

    /**
     * Returns the number of times the ship has been hit.
     *
     * @return the hit count
     */
    int getHitCount();

    /**
     * Sets the position and orientation of the ship.
     * Calculates all coordinates occupied by the ship.
     *
     * @param coordinate the starting coordinate for the ship
     * @param orientation the orientation of the ship (horizontal or vertical)
     */
    void setPosition(Coordinate coordinate, Orientation orientation);
}