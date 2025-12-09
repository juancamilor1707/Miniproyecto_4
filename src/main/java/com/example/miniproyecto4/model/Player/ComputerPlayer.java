package com.example.miniproyecto4.model.Player;

import com.example.miniproyecto4.model.Board.Board;
import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Coordinate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a computer-controlled player in the Battleship game.
 * Extends the basic Player functionality with automated shot selection
 * and tracking of available shot locations.
 */
public class ComputerPlayer extends Player {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * List of coordinates that are still available for the computer to target.
     * This list is updated as shots are taken.
     */
    private final List<Coordinate> availableShots;

    /**
     * Random number generator for selecting shot targets.
     * Marked as transient to avoid serialization issues.
     */
    private transient Random random;

    /**
     * Constructs a ComputerPlayer with default settings.
     * Initializes the player with the nickname "Computer" and a new board,
     * and sets up the list of available shots.
     */
    public ComputerPlayer() {
        super("Computer");
        this.availableShots = new ArrayList<>();
        this.random = new Random();
        initializeAvailableShots();
    }

    /**
     * Constructs a ComputerPlayer with a specified board.
     * Initializes the player with the nickname "Computer" and the provided board,
     * and sets up the list of available shots.
     *
     * @param board the game board to use for this player
     */
    public ComputerPlayer(IBoard board) {
        super("Computer", board);
        this.availableShots = new ArrayList<>();
        this.random = new Random();
        initializeAvailableShots();
    }

    /**
     * Initializes the list of available shots to include all coordinates on the board.
     * Clears any existing shots and populates the list with all possible coordinates.
     */
    private void initializeAvailableShots() {
        availableShots.clear();
        int size = getBoard().getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                availableShots.add(new Coordinate(x, y));
            }
        }
    }

    /**
     * Selects and returns the next shot coordinate for the computer player.
     * Randomly selects a coordinate from the available shots list and removes it.
     *
     * @return the next coordinate to shoot at, or null if no shots are available
     */
    public Coordinate getNextShot() {
        if (availableShots.isEmpty()) {
            return null;
        }

        if (random == null) {
            random = new Random();
        }

        int index = random.nextInt(availableShots.size());
        Coordinate shot = availableShots.remove(index);
        return shot;
    }

    /**
     * Marks a specific coordinate as taken, removing it from available shots.
     * Used to update the available shots list when a shot is taken by the AI strategy.
     *
     * @param coordinate the coordinate to mark as taken
     */
    public void markShotTaken(Coordinate coordinate) {
        availableShots.remove(coordinate);
    }

    /**
     * Returns the number of shots still available to the computer player.
     *
     * @return the count of available shot coordinates
     */
    public int getAvailableShotsCount() {
        return availableShots.size();
    }

    /**
     * Checks if a specific coordinate is still available for shooting.
     *
     * @param coordinate the coordinate to check
     * @return true if the coordinate is available, false otherwise
     */
    public boolean isShotAvailable(Coordinate coordinate) {
        return availableShots.contains(coordinate);
    }

    /**
     * Resets the computer player to its initial state.
     * Calls the parent reset method and reinitializes the available shots list
     * and random number generator.
     */
    @Override
    public void reset() {
        super.reset();
        initializeAvailableShots();
        if (random == null) {
            random = new Random();
        }
    }
}