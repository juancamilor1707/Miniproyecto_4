package com.example.miniproyecto4.model.Game;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Player.IPlayer;
import com.example.miniproyecto4.model.Shot.ShotResult;

public interface IGameManager {

    void startNewGame(String playerNickname);

    void loadGame();

    ShotResult processPlayerShot(Coordinate coordinate);

    ShotResult processComputerShot();

    IPlayer getHumanPlayer();

    IPlayer getComputerPlayer();

    GameStatus getGameStatus();

    boolean isPlayerTurn();

    void switchTurn();

    void saveGame();

    boolean hasWinner();

    IPlayer getWinner();

    void resetGame();

    Coordinate getLastComputerShot();
}