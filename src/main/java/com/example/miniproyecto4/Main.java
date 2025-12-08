package com.example.miniproyecto4;

import com.example.miniproyecto4.view.Menu;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Menu menuView = new Menu();
        menuView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}