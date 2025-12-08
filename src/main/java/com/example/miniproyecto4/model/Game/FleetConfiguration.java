package com.example.miniproyecto4.model.Game;

import com.example.miniproyecto4.model.Ship.ShipType;
import java.util.HashMap;
import java.util.Map;

public class FleetConfiguration {

    private static final Map<ShipType, Integer> FLEET_COMPOSITION = new HashMap<>();

    static {
        FLEET_COMPOSITION.put(ShipType.CARRIER, 1);
        FLEET_COMPOSITION.put(ShipType.SUBMARINE, 2);
        FLEET_COMPOSITION.put(ShipType.DESTROYER, 3);
        FLEET_COMPOSITION.put(ShipType.FRIGATE, 4);
    }

    public static int getTotalShipsCount() {
        return FLEET_COMPOSITION.values().stream().mapToInt(Integer::intValue).sum();
    }

    public static int getShipCount(ShipType type) {
        return FLEET_COMPOSITION.getOrDefault(type, 0);
    }

    public static Map<ShipType, Integer> getFleetComposition() {
        return new HashMap<>(FLEET_COMPOSITION);
    }
}