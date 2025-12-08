package com.example.miniproyecto4.controller;

import com.example.miniproyecto4.model.Game.GameManager;
import com.example.miniproyecto4.model.Game.GameStatus;
import com.example.miniproyecto4.view.Game;
import com.example.miniproyecto4.view.Help;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.File;

public class MenuController {

    @FXML
    private TextField nicknameField;

    @FXML
    private Button newGameButton;

    @FXML
    private Button continueButton;

    @FXML
    private Button helpButton;

    @FXML
    private Button exitButton;

    private GameManager gameManager;

    @FXML
    public void initialize() {
        gameManager = GameManager.getInstance();

        newGameButton.setOnAction(e -> handleNewGame());
        continueButton.setOnAction(e -> handleContinue());
        helpButton.setOnAction(e -> handleHelp());
        exitButton.setOnAction(e -> handleExit());

        // Verificar y limpiar partidas en SETUP antes de habilitar continuar
        checkAndCleanIncompleteSaves();
    }

    /**
     * Verifica si hay partidas guardadas en modo SETUP y las elimina
     */
    private void checkAndCleanIncompleteSaves() {
        File saveFile = new File("battleship_save.ser");

        if (saveFile.exists()) {
            try {
                // Intentar cargar para verificar el estado
                gameManager.loadGame();

                if (gameManager.getGameStatus() != GameStatus.PLAYING) {
                    // Partida incompleta, eliminar archivos
                    saveFile.delete();
                    File playerData = new File("player_data.txt");
                    playerData.delete();
                    gameManager.resetGame();
                    continueButton.setDisable(true);
                } else {
                    // Partida válida, habilitar continuar
                    gameManager.resetGame(); // Reset para no afectar el estado
                    continueButton.setDisable(false);
                }
            } catch (Exception e) {
                // Error al cargar, eliminar archivos corruptos
                saveFile.delete();
                File playerData = new File("player_data.txt");
                playerData.delete();
                continueButton.setDisable(true);
            }
        } else {
            continueButton.setDisable(true);
        }
    }

    private void handleNewGame() {
        String nickname = nicknameField.getText().trim();

        if (nickname.isEmpty()) {
            showAlert("Error", "Debes ingresar un nickname");
            return;
        }

        gameManager.startNewGame(nickname);

        Game gameView = new Game();
        gameView.show();

        Stage stage = (Stage) newGameButton.getScene().getWindow();
        stage.close();
    }

    private void handleContinue() {
        String nickname = nicknameField.getText().trim();

        if (nickname.isEmpty()) {
            showAlert("Error", "Debes ingresar tu nickname para continuar");
            return;
        }

        gameManager.loadGame();

        if (gameManager.getHumanPlayer() == null) {
            showAlert("Error", "No se pudo cargar la partida guardada");
            return;
        }

        String savedNickname = gameManager.getHumanPlayer().getNickname();

        if (!nickname.equalsIgnoreCase(savedNickname)) {
            showAlert("Error", "El nickname no coincide con la partida guardada.\n" +
                    "Partida guardada para: " + savedNickname);
            gameManager.resetGame();
            return;
        }

        // Verificación adicional (aunque ya se hizo en initialize)
        if (gameManager.getGameStatus() != GameStatus.PLAYING) {
            showAlert("Error", "La partida guardada está incompleta.\n" +
                    "Inicia un nuevo juego.");
            File saveFile = new File("battleship_save.ser");
            saveFile.delete();
            File playerData = new File("player_data.txt");
            playerData.delete();
            gameManager.resetGame();
            continueButton.setDisable(true);
            return;
        }

        Game gameView = new Game();
        gameView.show();

        Stage stage = (Stage) continueButton.getScene().getWindow();
        stage.close();
    }

    private void handleHelp() {
        Help helpView = new Help();
        helpView.show();
    }

    private void handleExit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}