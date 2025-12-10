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
 * 1. Probability Density Analysis - Statistical ship placement prediction
 * 2. Hunt Mode - Optimal search patterns using parity and density mapping
 * 3. Target Mode - AGGRESSIVE pursuit - stays on the ship until it's destroyed
 * 4. Destroy Mode - Systematic elimination with directional attacks
 * Key Feature: Once a ship is hit, the AI will NOT leave it until it's completely sunk.
 */
public class RandomAIStrategy implements IAIStrategy {

    private final Random random;
    private final Set<Coordinate> availableTargets;
    private final List<Coordinate> activeHits;
    private final Deque<Coordinate> targetStack;
    private final Map<Coordinate, Double> heatMap;
    private final Set<Coordinate> processedMisses;

    private AIMode currentMode;
    private ShipOrientation lockedOrientation;
    private IBoard opponentBoard;
    private int totalShots;
    private int successfulHits;
    private final List<Integer> remainingShips;
    private boolean useParityMode;

    /**
     * AI operational modes for different tactical situations.
     */
    private enum AIMode {
        HUNT,      // Systematic search for targets
        TARGET,    // Initial hit - exploring directions
        DESTROY    // Direction locked - finishing the ship
    }

    /**
     * Detected ship orientation.
     */
    private enum ShipOrientation {
        UNKNOWN,
        HORIZONTAL,
        VERTICAL
    }

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
     * Initializes all available target coordinates.
     */
    private void initializeTargets() {
        availableTargets.clear();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                availableTargets.add(new Coordinate(x, y));
            }
        }
    }

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
     *
     * @return the next target to pursue the active ship
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
     * Tries to find ANY valid adjacent to hits.
     *
     * @return a valid target adjacent to any hit
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
     * Extends from both ends of the hit chain.
     *
     * @return coordinate to attack in locked direction
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
     *
     * @return optimal coordinate for hunting
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
     * Scored target for priority comparison.
     */
    private static class ScoredTarget implements Comparable<ScoredTarget> {
        final Coordinate coord;
        final double score;

        ScoredTarget(Coordinate coord, double score) {
            this.coord = coord;
            this.score = score;
        }

        @Override
        public int compareTo(ScoredTarget other) {
            return Double.compare(other.score, this.score);
        }
    }

    /**
     * Determines if parity mode should be used based on remaining ships.
     *
     * @return true if parity optimization is beneficial
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
     * Uses sophisticated ship placement analysis.
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
     *
     * @param coord coordinate to check
     * @param shipSize size of ship
     * @param horizontal true for horizontal placement
     * @return count of valid placements
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
     *
     * @param x starting x coordinate
     * @param y starting y coordinate
     * @param size ship size
     * @param horizontal placement orientation
     * @return true if placement is valid
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
     *
     * @param coord coordinate to evaluate
     * @return strategic value modifier
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
     *
     * @param coord center coordinate
     * @return list of orthogonal adjacents
     */
    private List<Coordinate> getOrthogonalAdjacents(Coordinate coord) {
        return Arrays.asList(
                new Coordinate(coord.getX(), coord.getY() - 1),
                new Coordinate(coord.getX(), coord.getY() + 1),
                new Coordinate(coord.getX() - 1, coord.getY()),
                new Coordinate(coord.getX() + 1, coord.getY())
        );
    }

    /**
     * Finds a gap coordinate between two coordinates.
     *
     * @param a first coordinate
     * @param b second coordinate
     * @return gap coordinate if exists, null otherwise
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
     *
     * @param a first coordinate
     * @param b second coordinate
     * @return Manhattan distance
     */
    private int manhattanDistance(Coordinate a, Coordinate b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    /**
     * Checks if coordinate is a valid target.
     *
     * @param coord coordinate to check
     * @return true if valid and available
     */
    private boolean isValidTarget(Coordinate coord) {
        return isInBounds(coord) && availableTargets.contains(coord);
    }

    /**
     * Checks if coordinate is within board bounds.
     *
     * @param coord coordinate to check
     * @return true if in bounds
     */
    private boolean isInBounds(Coordinate coord) {
        return coord.getX() >= 0 && coord.getX() < 10 &&
                coord.getY() >= 0 && coord.getY() < 10;
    }

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
     * Used when first hitting a ship to explore all directions.
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
     * Used when ship direction is known to attack efficiently.
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
     * Locks orientation when a clear pattern emerges.
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
     * Verifies all active hits are marked as SUNK.
     *
     * @return true if all active hits are sunk
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
     * Clears active hits and returns to hunt mode.
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