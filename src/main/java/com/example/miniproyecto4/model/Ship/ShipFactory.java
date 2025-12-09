package com.example.miniproyecto4.model.Ship;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Validation.Orientation;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating ship instances in the Battleship game.
 * Provides methods to create individual ships and complete fleets
 * according to the game's ship composition rules.
 */
public class ShipFactory {

    /**
     * Creates a ship of the specified type without position information.
     *
     * @param type the type of ship to create
     * @return a new ship instance of the specified type
     */
    public static IShip createShip(ShipType type) {
        return new Ship(type);
    }

    /**
     * Creates a ship of the specified type with a starting position and orientation.
     *
     * @param type the type of ship to create
     * @param start the starting coordinate for the ship
     * @param orientation the orientation of the ship (horizontal or vertical)
     * @return a new ship instance with the specified configuration
     */
    public static IShip createShip(ShipType type, Coordinate start, Orientation orientation) {
        return new Ship(type, start, orientation);
    }

    /**
     * Creates a complete fleet of ships for the game.
     * The fleet consists of:
     * - 1 Carrier
     * - 2 Submarines
     * - 3 Destroyers
     * - 4 Frigates
     *
     * @return a list containing all ships in the fleet
     */
    public static List<IShip> createFleet() {
        List<IShip> fleet = new ArrayList<>();

        fleet.add(createShip(ShipType.CARRIER));

        fleet.add(createShip(ShipType.SUBMARINE));
        fleet.add(createShip(ShipType.SUBMARINE));

        fleet.add(createShip(ShipType.DESTROYER));
        fleet.add(createShip(ShipType.DESTROYER));
        fleet.add(createShip(ShipType.DESTROYER));

        fleet.add(createShip(ShipType.FRIGATE));
        fleet.add(createShip(ShipType.FRIGATE));
        fleet.add(createShip(ShipType.FRIGATE));
        fleet.add(createShip(ShipType.FRIGATE));

        return fleet;
    }
}