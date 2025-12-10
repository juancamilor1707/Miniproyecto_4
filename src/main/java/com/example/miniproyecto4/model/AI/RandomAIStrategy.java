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
 * Improved to handle edge cases when ships are partially surrounded.
 */
public class RandomAIStrategy implements IAIStrategy {

    private final Random random;
    private final List<Coordinate> availableTargets;
    private final List<Coordinate> targetQueue;
    private final List<Coordinate> currentHitChain;

    private Coordinate firstHitInChain;
    private boolean huntMode;
    private IBoard lastOpponentBoard;

    public RandomAIStrategy() {
        this.random = new Random();
        this.availableTargets = new ArrayList<>();
        this.targetQueue = new ArrayList<>();
        this.currentHitChain = new ArrayList<>();
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
        this.lastOpponentBoard = opponentBoard;

        if (availableTargets.isEmpty() && targetQueue.isEmpty()) {
            return null;
        }

        // Modo Target: tenemos hits que perseguir
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

        // Si el target queue está vacío pero tenemos hits activos, buscar más opciones
        if (!huntMode && !currentHitChain.isEmpty()) {
            Coordinate extended = findExtendedTarget();
            if (extended != null) {
                return extended;
            }

            // Verificar si el barco está hundido
            if (isChainSunk()) {
                resetToHuntMode();
            }
        }

        // Modo Hunt: búsqueda con patrón de tablero de ajedrez
        if (huntMode && !availableTargets.isEmpty()) {
            return selectHuntTarget();
        }

        // Fallback a hunt mode
        if (!availableTargets.isEmpty()) {
            huntMode = true;
            currentHitChain.clear();
            firstHitInChain = null;
            return selectHuntTarget();
        }

        return null;
    }

    /**
     * Busca un objetivo extendido cuando no hay adyacentes disponibles
     */
    private Coordinate findExtendedTarget() {
        if (currentHitChain.size() < 2) {
            return null;
        }

        // Determinar la dirección del barco
        Coordinate first = currentHitChain.get(0);
        Coordinate second = currentHitChain.get(1);

        int dx = Integer.compare(second.getX() - first.getX(), 0);
        int dy = Integer.compare(second.getY() - first.getY(), 0);

        if (dx == 0 && dy == 0) {
            return null;
        }

        // Intentar extender desde el último hit
        Coordinate last = currentHitChain.get(currentHitChain.size() - 1);
        for (int dist = 1; dist <= 2; dist++) {
            Coordinate extended = new Coordinate(
                    last.getX() + (dx * dist),
                    last.getY() + (dy * dist)
            );

            if (isValidCoordinate(extended) && availableTargets.contains(extended)) {
                // Verificar que no haya un MISS bloqueando
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

        // Intentar extender desde el primer hit en dirección opuesta
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
     * Verifica si una coordenada es un MISS
     */
    private boolean isMiss(Coordinate coord) {
        if (lastOpponentBoard == null) {
            return false;
        }
        Cell cell = lastOpponentBoard.getCell(coord);
        return cell != null && cell.getStatus() == CellStatus.MISS;
    }

    /**
     * Verifica si la cadena de hits actual está completamente hundida
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

    private Coordinate selectHuntTarget() {
        if (availableTargets.isEmpty()) {
            return null;
        }

        // Priorizar patrón de tablero de ajedrez
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

            // Agregar a la cadena de hits
            if (firstHitInChain == null) {
                firstHitInChain = lastShot;
                currentHitChain.clear();
            }
            currentHitChain.add(lastShot);

            // Agregar adyacentes a la cola
            addAdjacentTargets(lastShot);

            // Si ya tenemos 2+ hits, verificar si están hundidos
            if (currentHitChain.size() >= 2 && isChainSunk()) {
                resetToHuntMode();
            }
        } else {
            // Si erramos y no hay más targets en cola, verificar estado
            if (targetQueue.isEmpty()) {
                if (currentHitChain.isEmpty() || isChainSunk()) {
                    resetToHuntMode();
                }
            }
        }
    }

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

    private boolean isValidCoordinate(Coordinate coord) {
        return coord.getX() >= 0 && coord.getX() < 10 &&
                coord.getY() >= 0 && coord.getY() < 10;
    }

    private void resetToHuntMode() {
        huntMode = true;
        firstHitInChain = null;
        currentHitChain.clear();
        targetQueue.clear();
    }

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