package com.example.miniproyecto4.model.Player;

import com.example.miniproyecto4.model.Board.Board;
import com.example.miniproyecto4.model.Board.IBoard;
import java.io.Serializable;

public class Player implements IPlayer, Serializable {

    private static final long serialVersionUID = 1L;

    private String nickname;
    private final IBoard board;
    private int sunkShipsCount;

    public Player(String nickname) {
        this.nickname = nickname;
        this.board = new Board();
        this.sunkShipsCount = 0;
    }

    public Player(String nickname, IBoard board) {
        this.nickname = nickname;
        this.board = board;
        this.sunkShipsCount = 0;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public IBoard getBoard() {
        return board;
    }

    @Override
    public int getSunkShipsCount() {
        return sunkShipsCount;
    }

    @Override
    public boolean hasLost() {
        return board.allShipsSunk();
    }

    @Override
    public void incrementSunkShips() {
        this.sunkShipsCount++;
    }

    @Override
    public void reset() {
        board.reset();
        sunkShipsCount = 0;
    }
}