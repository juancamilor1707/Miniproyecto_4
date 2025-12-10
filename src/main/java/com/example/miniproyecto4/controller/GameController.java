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
import com.example.miniproyecto4.view.Menu;
import com.example.miniproyecto4.view.WinView;
import com.example.miniproyecto4.view.Components.BoardView;
import com.example.miniproyecto4.view.Components.CellView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the main game view.
 * Manages the game flow including ship placement, turn-based shooting,
 * board updates, and game state transitions.
 */
public class GameController {
    /**
     * Flag indicating whether the player is currently viewing enemy ships.
     */
    private boolean isSeeingEnemyShips = false;

    /**
     * Label displaying the current game status and turn information.
     */
    @FXML
    private Label statusLabel;

    /**
     * Label providing instructions to the player.
     */
    @FXML
    private Label instructionLabel;

    /**
     * Label showing the number of ships remaining to be placed.
     */
    @FXML
    private Label shipsRemainingLabel;

    /**
     * Label displaying information about the current ship being placed.
     */
    @FXML
    private Label currentShipLabel;

    /**
     * Label showing the player's remaining ships count.
     */
    @FXML
    private Label playerShipsLabel;

    /**
     * Label showing the enemy's remaining ships count.
     */
    @FXML
    private Label enemyShipsLabel;

    /**
     * Button to rotate the current ship during placement phase.
     */
    @FXML
    private Button rotateButton;

    /**
     * Button to start the game after all ships are placed.
     */
    @FXML
    private Button startGameButton;

    /**
     * Button to toggle visibility of enemy ships on the board.
     */
    @FXML
    private Button showEnemyBoardButton;

    /**
     * Button to return to the main menu.
     */
    @FXML
    private Button backToMenuButton;

    /**
     * The game manager instance controlling game logic and state.
     */
    private GameManager gameManager;

    /**
     * Visual representation of the player's board.
     */
    private BoardView playerBoard;

    /**
     * Visual representation of the enemy's board.
     */
    private BoardView enemyBoard;

    /**
     * List of ships in the player's fleet to be placed.
     */
    private List<IShip> playerFleet;

    /**
     * Index of the current ship being placed in the fleet.
     */
    private int currentShipIndex;

    /**
     * Current orientation for ship placement (horizontal or vertical).
     */
    private Orientation currentOrientation;

    /**
     * Flag indicating whether the game is in ship placement mode.
     */
    private boolean placementMode;

    /**
     * List of coordinates currently being previewed for ship placement.
     */
    private List<Coordinate> previewCoordinates;

