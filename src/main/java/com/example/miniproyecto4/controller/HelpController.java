package com.example.miniproyecto4.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller for the help screen.
 * Manages the help view and provides a way to return to the previous screen.
 */
public class HelpController {

    @FXML
    private Button backButton;

    /**
     * Initializes the controller after FXML injection.
     * Configures the back button action.
     */
    @FXML
    public void initialize() {
        backButton.setOnAction(e -> handleBack());
    }

    /**
     * Handles the back button click.
     * Closes the help window.
     */
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}