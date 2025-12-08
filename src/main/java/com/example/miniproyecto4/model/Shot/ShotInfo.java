package com.example.miniproyecto4.model.Shot;

import com.example.miniproyecto4.model.Cell.Coordinate;
import java.io.Serializable;

public class ShotInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Coordinate coordinate;
    private final ShotResult result;

    public ShotInfo(Coordinate coordinate, ShotResult result) {
        this.coordinate = coordinate;
        this.result = result;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public ShotResult getResult() {
        return result;
    }
}