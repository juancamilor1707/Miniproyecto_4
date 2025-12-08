package com.example.miniproyecto4.model.Board;

public class BoardFactory {

    public static IBoard createBoard() {
        return new Board();
    }

    public static IBoard createBoard(int size) {
        return new Board(size);
    }

    public static IBoard createEmptyBoard() {
        return new Board();
    }
}