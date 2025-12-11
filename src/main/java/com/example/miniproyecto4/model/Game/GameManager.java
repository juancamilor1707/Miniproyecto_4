package com.example.miniproyecto4.model.Game;

import com.example.miniproyecto4.model.AI.IAIStrategy;
import com.example.miniproyecto4.model.AI.RandomAIStrategy;
import com.example.miniproyecto4.model.Board.IBoard;
import com.example.miniproyecto4.model.Cell.Cell;
import com.example.miniproyecto4.model.Cell.CellStatus;
import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.GameSave.GameRepository;
import com.example.miniproyecto4.model.GameSave.IGameRepository;
import com.example.miniproyecto4.model.GameSave.SerializableGameData;
import com.example.miniproyecto4.model.Player.ComputerPlayer;
import com.example.miniproyecto4.model.Player.IPlayer;
import com.example.miniproyecto4.model.Player.Player;
import com.example.miniproyecto4.model.Ship.IShip;
import com.example.miniproyecto4.model.Ship.ShipFactory;
import com.example.miniproyecto4.model.Shot.ShotResult;
import com.example.miniproyecto4.model.Validation.Orientation;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages the game flow and state for a Battleship game.
 * Implements the Singleton pattern to ensure only one game instance exists.
 * Handles turn management, shot processing, game persistence, and win conditions.
 * Thread-safe implementation using ReentrantLock for concurrent access protection.
 */
public class GameManager implements IGameManager {
    /**
     * Singleton instance of the GameManager.
     */
    private static GameManager instance;

    /**
     * The human player participating in the game.
     */
    private IPlayer humanPlayer;

    /**
     * The computer-controlled opponent player.
     */
    private ComputerPlayer computerPlayer;

    /**
     * Current status of the game (SETUP, PLAYING, PLAYER_WON, COMPUTER_WON).
     */
    private GameStatus gameStatus;

    /**
     * Flag indicating whether it is currently the human player's turn.
     */
    private boolean isPlayerTurn;

    /**
     * Repository for saving and loading game state.
     */
    private final IGameRepository repository;

    /**
     * Strategy used by the AI to determine shot targets.
     */
    private final IAIStrategy aiStrategy;

    /**
     * Random number generator for AI ship placement and shot selection.
     */
    private final Random random;

    /**
     * The last coordinate where the computer player took a shot.
     */
    private Coordinate lastComputerShot;


    private static final ReentrantLock instanceLock = new ReentrantLock();


    /**
     * Lock for ensuring thread-safe game state modifications.
     */
    private final ReentrantLock gameLock;

    private GameManager() {
        this.repository = new GameRepository();
        this.aiStrategy = new RandomAIStrategy();
        this.random = new Random();
        this.gameStatus = GameStatus.SETUP;
        this.isPlayerTurn = true;
        this.gameLock = new ReentrantLock();
    }

    /**
     * Gets the singleton instance of GameManager.
     * Thread-safe implementation using double-checked locking.
     *
     * @return the single GameManager instance
     */
    public static GameManager getInstance() {
        if (instance == null) {
            instanceLock.lock();
            try {
                if (instance == null) {
                    instance = new GameManager();
                }
            } finally {
                instanceLock.unlock();
            }
        }
        return instance;
    }

