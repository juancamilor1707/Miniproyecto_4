package com.example.miniproyecto4.view.Components;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.view.utils.Colors;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

public class BoardView extends GridPane implements IBoardView {

    private final int size;
    private final double cellSize;
    private final CellView[][] cells;

    public BoardView(int size, double cellSize) {
        this.size = size;
        this.cellSize = cellSize;
        this.cells = new CellView[size][size];
        initializeBoard();
    }

    private void initializeBoard() {
        setHgap(2);
        setVgap(2);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                CellView cellView = new CellView(cellSize);
                cells[x][y] = cellView;
                add(cellView, x, y);
            }
        }
    }

    public CellView getCell(int x, int y) {
        if (x >= 0 && x < size && y >= 0 && y < size) {
            return cells[x][y];
        }
        return null;
    }

    public CellView getCell(Coordinate coordinate) {
        return getCell(coordinate.getX(), coordinate.getY());
    }

    @Override
    public void drawCell(Coordinate coordinate, String color) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.setFill(color);
        }
    }

    @Override
    public void markHit(Coordinate coordinate) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.markAsHit();
        }
    }

    @Override
    public void markMiss(Coordinate coordinate) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.markAsMiss();
        }
    }

    @Override
    public void markShip(Coordinate coordinate) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.markAsShip();
        }
    }

    @Override
    public void markSunk(Coordinate coordinate) {
        CellView cell = getCell(coordinate);
        if (cell != null) {
            cell.markAsSunk();
        }
    }

    @Override
    public void clear() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                cells[x][y].reset();
            }
        }
    }

    @Override
    public void refresh() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                cells[x][y].refresh();
            }
        }
    }
}