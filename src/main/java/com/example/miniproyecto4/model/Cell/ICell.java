package com.example.miniproyecto4.model.Cell;

import java.io.Serializable;

public interface ICell extends Serializable {

    Coordinate getCoordinate();

    CellStatus getStatus();

    void setStatus(CellStatus status);

    boolean isEmpty();

    boolean hasShip();

    boolean isHit();

    boolean isMiss();
}