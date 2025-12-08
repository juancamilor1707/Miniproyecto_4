package com.example.miniproyecto4.model.GameSave;

import com.example.miniproyecto4.model.Game.GameStatus;
import com.example.miniproyecto4.model.Player.IPlayer;
import java.io.*;

public class GameRepository implements IGameRepository {

    private static final String SAVE_FILE = "battleship_save.ser";
    private final ISerializer serializer;
    private final IFlatFileManager flatFileManager;

    public GameRepository() {
        this.serializer = new GameSerializer();
        this.flatFileManager = new FlatFileManager();
    }

    @Override
    public void saveGame(IPlayer humanPlayer, IPlayer computerPlayer, GameStatus gameStatus, boolean isPlayerTurn) {
        SerializableGameData gameData = new SerializableGameData(humanPlayer, computerPlayer, gameStatus, isPlayerTurn);

        serializer.serialize(gameData, SAVE_FILE);

        flatFileManager.savePlayerData(humanPlayer.getNickname(), humanPlayer.getSunkShipsCount());
    }

    @Override
    public SerializableGameData loadGame() {
        return serializer.deserialize(SAVE_FILE);
    }

    @Override
    public boolean hasSavedGame() {
        File file = new File(SAVE_FILE);
        return file.exists();
    }

    @Override
    public void deleteSavedGame() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
        }
        flatFileManager.deletePlayerData();
    }
}