package com.example.miniproyecto4.model.GameSave;

import com.example.miniproyecto4.model.Game.GameStatus;
import com.example.miniproyecto4.model.Player.IPlayer;
import java.io.Serializable;

/**
 * Data transfer object for serializing and deserializing game state.
 * Encapsulates all necessary game information for persistence.
 */
public class SerializableGameData implements Serializable {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The human player participating in the game.
     */
    private final IPlayer humanPlayer;

    /**
     * The computer player participating in the game.
     */
    private final IPlayer computerPlayer;

    /**
     * The current status of the game.
     */
    private final GameStatus gameStatus;

    /**
     * Flag indicating whether it is currently the human player's turn.
     */
    private final boolean isPlayerTurn;

    /**
     * Constructs a SerializableGameData object with the specified game state.
     *
     * @param humanPlayer the human player
     * @param computerPlayer the computer player
     * @param gameStatus the current game status
     * @param isPlayerTurn true if it is the player's turn, false otherwise
     */
    public SerializableGameData(IPlayer humanPlayer, IPlayer computerPlayer, GameStatus gameStatus, boolean isPlayerTurn) {
        this.humanPlayer = humanPlayer;
        this.computerPlayer = computerPlayer;
        this.gameStatus = gameStatus;
        this.isPlayerTurn = isPlayerTurn;
    }

    /**
     * Returns the human player.
     *
     * @return the human player
     */
    public IPlayer getHumanPlayer() {
        return humanPlayer;
    }

    /**
     * Returns the computer player.
     *
     * @return the computer player
     */
    public IPlayer getComputerPlayer() {
        return computerPlayer;
    }

    /**
     * Returns the current game status.
     *
     * @return the game status
     */
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    /**
     * Checks if it is currently the human player's turn.
     *
     * @return true if it is the player's turn, false otherwise
     */
    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }
}