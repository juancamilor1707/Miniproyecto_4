package com.example.miniproyecto4.model.Game;

/**
 * Enumeration representing the possible states of a Battleship game.
 * Tracks the game's progress from setup through playing to completion.
 */
public enum GameStatus {
    /**
     * Game is in setup phase where ships are being placed.
     */
    SETUP,

    /**
     * Game is actively being played with turns alternating between players.
     */
    PLAYING,

    /**
     * Game has ended with the human player as the winner.
     */
    PLAYER_WON,

    /**
     * Game has ended with the computer player as the winner.
     */
    COMPUTER_WON
}