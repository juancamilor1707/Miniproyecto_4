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

/**
 * Controller for the main game view.
 * Manages the game flow including ship placement, turn-based shooting,
 * board updates, and game state transitions.
 */
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

    /**
     * Initializes the controller after FXML injection.
     * Sets up the game manager, creates the player fleet, and configures button actions.
     */
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

    /**
     * Sets the board views for the player and enemy.
     * Determines whether to load a saved game state or start placement mode.
     *
     * @param playerBoard The visual board for the player
     * @param enemyBoard The visual board for the enemy
     */
    public void setBoards(BoardView playerBoard, BoardView enemyBoard) {
        this.playerBoard = playerBoard;
        this.enemyBoard = enemyBoard;

        if (gameManager.getGameStatus() == GameStatus.PLAYING) {
            loadGameState();
        } else {
            setupPlacementMode();
        }
    }

    /**
     * Loads and restores a previously saved game state.
     * Restores both boards, ship counts, and game mode.
     * If it's the computer's turn, automatically processes it.
     */
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

        if (!gameManager.isPlayerTurn() && !gameManager.hasWinner()) {
            statusLabel.setText("Turno de la máquina...");
            javafx.animation.PauseTransition initialDelay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
            initialDelay.setOnFinished(e -> processComputerTurn());
            initialDelay.play();
        } else if (gameManager.isPlayerTurn()) {
            statusLabel.setText("Tu turno - Selecciona una celda para disparar");
        }
    }

    /**
     * Restores the player's board state from the saved game.
     * Marks all ship positions, hits, misses, and sunk ships.
     */
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

    /**
     * Restores the enemy's board state from the saved game.
     * Only shows hits, misses, and sunk ships (not ship positions).
     */
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

    /**
     * Sets up the ship placement mode at the start of the game.
     * Configures cell event handlers for placement and preview display.
     */
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

    /**
     * Shows a visual preview of ship placement at the given coordinate.
     * Displays valid placements in green and invalid ones in red.
     *
     * @param coordinate The starting coordinate for the preview
     */
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

    /**
     * Checks if a coordinate is valid for ship preview.
     *
     * @param coord The coordinate to validate
     * @return true if the coordinate is within bounds and has no ship, false otherwise
     */
    private boolean isValidPreviewCoordinate(Coordinate coord) {
        if (coord.getX() < 0 || coord.getX() >= 10 || coord.getY() < 0 || coord.getY() >= 10) {
            return false;
        }
        return !gameManager.getHumanPlayer().getBoard().hasShipAt(coord);
    }

    /**
     * Clears all ship placement previews from the board.
     */
    private void clearShipPreview() {
        for (Coordinate coord : previewCoordinates) {
            CellView cell = playerBoard.getCell(coord);
            if (cell != null) {
                cell.clearPreview();
            }
        }
        previewCoordinates.clear();
    }

    /**
     * Handles cell click during ship placement mode.
     * Attempts to place the current ship at the clicked coordinate.
     *
     * @param coordinate The coordinate where the user clicked
     */
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

    /**
     * Handles the rotation button click.
     * Toggles ship orientation between horizontal and vertical.
     */
    private void handleRotate() {
        if (!placementMode) return;

        currentOrientation = (currentOrientation == Orientation.HORIZONTAL)
                ? Orientation.VERTICAL
                : Orientation.HORIZONTAL;

        updateCurrentShipLabel();
        clearShipPreview();
    }

    /**
     * Handles keyboard input for ship rotation.
     * Pressing 'R' rotates the ship during placement mode.
     *
     * @param event The keyboard event
     */
    public void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.R && placementMode) {
            handleRotate();
        }
    }

    /**
     * Handles the start game button click.
     * Transitions from placement mode to game mode and saves the game state.
     */
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

    /**
     * Sets up the game mode after ship placement.
     * Configures cell click handlers for shooting on the enemy board.
     */
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

    /**
     * Handles cell click during game mode (shooting phase).
     * Processes the player's shot and updates the board accordingly.
     *
     * @param coordinate The coordinate where the player shot
     */
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

    /**
     * Processes the computer's turn.
     * Displays animations and updates the board based on the computer's shot result.
     */
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

    /**
     * Converts a coordinate to alphanumeric notation (e.g., A1, B5).
     *
     * @param coord The coordinate to convert
     * @return The alphanumeric representation
     */
    private String coordToString(Coordinate coord) {
        char letter = (char) ('A' + coord.getY());
        int number = coord.getX() + 1;
        return letter + "" + number;
    }

    /**
     * Marks all coordinates of a sunk ship on the board.
     *
     * @param board The board containing the ship
     * @param hitCoord The coordinate that was hit
     * @param boardView The visual board to update
     */
    private void markSunkShipOnBoard(com.example.miniproyecto4.model.Board.IBoard board, Coordinate hitCoord, BoardView boardView) {
        IShip ship = board.getShipAt(hitCoord);
        if (ship != null && ship.isSunk()) {
            for (Coordinate coord : ship.getCoordinates()) {
                boardView.markSunk(coord);
            }
        }
    }

    /**
     * Shows all enemy ships on the board (for debugging/cheating).
     * Triggered by the "Show Enemy Board" button.
     */
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

    /**
     * Updates the label showing how many ships remain to be placed.
     */
    private void updateShipsRemaining() {
        int remaining = playerFleet.size() - currentShipIndex;
        shipsRemainingLabel.setText("Barcos restantes: " + remaining);
    }

    /**
     * Updates the label showing the current ship being placed.
     */
    private void updateCurrentShipLabel() {
        if (currentShipIndex < playerFleet.size()) {
            IShip ship = playerFleet.get(currentShipIndex);
            String orientationText = currentOrientation == Orientation.HORIZONTAL ? "Horizontal" : "Vertical";
            currentShipLabel.setText("Colocando: " + ship.getType().getDisplayName() +
                    " (" + ship.getSize() + " casillas) - " + orientationText + " [R para rotar]");
        }
    }

    /**
     * Updates the label showing the player's remaining ships.
     */
    private void updatePlayerShips() {
        int total = gameManager.getHumanPlayer().getBoard().getShips().size();
        int sunk = gameManager.getHumanPlayer().getBoard().getSunkShipsCount();
        int remaining = total - sunk;
        playerShipsLabel.setText("Tus barcos: " + remaining);
    }

    /**
     * Updates the label showing the enemy's remaining ships.
     */
    private void updateEnemyShips() {
        int total = gameManager.getComputerPlayer().getBoard().getShips().size();
        int sunk = gameManager.getComputerPlayer().getBoard().getSunkShipsCount();
        int remaining = total - sunk;
        enemyShipsLabel.setText("Barcos enemigos: " + remaining);
    }

    /**
     * Shows the win screen and closes the game window.
     */
    private void showWinScreen() {
        WinView winView = new WinView();
        winView.show();

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows the lose screen and closes the game window.
     */
    private void showLoseScreen() {
        Lose loseView = new Lose();
        loseView.show();

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }
}