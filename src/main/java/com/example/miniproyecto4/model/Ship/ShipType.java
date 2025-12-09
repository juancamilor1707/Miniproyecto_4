package com.example.miniproyecto4.model.Ship;

/**
 * Enumeration representing the different types of ships in the Battleship game.
 * Each ship type has a specific size and display name.
 */
public enum ShipType {
    /**
     * Carrier ship with size 4 and display name "Portaaviones".
     */
    CARRIER(4, "Portaaviones"),

    /**
     * Submarine ship with size 3 and display name "Submarino".
     */
    SUBMARINE(3, "Submarino"),

    /**
     * Destroyer ship with size 2 and display name "Destructor".
     */
    DESTROYER(2, "Destructor"),

    /**
     * Frigate ship with size 1 and display name "Fragata".
     */
    FRIGATE(1, "Fragata");

    /**
     * The size of the ship in grid cells.
     */
    private final int size;

    /**
     * The display name of the ship in Spanish.
     */
    private final String displayName;

    /**
     * Constructs a ShipType with the specified size and display name.
     *
     * @param size the size of the ship in cells
     * @param displayName the display name of the ship
     */
    ShipType(int size, String displayName) {
        this.size = size;
        this.displayName = displayName;
    }

    /**
     * Returns the size of the ship.
     *
     * @return the ship size in cells
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the display name of the ship.
     *
     * @return the ship's display name
     */
    public String getDisplayName() {
        return displayName;
    }
}