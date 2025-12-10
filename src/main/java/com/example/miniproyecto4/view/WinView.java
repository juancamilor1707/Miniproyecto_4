package com.example.miniproyecto4.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * View representing the victory screen in the Battleship game.
 * Displays a congratulatory message when the player wins.
 * Extends Stage to create an independent window.
 */
public class WinView extends Stage {

    /**
     * The root node of the FXML layout.
     */
    private Parent root;

    /**
     * Constructs a WinView and initializes the victory screen.
     * Loads the FXML layout and configures the stage.
     */
    public WinView() {
        loadFXML();
        configureStage();
    }

    /**
     * Loads the FXML file for the victory screen.
     * Initializes the root node from the FXML resource.
     */
    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/miniproyecto4/WinView.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the stage properties for the victory window.
     * Sets the scene, dimensions, and window title.
     */
    private void configureStage() {
        Scene scene = new Scene(root, 630, 615);
        setScene(scene);
        initStyle(StageStyle.UNDECORATED);
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