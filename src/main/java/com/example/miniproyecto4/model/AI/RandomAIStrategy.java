package com.example.miniproyecto4.model.AI;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Cell;
import com.example.miniproyecto4.model.Cell.CellStatus;
import com.example.miniproyecto4.model.Cell.Coordinate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Elite AI strategy implementation using military-grade battleship tactics.
 * Implements a sophisticated multi-layered decision system:
 * <ol>
 * <li>Probability Density Analysis - Statistical ship placement prediction</li>
 * <li>Hunt Mode - Optimal search patterns using parity and density mapping</li>
 * <li>Target Mode - AGGRESSIVE pursuit - stays on the ship until it's destroyed</li>
 * <li>Destroy Mode - Systematic elimination with directional attacks</li>
 * </ol>
 * <p>
 * Key Feature: Once a ship is hit, the AI will NOT leave it until it's completely sunk.
 * </p>
 *
 * @author Generated AI Strategy
 * @version 1.0
 */
public class RandomAIStrategy implements IAIStrategy {

    /** Random number generator for introducing controlled randomness in decisions. */
    private final Random random;

    /** Set of coordinates that have not been targeted yet. */
    private final Set<Coordinate> availableTargets;

    /** List of coordinates where ships have been hit but not yet sunk. */
    private final List<Coordinate> activeHits;

    /** Stack of prioritized targets to attack next. */
    private final Deque<Coordinate> targetStack;

    /** Heat map storing probability scores for each coordinate. */
    private final Map<Coordinate, Double> heatMap;

    /** Set of recently missed coordinates to avoid clustering. */
    private final Set<Coordinate> processedMisses;

    /** Current operational mode of the AI. */
    private AIMode currentMode;

    /** Detected orientation of the ship currently being targeted. */
    private ShipOrientation lockedOrientation;

    /** Reference to the opponent's board for status checking. */
    private IBoard opponentBoard;

    /** Total number of shots taken by the AI. */
    private int totalShots;

    /** Number of successful hits achieved. */
    private int successfulHits;

    /** List of remaining ship sizes that haven't been sunk yet. */
    private final List<Integer> remainingShips;

    /** Flag to enable/disable parity optimization mode. */
    private boolean useParityMode;

    /**
     * AI operational modes for different tactical situations.
     */
    private enum AIMode {
        /** Systematic search for targets. */
        HUNT,

        /** Initial hit - exploring directions. */
        TARGET,

        /** Direction locked - finishing the ship. */
        DESTROY
    }

    /**
     * Detected ship orientation.
     */
    private enum ShipOrientation {
        /** Orientation not yet determined. */
        UNKNOWN,

        /** Ship is aligned horizontally. */
        HORIZONTAL,

        /** Ship is aligned vertically. */
        VERTICAL
    }

    /**
     * Constructs a new RandomAIStrategy with initialized state.
     * Sets up all necessary data structures and initializes the board targets.
     */
    public RandomAIStrategy() {
        this.random = new Random();
        this.availableTargets = new HashSet<>();
        this.activeHits = new ArrayList<>();
        this.targetStack = new ArrayDeque<>();
        this.heatMap = new HashMap<>();
        this.processedMisses = new HashSet<>();
        this.remainingShips = new ArrayList<>(Arrays.asList(5, 4, 3, 3, 2));
        this.currentMode = AIMode.HUNT;
        this.lockedOrientation = ShipOrientation.UNKNOWN;
        this.useParityMode = true;
        this.totalShots = 0;
        this.successfulHits = 0;
        initializeTargets();
    }