    /**
     * Initializes the controller after FXML injection.
     * Sets up initial game state, orientation, and event handlers.
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

        if (backToMenuButton != null) {
            backToMenuButton.setOnAction(e -> handleBackToMenu());
        }
    }

    /**
     * Sets the board views for the game.
     * Loads existing game state if available, otherwise sets up placement mode.
     *
     * @param playerBoard the visual representation of the player's board
     * @param enemyBoard the visual representation of the enemy's board
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
     * Handles the back to menu button click.
     * Shows confirmation dialogs and saves game if necessary.
     */
    private void handleBackToMenu() {
        if (placementMode) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Salir al Menú");
            alert.setHeaderText("¿Estás seguro de salir?");
            alert.setContentText("No has iniciado el juego. El progreso no se guardará.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                openMenu();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Salir al Menú");
            alert.setHeaderText("¿Deseas guardar y salir?");
            alert.setContentText("La partida se guardará automáticamente.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                gameManager.saveGame();
                openMenu();
            }
        }
    }

    /**
     * Opens the main menu view and closes the current game window.
     */
    private void openMenu() {
        Menu menuView = new Menu();
        menuView.show();

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Loads a previously saved game state.
     * Restores board states, updates UI elements, and sets up game mode.
     */
    private void loadGameState() {
        placementMode = false;

        instructionLabel.setVisible(false);
        shipsRemainingLabel.setVisible(false);
        currentShipLabel.setVisible(false);
        rotateButton.setVisible(false);
        startGameButton.setVisible(false);
        showEnemyBoardButton.setVisible(true);

        if (backToMenuButton != null) {
            backToMenuButton.setVisible(true);
        }

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
     * Restores the player's board from saved game state.
     * Redraws ships and marks cells with their correct status (hits, misses, sunk).
     */
    private void restorePlayerBoard() {
        List<IShip> ships = gameManager.getHumanPlayer().getBoard().getShips();

        for (IShip ship : ships) {
            playerBoard.drawContinuousShip(ship);

            for (Coordinate coord : ship.getCoordinates()) {
                Cell cell = gameManager.getHumanPlayer().getBoard().getCell(coord);

                if (cell.getStatus() == CellStatus.SUNK) {
                    playerBoard.markSunk(coord);
                } else if (cell.getStatus() == CellStatus.HIT) {
                    playerBoard.markHit(coord);
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
     * Restores the enemy's board from saved game state.
     * Marks cells with their correct status (hits, misses, sunk).
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
     * Sets up the ship placement mode.
     * Initializes cell event handlers for ship placement and preview.
     */
    private void setupPlacementMode() {
        updateShipsRemaining();
        updateCurrentShipLabel();

        if (backToMenuButton != null) {
            backToMenuButton.setVisible(true);
        }

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
     * Shows a preview of the current ship at the specified coordinate.
     * Displays valid (green) or invalid (red) placement preview.
     *
     * @param coordinate the coordinate to preview ship placement at
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
     * Checks if a coordinate is valid for ship placement preview.
     * Validates bounds and checks for existing ships.
     *
     * @param coord the coordinate to validate
     * @return true if the coordinate is valid for placement, false otherwise
     */
    private boolean isValidPreviewCoordinate(Coordinate coord) {
        if (coord.getX() < 0 || coord.getX() >= 10 || coord.getY() < 0 || coord.getY() >= 10) {
            return false;
        }
        return !gameManager.getHumanPlayer().getBoard().hasShipAt(coord);
    }

    /**
     * Clears the current ship placement preview from all cells.
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
     * Handles cell click during ship placement phase.
     * Attempts to place the current ship at the clicked coordinate.
     *
     * @param coordinate the coordinate where the player clicked
     */
    private void handleCellClickPlacement(Coordinate coordinate) {
        if (currentShipIndex >= playerFleet.size()) {
            return;
        }

        IShip ship = playerFleet.get(currentShipIndex);
        ship.setPosition(coordinate, currentOrientation);

        if (gameManager.getHumanPlayer().getBoard().placeShip(ship)) {
            clearShipPreview();

            playerBoard.drawContinuousShip(ship);

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
     * Handles the rotate button click.
     * Toggles ship orientation between horizontal and vertical during placement mode.
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
     * Handles keyboard input for game controls.
     * Supports 'R' key for rotating ships during placement.
     *
     * @param event the key event
     */
    public void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.R && placementMode) {
            handleRotate();
        }
    }

    /**
     * Handles the start game button click.
     * Transitions from placement mode to game mode.
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
     * Sets up game mode after placement phase.
     * Configures enemy board cells to accept shot clicks.
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
     * Handles cell click during active gameplay.
     * Processes player shot and triggers appropriate game flow.
     *
     * @param coordinate the coordinate where the player shot
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
     * Processes the computer's turn with AI logic.
     * Handles shot selection, result processing, and turn continuation.
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
     * Converts a coordinate to alphanumeric string notation (e.g., A1, B5).
     *
     * @param coord the coordinate to convert
     * @return the alphanumeric string representation
     */
    private String coordToString(Coordinate coord) {
        char letter = (char) ('A' + coord.getY());
        int number = coord.getX() + 1;
        return letter + "" + number;
    }

    /**
     * Marks all cells of a sunk ship on the specified board view.
     *
     * @param board the game board model
     * @param hitCoord the coordinate where the final hit occurred
     * @param boardView the visual board to update
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
     * Toggles the visibility of enemy ships on the board.
     * Shows ships when false, hides them when true.
     */
    private void handleShowEnemyBoard() {
        if (!isSeeingEnemyShips) {
            for (IShip ship : gameManager.getComputerPlayer().getBoard().getShips()) {
                enemyBoard.drawContinuousShip(ship);
            }
            isSeeingEnemyShips = true;
            showEnemyBoardButton.setText("Ocultar Barcos Enemigos");
        } else {
            for (IShip ship : gameManager.getComputerPlayer().getBoard().getShips()) {
                enemyBoard.removeShipGraphic(ship);
            }
            isSeeingEnemyShips = false;
            showEnemyBoardButton.setText("Mostrar Barcos Enemigos");
        }
    }

    /**
     * Updates the ships remaining label during placement phase.
     */
    private void updateShipsRemaining() {
        int remaining = playerFleet.size() - currentShipIndex;
        shipsRemainingLabel.setText("Barcos restantes: " + remaining);
    }

    /**
     * Updates the current ship label with ship type, size, and orientation.
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
     * Updates the player's ships count label.
     */
    private void updatePlayerShips() {
        int total = gameManager.getHumanPlayer().getBoard().getShips().size();
        int sunk = gameManager.getHumanPlayer().getBoard().getSunkShipsCount();
        int remaining = total - sunk;
        playerShipsLabel.setText("Tus barcos: " + remaining);
    }

    /**
     * Updates the enemy's ships count label.
     */
    private void updateEnemyShips() {
        int total = gameManager.getComputerPlayer().getBoard().getShips().size();
        int sunk = gameManager.getComputerPlayer().getBoard().getSunkShipsCount();
        int remaining = total - sunk;
        enemyShipsLabel.setText("Barcos enemigos: " + remaining);
    }

    /**
     * Shows the victory screen and closes the game window.
     */
    private void showWinScreen() {
        WinView winView = new WinView();
        winView.show();

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows the defeat screen and closes the game window.
     */
    private void showLoseScreen() {
        Lose loseView = new Lose();
        loseView.show();

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }
}