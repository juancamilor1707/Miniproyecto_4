package com.example.miniproyecto4.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * View representing the help screen in the Battleship game.
 * Displays game instructions, rules, and controls to assist players.
 * Extends Stage to create an independent window.
 */
public class Help extends Stage {

    /**
     * The root node of the FXML layout.
     */
    private Parent root;

    /**
     * Constructs a Help view and initializes the help screen.
     * Loads the FXML layout and configures the stage.
     */
    public Help() {
        loadFXML();
        configureStage();
    }

    /**
     * Loads the FXML file for the help screen.
     * Initializes the root node from the FXML resource.
     */
    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/miniproyecto4/HelpView.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the stage properties for the help window.
     * Sets the scene, dimensions, and window title.
     */
    private void configureStage() {
        Scene scene = new Scene(root, 800, 600);
        setScene(scene);
        setTitle("Batalla Naval - Ayuda");
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