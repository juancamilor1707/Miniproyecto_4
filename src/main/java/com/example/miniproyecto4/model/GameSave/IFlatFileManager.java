package com.example.miniproyecto4.model.GameSave;

public interface IFlatFileManager {

    void savePlayerData(String nickname, int sunkShips);

    String[] loadPlayerData();

    void deletePlayerData();
}