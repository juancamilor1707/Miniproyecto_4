package com.example.miniproyecto4.view.Components;

import com.example.miniproyecto4.model.Cell.Coordinate;

public interface IBoardView {

    void drawCell(Coordinate coordinate, String color);

    void markHit(Coordinate coordinate);

    void markMiss(Coordinate coordinate);

    void markShip(Coordinate coordinate);

    void markSunk(Coordinate coordinate);

    void clear();

    void refresh();
}