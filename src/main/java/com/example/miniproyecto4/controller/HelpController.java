package com.example.miniproyecto4.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HelpController {

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        backButton.setOnAction(e -> handleBack());
    }

    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}