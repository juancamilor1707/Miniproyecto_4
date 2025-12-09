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
 * Main game view for the Battleship game.
 * Manages the creation of the game window, visual boards, and controller connection.
 * Extends Stage to create the primary game window.
 */
public class Game extends Stage {

    /**
     * The root node of the FXML layout.
     */
    private Parent root;

    /**
     * Container for the player's board visualization.
     */
    private VBox playerBoardContainer;

    /**
     * Container for the enemy's board visualization.
     */
    private VBox enemyBoardContainer;

    /**
     * Visual representation of the player's game board.
     */
    private BoardView playerBoard;

    /**
     * Visual representation of the enemy's game board.
     */
    private BoardView enemyBoard;

    /**
     * Controller that manages game logic and user interactions.
     */
    private GameController controller;

    /**
     * Constructs the game view.
     * Initializes all necessary components and configures the window.
     */
    public Game() {
        loadFXML();
        initializeBoards();
        configureStage();
    }

    /**
     * Loads the FXML file and obtains references to the containers.
     * Initializes the controller and board container references.
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
     * Initializes the visual boards and connects them with the controller.
     * Creates BoardView instances for both player and enemy boards.
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
     * Configures the game window with size and event handlers.
     * Sets up keyboard event handling for game controls.
     */
    private void configureStage() {
        Scene scene = new Scene(root, 1200, 800);

        // Add keyboard event handler for R key
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (controller != null) {
                controller.handleKeyPress(event);
            }
        });

        setScene(scene);
        setTitle("Batalla Naval");
        setResizable(false);
    }

    /**
     * Returns the root node of the view.
     *
     * @return the root Parent node
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Returns the player's visual board.
     *
     * @return the player's BoardView
     */
    public BoardView getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Returns the enemy's visual board.
     *
     * @return the enemy's BoardView
     */
    public BoardView getEnemyBoard() {
        return enemyBoard;
    }
}