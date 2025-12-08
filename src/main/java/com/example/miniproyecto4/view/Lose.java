package com.example.miniproyecto4.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Lose extends Stage {

    private Parent root;

    public Lose() {
        loadFXML();
        configureStage();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/miniproyecto4/LoseView.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configureStage() {
        Scene scene = new Scene(root, 800, 600);
        setScene(scene);
        setTitle("Batalla Naval - Derrota");
    }

    public Parent getRoot() {
        return root;
    }
}