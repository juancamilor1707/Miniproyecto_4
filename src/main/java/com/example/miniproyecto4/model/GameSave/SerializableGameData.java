package com.example.miniproyecto4.model.GameSave;

import com.example.miniproyecto4.model.Game.GameStatus;
import com.example.miniproyecto4.model.Player.IPlayer;
import java.io.Serializable;

public class SerializableGameData implements Serializable {

    private static final long serialVersionUID = 1L;

    private final IPlayer humanPlayer;
    private final IPlayer computerPlayer;
    private final GameStatus gameStatus;
    private final boolean isPlayerTurn;

    public SerializableGameData(IPlayer humanPlayer, IPlayer computerPlayer, GameStatus gameStatus, boolean isPlayerTurn) {
        this.humanPlayer = humanPlayer;
        this.computerPlayer = computerPlayer;
        this.gameStatus = gameStatus;
        this.isPlayerTurn = isPlayerTurn;
    }

    public IPlayer getHumanPlayer() {
        return humanPlayer;
    }

    public IPlayer getComputerPlayer() {
        return computerPlayer;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }
}