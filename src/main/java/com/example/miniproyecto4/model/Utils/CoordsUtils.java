package com.example.miniproyecto4.model.Utils;

import com.example.miniproyecto4.model.Cell.Coordinate;

/**
 * Utility class providing coordinate manipulation and conversion methods.
 * Includes functions for distance calculation, adjacency checking,
 * and conversion between grid coordinates and alphanumeric notation.
 * This class cannot be instantiated.
 */
public class CoordsUtils {

    /**
     * Checks if two coordinates are adjacent to each other.
     * Two coordinates are adjacent if they differ by exactly 1 in either
     * the x or y direction (but not diagonally).
     *
     * @param c1 the first coordinate
     * @param c2 the second coordinate
     * @return true if the coordinates are adjacent, false otherwise
     */
    public static boolean isAdjacent(Coordinate c1, Coordinate c2) {
        int dx = Math.abs(c1.getX() - c2.getX());
        int dy = Math.abs(c1.getY() - c2.getY());
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    /**
     * Calculates the Euclidean distance between two coordinates.
     *
     * @param c1 the first coordinate
     * @param c2 the second coordinate
     * @return the distance between the two coordinates
     */
    public static double distance(Coordinate c1, Coordinate c2) {
        int dx = c1.getX() - c2.getX();
        int dy = c1.getY() - c2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Converts a grid coordinate to alphanumeric notation.
     * For example, coordinate (0, 0) becomes "A1", (1, 0) becomes "A2", etc.
     *
     * @param coordinate the coordinate to convert
     * @return the alphanumeric representation of the coordinate
     */
    public static String toAlphanumeric(Coordinate coordinate) {
        char letter = (char) ('A' + coordinate.getY());
        int number = coordinate.getX() + 1;
        return letter + String.valueOf(number);
    }

    /**
     * Converts an alphanumeric coordinate string to a grid coordinate.
     * For example, "A1" becomes coordinate (0, 0), "B3" becomes (2, 1), etc.
     *
     * @param alphanumeric the alphanumeric string to convert (e.g., "A1", "B5")
     * @return the corresponding coordinate, or null if the input is invalid
     */
    public static Coordinate fromAlphanumeric(String alphanumeric) {
        if (alphanumeric == null || alphanumeric.length() < 2) {
            return null;
        }

        char letter = alphanumeric.charAt(0);
        int y = letter - 'A';
        int x = Integer.parseInt(alphanumeric.substring(1)) - 1;

        return new Coordinate(x, y);
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private CoordsUtils() {
    }
}