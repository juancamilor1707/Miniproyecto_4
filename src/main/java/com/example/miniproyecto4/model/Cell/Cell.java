package com.example.miniproyecto4.model.Cell;

import java.io.Serializable;

public class Cell implements ICell, Serializable {

    private static final long serialVersionUID = 1L;

    private final Coordinate coordinate;
    private CellStatus status;

    public Cell(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.status = CellStatus.EMPTY;
    }

    public Cell(int x, int y) {
        this(new Coordinate(x, y));
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public CellStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(CellStatus status) {
        this.status = status;
    }

    @Override
    public boolean isEmpty() {
        return status == CellStatus.EMPTY;
    }

    @Override
    public boolean hasShip() {
        return status == CellStatus.SHIP;
    }

    @Override
    public boolean isHit() {
        return status == CellStatus.HIT;
    }

    @Override
    public boolean isMiss() {
        return status == CellStatus.MISS;
    }
}