    /**
     * Starts a new game with the specified player nickname.
     * Creates new player and computer player instances, places computer ships,
     * and initializes the game state.
     *
     * @param playerNickname the nickname for the human player
     */
    @Override
    public void startNewGame(String playerNickname) {
        gameLock.lock();
        try {
            humanPlayer = new Player(playerNickname);
            computerPlayer = new ComputerPlayer();

            placeComputerShips();

            gameStatus = GameStatus.SETUP;
            isPlayerTurn = true;
            lastComputerShot = null;
            aiStrategy.reset();
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Places all computer ships randomly on the board.
     * Uses random coordinates and orientations with collision detection.
     */
    private void placeComputerShips() {
        List<IShip> fleet = ShipFactory.createFleet();
        IBoard board = computerPlayer.getBoard();

        for (IShip ship : fleet) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 1000) {
                int x = random.nextInt(board.getSize());
                int y = random.nextInt(board.getSize());
                Orientation orientation = random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

                ship.setPosition(new Coordinate(x, y), orientation);

                if (board.placeShip(ship)) {
                    placed = true;
                }
                attempts++;
            }
        }
    }
    /**
     * Loads a previously saved game from the repository.
     * Restores players, game status, turn information, and updates available shots.
     */
    @Override
    public void loadGame() {
        gameLock.lock();
        try {
            SerializableGameData data = repository.loadGame();

            if (data != null) {
                this.humanPlayer = data.getHumanPlayer();
                this.computerPlayer = (ComputerPlayer) data.getComputerPlayer();
                this.gameStatus = data.getGameStatus();
                this.isPlayerTurn = data.isPlayerTurn();

                updateComputerAvailableShots();
            }
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Updates the computer's AI strategy with already-taken shots.
     * Synchronizes AI state with loaded game state.
     */
    private void updateComputerAvailableShots() {
        IBoard playerBoard = humanPlayer.getBoard();
        int size = playerBoard.getSize();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Coordinate coord = new Coordinate(x, y);
                Cell cell = playerBoard.getCell(coord);

                if (cell != null && (cell.isHit() || cell.isMiss())) {
                    computerPlayer.markShotTaken(coord);
                }
            }
        }
    }
    /**
     * Processes a shot from the human player at the specified coordinate.
     * Updates cell status, ship hit state, and checks for sunk ships and win conditions.
     *
     * @param coordinate the target coordinate for the shot
     * @return the result of the shot (INVALID, WATER, HIT, or SUNK)
     */
    @Override
    public ShotResult processPlayerShot(Coordinate coordinate) {
        gameLock.lock();
        try {
            if (!isPlayerTurn || gameStatus != GameStatus.PLAYING) {
                return ShotResult.INVALID;
            }

            IBoard computerBoard = computerPlayer.getBoard();
            Cell cell = computerBoard.getCell(coordinate);

            if (cell == null || cell.isHit() || cell.isMiss()) {
                return ShotResult.INVALID;
            }

            IShip ship = computerBoard.getShipAt(coordinate);

            if (ship == null) {
                cell.setStatus(CellStatus.MISS);
                isPlayerTurn = false;
                saveGameInternal();
                return ShotResult.WATER;
            }

            ship.hit(coordinate);
            cell.setStatus(CellStatus.HIT);

            if (ship.isSunk()) {
                markShipAsSunk(computerBoard, ship);

                if (computerBoard.allShipsSunk()) {
                    gameStatus = GameStatus.PLAYER_WON;
                    saveGameInternal();
                    return ShotResult.SUNK;
                }

                saveGameInternal();
                return ShotResult.SUNK;
            }

            saveGameInternal();
            return ShotResult.HIT;
        } finally {
            gameLock.unlock();
        }
    }
    /**
     * Processes a shot from the computer player.
     * Uses AI strategy to select a target, updates cell status, and checks for
     * sunk ships and win conditions.
     *
     * @return the result of the shot (INVALID, WATER, HIT, or SUNK)
     */
    @Override
    public ShotResult processComputerShot() {
        gameLock.lock();
        try {
            if (isPlayerTurn || gameStatus != GameStatus.PLAYING) {
                return ShotResult.INVALID;
            }

            Coordinate coordinate = aiStrategy.selectTarget(humanPlayer.getBoard());

            if (coordinate == null) {
                coordinate = computerPlayer.getNextShot();
            }

            if (coordinate == null) {
                return ShotResult.INVALID;
            }

            lastComputerShot = coordinate;
            computerPlayer.markShotTaken(coordinate);

            IBoard playerBoard = humanPlayer.getBoard();
            Cell cell = playerBoard.getCell(coordinate);

            IShip ship = playerBoard.getShipAt(coordinate);

            if (ship == null) {
                cell.setStatus(CellStatus.MISS);
                isPlayerTurn = true;
                aiStrategy.updateStrategy(coordinate, false);
                saveGameInternal();
                return ShotResult.WATER;
            }

            ship.hit(coordinate);
            cell.setStatus(CellStatus.HIT);
            aiStrategy.updateStrategy(coordinate, true);

            if (ship.isSunk()) {
                markShipAsSunk(playerBoard, ship);

                if (playerBoard.allShipsSunk()) {
                    gameStatus = GameStatus.COMPUTER_WON;
                    saveGameInternal();
                    return ShotResult.SUNK;
                }

                saveGameInternal();
                return ShotResult.SUNK;
            }

            saveGameInternal();
            return ShotResult.HIT;
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Marks all cells of a sunk ship with SUNK status.
     *
     * @param board the board containing the ship
     * @param ship the ship that has been sunk
     */
    private void markShipAsSunk(IBoard board, IShip ship) {
        for (Coordinate coord : ship.getCoordinates()) {
            Cell cell = board.getCell(coord);
            if (cell != null) {
                cell.setStatus(CellStatus.SUNK);
            }
        }
    }
    /**
     * Returns the human player instance.
     *
     * @return the human player
     */
    @Override
    public IPlayer getHumanPlayer() {
        gameLock.lock();
        try {
            return humanPlayer;
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Gets the coordinate of the last computer shot.
     * Thread-safe accessor method.
     *
     * @return the last shot coordinate, or null if no shot has been made
     */
    public Coordinate getLastComputerShot() {
        gameLock.lock();
        try {
            return lastComputerShot;
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Returns the computer player instance.
     *
     * @return the computer player
     */
    @Override
    public IPlayer getComputerPlayer() {
        gameLock.lock();
        try {
            return computerPlayer;
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Returns the current game status.
     *
     * @return the current game status
     */
    @Override
    public GameStatus getGameStatus() {
        gameLock.lock();
        try {
            return gameStatus;
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Sets the current game status.
     * Automatically saves the game if transitioning to PLAYING status.
     *
     * @param status the new game status
     */
    public void setGameStatus(GameStatus status) {
        gameLock.lock();
        try {
            this.gameStatus = status;
            if (status == GameStatus.PLAYING) {
                saveGameInternal();
            }
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Checks if it is currently the human player's turn.
     *
     * @return true if it is the player's turn, false otherwise
     */
    @Override
    public boolean isPlayerTurn() {
        gameLock.lock();
        try {
            return isPlayerTurn;
        } finally {
            gameLock.unlock();
        }
    }
    /**
     * Switches the turn between the human player and computer player.
     */
    @Override
    public void switchTurn() {
        gameLock.lock();
        try {
            isPlayerTurn = !isPlayerTurn;
        } finally {
            gameLock.unlock();
        }
    }
    /**
     * Saves the current game state to the repository.
     * Only saves if the game status is PLAYING.
     */
    @Override
    public void saveGame() {
        gameLock.lock();
        try {
            saveGameInternal();
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Internal method to save the game without acquiring the lock.
     * Should only be called when lock is already held.
     */
    private void saveGameInternal() {
        if (gameStatus == GameStatus.PLAYING) {
            repository.saveGame(humanPlayer, computerPlayer, gameStatus, isPlayerTurn);
        }
    }
    /**
     * Checks if the game has a winner.
     *
     * @return true if either player or computer has won, false otherwise
     */
    @Override
    public boolean hasWinner() {
        gameLock.lock();
        try {
            return gameStatus == GameStatus.PLAYER_WON || gameStatus == GameStatus.COMPUTER_WON;
        } finally {
            gameLock.unlock();
        }
    }
    /**
     * Returns the winner of the game.
     *
     * @return the winning player, or null if there is no winner yet
     */
    @Override
    public IPlayer getWinner() {
        gameLock.lock();
        try {
            if (gameStatus == GameStatus.PLAYER_WON) {
                return humanPlayer;
            } else if (gameStatus == GameStatus.COMPUTER_WON) {
                return computerPlayer;
            }
            return null;
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Resets the game to its initial state.
     * Resets both players, sets status to SETUP, and reinitializes turn and AI strategy.
     */
    @Override
    public void resetGame() {
        gameLock.lock();
        try {
            if (humanPlayer != null) {
                humanPlayer.reset();
            }
            if (computerPlayer != null) {
                computerPlayer.reset();
            }
            gameStatus = GameStatus.SETUP;
            isPlayerTurn = true;
            lastComputerShot = null;
            aiStrategy.reset();
        } finally {
            gameLock.unlock();
        }
    }
}