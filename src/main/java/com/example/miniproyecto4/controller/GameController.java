package com.example.miniproyecto4.controller;

import com.example.miniproyecto4.model.Cell.Cell;
import com.example.miniproyecto4.model.Cell.CellStatus;
import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Game.GameManager;
import com.example.miniproyecto4.model.Game.GameStatus;
import com.example.miniproyecto4.model.Ship.IShip;
import com.example.miniproyecto4.model.Ship.ShipFactory;
import com.example.miniproyecto4.model.Shot.ShotResult;
import com.example.miniproyecto4.model.Validation.Orientation;
import com.example.miniproyecto4.view.Lose;
import com.example.miniproyecto4.view.WinView;
import com.example.miniproyecto4.view.Components.BoardView;
import com.example.miniproyecto4.view.Components.CellView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    @FXML
    private Label statusLabel;

    @FXML
    private Label instructionLabel;

    @FXML
    private Label shipsRemainingLabel;

    @FXML
    private Label currentShipLabel;

    @FXML
    private Label playerShipsLabel;

    @FXML
    private Label enemyShipsLabel;

    @FXML
    private Button rotateButton;

    @FXML
    private Button startGameButton;

    @FXML
    private Button showEnemyBoardButton;

    private GameManager gameManager;
    private BoardView playerBoard;
    private BoardView enemyBoard;
    private List<IShip> playerFleet;
    private int currentShipIndex;
    private Orientation currentOrientation;
    private boolean placementMode;
    private List<Coordinate> previewCoordinates;

    @FXML
    public void initialize() {
        gameManager = GameManager.getInstance();
        currentOrientation = Orientation.HORIZONTAL;
        placementMode = true;
        currentShipIndex = 0;
        previewCoordinates = new ArrayList<>();

        playerFleet = ShipFactory.createFleet();

        rotateButton.setOnAction(e -> handleRotate());
        startGameButton.setOnAction(e -> handleStartGame());
        showEnemyBoardButton.setOnAction(e -> handleShowEnemyBoard());
    }

    public void setBoards(BoardView playerBoard, BoardView enemyBoard) {
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;

        if (gameManager.getGameStatus() == GameStatus.PLAYING) {
            loadGameState();
        } else {
            setupPlacementMode();
        }
    }

    private void loadGameState() {
        placementMode = false;

        instructionLabel.setVisible(false);
        shipsRemainingLabel.setVisible(false);
        currentShipLabel.setVisible(false);
        rotateButton.setVisible(false);
        startGameButton.setVisible(false);
        showEnemyBoardButton.setVisible(true);

        javafx.scene.layout.VBox enemyBox = (javafx.scene.layout.VBox) showEnemyBoardButton.getParent().getParent().lookup("#enemyBoardBox");
        if (enemyBox != null) {
            enemyBox.setVisible(true);
        }

        restorePlayerBoard();
        restoreEnemyBoard();
        updatePlayerShips();
        updateEnemyShips();

        setupGameMode();

        // NUEVO: Verificar si es turno de la máquina y ejecutarlo automáticamente
        if (!gameManager.isPlayerTurn() && !gameManager.hasWinner()) {
            statusLabel.setText("Turno de la máquina...");
            // Dar un pequeño delay para que el jugador vea el estado del tablero
            javafx.animation.PauseTransition initialDelay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
            initialDelay.setOnFinished(e -> processComputerTurn());
            initialDelay.play();
        } else if (gameManager.isPlayerTurn()) {
            statusLabel.setText("Tu turno - Selecciona una celda para disparar");
        }
    }

    private void restorePlayerBoard() {
        List<IShip> ships = gameManager.getHumanPlayer().getBoard().getShips();

        for (IShip ship : ships) {
            for (Coordinate coord : ship.getCoordinates()) {
                Cell cell = gameManager.getHumanPlayer().getBoard().getCell(coord);

                if (cell.getStatus() == CellStatus.SUNK) {
                    playerBoard.markSunk(coord);
                } else if (cell.getStatus() == CellStatus.HIT) {
                    playerBoard.markHit(coord);
                } else if (cell.getStatus() == CellStatus.SHIP) {
                    playerBoard.markShip(coord);
                }
            }
        }

        int size = gameManager.getHumanPlayer().getBoard().getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Coordinate coord = new Coordinate(x, y);
                Cell cell = gameManager.getHumanPlayer().getBoard().getCell(coord);

                if (cell.getStatus() == CellStatus.MISS) {
                    playerBoard.markMiss(coord);
                }
            }
        }
    }

    private void restoreEnemyBoard() {
        int size = gameManager.getComputerPlayer().getBoard().getSize();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Coordinate coord = new Coordinate(x, y);
                Cell cell = gameManager.getComputerPlayer().getBoard().getCell(coord);

                if (cell.getStatus() == CellStatus.SUNK) {
                    enemyBoard.markSunk(coord);
                } else if (cell.getStatus() == CellStatus.HIT) {
                    enemyBoard.markHit(coord);
                } else if (cell.getStatus() == CellStatus.MISS) {
                    enemyBoard.markMiss(coord);
                }
            }
        }
    }

    private void setupPlacementMode() {
        updateShipsRemaining();
        updateCurrentShipLabel();

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                CellView cell = playerBoard.getCell(x, y);
                final int finalX = x;
                final int finalY = y;

                cell.setOnMouseClicked(e -> handleCellClickPlacement(new Coordinate(finalX, finalY)));
                cell.setOnMouseEntered(e -> showShipPreview(new Coordinate(finalX, finalY)));
                cell.setOnMouseExited(e -> clearShipPreview());
            }
        }
    }

    private void showShipPreview(Coordinate coordinate) {
        if (currentShipIndex >= playerFleet.size()) {
            return;
        }

        clearShipPreview();

        IShip ship = playerFleet.get(currentShipIndex);
        int size = ship.getSize();
        previewCoordinates.clear();

        for (int i = 0; i < size; i++) {
            Coordinate previewCoord;
            if (currentOrientation == Orientation.HORIZONTAL) {
                previewCoord = new Coordinate(coordinate.getX() + i, coordinate.getY());
            } else {
                previewCoord = new Coordinate(coordinate.getX(), coordinate.getY() + i);
            }

            if (isValidPreviewCoordinate(previewCoord)) {
                previewCoordinates.add(previewCoord);
                CellView cell = playerBoard.getCell(previewCoord);
                if (cell != null) {
                    cell.showPreview(true);
                }
            } else {
                if (previewCoord.getX() >= 0 && previewCoord.getX() < 10 &&
                        previewCoord.getY() >= 0 && previewCoord.getY() < 10) {
                    previewCoordinates.add(previewCoord);
                    CellView cell = playerBoard.getCell(previewCoord);
                    if (cell != null) {
                        cell.showPreview(false);
                    }
                }
            }
        }
    }

    private boolean isValidPreviewCoordinate(Coordinate coord) {
        if (coord.getX() < 0 || coord.getX() >= 10 || coord.getY() < 0 || coord.getY() >= 10) {
            return false;
        }
        return !gameManager.getHumanPlayer().getBoard().hasShipAt(coord);
    }

    private void clearShipPreview() {
        for (Coordinate coord : previewCoordinates) {
            CellView cell = playerBoard.getCell(coord);
            if (cell != null) {
                cell.clearPreview();
            }
        }
        previewCoordinates.clear();
    }

    private void handleCellClickPlacement(Coordinate coordinate) {
        if (currentShipIndex >= playerFleet.size()) {
            return;
        }

        IShip ship = playerFleet.get(currentShipIndex);
        ship.setPosition(coordinate, currentOrientation);

        if (gameManager.getHumanPlayer().getBoard().placeShip(ship)) {
            clearShipPreview();

            for (Coordinate coord : ship.getCoordinates()) {
                playerBoard.markShip(coord);
            }

            currentShipIndex++;
            updateShipsRemaining();
            updateCurrentShipLabel();

            if (currentShipIndex >= playerFleet.size()) {
                startGameButton.setDisable(false);
                currentShipLabel.setVisible(false);
                statusLabel.setText("¡Todos los barcos colocados! Presiona 'Iniciar Juego'");
            }
        } else {
            statusLabel.setText("Posición inválida - Intenta otra ubicación");
        }
    }

    private void handleRotate() {
        if (!placementMode) return;

        currentOrientation = (currentOrientation == Orientation.HORIZONTAL)
                ? Orientation.VERTICAL
                : Orientation.HORIZONTAL;

        updateCurrentShipLabel();
        clearShipPreview();
    }

    public void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.R && placementMode) {
            handleRotate();
        }
    }

    private void handleStartGame() {
        placementMode = false;
        clearShipPreview();
        gameManager.setGameStatus(GameStatus.PLAYING);

        instructionLabel.setVisible(false);
        shipsRemainingLabel.setVisible(false);
        currentShipLabel.setVisible(false);
        rotateButton.setVisible(false);
        startGameButton.setVisible(false);
        showEnemyBoardButton.setVisible(true);

        javafx.scene.layout.VBox enemyBox = (javafx.scene.layout.VBox) showEnemyBoardButton.getParent().getParent().lookup("#enemyBoardBox");
        if (enemyBox != null) {
            enemyBox.setVisible(true);
        }

        statusLabel.setText("Tu turno - Selecciona una celda para disparar");

        updatePlayerShips();
        updateEnemyShips();

        setupGameMode();
    }

    private void setupGameMode() {
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                CellView cell = enemyBoard.getCell(x, y);
                final int finalX = x;
                final int finalY = y;
                cell.setOnMouseClicked(e -> handleCellClickGame(new Coordinate(finalX, finalY)));
            }
        }
    }

    private void handleCellClickGame(Coordinate coordinate) {
        if (!gameManager.isPlayerTurn() || gameManager.hasWinner()) {
            return;
        }

        ShotResult result = gameManager.processPlayerShot(coordinate);

        switch (result) {
            case WATER:
                enemyBoard.markMiss(coordinate);
                statusLabel.setText("¡Agua! Turno de la máquina");
                updateEnemyShips();
                javafx.animation.PauseTransition pauseWater = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(0.5));
                pauseWater.setOnFinished(e -> processComputerTurn());
                pauseWater.play();
                break;
            case HIT:
                enemyBoard.markHit(coordinate);
                statusLabel.setText("¡Tocado! Dispara de nuevo");
                updateEnemyShips();
                break;
            case SUNK:
                markSunkShipOnBoard(gameManager.getComputerPlayer().getBoard(), coordinate, enemyBoard);
                updateEnemyShips();
                if (gameManager.hasWinner()) {
                    showWinScreen();
                } else {
                    statusLabel.setText("¡Hundido! Dispara de nuevo");
                }
                break;
            case INVALID:
                statusLabel.setText("Disparo inválido - Ya disparaste ahí");
                break;
        }
    }

    private void processComputerTurn() {
        if (gameManager.hasWinner()) {
            return;
        }

        statusLabel.setText("Turno de la máquina...");

        javafx.animation.PauseTransition pauseComputer = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        pauseComputer.setOnFinished(e -> {
            ShotResult result = gameManager.processComputerShot();
            Coordinate lastShot = gameManager.getLastComputerShot();

            if (lastShot == null) {
                return;
            }

            switch (result) {
                case WATER:
                    playerBoard.markMiss(lastShot);
                    statusLabel.setText("La máquina falló en " + coordToString(lastShot) + " - Tu turno");
                    updatePlayerShips();
                    break;
                case HIT:
                    playerBoard.markHit(lastShot);
                    statusLabel.setText("¡La máquina te tocó en " + coordToString(lastShot) + "!");
                    updatePlayerShips();
                    javafx.animation.PauseTransition pauseHit = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                    pauseHit.setOnFinished(ev -> processComputerTurn());
                    pauseHit.play();
                    break;
                case SUNK:
                    markSunkShipOnBoard(gameManager.getHumanPlayer().getBoard(), lastShot, playerBoard);
                    updatePlayerShips();
                    if (gameManager.hasWinner()) {
                        showLoseScreen();
                    } else {
                        statusLabel.setText("¡Te hundieron un barco en " + coordToString(lastShot) + "!");
                        javafx.animation.PauseTransition pauseSunk = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
                        pauseSunk.setOnFinished(ev -> processComputerTurn());
                        pauseSunk.play();
                    }
                    break;
            }
        });
        pauseComputer.play();
    }

    private String coordToString(Coordinate coord) {
        char letter = (char) ('A' + coord.getY());
        int number = coord.getX() + 1;
        return letter + "" + number;
    }

    private void markSunkShipOnBoard(com.example.miniproyecto4.model.Board.IBoard board, Coordinate hitCoord, BoardView boardView) {
        IShip ship = board.getShipAt(hitCoord);
        if (ship != null && ship.isSunk()) {
            for (Coordinate coord : ship.getCoordinates()) {
                boardView.markSunk(coord);
            }
        }
    }

    private void handleShowEnemyBoard() {
        for (IShip ship : gameManager.getComputerPlayer().getBoard().getShips()) {
            for (Coordinate coord : ship.getCoordinates()) {
                Cell cell = gameManager.getComputerPlayer().getBoard().getCell(coord);
                if (cell.getStatus() == CellStatus.SHIP) {
                    enemyBoard.markShip(coord);
                }
            }
        }
    }

    private void updateShipsRemaining() {
        int remaining = playerFleet.size() - currentShipIndex;
        shipsRemainingLabel.setText("Barcos restantes: " + remaining);
    }

    private void updateCurrentShipLabel() {
        if (currentShipIndex < playerFleet.size()) {
            IShip ship = playerFleet.get(currentShipIndex);
            String orientationText = currentOrientation == Orientation.HORIZONTAL ? "Horizontal" : "Vertical";
            currentShipLabel.setText("Colocando: " + ship.getType().getDisplayName() +
                    " (" + ship.getSize() + " casillas) - " + orientationText + " [R para rotar]");
        }
    }

    private void updatePlayerShips() {
        int total = gameManager.getHumanPlayer().getBoard().getShips().size();
        int sunk = gameManager.getHumanPlayer().getBoard().getSunkShipsCount();
        int remaining = total - sunk;
        playerShipsLabel.setText("Tus barcos: " + remaining);
    }

    private void updateEnemyShips() {
        int total = gameManager.getComputerPlayer().getBoard().getShips().size();
        int sunk = gameManager.getComputerPlayer().getBoard().getSunkShipsCount();
        int remaining = total - sunk;
        enemyShipsLabel.setText("Barcos enemigos: " + remaining);
    }

    private void showWinScreen() {
        WinView winView = new WinView();
        winView.show();

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    private void showLoseScreen() {
        Lose loseView = new Lose();
        loseView.show();

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }
}