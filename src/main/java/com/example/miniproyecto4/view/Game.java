package com.example.miniproyecto4.view;

import com.example.miniproyecto4.controller.GameController;
import com.example.miniproyecto4.view.Components.BoardView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Vista principal del juego de Batalla Naval.
 * Gestiona la creación de la ventana, los tableros visuales y la conexión con el controlador.
 */
public class Game extends Stage {

    private Parent root;
    private VBox playerBoardContainer;
    private VBox enemyBoardContainer;
    private BoardView playerBoard;
    private BoardView enemyBoard;
    private GameController controller;

    /**
     * Constructor de la vista del juego.
     * Inicializa todos los componentes necesarios y configura la ventana.
     */
    public Game() {
        loadFXML();
        initializeBoards();
        configureStage();
    }

    /**
     * Carga el archivo FXML y obtiene las referencias a los contenedores.
     */
    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/miniproyecto4/GameView.fxml"));
            root = loader.load();

            controller = loader.getController();

            playerBoardContainer = (VBox) root.lookup("#playerBoardContainer");
            enemyBoardContainer = (VBox) root.lookup("#enemyBoardContainer");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicializa los tableros visuales y los conecta con el controlador.
     */
    private void initializeBoards() {
        playerBoard = new BoardView(10, 40);
        enemyBoard = new BoardView(10, 40);

        if (playerBoardContainer != null) {
            playerBoardContainer.getChildren().add(playerBoard);
        }

        if (enemyBoardContainer != null) {
            enemyBoardContainer.getChildren().add(enemyBoard);
        }

        if (controller != null) {
            controller.setBoards(playerBoard, enemyBoard);
        }
    }

    /**
     * Configura la ventana del juego con el tamaño y los manejadores de eventos.
     */
    private void configureStage() {
        Scene scene = new Scene(root, 1200, 800);

        // Agregar manejador de eventos de teclado para la tecla R
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (controller != null) {
                controller.handleKeyPress(event);
            }
        });

        setScene(scene);
        setTitle("Batalla Naval");
        setResizable(false); // Opcional: evitar que se redimensione la ventana
    }

    /**
     * Obtiene el root de la vista.
     * @return El nodo raíz de la vista
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Obtiene el tablero visual del jugador.
     * @return El BoardView del jugador
     */
    public BoardView getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Obtiene el tablero visual del enemigo.
     * @return El BoardView del enemigo
     */
    public BoardView getEnemyBoard() {
        return enemyBoard;
    }
}