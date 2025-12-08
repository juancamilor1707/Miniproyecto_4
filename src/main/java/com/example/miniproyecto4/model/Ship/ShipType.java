package com.example.miniproyecto4.model.Ship;

public enum ShipType {
    CARRIER(4, "Portaaviones"),
    SUBMARINE(3, "Submarino"),
    DESTROYER(2, "Destructor"),
    FRIGATE(1, "Fragata");

    private final int size;
    private final String displayName;

    ShipType(int size, String displayName) {
        this.size = size;
        this.displayName = displayName;
    }

    public int getSize() {
        return size;
    }

    public String getDisplayName() {
        return displayName;
    }
}