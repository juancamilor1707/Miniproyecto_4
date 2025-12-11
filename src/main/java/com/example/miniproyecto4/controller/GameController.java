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
import javafx.application.Platform;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller for the main game view in the Battleship application.
 * <p>
 * This controller manages the complete game flow including:
 * <ul>
 * <li>Ship placement phase with interactive preview and validation</li>
 * <li>Turn-based gameplay between human and AI players</li>
 * <li>Real-time board updates and visual feedback</li>
 * <li>Game state persistence (save/load functionality)</li>
 * <li>Multi-threaded AI computation for smooth UI responsiveness</li>
 * </ul>
 * </p>
 * <p>
 * The controller uses JavaFX's Platform.runLater() for thread-safe UI updates
 * and maintains a single-threaded executor service for AI operations to prevent
 * race conditions and ensure deterministic game behavior.
 * </p>
 *
 * @author Mini Proyecto 4 Team
 * @version 1.0
 * @see GameManager
 * @see BoardView
 * @see IShip
 */
public class GameController {
    /**
     * Flag indicating whether the player is currently viewing enemy ships.
     * When true, enemy ships are visible on the board (cheat mode).
     */
    private boolean isSeeingEnemyShips = false;

    /**
     * Thread-safe flag to prevent multiple simultaneous computer turns.
     * Uses atomic operations to ensure only one AI computation runs at a time,
     * preventing race conditions and duplicate shots.
     */
    private final AtomicBoolean isProcessingComputerTurn = new AtomicBoolean(false);

