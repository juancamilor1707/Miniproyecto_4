package com.example.miniproyecto4.model.AI;

import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Coordinate;

public interface IAIStrategy {

    Coordinate selectTarget(IBoard opponentBoard);

    void updateStrategy(Coordinate lastShot, boolean wasHit);

    void reset();
}