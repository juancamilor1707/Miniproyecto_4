package com.example.miniproyecto4.model.AI;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Coordinate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * AI strategy implementation using a combination of random and intelligent targeting.
 * Uses a "hunt and target" approach: randomly searches for ships using a checkerboard
 * pattern, then targets adjacent cells when a hit is detected.
 */
public class RandomAIStrategy implements IAIStrategy {

    /**
     * Random number generator for selecting targets.
     */
    private final Random random;

    /**
     * List of coordinates that have not yet been targeted.
     */
    private final List<Coordinate> availableTargets;

    /**
     * Queue of high-priority targets (adjacent to recent hits).
     */
    private final List<Coordinate> targetQueue;

    /**
     * The coordinate of the last successful hit.
     */
    private Coordinate lastHit;

    /**
     * Flag indicating whether the AI is in hunt mode (true) or target mode (false).
     * Hunt mode searches randomly, target mode focuses on adjacent cells.
     */
    private boolean huntMode;

    /**
     * Constructs a new RandomAIStrategy.
     * Initializes all available targets on a 10x10 board and sets hunt mode active.
     */
    public RandomAIStrategy() {
        this.random = new Random();
        this.availableTargets = new ArrayList<>();
        this.targetQueue = new ArrayList<>();
        this.huntMode = true;
        initializeAvailableTargets();
    }

    /**
     * Initializes the list of all available target coordinates on the board.
     * Populates with all coordinates in a 10x10 grid.
     */
    private void initializeAvailableTargets() {
        availableTargets.clear();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                availableTargets.add(new Coordinate(x, y));
            }
        }
    }

    /**
     * Selects the next target coordinate for shooting.
     * If in target mode (after a hit), prioritizes adjacent cells from the target queue.
     * Otherwise, uses hunt mode to select a random checkerboard pattern coordinate.
     *
     * @param opponentBoard the opponent's board (not used in this implementation)
     * @return the selected coordinate, or null if no valid targets remain
     */
    @Override
    public Coordinate selectTarget(IBoard opponentBoard) {

        if (availableTargets.isEmpty() && targetQueue.isEmpty()) {
            return null;
        }

        if (!huntMode && !targetQueue.isEmpty()) {
            Iterator<Coordinate> iterator = targetQueue.iterator();
            while (iterator.hasNext()) {
                Coordinate target = iterator.next();
                iterator.remove();

                if (availableTargets.contains(target)) {
                    return target;
                }
            }
            huntMode = true;
        }

        if (huntMode && !availableTargets.isEmpty()) {
            return selectHuntTarget();
        }

        return null;
    }

    /**
     * Selects a target during hunt mode using an efficient checkerboard pattern.
     * Prioritizes coordinates where (x+y) is even, as ships of size 2+ must occupy
     * at least one such cell. Falls back to random selection if no checkerboard targets remain.
     *
     * @return a randomly selected hunt target, or null if no targets available
     */
    private Coordinate selectHuntTarget() {
        if (availableTargets.isEmpty()) {
            return null;
        }

        List<Coordinate> checkerboardTargets = new ArrayList<>();

        for (Coordinate coord : availableTargets) {
            if ((coord.getX() + coord.getY()) % 2 == 0) {
                checkerboardTargets.add(coord);
            }
        }

        if (checkerboardTargets.isEmpty()) {
            checkerboardTargets = new ArrayList<>(availableTargets);
        }

        if (!checkerboardTargets.isEmpty()) {
            int index = random.nextInt(checkerboardTargets.size());
            return checkerboardTargets.get(index);
        }

        if (!availableTargets.isEmpty()) {
            int index = random.nextInt(availableTargets.size());
            return availableTargets.get(index);
        }

        return null;
    }

    /**
     * Updates the strategy based on the result of the last shot.
     * If the shot was a hit, switches to target mode and adds adjacent cells to the queue.
     * If it was a miss and no targets remain in the queue, switches back to hunt mode.
     *
     * @param lastShot the coordinate of the last shot taken
     * @param wasHit true if the shot was a hit, false if it was a miss
     */
    @Override
    public void updateStrategy(Coordinate lastShot, boolean wasHit) {
        availableTargets.remove(lastShot);

        if (wasHit) {
            huntMode = false;
            lastHit = lastShot;

            addAdjacentTargets(lastShot);
        } else {
            if (targetQueue.isEmpty()) {
                huntMode = true;
                lastHit = null;
            }
        }
    }

    /**
     * Adds all valid adjacent coordinates (up, down, left, right) to the target queue.
     * Only adds coordinates that are within bounds and not already in the queue.
     *
     * @param hit the coordinate that was hit
     */
    private void addAdjacentTargets(Coordinate hit) {
        Coordinate up = new Coordinate(hit.getX(), hit.getY() - 1);
        if (isValidCoordinate(up) && !targetQueue.contains(up)) {
            targetQueue.add(up);
        }

        Coordinate down = new Coordinate(hit.getX(), hit.getY() + 1);
        if (isValidCoordinate(down) && !targetQueue.contains(down)) {
            targetQueue.add(down);
        }

        Coordinate left = new Coordinate(hit.getX() - 1, hit.getY());
        if (isValidCoordinate(left) && !targetQueue.contains(left)) {
            targetQueue.add(left);
        }

        Coordinate right = new Coordinate(hit.getX() + 1, hit.getY());
        if (isValidCoordinate(right) && !targetQueue.contains(right)) {
            targetQueue.add(right);
        }
    }

    /**
     * Checks if a coordinate is within the valid board bounds (0-9 for both x and y).
     *
     * @param coord the coordinate to validate
     * @return true if the coordinate is valid, false otherwise
     */
    private boolean isValidCoordinate(Coordinate coord) {
        return coord.getX() >= 0 && coord.getX() < 10 &&
                coord.getY() >= 0 && coord.getY() < 10;
    }

    /**
     * Resets the strategy to its initial state.
     * Clears all targets and queues, resets hunt mode, and reinitializes available targets.
     */
    @Override
    public void reset() {
        availableTargets.clear();
        targetQueue.clear();
        lastHit = null;
        huntMode = true;
        initializeAvailableTargets();
    }
}