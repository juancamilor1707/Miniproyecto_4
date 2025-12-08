module com.example.miniproyecto4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.example.miniproyecto4 to javafx.fxml;
    opens com.example.miniproyecto4.controller to javafx.fxml;
    opens com.example.miniproyecto4.view to javafx.fxml;

    exports com.example.miniproyecto4;
    exports com.example.miniproyecto4.controller;
    exports com.example.miniproyecto4.view;
    exports com.example.miniproyecto4.model.Ship;
    exports com.example.miniproyecto4.model.Cell;
    exports com.example.miniproyecto4.model.Board;
    exports com.example.miniproyecto4.model.Player;
    exports com.example.miniproyecto4.model.Game;
    exports com.example.miniproyecto4.model.Validation;
    exports com.example.miniproyecto4.model.Shot;
    exports com.example.miniproyecto4.model.AI;
    exports com.example.miniproyecto4.model.GameSave;
    exports com.example.miniproyecto4.model.Exceptions;
    exports com.example.miniproyecto4.model.Utils;
    exports com.example.miniproyecto4.view.Components;
    exports com.example.miniproyecto4.view.utils;
}