package com.example.miniproyecto4.model.Shot;

import com.example.miniproyecto4.model.Cell.Coordinate;
import java.io.Serializable;

/**
 * Represents information about a shot taken in the Battleship game.
 * Encapsulates the coordinate where the shot was taken and its result.
 * Implements Serializable to support game state persistence.
 */
public class ShotInfo implements Serializable {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The coordinate where the shot was taken.
     */
    private final Coordinate coordinate;

    /**
     * The result of the shot (WATER, HIT, SUNK, or INVALID).
     */
    private final ShotResult result;

    /**
     * Constructs a ShotInfo with the specified coordinate and result.
     *
     * @param coordinate the coordinate where the shot was taken
     * @param result the result of the shot
     */
    public ShotInfo(Coordinate coordinate, ShotResult result) {
        this.coordinate = coordinate;
        this.result = result;
    }

    /**
     * Returns the coordinate where the shot was taken.
     *
     * @return the shot coordinate
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Returns the result of the shot.
     *
     * @return the shot result
     */
    public ShotResult getResult() {
        return result;
    }
}