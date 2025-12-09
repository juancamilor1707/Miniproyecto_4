package com.example.miniproyecto4.model.Shot;

/**
 * Enumeration representing the possible results of a shot in the Battleship game.
 * Indicates whether a shot hit water, hit a ship, sunk a ship, or was invalid.
 */
public enum ShotResult {
    /**
     * The shot hit water and missed all ships.
     */
    WATER,

    /**
     * The shot hit a ship but did not sink it.
     */
    HIT,

    /**
     * The shot hit and sunk a ship completely.
     */
    SUNK,

    /**
     * The shot was invalid (out of bounds or already targeted).
     */
    INVALID
}