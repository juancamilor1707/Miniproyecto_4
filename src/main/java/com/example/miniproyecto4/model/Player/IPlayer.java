package com.example.miniproyecto4.model.Player;

import com.example.miniproyecto4.model.Board.IBoard;
import java.io.Serializable;

/**
 * Interface defining the contract for a player in the Battleship game.
 * Extends Serializable to support game state persistence.
 * Provides methods for managing player identity, board access, and game statistics.
 */
public interface IPlayer extends Serializable {

    /**
     * Returns the player's nickname.
     *
     * @return the player's nickname
     */
    String getNickname();

    /**
     * Sets the player's nickname.
     *
     * @param nickname the new nickname for the player
     */
    void setNickname(String nickname);

    /**
     * Returns the game board associated with this player.
     *
     * @return the player's game board
     */
    IBoard getBoard();

    /**
     * Returns the number of opponent's ships that this player has sunk.
     *
     * @return the count of sunk ships
     */
    int getSunkShipsCount();

    /**
     * Checks if the player has lost the game.
     * A player loses when all of their ships have been sunk.
     *
     * @return true if the player has lost, false otherwise
     */
    boolean hasLost();

    /**
     * Increments the count of ships that this player has sunk.
     * Called when the player successfully sinks an opponent's ship.
     */
    void incrementSunkShips();

    /**
     * Resets the player to their initial state.
     * Clears the board and resets all statistics.
     */
    void reset();
}