    /**
     * Initializes all available target coordinates on a 10x10 board.
     * Populates the availableTargets set with all coordinates from (0,0) to (9,9).
     */
    private void initializeTargets() {
        availableTargets.clear();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                availableTargets.add(new Coordinate(x, y));
            }
        }
    }

    /**
     * Selects the next target coordinate to attack based on current AI state.
     * Prioritizes pursuing active hits before entering hunt mode.
     *
     * @param opponentBoard the opponent's board to analyze
     * @return the coordinate to target next, or null if no targets available
     */
    @Override
    public Coordinate selectTarget(IBoard opponentBoard) {
        this.opponentBoard = opponentBoard;
        totalShots++;

        if (availableTargets.isEmpty()) {
            return null;
        }

        // CRITICAL: If we have active hits, we MUST pursue them
        if (!activeHits.isEmpty()) {
            Coordinate target = pursuActiveShip();
            if (target != null) {
                return target;
            }

            // If we can't find a target but have hits, the ship might be sunk
            if (isCurrentShipSunk()) {
                registerSunkShip();
                // Continue to hunt mode below
            } else {
                // Something's wrong, try fallback
                return fallbackPursueTarget();
            }
        }

        // No active hits, use hunt mode
        return executeHuntMode();
    }

    /**
     * CRITICAL METHOD: Pursues the currently hit ship until it's completely sunk.
     * This ensures the AI doesn't abandon a partially destroyed ship.
     * <p>
     * Priority order:
     * <ol>
     * <li>Use queued targets from the target stack</li>
     * <li>Attack in locked direction if orientation is known</li>
     * <li>Attack all adjacent cells to all active hits</li>
     * </ol>
     *
     * @return the next target to pursue the active ship, or null if none available
     */
    private Coordinate pursuActiveShip() {
        // Priority 1: Use target stack if we have queued targets
        while (!targetStack.isEmpty()) {
            Coordinate target = targetStack.pop();
            if (isValidTarget(target)) {
                return target;
            }
        }

        // Priority 2: If we have 2+ hits, try to finish in locked direction
        if (activeHits.size() >= 2 && lockedOrientation != ShipOrientation.UNKNOWN) {
            Coordinate target = attackInDirection();
            if (target != null) {
                return target;
            }
        }

        // Priority 3: Attack all adjacent cells to all hits
        for (Coordinate hit : activeHits) {
            List<Coordinate> adjacents = getOrthogonalAdjacents(hit);
            for (Coordinate adj : adjacents) {
                if (isValidTarget(adj)) {
                    return adj;
                }
            }
        }

        return null;
    }

    /**
     * Fallback method when normal pursuit fails.
     * Attempts to find ANY valid adjacent cell to any active hit.
     * This method is called as a last resort when the primary pursuit logic
     * cannot find a suitable target.
     *
     * @return a valid target adjacent to any hit, or null if none found
     */
    private Coordinate fallbackPursueTarget() {
        // Try to find ANY adjacent cell to ANY hit
        Set<Coordinate> allPossible = new HashSet<>();
        for (Coordinate hit : activeHits) {
            allPossible.addAll(getOrthogonalAdjacents(hit));
        }

        for (Coordinate coord : allPossible) {
            if (isValidTarget(coord)) {
                return coord;
            }
        }

        return null;
    }

    /**
     * Attacks in the locked direction to finish the ship.
     * Extends from both ends of the hit chain and checks for gaps.
     * <p>
     * Strategy:
     * <ol>
     * <li>Try extending forward from the last hit</li>
     * <li>Try extending backward from the first hit</li>
     * <li>Check for gaps between consecutive hits</li>
     * </ol>
     *
     * @return coordinate to attack in locked direction, or null if none available
     */
    private Coordinate attackInDirection() {
        if (activeHits.isEmpty()) {
            return null;
        }

        // Sort hits by orientation
        List<Coordinate> sorted = new ArrayList<>(activeHits);
        if (lockedOrientation == ShipOrientation.HORIZONTAL) {
            sorted.sort(Comparator.comparingInt(Coordinate::getX));
        } else {
            sorted.sort(Comparator.comparingInt(Coordinate::getY));
        }

        Coordinate first = sorted.get(0);
        Coordinate last = sorted.get(sorted.size() - 1);

        // Try extending from the last position (forward)
        Coordinate forward = null;
        if (lockedOrientation == ShipOrientation.HORIZONTAL) {
            forward = new Coordinate(last.getX() + 1, last.getY());
        } else {
            forward = new Coordinate(last.getX(), last.getY() + 1);
        }

        if (isValidTarget(forward)) {
            return forward;
        }

        // Try extending from the first position (backward)
        Coordinate backward = null;
        if (lockedOrientation == ShipOrientation.HORIZONTAL) {
            backward = new Coordinate(first.getX() - 1, first.getY());
        } else {
            backward = new Coordinate(first.getX(), first.getY() - 1);
        }

        if (isValidTarget(backward)) {
            return backward;
        }

        // Check for gaps between hits
        for (int i = 0; i < sorted.size() - 1; i++) {
            Coordinate gap = findGap(sorted.get(i), sorted.get(i + 1));
            if (gap != null && isValidTarget(gap)) {
                return gap;
            }
        }

        return null;
    }

    /**
     * HUNT MODE: Systematic search using optimal coverage patterns.
     * Only used when NO ships are currently being pursued.
     * <p>
     * Uses probability density heat mapping and optional parity optimization
     * to maximize the chances of finding enemy ships efficiently.
     *
     * @return optimal coordinate for hunting, or null if no candidates available
     */
    private Coordinate executeHuntMode() {
        // Rebuild heat map
        calculateHeatMap();

        // Get candidates based on parity if applicable
        List<Coordinate> candidates = new ArrayList<>();

        if (useParityMode && shouldUseParity()) {
            // Use checkerboard pattern for efficiency
            for (Coordinate coord : availableTargets) {
                if ((coord.getX() + coord.getY()) % 2 == 0) {
                    candidates.add(coord);
                }
            }
        }

        if (candidates.isEmpty()) {
            candidates = new ArrayList<>(availableTargets);
        }

        if (candidates.isEmpty()) {
            return null;
        }

        // Score and sort candidates
        List<ScoredTarget> scored = candidates.stream()
                .map(c -> new ScoredTarget(c, heatMap.getOrDefault(c, 0.0)))
                .sorted()
                .collect(Collectors.toList());

        // Take top 3% for slight randomness
        int topCount = Math.max(1, scored.size() / 33);
        List<ScoredTarget> topTargets = scored.subList(0, Math.min(topCount, scored.size()));

        return topTargets.get(random.nextInt(topTargets.size())).coord;
    }

    /**
     * Scored target for priority comparison in hunt mode.
     * Associates a coordinate with its probability score from the heat map.
     */
    private static class ScoredTarget implements Comparable<ScoredTarget> {
        /** The coordinate being scored. */
        final Coordinate coord;

        /** The probability/priority score for this coordinate. */
        final double score;

        /**
         * Constructs a new ScoredTarget.
         *
         * @param coord the coordinate
         * @param score the probability score
         */
        ScoredTarget(Coordinate coord, double score) {
            this.coord = coord;
            this.score = score;
        }

        /**
         * Compares this target with another based on score.
         * Higher scores are considered "less than" for descending sort order.
         *
         * @param other the other ScoredTarget to compare to
         * @return negative if this score is higher, positive if lower, zero if equal
         */
        @Override
        public int compareTo(ScoredTarget other) {
            return Double.compare(other.score, this.score);
        }
    }

    /**
     * Determines if parity mode should be used based on remaining ships.
     * Parity optimization is beneficial when there are ships of size 3 or larger
     * still remaining, as checkerboard patterns guarantee hitting them.
     *
     * @return true if parity optimization is beneficial, false otherwise
     */
    private boolean shouldUseParity() {
        for (int size : remainingShips) {
            if (size > 0 && size >= 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates probability density heat map for all available targets.
     * Uses sophisticated ship placement analysis considering:
     * <ul>
     * <li>All possible placements for remaining ships</li>
     * <li>Strategic value modifiers (center bias, edge penalties)</li>
     * <li>Proximity to known misses</li>
     * </ul>
     * The heat map guides the AI's hunting decisions.
     */
    private void calculateHeatMap() {
        heatMap.clear();

        for (Coordinate coord : availableTargets) {
            double heat = 0.0;

            // Calculate for each remaining ship size
            for (int shipSize : remainingShips) {
                if (shipSize == 0) continue;

                // Try all possible placements containing this coordinate
                heat += countValidPlacements(coord, shipSize, true);  // Horizontal
                heat += countValidPlacements(coord, shipSize, false); // Vertical
            }

            // Apply strategic modifiers
            heat += calculateStrategicValue(coord);

            heatMap.put(coord, heat);
        }
    }

    /**
     * Counts valid ship placements that include the given coordinate.
     * Iterates through all possible starting positions where a ship of the
     * given size could be placed such that it covers the specified coordinate.
     *
     * @param coord coordinate to check
     * @param shipSize size of ship
     * @param horizontal true for horizontal placement, false for vertical
     * @return count of valid placements (as a double for probability calculations)
     */
    private double countValidPlacements(Coordinate coord, int shipSize, boolean horizontal) {
        double count = 0.0;

        for (int offset = 0; offset < shipSize; offset++) {
            int startX = horizontal ? coord.getX() - offset : coord.getX();
            int startY = horizontal ? coord.getY() : coord.getY() - offset;

            if (isValidPlacement(startX, startY, shipSize, horizontal)) {
                count += 1.0;
            }
        }

        return count;
    }

    /**
     * Validates if a ship can be placed at the given position.
     * Checks that all cells of the ship would:
     * <ul>
     * <li>Be within board bounds</li>
     * <li>Not overlap with known misses or sunk ships</li>
     * <li>Be available for targeting</li>
     * </ul>
     *
     * @param x starting x coordinate
     * @param y starting y coordinate
     * @param size ship size
     * @param horizontal placement orientation (true=horizontal, false=vertical)
     * @return true if placement is valid, false otherwise
     */
    private boolean isValidPlacement(int x, int y, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            int checkX = horizontal ? x + i : x;
            int checkY = horizontal ? y : y + i;

            Coordinate check = new Coordinate(checkX, checkY);

            if (!isInBounds(check)) {
                return false;
            }

            if (!availableTargets.contains(check)) {
                if (opponentBoard != null) {
                    Cell cell = opponentBoard.getCell(check);
                    if (cell == null || cell.getStatus() == CellStatus.MISS ||
                            cell.getStatus() == CellStatus.SUNK) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Calculates strategic value modifiers for a coordinate.
     * Applies tactical bonuses and penalties:
     * <ul>
     * <li>Center bias - coordinates near the center receive bonuses</li>
     * <li>Edge penalty - coordinates on board edges receive penalties</li>
     * <li>Miss proximity penalty - coordinates near recent misses are penalized</li>
     * </ul>
     *
     * @param coord coordinate to evaluate
     * @return strategic value modifier (can be positive or negative)
     */
    private double calculateStrategicValue(Coordinate coord) {
        double value = 0.0;

        // Center bias
        int centerDist = Math.abs(coord.getX() - 4) + Math.abs(coord.getY() - 4);
        value += (10 - centerDist) * 0.5;

        // Edge penalty
        if (coord.getX() == 0 || coord.getX() == 9 || coord.getY() == 0 || coord.getY() == 9) {
            value -= 2.0;
        }

        // Penalty near misses
        for (Coordinate miss : processedMisses) {
            int dist = manhattanDistance(coord, miss);
            if (dist <= 1) {
                value -= 3.0;
            }
        }

        return value;
    }

    /**
     * Gets orthogonal adjacent coordinates (up, down, left, right).
     * Does not include diagonal adjacents.
     *
     * @param coord center coordinate
     * @return list of four orthogonal adjacent coordinates
     */
    private List<Coordinate> getOrthogonalAdjacents(Coordinate coord) {
        return Arrays.asList(
                new Coordinate(coord.getX(), coord.getY() - 1),  // Up
                new Coordinate(coord.getX(), coord.getY() + 1),  // Down
                new Coordinate(coord.getX() - 1, coord.getY()),  // Left
                new Coordinate(coord.getX() + 1, coord.getY())   // Right
        );
    }

    /**
     * Finds a gap coordinate between two coordinates.
     * Detects if there's exactly one cell between two coordinates in a straight line,
     * which could indicate a missed cell in a ship sequence.
     *
     * @param a first coordinate
     * @param b second coordinate
     * @return gap coordinate if exactly one cell apart, null otherwise
     */
    private Coordinate findGap(Coordinate a, Coordinate b) {
        int dx = b.getX() - a.getX();
        int dy = b.getY() - a.getY();

        if (Math.abs(dx) == 2 && dy == 0) {
            return new Coordinate(a.getX() + dx / 2, a.getY());
        } else if (Math.abs(dy) == 2 && dx == 0) {
            return new Coordinate(a.getX(), a.getY() + dy / 2);
        }

        return null;
    }

    /**
     * Calculates Manhattan distance between two coordinates.
     * Manhattan distance is the sum of absolute differences in x and y coordinates,
     * representing the minimum number of orthogonal moves needed to travel between points.
     *
     * @param a first coordinate
     * @param b second coordinate
     * @return Manhattan distance as an integer
     */
    private int manhattanDistance(Coordinate a, Coordinate b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    /**
     * Checks if coordinate is a valid target.
     * A coordinate is valid if it's within bounds and hasn't been targeted yet.
     *
     * @param coord coordinate to check
     * @return true if valid and available for targeting, false otherwise
     */
    private boolean isValidTarget(Coordinate coord) {
        return isInBounds(coord) && availableTargets.contains(coord);
    }

    /**
     * Checks if coordinate is within board bounds.
     * Valid coordinates have both x and y values between 0 and 9 inclusive.
     *
     * @param coord coordinate to check
     * @return true if coordinate is within the 10x10 board, false otherwise
     */
    private boolean isInBounds(Coordinate coord) {
        return coord.getX() >= 0 && coord.getX() < 10 &&
                coord.getY() >= 0 && coord.getY() < 10;
    }

    /**
     * Updates the AI strategy based on the result of the last shot.
     * Handles state transitions between hunt, target, and destroy modes.
     * <p>
     * On hit: Adds coordinate to active hits, queues adjacent targets, detects orientation
     * <br>
     * On miss: Records the miss for future probability calculations
     *
     * @param lastShot the coordinate of the last shot taken
     * @param wasHit true if the shot was a hit, false if it was a miss
     */
    @Override
    public void updateStrategy(Coordinate lastShot, boolean wasHit) {
        availableTargets.remove(lastShot);

        if (wasHit) {
            // HIT - Add to active hits and update mode
            successfulHits++;
            activeHits.add(lastShot);

            // IMMEDIATE: Queue adjacent cells for investigation
            if (activeHits.size() == 1) {
                // First hit - queue all 4 directions
                currentMode = AIMode.TARGET;
                queueAdjacentTargets(lastShot);
            } else {
                // Multiple hits - detect orientation
                detectOrientation();

                if (lockedOrientation != ShipOrientation.UNKNOWN) {
                    currentMode = AIMode.DESTROY;
                    // Queue only aligned directions
                    queueAlignedTargets(lastShot);
                } else {
                    // Still exploring
                    currentMode = AIMode.TARGET;
                    queueAdjacentTargets(lastShot);
                }
            }

            // Check if ship is completely sunk
            if (isCurrentShipSunk()) {
                registerSunkShip();
            }

        } else {
            // MISS - Record it
            processedMisses.add(lastShot);

            if (processedMisses.size() > 15) {
                Iterator<Coordinate> it = processedMisses.iterator();
                it.next();
                it.remove();
            }

            // If we have active hits but missed, check if ship is actually sunk
            if (!activeHits.isEmpty() && isCurrentShipSunk()) {
                registerSunkShip();
            }
        }
    }

    /**
     * Queues all orthogonal adjacent targets to the target stack.
     * Used when first hitting a ship to explore all four directions.
     * The adjacents are shuffled to introduce randomness in exploration order.
     *
     * @param coord center coordinate to queue adjacents from
     */
    private void queueAdjacentTargets(Coordinate coord) {
        List<Coordinate> adjacents = getOrthogonalAdjacents(coord);

        // Shuffle for randomness in exploration order
        Collections.shuffle(adjacents);

        for (Coordinate adj : adjacents) {
            if (isValidTarget(adj)) {
                targetStack.push(adj);
            }
        }
    }

    /**
     * Queues only aligned targets based on locked orientation.
     * Used when ship direction is known to attack efficiently along that axis.
     * <p>
     * For horizontal orientation: queues left and right
     * <br>
     * For vertical orientation: queues up and down
     *
     * @param coord center coordinate to queue aligned targets from
     */
    private void queueAlignedTargets(Coordinate coord) {
        if (lockedOrientation == ShipOrientation.HORIZONTAL) {
            // Only queue left and right
            Coordinate left = new Coordinate(coord.getX() - 1, coord.getY());
            Coordinate right = new Coordinate(coord.getX() + 1, coord.getY());

            if (isValidTarget(right)) targetStack.push(right);
            if (isValidTarget(left)) targetStack.push(left);

        } else if (lockedOrientation == ShipOrientation.VERTICAL) {
            // Only queue up and down
            Coordinate up = new Coordinate(coord.getX(), coord.getY() - 1);
            Coordinate down = new Coordinate(coord.getX(), coord.getY() + 1);

            if (isValidTarget(down)) targetStack.push(down);
            if (isValidTarget(up)) targetStack.push(up);
        }
    }

    /**
     * Detects ship orientation based on hit pattern.
     * Locks orientation when a clear pattern emerges from active hits.
     * <p>
     * Orientation is locked when:
     * <ul>
     * <li>All hits share the same row (horizontal)</li>
     * <li>All hits share the same column (vertical)</li>
     * </ul>
     * If hits don't form a clear line, orientation remains UNKNOWN.
     */
    private void detectOrientation() {
        if (activeHits.size() < 2) {
            lockedOrientation = ShipOrientation.UNKNOWN;
            return;
        }

        boolean allSameRow = true;
        boolean allSameCol = true;

        int firstRow = activeHits.get(0).getY();
        int firstCol = activeHits.get(0).getX();

        for (Coordinate hit : activeHits) {
            if (hit.getY() != firstRow) allSameRow = false;
            if (hit.getX() != firstCol) allSameCol = false;
        }

        if (allSameRow && !allSameCol) {
            lockedOrientation = ShipOrientation.HORIZONTAL;
        } else if (allSameCol && !allSameRow) {
            lockedOrientation = ShipOrientation.VERTICAL;
        } else {
            lockedOrientation = ShipOrientation.UNKNOWN;
        }
    }

    /**
     * Checks if current targeted ship is completely sunk.
     * Verifies that all active hit coordinates are marked as SUNK on the board.
     *
     * @return true if all active hits have SUNK status, false otherwise
     */
    private boolean isCurrentShipSunk() {
        if (opponentBoard == null || activeHits.isEmpty()) {
            return false;
        }

        for (Coordinate hit : activeHits) {
            Cell cell = opponentBoard.getCell(hit);
            if (cell == null || cell.getStatus() != CellStatus.SUNK) {
                return false;
            }
        }

        return true;
    }

    /**
     * Registers a sunk ship and resets targeting state.
     * <p>
     * Actions performed:
     * <ul>
     * <li>Removes the ship size from remaining ships list</li>
     * <li>Clears all active hits</li>
     * <li>Clears the target stack</li>
     * <li>Returns to HUNT mode</li>
     * <li>Resets orientation to UNKNOWN</li>
     * </ul>
     */
    private void registerSunkShip() {
        int sunkSize = activeHits.size();

        // Remove ship from remaining list
        for (int i = 0; i < remainingShips.size(); i++) {
            if (remainingShips.get(i) == sunkSize) {
                remainingShips.set(i, 0);
                break;
            }
        }

        // Reset state
        activeHits.clear();
        targetStack.clear();
        currentMode = AIMode.HUNT;
        lockedOrientation = ShipOrientation.UNKNOWN;
    }

    /**
     * Resets the AI strategy to initial state.
     * Clears all tracking data and reinitializes for a new game.
     * <p>
     * This method should be called when starting a new game.
     */
    @Override
    public void reset() {
        availableTargets.clear();
        initializeTargets();
        activeHits.clear();
        targetStack.clear();
        heatMap.clear();
        processedMisses.clear();

        remainingShips.clear();
        remainingShips.addAll(Arrays.asList(5, 4, 3, 3, 2));

        currentMode = AIMode.HUNT;
        lockedOrientation = ShipOrientation.UNKNOWN;
        opponentBoard = null;
        totalShots = 0;
        successfulHits = 0;
        useParityMode = true;
    }
}