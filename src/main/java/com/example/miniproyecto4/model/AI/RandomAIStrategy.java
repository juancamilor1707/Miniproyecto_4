package com.example.miniproyecto4.model.AI;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Coordinate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RandomAIStrategy implements IAIStrategy {

    private final Random random;
    private final List<Coordinate> availableTargets;
    private final List<Coordinate> targetQueue;
    private Coordinate lastHit;
    private boolean huntMode;

    public RandomAIStrategy() {
        this.random = new Random();
        this.availableTargets = new ArrayList<>();
        this.targetQueue = new ArrayList<>();
        this.huntMode = true;
        initializeAvailableTargets();
    }


    private void initializeAvailableTargets() {
        availableTargets.clear();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                availableTargets.add(new Coordinate(x, y));
            }
        }
    }

    @Override
    public Coordinate selectTarget(IBoard opponentBoard) {

        if (availableTargets.isEmpty() && targetQueue.isEmpty()) {
            return null;
        }

        if (!huntMode && !targetQueue.isEmpty()) {
            Iterator<Coordinate> iterator = targetQueue.iterator();
            while (iterator.hasNext()) {
                Coordinate target = iterator.next();
                iterator.remove(); // Remover de la cola

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

    private boolean isValidCoordinate(Coordinate coord) {
        return coord.getX() >= 0 && coord.getX() < 10 &&
                coord.getY() >= 0 && coord.getY() < 10;
    }

    @Override
    public void reset() {
        availableTargets.clear();
        targetQueue.clear();
        lastHit = null;
        huntMode = true;
        initializeAvailableTargets();
    }
}