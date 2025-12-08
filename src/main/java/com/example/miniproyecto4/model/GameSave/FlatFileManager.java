package com.example.miniproyecto4.model.GameSave;

import java.io.*;

public class FlatFileManager implements IFlatFileManager {

    private static final String PLAYER_DATA_FILE = "player_data.txt";

    @Override
    public void savePlayerData(String nickname, int sunkShips) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PLAYER_DATA_FILE))) {
            writer.println(nickname);
            writer.println(sunkShips);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] loadPlayerData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PLAYER_DATA_FILE))) {
            String nickname = reader.readLine();
            String sunkShips = reader.readLine();
            return new String[]{nickname, sunkShips};
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deletePlayerData() {
        File file = new File(PLAYER_DATA_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}