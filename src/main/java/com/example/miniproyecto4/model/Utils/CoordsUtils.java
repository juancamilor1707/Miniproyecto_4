package com.example.miniproyecto4.model.Utils;

import com.example.miniproyecto4.model.Cell.Coordinate;

public class CoordsUtils {

    public static boolean isAdjacent(Coordinate c1, Coordinate c2) {
        int dx = Math.abs(c1.getX() - c2.getX());
        int dy = Math.abs(c1.getY() - c2.getY());
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    public static double distance(Coordinate c1, Coordinate c2) {
        int dx = c1.getX() - c2.getX();
        int dy = c1.getY() - c2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static String toAlphanumeric(Coordinate coordinate) {
        char letter = (char) ('A' + coordinate.getY());
        int number = coordinate.getX() + 1;
        return letter + String.valueOf(number);
    }

    public static Coordinate fromAlphanumeric(String alphanumeric) {
        if (alphanumeric == null || alphanumeric.length() < 2) {
            return null;
        }

        char letter = alphanumeric.charAt(0);
        int y = letter - 'A';
        int x = Integer.parseInt(alphanumeric.substring(1)) - 1;

        return new Coordinate(x, y);
    }

    private CoordsUtils() {
    }
}