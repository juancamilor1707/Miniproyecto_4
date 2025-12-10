package com.example.miniproyecto4.controller;

import com.example.miniproyecto4.model.Game.GameManager;
import com.example.miniproyecto4.view.Game;
import com.example.miniproyecto4.view.Menu;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.File;

/**
 * Controller for the win screen.
 * Displays game statistics and provides options to start a new game or return to the menu.
 */
public class WinController {

    @FXML
    private Label statsLabel;

    @FXML
    private Button newGameButton;

    @FXML
    private Button menuButton;

    private GameManager gameManager;

    /**
     * Initializes the controller after FXML injection.
     * Displays game statistics, deletes the saved game, and configures button actions.
     */
    @FXML
    public void initialize() {
        gameManager = GameManager.getInstance();

        int enemyShipsSunk = gameManager.getComputerPlayer().getBoard().getSunkShipsCount();
        int playerShipsSunk = gameManager.getHumanPlayer().getBoard().getSunkShipsCount();

        statsLabel.setText("Barcos enemigos hundidos: " + enemyShipsSunk + "\nTus barcos perdidos: " + playerShipsSunk);

        deleteSavedGame();

        menuButton.setOnAction(e -> handleMenu());
    }

    /**
     * Deletes the saved game files after a win.
     */
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

    /**
     * Handles the new game button click.
     * Resets the game and opens the game view.
     */
    private void handleNewGame() {
        gameManager.resetGame();

        Game gameView = new Game();
        gameView.show();

        Stage stage = (Stage) newGameButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the menu button click.
     * Returns to the main menu.
     */
    private void handleMenu() {
        Menu menuView = new Menu();
        menuView.show();

        Stage stage = (Stage) menuButton.getScene().getWindow();
        stage.close();
    }
}