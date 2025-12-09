package com.example.miniproyecto4.model.Utils;

/**
 * Utility class containing game constants for the Battleship game.
 * Defines board dimensions, ship counts, ship sizes, and file paths.
 * This class cannot be instantiated.
 */
public class Constants {

    /**
     * The size of the game board (10x10 grid).
     */
    public static final int BOARD_SIZE = 10;

    /**
     * The total number of ships in the game fleet.
     */
    public static final int TOTAL_SHIPS = 10;

    /**
     * The number of Carrier ships in the fleet.
     */
    public static final int CARRIER_COUNT = 1;

    /**
     * The number of Submarine ships in the fleet.
     */
    public static final int SUBMARINE_COUNT = 2;

    /**
     * The number of Destroyer ships in the fleet.
     */
    public static final int DESTROYER_COUNT = 3;

    /**
     * The number of Frigate ships in the fleet.
     */
    public static final int FRIGATE_COUNT = 4;

    /**
     * The size of a Carrier ship in cells.
     */
    public static final int CARRIER_SIZE = 4;

    /**
     * The size of a Submarine ship in cells.
     */
    public static final int SUBMARINE_SIZE = 3;

    /**
     * The size of a Destroyer ship in cells.
     */
    public static final int DESTROYER_SIZE = 2;

    /**
     * The size of a Frigate ship in cells.
     */
    public static final int FRIGATE_SIZE = 1;

    /**
     * The filename for the serialized game save file.
     */
    public static final String SAVE_FILE = "battleship_save.ser";

    /**
     * The filename for the player data text file.
     */
    public static final String PLAYER_DATA_FILE = "player_data.txt";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Constants() {
    }
}