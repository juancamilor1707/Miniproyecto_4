package com.example.miniproyecto4.model.AI;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Coordinate;

/**
 * Interface for AI strategy implementations.
 * Defines the behavior for computer-controlled shooting decisions.
 */
public interface IAIStrategy {

    /**
     * Selects the next target coordinate for shooting.
     *
     * @param opponentBoard The opponent's board
     * @return The coordinate to shoot at, or null if no valid target exists
     */
    Coordinate selectTarget(IBoard opponentBoard);

    /**
     * Updates the strategy based on the result of the last shot.
     *
     * @param lastShot The coordinate of the last shot
     * @param wasHit true if the shot was a hit, false if it was a miss
     */
    void updateStrategy(Coordinate lastShot, boolean wasHit);

    /**
     * Resets the strategy to its initial state.
     */
    void reset();
}
