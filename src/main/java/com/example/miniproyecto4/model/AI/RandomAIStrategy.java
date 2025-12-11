package com.example.miniproyecto4.model.AI;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Cell;
import com.example.miniproyecto4.model.Cell.CellStatus;
import com.example.miniproyecto4.model.Cell.Coordinate;
import java.util.*;

/**
 * AI strategy implementation using a combination of random and intelligent targeting.
 * Uses a "hunt and target" approach: randomly searches for ships using a checkerboard
 * pattern, then targets adjacent cells when a hit is detected.
 * Improved to handle edge cases when ships are partially surrounded by misses.
 */
public class RandomAIStrategy extends AIStrategyAdapter {

    /**
     * Random number generator for selecting hunt targets.
     */
    private final Random random;

    /**
     * List of coordinates that haven't been shot at yet.
     */
    private final List<Coordinate> availableTargets;

    /**
     * Queue of coordinates to target after a hit (adjacent cells to pursue).
     */
    private final List<Coordinate> targetQueue;

    /**
     * Chain of consecutive hits on the current ship being targeted.
     */
    private final List<Coordinate> currentHitChain;

    /**
     * The first hit coordinate in the current targeting chain.
     */
    private Coordinate firstHitInChain;

    /**
     * Flag indicating whether the AI is in hunt mode (searching) or target mode (pursuing).
     */
    private boolean huntMode;

    /**
     * Reference to the last opponent board for checking cell states.
     */
    private IBoard lastOpponentBoard;

    /**
     * Constructs a new RandomAIStrategy with default settings.
     * Initializes the AI in hunt mode with all 10x10 board coordinates available.
     */
    public RandomAIStrategy() {
        this.random = new Random();
        this.availableTargets = new ArrayList<>();
        this.targetQueue = new ArrayList<>();
        this.currentHitChain = new ArrayList<>();
        this.huntMode = true;
        initializeAvailableTargets();
    }

    /**
     * Initializes the list of all available target coordinates on a 10x10 board.
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
     * Selects the next target coordinate based on current strategy mode.
     * In target mode (after a hit), prioritizes cells from the target queue and
     * attempts to extend the hit chain. In hunt mode, uses a checkerboard pattern.
     *
     * @param opponentBoard the opponent's board to analyze
     * @return the selected coordinate to target, or null if no valid targets remain
     */
    @Override
    public Coordinate selectTarget(IBoard opponentBoard) {
        this.lastOpponentBoard = opponentBoard;

        if (availableTargets.isEmpty() && targetQueue.isEmpty()) {
            return null;
        }

        // Target mode: pursuing hits
        if (!huntMode && !targetQueue.isEmpty()) {
            Iterator<Coordinate> iterator = targetQueue.iterator();
            while (iterator.hasNext()) {
                Coordinate target = iterator.next();
                iterator.remove();

                if (availableTargets.contains(target)) {
                    return target;
                }
            }
        }

        // If target queue is empty but we have active hits, look for more options
        if (!huntMode && !currentHitChain.isEmpty()) {
            Coordinate extended = findExtendedTarget();
            if (extended != null) {
                return extended;
            }

            // Check if the ship is sunk
            if (isChainSunk()) {
                resetToHuntMode();
            }
        }

        // Hunt mode: search with checkerboard pattern
        if (huntMode && !availableTargets.isEmpty()) {
            return selectHuntTarget();
        }

        // Fallback to hunt mode
        if (!availableTargets.isEmpty()) {
            huntMode = true;
            currentHitChain.clear();
            firstHitInChain = null;
            return selectHuntTarget();
        }

        return null;
    }

    /**
     * Finds an extended target when no adjacent cells are available.
     * Attempts to continue firing along the direction of a hit chain,
     * skipping up to 2 cells to find the next valid target.
     * Checks for blocking misses along the path.
     *
     * @return an extended target coordinate, or null if none found
     */
    private Coordinate findExtendedTarget() {
        if (currentHitChain.size() < 2) {
            return null;
        }

        // Determine ship direction from first two hits
        Coordinate first = currentHitChain.get(0);
        Coordinate second = currentHitChain.get(1);

        int dx = Integer.compare(second.getX() - first.getX(), 0);
        int dy = Integer.compare(second.getY() - first.getY(), 0);

        if (dx == 0 && dy == 0) {
            return null;
        }

        // Try extending from the last hit
        Coordinate last = currentHitChain.get(currentHitChain.size() - 1);
        for (int dist = 1; dist <= 2; dist++) {
            Coordinate extended = new Coordinate(
                    last.getX() + (dx * dist),
                    last.getY() + (dy * dist)
            );

            if (isValidCoordinate(extended) && availableTargets.contains(extended)) {
                // Check that there's no MISS blocking the path
                boolean blocked = false;
                for (int i = 1; i < dist; i++) {
                    Coordinate intermediate = new Coordinate(
                            last.getX() + (dx * i),
                            last.getY() + (dy * i)
                    );
                    if (isMiss(intermediate)) {
                        blocked = true;
                        break;
                    }
                }
                if (!blocked) {
                    return extended;
                }
            }
        }

        // Try extending from the first hit in opposite direction
        for (int dist = 1; dist <= 2; dist++) {
            Coordinate extended = new Coordinate(
                    first.getX() - (dx * dist),
                    first.getY() - (dy * dist)
            );

            if (isValidCoordinate(extended) && availableTargets.contains(extended)) {
                boolean blocked = false;
                for (int i = 1; i < dist; i++) {
                    Coordinate intermediate = new Coordinate(
                            first.getX() - (dx * i),
                            first.getY() - (dy * i)
                    );
                    if (isMiss(intermediate)) {
                        blocked = true;
                        break;
                    }
                }
                if (!blocked) {
                    return extended;
                }
            }
        }

        return null;
    }

