package com.example.miniproyecto4.model.Game;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Player.IPlayer;
import com.example.miniproyecto4.model.Shot.ShotResult;

/**
 * Adapter class providing default implementations for IGameManager interface.
 * This adapter allows subclasses to override only the methods they need,
 * following the Adapter design pattern.
 * Provides sensible defaults for game management functionality to minimize boilerplate code.
 * Useful for creating specialized game managers or testing implementations.
 */
public abstract class GameManagerAdapter implements IGameManager {

    /**
     * Default empty implementation.
     * Subclasses should override this method to initialize a new game.
     *
     * @param playerNickname the nickname for the human player
     */
    @Override
    public void startNewGame(String playerNickname) {
        // Default: do nothing
    }

    /**
     * Default empty implementation.
     * Subclasses should override this method to load a saved game.
     */
    @Override
    public void loadGame() {
        // Default: do nothing
    }

    /**
     * Default implementation that returns INVALID.
     * Subclasses should override this method to process actual player shots.
     *
     * @param coordinate the target coordinate for the shot
     * @return INVALID by default
     */
    @Override
    public ShotResult processPlayerShot(Coordinate coordinate) {
        return ShotResult.INVALID;
    }

    /**
     * Default implementation that returns INVALID.
     * Subclasses should override this method to process actual computer shots.
     *
     * @return INVALID by default
     */
    @Override
    public ShotResult processComputerShot() {
        return ShotResult.INVALID;
    }

    /**
     * Default implementation that returns null.
     * Subclasses should override this method to return the actual human player.
     *
     * @return null by default
     */
    @Override
    public IPlayer getHumanPlayer() {
        return null;
    }

    /**
     * Default implementation that returns null.
     * Subclasses should override this method to return the actual computer player.
     *
     * @return null by default
     */
    @Override
    public IPlayer getComputerPlayer() {
        return null;
    }

    /**
     * Default implementation that returns SETUP.
     * Subclasses should override this method to return the actual game status.
     *
     * @return GameStatus.SETUP by default
     */
    @Override
    public GameStatus getGameStatus() {
        return GameStatus.SETUP;
    }

    /**
     * Default implementation that returns true.
     * Subclasses should override this method to check actual turn state.
     *
     * @return true by default
     */
    @Override
    public boolean isPlayerTurn() {
        return true;
    }

    /**
     * Default empty implementation.
     * Subclasses should override this method to switch turns between players.
     */
    @Override
    public void switchTurn() {
        // Default: do nothing
    }

    /**
     * Default empty implementation.
     * Subclasses should override this method to save game state.
     */
    @Override
    public void saveGame() {
        // Default: do nothing
    }

    /**
     * Default implementation that returns false.
     * Subclasses should override this method to check for actual winner.
     *
     * @return false by default
     */
    @Override
    public boolean hasWinner() {
        return false;
    }

    /**
     * Default implementation that returns null.
     * Subclasses should override this method to return the actual winner.
     *
     * @return null by default
     */
    @Override
    public IPlayer getWinner() {
        return null;
    }

    /**
     * Default empty implementation.
     * Subclasses should override this method to reset game state.
     */
    @Override
    public void resetGame() {
        // Default: do nothing
    }

    /**
     * Default implementation that returns null.
     * Subclasses should override this method to return the last computer shot.
     *
     * @return null by default
     */
    @Override
    public Coordinate getLastComputerShot() {
        return null;
    }
}