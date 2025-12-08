package com.example.miniproyecto4.model.Player;

import com.example.miniproyecto4.model.Board.IBoard;
import java.io.Serializable;

public interface IPlayer extends Serializable {

    String getNickname();

    void setNickname(String nickname);

    IBoard getBoard();

    int getSunkShipsCount();

    boolean hasLost();

    void incrementSunkShips();

    void reset();
}