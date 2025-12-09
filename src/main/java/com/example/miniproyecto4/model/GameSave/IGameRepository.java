package com.example.miniproyecto4.model.GameSave;

import com.example.miniproyecto4.model.Game.GameStatus;
import com.example.miniproyecto4.model.Player.IPlayer;

/**
 * Interface defining repository operations for game persistence.
 * Provides methods for saving, loading, and managing saved game data.
 */
public interface IGameRepository {

    /**
     * Saves the current game state to persistent storage.
     * Stores player information, game status, and turn state.
     *
     * @param humanPlayer the human player
     * @param computerPlayer the computer player
     * @param gameStatus the current game status
     * @param isPlayerTurn true if it is the player's turn, false otherwise
     */
    void saveGame(IPlayer humanPlayer, IPlayer computerPlayer, GameStatus gameStatus, boolean isPlayerTurn);

    /**
     * Loads a previously saved game from persistent storage.
     * Retrieves all game state information.
     *
     * @return the serialized game data, or null if no save exists or loading fails
     */
    SerializableGameData loadGame();

    /**
     * Checks if a saved game exists in persistent storage.
     *
     * @return true if a saved game exists, false otherwise
     */
    boolean hasSavedGame();

    /**
     * Deletes the saved game from persistent storage.
     * Removes all game state and player data files.
     */
    void deleteSavedGame();
}