    /**
     * Single-threaded executor service for running AI computations in background.
     * Configured as a daemon thread named "AI-Worker" to ensure proper shutdown
     * when the application closes. This prevents UI freezing during AI thinking time.
     */
    private final ExecutorService aiExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName("AI-Worker");
        return thread;
    });

    /**
     * Label displaying the current game status and turn information.
     * Shows messages like "Tu turno", "Turno de la máquina", "¡Tocado!", etc.
     */
    @FXML
    private Label statusLabel;

    /**
     * Label providing instructions to the player during different game phases.
     * Visible during placement mode, hidden during active gameplay.
     */
    @FXML
    private Label instructionLabel;

    /**
     * Label showing the number of ships remaining to be placed.
     * Only visible during the ship placement phase.
     */
    @FXML
    private Label shipsRemainingLabel;

    /**
     * Label displaying information about the current ship being placed.
     * Shows ship type, size, orientation, and rotation hint.
     */
    @FXML
    private Label currentShipLabel;

    /**
     * Label showing the player's remaining ships count during gameplay.
     * Format: "Tus barcos: X" where X is the number of non-sunk ships.
     */
    @FXML
    private Label playerShipsLabel;

    /**
     * Label showing the enemy's remaining ships count during gameplay.
     * Format: "Barcos enemigos: X" where X is the number of non-sunk ships.
     */
    @FXML
    private Label enemyShipsLabel;

    /**
     * Button to rotate the current ship during placement phase.
     * Toggles between horizontal and vertical orientation.
     * Disabled during active gameplay.
     */
    @FXML
    private Button rotateButton;

    /**
     * Button to start the game after all ships are placed.
     * Initially disabled, becomes enabled once all 5 ships are placed.
     */
    @FXML
    private Button startGameButton;

    /**
     * Button to toggle visibility of enemy ships on the board.
     * Used for debugging or cheat mode. Only visible during active gameplay.
     */
    @FXML
    private Button showEnemyBoardButton;

    /**
     * Button to return to the main menu.
     * Shows confirmation dialog and handles game saving if in progress.
     */
    @FXML
    private Button backToMenuButton;

    /**
     * The singleton game manager instance controlling game logic and state.
     * Manages turn order, shot processing, win conditions, and persistence.
     */
    private GameManager gameManager;

    /**
     * Visual representation of the player's board (left side).
     * Displays player's ships, hits, misses, and sunk ships.
     */
    private BoardView playerBoard;

    /**
     * Visual representation of the enemy's board (right side).
     * Shows only the results of player's shots unless cheat mode is active.
     */
    private BoardView enemyBoard;

    /**
     * List of ships in the player's fleet to be placed during setup.
     * Contains 5 ships: Carrier(5), Battleship(4), Cruiser(3), Submarine(3), Destroyer(2).
     */
    private List<IShip> playerFleet;

    /**
     * Index of the current ship being placed in the fleet.
     * Ranges from 0 to 4 during placement, equals 5 when all ships are placed.
     */
    private int currentShipIndex;

    /**
     * Current orientation for ship placement (horizontal or vertical).
     * Can be toggled by the rotate button or 'R' key during placement mode.
     */
    private Orientation currentOrientation;

    /**
     * Flag indicating whether the game is in ship placement mode.
     * When true, player is placing ships. When false, game is in progress.
     */
    private boolean placementMode;

    /**
     * List of coordinates currently being previewed for ship placement.
     * Used to show valid (green) or invalid (red) placement before confirmation.
     * Cleared after each preview update.
     */
    private List<Coordinate> previewCoordinates;

    /**
     * Initializes the controller after FXML injection.
     * <p>
     * Sets up initial game state including:
     * <ul>
     * <li>Game manager instance</li>
     * <li>Default orientation (horizontal)</li>
     * <li>Placement mode activation</li>
     * <li>Player fleet creation</li>
     * <li>Button event handlers</li>
     * </ul>
     * </p>
     * <p>
     * This method is automatically called by JavaFX after the FXML elements
     * have been injected. Should not be called manually.
     * </p>
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
     * Sets the board views for the game and initializes the appropriate game mode.
     * <p>
     * If a saved game exists (GameStatus.PLAYING), loads the previous state.
     * Otherwise, sets up the ship placement mode for a new game.
     * </p>
     * <p>
     * This method must be called after initialization and before any game interaction.
     * </p>
     *
     * @param playerBoard the visual representation of the player's board (left side)
     * @param enemyBoard the visual representation of the enemy's board (right side)
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
     * Handles the back to menu button click event.
     * <p>
     * Shows appropriate confirmation dialogs based on game state:
     * <ul>
     * <li>During placement: Warns that progress will not be saved</li>
     * <li>During gameplay: Confirms automatic game saving</li>
     * </ul>
     * </p>
     * <p>
     * Performs cleanup operations including:
     * <ul>
     * <li>Shutting down the AI executor service</li>
     * <li>Saving the game (if in progress)</li>
     * <li>Opening the main menu</li>
     * </ul>
     * </p>
     */
    private void handleBackToMenu() {
        if (placementMode) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Salir al Menú");
            alert.setHeaderText("¿Estás seguro de salir?");
            alert.setContentText("No has iniciado el juego. El progreso no se guardará.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                shutdownExecutor();
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
                shutdownExecutor();
                openMenu();
            }
        }
    }

    /**
     * Shuts down the AI executor service gracefully with a 2-second timeout.
     * <p>
     * Shutdown process:
     * <ol>
     * <li>Initiates graceful shutdown, allowing running tasks to complete</li>
     * <li>Waits up to 2 seconds for termination</li>
     * <li>Forces immediate shutdown if timeout is reached</li>
     * <li>Restores interrupt status if interrupted during shutdown</li>
     * </ol>
     * </p>
     * <p>
     * This method should be called before closing the game window or
     * transitioning to another scene to prevent thread leaks.
     * </p>
     */
    private void shutdownExecutor() {
        aiExecutor.shutdown();
        try {
            if (!aiExecutor.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS)) {
                aiExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            aiExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Opens the main menu view and closes the current game window.
     * <p>
     * Creates a new Menu instance, displays it, and closes the current
     * game stage. This method assumes the game has been properly saved
     * and the executor service has been shut down.
     * </p>
     */
    private void openMenu() {
        Menu menuView = new Menu();
        menuView.show();

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Loads a previously saved game state and restores all visual elements.
     * <p>
     * Restoration process includes:
     * <ol>
     * <li>Disabling placement mode UI elements</li>
     * <li>Showing gameplay UI elements (enemy board, ship counters)</li>
     * <li>Restoring both player and enemy board states</li>
     * <li>Updating ship counters</li>
     * <li>Setting up game mode event handlers</li>
     * <li>Resuming turn sequence (triggers computer turn if necessary)</li>
     * </ol>
     * </p>
     * <p>
     * This method is called automatically by setBoards() when a saved
     * game with status PLAYING is detected.
     * </p>
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
            scheduleDelayedComputerTurn(1500);
        } else if (gameManager.isPlayerTurn()) {
            statusLabel.setText("Tu turno - Selecciona una celda para disparar");
        }
    }

    /**
     * Restores the player's board from saved game state to its visual representation.
     * <p>
     * Restoration includes:
     * <ul>
     * <li>Drawing all placed ships with their correct positions</li>
     * <li>Marking cells that have been hit</li>
     * <li>Marking cells that belong to sunk ships</li>
     * <li>Marking all missed shots (water)</li>
     * </ul>
     * </p>
     * <p>
     * This method iterates through all ships first, then scans the entire
     * board for miss markers to ensure complete state restoration.
     * </p>
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
     * Restores the enemy's board from saved game state to its visual representation.
     * <p>
     * Only restores the information visible to the player:
     * <ul>
     * <li>Cells marked as hit</li>
     * <li>Cells marked as sunk</li>
     * <li>Cells marked as miss (water)</li>
     * </ul>
     * </p>
     * <p>
     * Enemy ship positions are NOT restored visually (unless cheat mode was active).
     * This maintains the fog of war aspect of the game.
     * </p>
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
     * Sets up the ship placement mode for a new game.
     * <p>
     * Initialization includes:
     * <ul>
     * <li>Updating UI labels for remaining ships and current ship info</li>
     * <li>Making the back to menu button visible</li>
     * <li>Configuring mouse event handlers for all player board cells</li>
     * <li>Enabling hover preview for ship placement</li>
     * </ul>
     * </p>
     * <p>
     * Each cell receives three event handlers:
     * <ul>
     * <li>onMouseClicked - Attempts to place the ship</li>
     * <li>onMouseEntered - Shows placement preview</li>
     * <li>onMouseExited - Clears placement preview</li>
     * </ul>
     * </p>
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
     * Shows a visual preview of the current ship at the specified coordinate.
     * <p>
     * Preview behavior:
     * <ul>
     * <li>Green cells - Valid placement (no collisions, within bounds)</li>
     * <li>Red cells - Invalid placement (collision or out of bounds)</li>
     * </ul>
     * </p>
     * <p>
     * The preview extends from the hover coordinate based on:
     * <ul>
     * <li>Current ship size</li>
     * <li>Current orientation (horizontal extends right, vertical extends down)</li>
     * </ul>
     * </p>
     * <p>
     * Clears any existing preview before showing the new one.
     * </p>
     *
     * @param coordinate the starting coordinate for the preview (where mouse is hovering)
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
     * Validates if a coordinate is suitable for ship placement preview.
     * <p>
     * A coordinate is valid if:
     * <ul>
     * <li>It is within the 10x10 board bounds (0-9 for both x and y)</li>
     * <li>There is no existing ship at that location</li>
     * </ul>
     * </p>
     * <p>
     * This is used for preview coloring (green/red) and does not check
     * for complete ship placement validity, only individual cell validity.
     * </p>
     *
     * @param coord the coordinate to validate
     * @return true if the coordinate is valid for preview, false otherwise
     */
    private boolean isValidPreviewCoordinate(Coordinate coord) {
        if (coord.getX() < 0 || coord.getX() >= 10 || coord.getY() < 0 || coord.getY() >= 10) {
            return false;
        }
        return !gameManager.getHumanPlayer().getBoard().hasShipAt(coord);
    }

    /**
     * Clears the current ship placement preview from all previously highlighted cells.
     * <p>
     * Iterates through all coordinates in the previewCoordinates list and
     * removes their preview styling, returning them to their normal appearance.
     * Then clears the list for the next preview operation.
     * </p>
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
     * <p>
     * Attempts to place the current ship at the clicked coordinate with
     * the current orientation. If successful:
     * <ol>
     * <li>Clears the preview</li>
     * <li>Draws the ship on the board</li>
     * <li>Advances to the next ship</li>
     * <li>Updates UI labels</li>
     * <li>Enables start button if all ships are placed</li>
     * </ol>
     * </p>
     * <p>
     * If placement fails (invalid position), shows an error message
     * in the status label and keeps the current ship active.
     * </p>
     *
     * @param coordinate the coordinate where the player clicked to place the ship
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
     * Handles the rotate button click event.
     * <p>
     * Toggles the ship orientation between horizontal and vertical during
     * placement mode. Has no effect if called during active gameplay.
     * </p>
     * <p>
     * After rotation:
     * <ul>
     * <li>Updates the current ship label to show new orientation</li>
     * <li>Clears any existing preview (will be redrawn on next hover)</li>
     * </ul>
     * </p>
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
     * <p>
     * Currently supported keys:
     * <ul>
     * <li>R - Rotates the current ship during placement mode</li>
     * </ul>
     * </p>
     * <p>
     * Additional keys can be added for future features like:
     * <ul>
     * <li>ESC - Cancel placement or open menu</li>
     * <li>SPACE - Confirm placement</li>
     * <li>Arrow keys - Move preview position</li>
     * </ul>
     * </p>
     *
     * @param event the keyboard event containing the pressed key
     */
    public void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.R && placementMode) {
            handleRotate();
        }
    }

    /**
     * Handles the start game button click event.
     * <p>
     * Transitions from placement mode to active gameplay by:
     * <ol>
     * <li>Disabling placement mode</li>
     * <li>Clearing any remaining preview</li>
     * <li>Setting game status to PLAYING</li>
     * <li>Hiding placement UI elements</li>
     * <li>Showing gameplay UI elements (enemy board, ship counters)</li>
     * <li>Setting up game mode event handlers</li>
     * <li>Initializing ship counters</li>
     * </ol>
     * </p>
     * <p>
     * This method should only be called after all 5 ships have been
     * successfully placed on the player's board.
     * </p>
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
     * Sets up game mode after the placement phase is complete.
     * <p>
     * Configures all enemy board cells to accept shot clicks by:
     * <ul>
     * <li>Removing any existing event handlers</li>
     * <li>Setting onMouseClicked handlers for each cell</li>
     * <li>Capturing coordinates in final variables for lambda expressions</li>
     * </ul>
     * </p>
     * <p>
     * Player board cells remain non-interactive during gameplay to prevent
     * accidental clicks on the wrong board.
     * </p>
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
     * Handles cell click during active gameplay (player shooting phase).
     * <p>
     * Validates the click is during player's turn and game is not over,
     * then processes the shot through the game manager. Based on the result:
     * </p>
     * <ul>
     * <li><b>WATER:</b> Marks miss, switches to computer turn with 500ms delay</li>
     * <li><b>HIT:</b> Marks hit, player shoots again (no turn switch)</li>
     * <li><b>SUNK:</b> Marks entire ship as sunk, checks for victory, player shoots again</li>
     * <li><b>INVALID:</b> Shows error message, no turn switch</li>
     * </ul>
     * <p>
     * Updates enemy ship counter after each valid shot.
     * </p>
     *
     * @param coordinate the coordinate on the enemy board where the player shot
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
                scheduleDelayedComputerTurn(500);
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
     * Schedules a delayed computer turn execution using the AI executor service.
     * <p>
     * Uses a background thread to implement the delay without freezing the UI.
     * After the specified delay, schedules the actual turn processing on the
     * JavaFX Application Thread using Platform.runLater().
     * </p>
     * <p>
     * The delay serves two purposes:
     * <ul>
     * <li>Makes AI behavior feel more natural and human-like</li>
     * <li>Gives player time to process the previous turn result</li>
     * </ul>
     * </p>
     * <p>
     * If the thread is interrupted during the delay, the interrupt flag
     * is restored and the turn is not processed.
     * </p>
     *
     * @param delayMillis delay in milliseconds before executing the computer turn
     */
    private void scheduleDelayedComputerTurn(long delayMillis) {
        aiExecutor.submit(() -> {
            try {
                Thread.sleep(delayMillis);
                Platform.runLater(this::processComputerTurn);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Processes the computer's turn with AI logic in a background thread.
     * <p>
     * This method is the core of the AI turn execution system. It:
     * <ol>
     * <li>Checks if game has winner or turn is already processing (atomic check-and-set)</li>
     * <li>Updates status label to show "Turno de la máquina..."</li>
     * <li>Submits AI computation to background thread with simulated thinking time</li>
     * <li>Processes the shot through game manager</li>
     * <li>Updates UI on JavaFX thread with results</li>
     * <li>Resets processing flag in finally block to ensure proper cleanup</li>
     * </ol>
     * </p>
     * <p>
     * <b>Thread Safety:</b> Uses AtomicBoolean compareAndSet() to prevent race
     * conditions where multiple computer turns could execute simultaneously.
     * This is critical for maintaining game state consistency.
     * </p>
     * <p>
     * <b>Thinking Time:</b> Adds 800-1200ms random delay to simulate realistic
     * AI decision making, improving user experience.
     * </p>
     * <p>
     * <b>Error Handling:</b> Catches both InterruptedException and general
     * exceptions, ensuring the processing flag is always reset even if
     * an error occurs during shot processing.
     * </p>
     */
    private void processComputerTurn() {
        if (gameManager.hasWinner() || !isProcessingComputerTurn.compareAndSet(false, true)) {
            return;
        }

        Platform.runLater(() -> statusLabel.setText("Turno de la máquina..."));

        aiExecutor.submit(() -> {
            try {
                // Simulate thinking time for more realistic AI behavior
                Thread.sleep(800 + (long)(Math.random() * 400));

                // Process shot in background
                final ShotResult result = gameManager.processComputerShot();
                final Coordinate lastShot = gameManager.getLastComputerShot();

                if (lastShot == null) {
                    isProcessingComputerTurn.set(false);
                    return;
                }

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    try {
                        handleComputerShotResult(result, lastShot);
                    } finally {
                        isProcessingComputerTurn.set(false);
                    }
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isProcessingComputerTurn.set(false);
            } catch (Exception e) {
                e.printStackTrace();
                isProcessingComputerTurn.set(false);
            }
        });
    }

    /**
     * Handles the result of a computer shot and updates the UI accordingly.
     * <p>
     * Processes each shot result type:
     * </p>
     * <ul>
     * <li><b>WATER:</b> Marks miss on player board, switches turn to player</li>
     * <li><b>HIT:</b> Marks hit on player board, schedules next computer shot (1000ms delay)</li>
     * <li><b>SUNK:</b> Marks entire ship as sunk, checks for game over, or continues (1500ms delay)</li>
     * </ul>
     * <p>
     * Updates player ship counter after each shot. Shows coordinate in
     * alphanumeric notation (e.g., "A1", "B5") in status messages.
     * </p>
     * <p>
     * <b>Turn Continuation:</b> On HIT or SUNK, computer continues shooting
     * (following Battleship rules). Delays are slightly longer for dramatic effect.
     * </p>
     *
     * @param result the result of the shot (WATER, HIT, or SUNK)
     * @param lastShot the coordinate where the computer shot
     */
    private void handleComputerShotResult(ShotResult result, Coordinate lastShot) {
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
                scheduleDelayedComputerTurn(1000);
                break;
            case SUNK:
                markSunkShipOnBoard(gameManager.getHumanPlayer().getBoard(), lastShot, playerBoard);
                updatePlayerShips();
                if (gameManager.hasWinner()) {
                    showLoseScreen();
                } else {
                    statusLabel.setText("¡Te hundieron un barco en " + coordToString(lastShot) + "!");
                    scheduleDelayedComputerTurn(1500);
                }
                break;
        }
    }

    /**
     * Converts a coordinate to alphanumeric string notation for display.
     * <p>
     * Format: Letter + Number
     * <ul>
     * <li>Letter: A-J (y-coordinate mapped to letters)</li>
     * <li>Number: 1-10 (x-coordinate + 1 for 1-based indexing)</li>
     * </ul>
     * </p>
     * <p>
     * Examples:
     * <ul>
     * <li>Coordinate(0, 0) → "A1"</li>
     * <li>Coordinate(4, 2) → "C5"</li>
     * <li>Coordinate(9, 9) → "J10"</li>
     * </ul>
     * </p>
     *
     * @param coord the coordinate to convert
     * @return the alphanumeric string representation (e.g., "A1", "B5", "J10")
     */
    private String coordToString(Coordinate coord) {
        char letter = (char) ('A' + coord.getY());
        int number = coord.getX() + 1;
        return letter + "" + number;
    }

    /**
     * Marks all cells of a sunk ship on the specified board view.
     * <p>
     * Process:
     * <ol>
     * <li>Retrieves the ship at the hit coordinate from the board model</li>
     * <li>Verifies the ship is actually sunk (isSunk() returns true)</li>
     * <li>Iterates through all ship coordinates</li>
     * <li>Marks each coordinate as sunk on the visual board</li>
     * </ol>
     * </p>
     * <p>
     * This method handles both player and enemy boards, as indicated by
     * the parameters. The visual marking helps distinguish sunk ships
     * from merely hit ships.
     * </p>
     *
     * @param board the game board model containing ship data
     * @param hitCoord the coordinate where the final hit occurred
     * @param boardView the visual board view to update with sunk markers
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
     * Toggles the visibility of enemy ships on the enemy board.
     * <p>
     * <b>When hiding ships (isSeeingEnemyShips = false):</b>
     * <ul>
     * <li>Draws all enemy ships with their graphics</li>
     * <li>Sets flag to true</li>
     * <li>Changes button text to "Ocultar Barcos Enemigos"</li>
     * </ul>
     * </p>
     * <p>
     * <b>When showing ships (isSeeingEnemyShips = true):</b>
     * <ul>
     * <li>Removes ship graphics from board</li>
     * <li>Sets flag to false</li>
     * <li>Changes button text to "Mostrar Barcos Enemigos"</li>
     * </ul>
     * </p>
     * <p>
     * <b>Note:</b> This is a cheat/debug feature that allows players to see
     * enemy ship positions. Hit/miss markers remain visible regardless of
     * this setting to maintain game state visibility.
     * </p>
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
     * <p>
     * Calculates remaining ships by subtracting the current ship index
     * from the total fleet size (5 ships). Displays the count in Spanish:
     * "Barcos restantes: X"
     * </p>
     * <p>
     * This label is only visible during placement mode and helps players
     * track their progress through the setup phase.
     * </p>
     */
    private void updateShipsRemaining() {
        int remaining = playerFleet.size() - currentShipIndex;
        shipsRemainingLabel.setText("Barcos restantes: " + remaining);
    }

    /**
     * Updates the current ship label with detailed ship information.
     * <p>
     * If there are ships remaining to place, displays:
     * <ul>
     * <li>Ship type display name (e.g., "Portaaviones", "Acorazado")</li>
     * <li>Ship size in cells (e.g., "5 casillas", "4 casillas")</li>
     * <li>Current orientation ("Horizontal" or "Vertical")</li>
     * <li>Rotation hint "[R para rotar]"</li>
     * </ul>
     * </p>
     * <p>
     * Format example: "Colocando: Portaaviones (5 casillas) - Horizontal [R para rotar]"
     * </p>
     * <p>
     * This label is hidden once all ships are placed.
     * </p>
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
     * Updates the player's ships count label during active gameplay.
     * <p>
     * Calculates and displays the number of remaining (non-sunk) ships by:
     * <ol>
     * <li>Getting total ships from player's board</li>
     * <li>Getting count of sunk ships</li>
     * <li>Subtracting to get remaining ships</li>
     * </ol>
     * </p>
     * <p>
     * Format: "Tus barcos: X" where X ranges from 5 (all intact) to 0 (all sunk).
     * </p>
     * <p>
     * This label is continuously updated after each shot during gameplay
     * to provide real-time fleet status to the player.
     * </p>
     */
    private void updatePlayerShips() {
        int total = gameManager.getHumanPlayer().getBoard().getShips().size();
        int sunk = gameManager.getHumanPlayer().getBoard().getSunkShipsCount();
        int remaining = total - sunk;
        playerShipsLabel.setText("Tus barcos: " + remaining);
    }

    /**
     * Updates the enemy's ships count label during active gameplay.
     * <p>
     * Calculates and displays the number of remaining enemy ships by:
     * <ol>
     * <li>Getting total ships from computer player's board</li>
     * <li>Getting count of sunk ships</li>
     * <li>Subtracting to get remaining ships</li>
     * </ol>
     * </p>
     * <p>
     * Format: "Barcos enemigos: X" where X ranges from 5 (all intact) to 0 (all sunk).
     * </p>
     * <p>
     * This label is updated after each player shot to show progress
     * toward victory. When this reaches 0, the player wins the game.
     * </p>
     */
    private void updateEnemyShips() {
        int total = gameManager.getComputerPlayer().getBoard().getShips().size();
        int sunk = gameManager.getComputerPlayer().getBoard().getSunkShipsCount();
        int remaining = total - sunk;
        enemyShipsLabel.setText("Barcos enemigos: " + remaining);
    }

    /**
     * Shows the victory screen and closes the current game window.
     * <p>
     * Victory sequence:
     * <ol>
     * <li>Shuts down AI executor service to prevent memory leaks</li>
     * <li>Creates and displays the WinView</li>
     * <li>Closes the current game stage</li>
     * </ol>
     * </p>
     * <p>
     * This method is called when all enemy ships have been sunk.
     * The executor shutdown ensures all background threads are properly
     * terminated before the window closes.
     * </p>
     */
    private void showWinScreen() {
        shutdownExecutor();
        WinView winView = new WinView();
        winView.show();

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows the defeat screen and closes the current game window.
     * <p>
     * Defeat sequence:
     * <ol>
     * <li>Shuts down AI executor service to prevent memory leaks</li>
     * <li>Creates and displays the Lose view</li>
     * <li>Closes the current game stage</li>
     * </ol>
     * </p>
     * <p>
     * This method is called when all player ships have been sunk.
     * The executor shutdown ensures all background threads are properly
     * terminated before the window closes.
     * </p>
     */
    private void showLoseScreen() {
        shutdownExecutor();
        Lose loseView = new Lose();
        loseView.show();

        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Cleanup method to be called when the controller is being destroyed.
     * <p>
     * Ensures proper resource cleanup by shutting down the AI executor service.
     * This prevents thread leaks and ensures all background tasks are terminated.
     * </p>
     * <p>
     * <b>Important:</b> This method should be called by the framework or parent
     * component when the game view is being closed or disposed. Failure to call
     * this method may result in lingering threads and resource leaks.
     * </p>
     * <p>
     * <b>Usage scenarios:</b>
     * <ul>
     * <li>Application shutdown</li>
     * <li>Scene transitions</li>
     * <li>Window closing</li>
     * <li>Manual cleanup before disposal</li>
     * </ul>
     * </p>
     */
    public void cleanup() {
        shutdownExecutor();
    }
}