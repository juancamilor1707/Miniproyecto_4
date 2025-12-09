package com.example.miniproyecto4.model.Board;

/**
 * Factory class for creating board instances.
 * Provides convenient methods for board creation with different configurations.
 */
public class BoardFactory {

    /**
     * Creates a board with the default size (10x10).
     *
     * @return A new board instance
     */
    public static IBoard createBoard() {
        return new Board();
    }

    /**
     * Creates a board with a custom size.
     *
     * @param size The size of the board
     * @return A new board instance
     */
    public static IBoard createBoard(int size) {
        return new Board(size);
    }

    /**
     * Creates an empty board with the default size.
     *
     * @return A new empty board instance
     */
    public static IBoard createEmptyBoard() {
        return new Board();
    }
}