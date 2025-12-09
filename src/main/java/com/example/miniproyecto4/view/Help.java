package com.example.miniproyecto4.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Help extends Stage {

    private Parent root;

    public Help() {
        loadFXML();
        configureStage();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/miniproyecto4/HelpView.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configureStage() {
        Scene scene = new Scene(root, 630, 615);
        setScene(scene);
        setTitle("Batalla Naval - Ayuda");
    }

    public Parent getRoot() {
        return root;
    }
}