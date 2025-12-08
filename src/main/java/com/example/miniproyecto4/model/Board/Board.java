package com.example.miniproyecto4.model.Board;

import com.example.miniproyecto4.model.Cell.Cell;
import com.example.miniproyecto4.model.Cell.CellStatus;
import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.IShip;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board implements IBoard, Serializable {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_SIZE = 10;

    private final int size;
    private final Cell[][] grid;
    private final List<IShip> ships;
    private final Map<Coordinate, IShip> shipPositions;

    public Board() {
        this(DEFAULT_SIZE);
    }

    public Board(int size) {
        this.size = size;
        this.grid = new Cell[size][size];
        this.ships = new ArrayList<>();
        this.shipPositions = new HashMap<>();
        initializeGrid();
    }

    private void initializeGrid() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                grid[x][y] = new Cell(x, y);
            }
        }
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Cell getCell(int x, int y) {
        if (!isValidCoordinate(new Coordinate(x, y))) {
            return null;
        }
        return grid[x][y];
    }

    @Override
    public Cell getCell(Coordinate coordinate) {
        return getCell(coordinate.getX(), coordinate.getY());
    }

    @Override
    public boolean placeShip(IShip ship) {
        if (ship == null || ship.getStartCoordinate() == null) {
            return false;
        }

        List<Coordinate> coordinates = ship.getCoordinates();

        for (Coordinate coord : coordinates) {
            if (!isValidCoordinate(coord) || hasShipAt(coord)) {
                return false;
            }
        }

        for (Coordinate coord : coordinates) {
            Cell cell = getCell(coord);
            cell.setStatus(CellStatus.SHIP);
            shipPositions.put(coord, ship);
        }

        ships.add(ship);
        return true;
    }

    @Override
    public boolean removeShip(IShip ship) {
        if (!ships.contains(ship)) {
            return false;
        }

        for (Coordinate coord : ship.getCoordinates()) {
            Cell cell = getCell(coord);
            if (cell != null) {
                cell.setStatus(CellStatus.EMPTY);
            }
            shipPositions.remove(coord);
        }

        ships.remove(ship);
        return true;
    }

    @Override
    public List<IShip> getShips() {
        return new ArrayList<>(ships);
    }

    @Override
    public IShip getShipAt(Coordinate coordinate) {
        return shipPositions.get(coordinate);
    }

    @Override
    public boolean hasShipAt(Coordinate coordinate) {
        return shipPositions.containsKey(coordinate);
    }

    @Override
    public boolean isValidCoordinate(Coordinate coordinate) {
        int x = coordinate.getX();
        int y = coordinate.getY();
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    @Override
    public int getSunkShipsCount() {
        int count = 0;
        for (IShip ship : ships) {
            if (ship.isSunk()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean allShipsSunk() {
        return !ships.isEmpty() && getSunkShipsCount() == ships.size();
    }

    @Override
    public void reset() {
        ships.clear();
        shipPositions.clear();
        initializeGrid();
    }
}