package com.example.miniproyecto4.model.Game;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the default fleet configuration used in the game.
 * This class defines the number of ships per type and provides
 * access to an unmodifiable view of the fleet composition.
 */
public class FleetConfiguration {

    /**
     * Internal mapping that stores the number of ships for each ship type.
     */
    private static final Map<String, Integer> FLEET_COMPOSITION = new HashMap<>();

    static {
        FLEET_COMPOSITION.put("Carrier", 1);
        FLEET_COMPOSITION.put("Battleship", 1);
        FLEET_COMPOSITION.put("Cruiser", 1);
        FLEET_COMPOSITION.put("Submarine", 1);
        FLEET_COMPOSITION.put("Destroyer", 1);
    }

    /**
     * Returns an unmodifiable view of the predefined fleet composition.
     *
     * @return a map representing the number of ships per type
     */
    public static Map<String, Integer> getFleetComposition() {
        return Collections.unmodifiableMap(FLEET_COMPOSITION);
    }

    /**
     * Returns a mutable copy of the predefined fleet composition.
     * Useful when the caller needs to modify the returned map.
     *
     * @return a new modifiable map with the fleet configuration
     */
    public static Map<String, Integer> getMutableFleetComposition() {
        return new HashMap<>(FLEET_COMPOSITION);
    }
}