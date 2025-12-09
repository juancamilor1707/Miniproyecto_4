package com.example.miniproyecto4.model.GameSave;

import java.io.*;

/**
 * Manages flat file operations for storing and retrieving player data.
 * Implements simple text-based persistence for player information.
 */
public class FlatFileManager implements IFlatFileManager {

    /**
     * The filename used to store player data in text format.
     */
    private static final String PLAYER_DATA_FILE = "player_data.txt";

    /**
     * Saves player data to a flat file.
     * Writes the player's nickname and number of sunk ships to a text file.
     *
     * @param nickname the player's nickname
     * @param sunkShips the number of ships the player has sunk
     */
    @Override
    public void savePlayerData(String nickname, int sunkShips) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PLAYER_DATA_FILE))) {
            writer.println(nickname);
            writer.println(sunkShips);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads player data from the flat file.
     * Reads the player's nickname and number of sunk ships from the text file.
     *
     * @return an array containing the nickname at index 0 and sunk ships count at index 1,
     *         or null if the file cannot be read
     */
    @Override
    public String[] loadPlayerData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PLAYER_DATA_FILE))) {
            String nickname = reader.readLine();
            String sunkShips = reader.readLine();
            return new String[]{nickname, sunkShips};
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes the player data file if it exists.
     * Removes all stored player information from the file system.
     */
    @Override
    public void deletePlayerData() {
        File file = new File(PLAYER_DATA_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}