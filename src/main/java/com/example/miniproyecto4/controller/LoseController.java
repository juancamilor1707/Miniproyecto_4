package com.example.miniproyecto4.controller;

import com.example.miniproyecto4.model.Game.GameManager;
import com.example.miniproyecto4.view.Game;
import com.example.miniproyecto4.view.Menu;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.File;

public class LoseController {

    @FXML
    private Label statsLabel;

    @FXML
    private Button newGameButton;

    @FXML
    private Button menuButton;

    private GameManager gameManager;

    @FXML
    public void initialize() {
        gameManager = GameManager.getInstance();

        int enemyShipsSunk = gameManager.getComputerPlayer().getBoard().getSunkShipsCount();

        statsLabel.setText("Barcos enemigos hundidos: " + enemyShipsSunk + "\nTodos tus barcos fueron hundidos");

        deleteSavedGame();

        newGameButton.setOnAction(e -> handleNewGame());
        menuButton.setOnAction(e -> handleMenu());
    }

    private void deleteSavedGame() {
        File saveFile = new File("battleship_save.ser");
        if (saveFile.exists()) {
            saveFile.delete();
        }
        File playerData = new File("player_data.txt");
        if (playerData.exists()) {
            playerData.delete();
        }
    }

    private void handleNewGame() {
        gameManager.resetGame();

        Game gameView = new Game();
        gameView.show();

        Stage stage = (Stage) newGameButton.getScene().getWindow();
        stage.close();
    }

    private void handleMenu() {
        Menu menuView = new Menu();
        menuView.show();

        Stage stage = (Stage) menuButton.getScene().getWindow();
        stage.close();
    }
}