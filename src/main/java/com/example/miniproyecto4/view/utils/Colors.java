package com.example.miniproyecto4.view.utils;

/**
 * Utility class containing color constants for the game's visual elements.
 * All colors are defined in hexadecimal format for use with JavaFX.
 * This class cannot be instantiated.
 */
public class Colors {

    /**
     * Color for water cells (light blue).
     */
    public static final String WATER = "#4A90E2";

    /**
     * Color for ship segments (gray).
     */
    public static final String SHIP = "#7C7C7C";

    /**
     * Color for hit cells (red).
     */
    public static final String HIT = "#E74C3C";

    /**
     * Color for miss cells (white).
     */
    public static final String MISS = "#FFFFFF";

    /**
     * Color for sunk ship cells (dark gray).
     */
    public static final String SUNK = "#34495E";

    /**
     * Color for cell hover effect (lighter blue).
     */
    public static final String HOVER = "#5DADE2";

    /**
     * Color for grid lines (dark blue-gray).
     */
    public static final String GRID = "#2C3E50";

    /**
     * Color for hit marker indicators (dark red).
     */
    public static final String HIT_MARKER = "#C0392B";

    /**
     * Color for sunk marker indicators (very dark gray).
     */
    public static final String SUNK_MARKER = "#1C2833";

    /**
     * Color for ship borders (dark blue-gray).
     */
    public static final String SHIP_BORDER = "#2C3E50";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Colors() {
    }
}