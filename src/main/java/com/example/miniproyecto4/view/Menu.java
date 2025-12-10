package com.example.miniproyecto4.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * View representing the main menu screen of the Battleship game.
 * Provides navigation options to start a new game, load a saved game,
 * view help, or exit the application.
 * Extends Stage to create an independent window.
 */
public class Menu extends Stage {

    /**
     * The root node of the FXML layout.
     */
    private Parent root;

    /**
     * Constructs a Menu view and initializes the main menu screen.
     * Loads the FXML layout and configures the stage.
     */
    public Menu() {
        loadFXML();
        configureStage();
    }

    /**
     * Loads the FXML file for the main menu screen.
     * Initializes the root node from the FXML resource.
     */
    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/miniproyecto4/MenuView.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the stage properties for the main menu window.
     * Sets the scene, dimensions, and window title.
     */
    private void configureStage() {
        Scene scene = new Scene(root, 630, 615);
        setResizable(false);
        setScene(scene);
        setTitle("Batalla Naval - Menú Principal");
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