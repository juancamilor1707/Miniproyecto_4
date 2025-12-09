package com.example.miniproyecto4.model.Cell;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a coordinate position on the game board.
 * Uses a 2D coordinate system with x (column) and y (row) values.
 * Implements Serializable to support game state persistence.
 * Immutable class with proper equals and hashCode implementations for use in collections.
 */
public class Coordinate implements Serializable {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The x coordinate (column position).
     */
    private final int x;

    /**
     * The y coordinate (row position).
     */
    private final int y;

    /**
     * Constructs a new coordinate with the specified x and y values.
     *
     * @param x the x coordinate (column)
     * @param y the y coordinate (row)
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate.
     *
     * @return the x value (column)
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate.
     *
     * @return the y value (row)
     */
    public int getY() {
        return y;
    }

    /**
     * Compares this coordinate with another object for equality.
     * Two coordinates are equal if they have the same x and y values.
     *
     * @param o the object to compare with
     * @return true if the coordinates are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x && y == that.y;
    }

    /**
     * Returns a hash code value for this coordinate.
     * Ensures proper behavior in hash-based collections.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Returns a string representation of this coordinate.
     *
     * @return a string in the format "(x,y)"
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}