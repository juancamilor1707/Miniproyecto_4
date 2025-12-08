package com.example.miniproyecto4.model.Player;

import com.example.miniproyecto4.model.Board.Board;
import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Coordinate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerPlayer extends Player {

    private static final long serialVersionUID = 1L;

    private final List<Coordinate> availableShots;
    private transient Random random;

    public ComputerPlayer() {
        super("Computer");
        this.availableShots = new ArrayList<>();
        this.random = new Random();
        initializeAvailableShots();
    }

    public ComputerPlayer(IBoard board) {
        super("Computer", board);
        this.availableShots = new ArrayList<>();
        this.random = new Random();
        initializeAvailableShots();
    }

    private void initializeAvailableShots() {
        availableShots.clear();
        int size = getBoard().getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                availableShots.add(new Coordinate(x, y));
            }
        }
    }

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

    public void markShotTaken(Coordinate coordinate) {
        availableShots.remove(coordinate);
    }

    public int getAvailableShotsCount() {
        return availableShots.size();
    }

    public boolean isShotAvailable(Coordinate coordinate) {
        return availableShots.contains(coordinate);
    }

    @Override
    public void reset() {
        super.reset();
        initializeAvailableShots();
        if (random == null) {
            random = new Random();
        }
    }
}