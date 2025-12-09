package com.example.miniproyecto4.model.Game;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Player.IPlayer;
import com.example.miniproyecto4.model.Shot.ShotResult;

/**
 * Interface defining the contract for game management operations in Battleship.
 * Provides methods for game lifecycle, turn management, shot processing,
 * and game state persistence.
 */
public interface IGameManager {

    /**
     * Starts a new game with the specified player nickname.
     * Initializes both players, places ships, and sets up initial game state.
     *
     * @param playerNickname the nickname for the human player
     */
    void startNewGame(String playerNickname);

    /**
     * Loads a previously saved game from persistent storage.
     * Restores players, game status, and turn information.
     */
    void loadGame();

    /**
     * Processes a shot from the human player at the specified coordinate.
     *
     * @param coordinate the target coordinate for the shot
     * @return the result of the shot (INVALID, WATER, HIT, or SUNK)
     */
    ShotResult processPlayerShot(Coordinate coordinate);

    /**
     * Processes a shot from the computer player.
     * Uses AI strategy to select a target and processes the shot.
     *
     * @return the result of the shot (INVALID, WATER, HIT, or SUNK)
     */
    ShotResult processComputerShot();

    /**
     * Returns the human player instance.
     *
     * @return the human player
     */
    IPlayer getHumanPlayer();

    /**
     * Returns the computer player instance.
     *
     * @return the computer player
     */
    IPlayer getComputerPlayer();

    /**
     * Returns the current game status.
     *
     * @return the current game status
     */
    GameStatus getGameStatus();

    /**
     * Checks if it is currently the human player's turn.
     *
     * @return true if it is the player's turn, false otherwise
     */
    boolean isPlayerTurn();

    /**
     * Switches the turn between the human player and computer player.
     */
    void switchTurn();

    /**
     * Saves the current game state to persistent storage.
     */
    void saveGame();

    /**
     * Checks if the game has a winner.
     *
     * @return true if either player or computer has won, false otherwise
     */
    boolean hasWinner();

    /**
     * Returns the winner of the game.
     *
     * @return the winning player, or null if there is no winner yet
     */
    IPlayer getWinner();

    /**
     * Resets the game to its initial state.
     * Clears both player boards and reinitializes game settings.
     */
    void resetGame();

    /**
     * Returns the last coordinate where the computer player took a shot.
     *
     * @return the last computer shot coordinate
     */
    Coordinate getLastComputerShot();
}