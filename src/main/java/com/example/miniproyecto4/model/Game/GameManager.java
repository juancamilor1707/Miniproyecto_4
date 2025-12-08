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

public class GameManager implements IGameManager {

    private static GameManager instance;

    private IPlayer humanPlayer;
    private ComputerPlayer computerPlayer;
    private GameStatus gameStatus;
    private boolean isPlayerTurn;
    private final IGameRepository repository;
    private final IAIStrategy aiStrategy;
    private final Random random;
    private Coordinate lastComputerShot;

    private GameManager() {
        this.repository = new GameRepository();
        this.aiStrategy = new RandomAIStrategy();
        this.random = new Random();
        this.gameStatus = GameStatus.SETUP;
        this.isPlayerTurn = true;
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    @Override
    public void startNewGame(String playerNickname) {
        humanPlayer = new Player(playerNickname);
        computerPlayer = new ComputerPlayer();

        placeComputerShips();

        gameStatus = GameStatus.SETUP;
        isPlayerTurn = true;
        lastComputerShot = null;
        aiStrategy.reset();
    }

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

    @Override
    public void loadGame() {
        SerializableGameData data = repository.loadGame();

        if (data != null) {
            this.humanPlayer = data.getHumanPlayer();
            this.computerPlayer = (ComputerPlayer) data.getComputerPlayer();
            this.gameStatus = data.getGameStatus();
            this.isPlayerTurn = data.isPlayerTurn();

            updateComputerAvailableShots();
        }
    }

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

    @Override
    public ShotResult processPlayerShot(Coordinate coordinate) {
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
            aiStrategy.updateStrategy(coordinate, false);
            saveGame();
            return ShotResult.WATER;
        }

        ship.hit(coordinate);
        cell.setStatus(CellStatus.HIT);
        aiStrategy.updateStrategy(coordinate, true);

        if (ship.isSunk()) {
            markShipAsSunk(computerBoard, ship);

            if (computerBoard.allShipsSunk()) {
                gameStatus = GameStatus.PLAYER_WON;
                saveGame();
                return ShotResult.SUNK;
            }

            saveGame();
            return ShotResult.SUNK;
        }

        saveGame();
        return ShotResult.HIT;
    }

    @Override
    public ShotResult processComputerShot() {
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
            saveGame();
            return ShotResult.WATER;
        }

        ship.hit(coordinate);
        cell.setStatus(CellStatus.HIT);
        aiStrategy.updateStrategy(coordinate, true);

        if (ship.isSunk()) {
            markShipAsSunk(playerBoard, ship);

            if (playerBoard.allShipsSunk()) {
                gameStatus = GameStatus.COMPUTER_WON;
                saveGame();
                return ShotResult.SUNK;
            }

            saveGame();
            return ShotResult.SUNK;
        }

        saveGame();
        return ShotResult.HIT;
    }

    private void markShipAsSunk(IBoard board, IShip ship) {
        for (Coordinate coord : ship.getCoordinates()) {
            Cell cell = board.getCell(coord);
            if (cell != null) {
                cell.setStatus(CellStatus.SUNK);
            }
        }
    }

    @Override
    public IPlayer getHumanPlayer() {
        return humanPlayer;
    }

    public Coordinate getLastComputerShot() {
        return lastComputerShot;
    }

    @Override
    public IPlayer getComputerPlayer() {
        return computerPlayer;
    }

    @Override
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus status) {
        this.gameStatus = status;
        // Solo guardar si está en PLAYING
        if (status == GameStatus.PLAYING) {
            saveGame();
        }
    }

    @Override
    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    @Override
    public void switchTurn() {
        isPlayerTurn = !isPlayerTurn;
    }

    @Override
    public void saveGame() {
        // SOLO guardar si el juego está en modo PLAYING
        if (gameStatus == GameStatus.PLAYING) {
            repository.saveGame(humanPlayer, computerPlayer, gameStatus, isPlayerTurn);
        }
    }

    @Override
    public boolean hasWinner() {
        return gameStatus == GameStatus.PLAYER_WON || gameStatus == GameStatus.COMPUTER_WON;
    }

    @Override
    public IPlayer getWinner() {
        if (gameStatus == GameStatus.PLAYER_WON) {
            return humanPlayer;
        } else if (gameStatus == GameStatus.COMPUTER_WON) {
            return computerPlayer;
        }
        return null;
    }

    @Override
    public void resetGame() {
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
    }
}