package com.example.miniproyecto4.model.GameSave;

import com.example.miniproyecto4.model.Game.GameStatus;
import com.example.miniproyecto4.model.Player.IPlayer;
import java.io.*;

/**
 * Repository implementation for managing game state persistence.
 * Combines serialization for game state and flat file storage for player data.
 */
public class GameRepository implements IGameRepository {

    /**
     * The filename used to store serialized game state.
     */
    private static final String SAVE_FILE = "battleship_save.ser";

    /**
     * Serializer instance for handling game data serialization.
     */
    private final ISerializer serializer;

    /**
     * Flat file manager instance for handling player data in text format.
     */
    private final IFlatFileManager flatFileManager;

    /**
     * Constructs a GameRepository with default serializer and flat file manager.
     * Initializes the repository with a GameSerializer and FlatFileManager.
     */
    public GameRepository() {
        this.serializer = new GameSerializer();
        this.flatFileManager = new FlatFileManager();
    }

    /**
     * Saves the current game state to persistent storage.
     * Serializes the game data and saves player information to a flat file.
     *
     * @param humanPlayer the human player
     * @param computerPlayer the computer player
     * @param gameStatus the current game status
     * @param isPlayerTurn true if it is the player's turn, false otherwise
     */
    @Override
    public void saveGame(IPlayer humanPlayer, IPlayer computerPlayer, GameStatus gameStatus, boolean isPlayerTurn) {
        SerializableGameData gameData = new SerializableGameData(humanPlayer, computerPlayer, gameStatus, isPlayerTurn);

        serializer.serialize(gameData, SAVE_FILE);

        flatFileManager.savePlayerData(humanPlayer.getNickname(), humanPlayer.getSunkShipsCount());
    }

    /**
     * Loads a previously saved game from persistent storage.
     * Deserializes the game state from the save file.
     *
     * @return the serialized game data, or null if no save exists or loading fails
     */
    @Override
    public SerializableGameData loadGame() {
        return serializer.deserialize(SAVE_FILE);
    }

    /**
     * Checks if a saved game exists in persistent storage.
     *
     * @return true if the save file exists, false otherwise
     */
    @Override
    public boolean hasSavedGame() {
        File file = new File(SAVE_FILE);
        return file.exists();
    }

    /**
     * Deletes the saved game from persistent storage.
     * Removes both the serialized game file and the player data flat file.
     */
    @Override
    public void deleteSavedGame() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
        }
        flatFileManager.deletePlayerData();
    }
}