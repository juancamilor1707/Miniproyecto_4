package com.example.miniproyecto4.model.Player;

import com.example.miniproyecto4.model.Board.Board;
import com.example.miniproyecto4.model.Board.IBoard;
import java.io.Serializable;

/**
 * Represents a player in the Battleship game.
 * Manages player information including nickname, game board, and ship sinking statistics.
 * Implements Serializable to support game state persistence.
 */
public class Player extends PlayerAdapter implements Serializable {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The player's nickname or display name.
     */
    private String nickname;

    /**
     * The game board associated with this player where ships are placed and shots are tracked.
     */
    private final IBoard board;

    /**
     * The number of opponent's ships that this player has successfully sunk.
     */
    private int sunkShipsCount;

    /**
     * Constructs a Player with the specified nickname.
     * Initializes the player with a new game board and zero sunk ships.
     *
     * @param nickname the player's nickname
     */
    public Player(String nickname) {
        this.nickname = nickname;
        this.board = new Board();
        this.sunkShipsCount = 0;
    }

    /**
     * Constructs a Player with the specified nickname and board.
     * Initializes the player with the provided game board and zero sunk ships.
     *
     * @param nickname the player's nickname
     * @param board the game board to use for this player
     */
    public Player(String nickname, IBoard board) {
        this.nickname = nickname;
        this.board = board;
        this.sunkShipsCount = 0;
    }

    /**
     * Returns the player's nickname.
     *
     * @return the player's nickname
     */
    @Override
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the player's nickname.
     *
     * @param nickname the new nickname for the player
     */
    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Returns the game board associated with this player.
     *
     * @return the player's game board
     */
    @Override
    public IBoard getBoard() {
        return board;
    }

    /**
     * Returns the number of opponent's ships that this player has sunk.
     *
     * @return the count of sunk ships
     */
    @Override
    public int getSunkShipsCount() {
        return sunkShipsCount;
    }

    /**
     * Checks if the player has lost the game.
     * A player loses when all of their ships have been sunk.
     *
     * @return true if all ships on the player's board are sunk, false otherwise
     */
    @Override
    public boolean hasLost() {
        return board.allShipsSunk();
    }

    /**
     * Increments the count of ships that this player has sunk.
     * Called when the player successfully sinks an opponent's ship.
     */
    @Override
    public void incrementSunkShips() {
        this.sunkShipsCount++;
    }

    /**
     * Resets the player to their initial state.
     * Clears the board and resets the sunk ships count to zero.
     */
    @Override
    public void reset() {
        board.reset();
        sunkShipsCount = 0;
    }
}