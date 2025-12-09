package com.example.miniproyecto4.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * View representing the defeat screen in the Battleship game.
 * Displays a message when the player loses the game.
 * Extends Stage to create an independent window.
 */
public class Lose extends Stage {

    /**
     * The root node of the FXML layout.
     */
    private Parent root;

    /**
     * Constructs a Lose view and initializes the defeat screen.
     * Loads the FXML layout and configures the stage.
     */
    public Lose() {
        loadFXML();
        configureStage();
    }

    /**
     * Loads the FXML file for the defeat screen.
     * Initializes the root node from the FXML resource.
     */
    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/miniproyecto4/LoseView.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the stage properties for the defeat window.
     * Sets the scene, dimensions, and window title.
     */
    private void configureStage() {
        Scene scene = new Scene(root, 630, 615);
        setScene(scene);
        setTitle("Batalla Naval - Derrota");
    }

    /**
     * Returns the root node of the view.
     *
     * @return the root Parent node
     */
    public Parent getRoot() {
        return root;
    }
}