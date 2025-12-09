package com.example.miniproyecto4.model.GameSave;

/**
 * Interface defining flat file operations for player data persistence.
 * Provides methods for saving, loading, and deleting player information
 * in text file format.
 */
public interface IFlatFileManager {

    /**
     * Saves player data to a flat file.
     * Stores the player's nickname and number of sunk ships.
     *
     * @param nickname the player's nickname
     * @param sunkShips the number of ships the player has sunk
     */
    void savePlayerData(String nickname, int sunkShips);

    /**
     * Loads player data from the flat file.
     * Retrieves the player's nickname and number of sunk ships.
     *
     * @return an array containing the nickname at index 0 and sunk ships count at index 1,
     *         or null if the file cannot be read
     */
    String[] loadPlayerData();

    /**
     * Deletes the player data file.
     * Removes all stored player information from the file system.
     */
    void deletePlayerData();
}