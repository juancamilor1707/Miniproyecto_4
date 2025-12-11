package com.example.miniproyecto4.model.Player;

import com.example.miniproyecto4.model.Board.IBoard;

/**
 * Adapter class providing default implementations for IPlayer interface.
 * This adapter allows subclasses to override only the methods they need,
 * following the Adapter design pattern.
 * Provides sensible defaults for player functionality to minimize boilerplate code.
 */
public abstract class PlayerAdapter implements IPlayer {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default implementation that returns an empty string.
     * Subclasses should override this method to return the actual player nickname.
     *
     * @return an empty string by default
     */
    @Override
    public String getNickname() {
        return "";
    }

    /**
     * Default empty implementation.
     * Subclasses should override this method to set the player's nickname.
     *
     * @param nickname the new nickname for the player
     */
    @Override
    public void setNickname(String nickname) {
        // Default: do nothing
    }

    /**
     * Default implementation that returns null.
     * Subclasses should override this method to return the actual game board.
     *
     * @return null by default
     */
    @Override
    public IBoard getBoard() {
        return null;
    }

    /**
     * Default implementation that returns 0.
     * Subclasses should override this method to return the actual count.
     *
     * @return 0 by default
     */
    @Override
    public int getSunkShipsCount() {
        return 0;
    }

    /**
     * Default implementation that returns false.
     * Subclasses should override this method to check actual loss condition.
     *
     * @return false by default
     */
    @Override
    public boolean hasLost() {
        return false;
    }

    /**
     * Default empty implementation.
     * Subclasses should override this method to increment the sunk ships counter.
     */
    @Override
    public void incrementSunkShips() {
        // Default: do nothing
    }

    /**
     * Default empty implementation.
     * Subclasses should override this method to reset player state.
     */
    @Override
    public void reset() {
        // Default: do nothing
    }
}