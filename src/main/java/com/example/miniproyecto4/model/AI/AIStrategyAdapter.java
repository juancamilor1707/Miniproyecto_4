package com.example.miniproyecto4.model.AI;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Coordinate;

/**
 * Adapter class providing default empty implementations for IAIStrategy interface.
 * This adapter allows subclasses to override only the methods they need,
 * following the Adapter design pattern.
 * Useful for creating simple or specialized AI strategies without implementing all methods.
 */
public abstract class AIStrategyAdapter implements IAIStrategy {

    /**
     * Default implementation that returns null.
     * Subclasses should override this method to provide actual target selection logic.
     *
     * @param opponentBoard the opponent's board
     * @return null by default, subclasses should return a valid coordinate
     */
    @Override
    public Coordinate selectTarget(IBoard opponentBoard) {
        return null;
    }

    /**
     * Default empty implementation.
     * Subclasses should override this method to update their strategy based on shot results.
     *
     * @param lastShot the coordinate of the last shot
     * @param wasHit true if the shot was a hit, false if it was a miss
     */
    @Override
    public void updateStrategy(Coordinate lastShot, boolean wasHit) {
        // Default: do nothing
    }

    /**
     * Default empty implementation.
     * Subclasses should override this method to reset their internal state.
     */
    @Override
    public void reset() {
        // Default: do nothing
    }
}