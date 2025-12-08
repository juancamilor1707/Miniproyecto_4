package com.example.miniproyecto4.model.GameSave;

import com.example.miniproyecto4.model.Game.GameStatus;
import com.example.miniproyecto4.model.Player.IPlayer;

public interface IGameRepository {

    void saveGame(IPlayer humanPlayer, IPlayer computerPlayer, GameStatus gameStatus, boolean isPlayerTurn);

    SerializableGameData loadGame();

    boolean hasSavedGame();

    void deleteSavedGame();
}