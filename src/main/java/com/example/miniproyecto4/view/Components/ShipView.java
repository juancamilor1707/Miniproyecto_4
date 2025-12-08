package com.example.miniproyecto4.view.Components;

import com.example.miniproyecto4.model.Ship.ShipType;
import com.example.miniproyecto4.view.utils.Colors;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ShipView extends Group {

    private final ShipType type;
    private final double cellSize;
    private final boolean horizontal;

    public ShipView(ShipType type, double cellSize, boolean horizontal) {
        this.type = type;
        this.cellSize = cellSize;
        this.horizontal = horizontal;
        draw();
    }

    private void draw() {
        int size = type.getSize();

        for (int i = 0; i < size; i++) {
            Rectangle segment = new Rectangle(cellSize, cellSize);
            segment.setFill(Color.web(Colors.SHIP));
            segment.setStroke(Color.web(Colors.SHIP_BORDER));
            segment.setStrokeWidth(2);

            if (horizontal) {
                segment.setX(i * (cellSize + 2));
                segment.setY(0);
            } else {
                segment.setX(0);
                segment.setY(i * (cellSize + 2));
            }

            getChildren().add(segment);
        }
    }
}