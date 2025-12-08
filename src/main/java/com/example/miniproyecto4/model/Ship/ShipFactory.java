package com.example.miniproyecto4.model.Ship;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Validation.Orientation;
import java.util.ArrayList;
import java.util.List;

public class ShipFactory {

    public static IShip createShip(ShipType type) {
        return new Ship(type);
    }

    public static IShip createShip(ShipType type, Coordinate start, Orientation orientation) {
        return new Ship(type, start, orientation);
    }

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