    /**
     * Checks if a coordinate contains a miss.
     *
     * @param coord the coordinate to check
     * @return true if the coordinate has been shot and was a miss, false otherwise
     */
    private boolean isMiss(Coordinate coord) {
        if (lastOpponentBoard == null) {
            return false;
        }
        Cell cell = lastOpponentBoard.getCell(coord);
        return cell != null && cell.getStatus() == CellStatus.MISS;
    }

    /**
     * Checks if the current hit chain represents a completely sunk ship.
     * Verifies that all coordinates in the hit chain have SUNK status.
     *
     * @return true if all hits in the chain are marked as sunk, false otherwise
     */
    private boolean isChainSunk() {
        if (lastOpponentBoard == null || currentHitChain.isEmpty()) {
            return false;
        }

        for (Coordinate hit : currentHitChain) {
            Cell cell = lastOpponentBoard.getCell(hit);
            if (cell == null || cell.getStatus() != CellStatus.SUNK) {
                return false;
            }
        }
        return true;
    }

    /**
     * Selects a target during hunt mode using a checkerboard pattern.
     * Prioritizes coordinates where (x + y) is even for optimal ship detection,
     * as this pattern ensures hitting any ship of size 2 or larger.
     *
     * @return a randomly selected hunt target coordinate, or null if none available
     */
    private Coordinate selectHuntTarget() {
        if (availableTargets.isEmpty()) {
            return null;
        }

        // Prioritize checkerboard pattern (x+y is even)
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
     * Updates the AI strategy based on the result of the last shot.
     * On a hit, switches to target mode and adds adjacent cells to pursue.
     * On a miss, checks if the target queue is empty and whether to return to hunt mode.
     * Automatically detects when a ship is sunk and resets to hunt mode.
     *
     * @param lastShot the coordinate that was just shot
     * @param wasHit true if the shot hit a ship, false if it was a miss
     */
    @Override
    public void updateStrategy(Coordinate lastShot, boolean wasHit) {
        availableTargets.remove(lastShot);

        if (wasHit) {
            huntMode = false;

            // Add to hit chain
            if (firstHitInChain == null) {
                firstHitInChain = lastShot;
                currentHitChain.clear();
            }
            currentHitChain.add(lastShot);

            // Add adjacent cells to target queue
            addAdjacentTargets(lastShot);

            // If we have 2+ hits, check if they're all sunk
            if (currentHitChain.size() >= 2 && isChainSunk()) {
                resetToHuntMode();
            }
        } else {
            // If we missed and no more targets in queue, check status
            if (targetQueue.isEmpty()) {
                if (currentHitChain.isEmpty() || isChainSunk()) {
                    resetToHuntMode();
                }
            }
        }
    }

    /**
     * Adds all valid adjacent coordinates (up, down, left, right) to the target queue.
     * Only adds coordinates that are within bounds, available, and not already queued.
     *
     * @param hit the coordinate that was hit
     */
    private void addAdjacentTargets(Coordinate hit) {
        Coordinate up = new Coordinate(hit.getX(), hit.getY() - 1);
        if (isValidCoordinate(up) && !targetQueue.contains(up) && availableTargets.contains(up)) {
            targetQueue.add(up);
        }

        Coordinate down = new Coordinate(hit.getX(), hit.getY() + 1);
        if (isValidCoordinate(down) && !targetQueue.contains(down) && availableTargets.contains(down)) {
            targetQueue.add(down);
        }

        Coordinate left = new Coordinate(hit.getX() - 1, hit.getY());
        if (isValidCoordinate(left) && !targetQueue.contains(left) && availableTargets.contains(left)) {
            targetQueue.add(left);
        }

        Coordinate right = new Coordinate(hit.getX() + 1, hit.getY());
        if (isValidCoordinate(right) && !targetQueue.contains(right) && availableTargets.contains(right)) {
            targetQueue.add(right);
        }
    }

    /**
     * Checks if a coordinate is within valid board bounds (0-9 for both x and y).
     *
     * @param coord the coordinate to validate
     * @return true if the coordinate is within the 10x10 board, false otherwise
     */
    private boolean isValidCoordinate(Coordinate coord) {
        return coord.getX() >= 0 && coord.getX() < 10 &&
                coord.getY() >= 0 && coord.getY() < 10;
    }

    /**
     * Resets the AI to hunt mode, clearing all target data.
     * Clears the hit chain, target queue, and first hit reference.
     */
    private void resetToHuntMode() {
        huntMode = true;
        firstHitInChain = null;
        currentHitChain.clear();
        targetQueue.clear();
    }

    /**
     * Resets the strategy to its initial state.
     * Clears all targets, hit chains, and reinitializes available targets to the full board.
     * Returns the AI to hunt mode.
     */
    @Override
    public void reset() {
        availableTargets.clear();
        targetQueue.clear();
        currentHitChain.clear();
        firstHitInChain = null;
        huntMode = true;
        lastOpponentBoard = null;
        initializeAvailableTargets();
    }
}