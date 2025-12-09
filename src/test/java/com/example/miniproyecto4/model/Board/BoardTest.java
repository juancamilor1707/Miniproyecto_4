package com.example.miniproyecto4.model.Board;

import com.example.miniproyecto4.model.Cell.Cell;
import com.example.miniproyecto4.model.Cell.CellStatus;
import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.Ship;
import com.example.miniproyecto4.model.Ship.ShipType;
import com.example.miniproyecto4.model.Validation.Orientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Board class.
 * Tests all board operations including ship placement, removal, validation,
 * and state management.
 */
@DisplayName("Board Unit Tests")
class BoardTest {

    /**
     * The game board instance used for testing.
     */
    private Board board;

    /**
     * Sets up the test environment before each test.
     * Initializes a new board with default size.
     */
    @BeforeEach
    void setUp() {
        board = new Board();
    }

    /**
     * Tests that the default constructor creates a board of size 10.
     */
    @Test
    @DisplayName("Constructor por defecto debe crear tablero de tamaño 10")
    void testDefaultConstructor() {
        assertEquals(10, board.getSize());
    }

    /**
     * Tests that the constructor with size parameter creates a board of the specified size.
     */
    @Test
    @DisplayName("Constructor con tamaño debe crear tablero del tamaño especificado")
    void testConstructorWithSize() {
        Board customBoard = new Board(15);
        assertEquals(15, customBoard.getSize());
    }

    /**
     * Tests that getSize returns the correct board size.
     */
    @Test
    @DisplayName("getSize debe retornar el tamaño correcto")
    void testGetSize() {
        Board board5 = new Board(5);
        assertEquals(5, board5.getSize());
    }

    /**
     * Tests that getCell returns a cell for valid coordinates.
     */
    @Test
    @DisplayName("getCell con coordenadas válidas debe retornar celda")
    void testGetCell_ValidCoordinates() {
        Cell cell = board.getCell(5, 5);
        assertNotNull(cell);
        assertEquals(5, cell.getCoordinate().getX());
        assertEquals(5, cell.getCoordinate().getY());
    }

    /**
     * Tests that getCell returns null for invalid coordinates.
     */
    @Test
    @DisplayName("getCell con coordenadas inválidas debe retornar null")
    void testGetCell_InvalidCoordinates() {
        assertNull(board.getCell(-1, 5));
        assertNull(board.getCell(5, -1));
        assertNull(board.getCell(10, 5));
        assertNull(board.getCell(5, 10));
    }

    /**
     * Tests that getCell with Coordinate object returns the correct cell.
     */
    @Test
    @DisplayName("getCell con Coordinate válida debe retornar celda")
    void testGetCell_WithCoordinate() {
        Coordinate coord = new Coordinate(3, 3);
        Cell cell = board.getCell(coord);
        assertNotNull(cell);
        assertEquals(coord, cell.getCoordinate());
    }

    /**
     * Tests that all cells are initialized with EMPTY status.
     */
    @Test
    @DisplayName("Todas las celdas deben inicializarse como EMPTY")
    void testInitialCellStatus() {
        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                Cell cell = board.getCell(x, y);
                assertEquals(CellStatus.EMPTY, cell.getStatus());
            }
        }
    }

    /**
     * Tests that placeShip returns false for null ship.
     */
    @Test
    @DisplayName("placeShip con ship null debe retornar false")
    void testPlaceShip_NullShip() {
        assertFalse(board.placeShip(null));
    }

    /**
     * Tests that placeShip returns false when ship has no start coordinate.
     */
    @Test
    @DisplayName("placeShip con barco sin coordenada inicial debe retornar false")
    void testPlaceShip_NoStartCoordinate() {
        Ship ship = new Ship(ShipType.DESTROYER);
        assertFalse(board.placeShip(ship));
    }

    /**
     * Tests that placeShip returns true for valid ship placement.
     */
    @Test
    @DisplayName("placeShip con barco válido debe retornar true")
    void testPlaceShip_ValidShip() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        assertTrue(board.placeShip(ship));
    }

    /**
     * Tests that placeShip updates cell status to SHIP.
     */
    @Test
    @DisplayName("placeShip debe cambiar el estado de las celdas a SHIP")
    void testPlaceShip_UpdatesCellStatus() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(2, 2), Orientation.HORIZONTAL);
        board.placeShip(ship);

        assertEquals(CellStatus.SHIP, board.getCell(2, 2).getStatus());
        assertEquals(CellStatus.SHIP, board.getCell(3, 2).getStatus());
    }

    /**
     * Tests that placeShip adds the ship to the ships list.
     */
    @Test
    @DisplayName("placeShip debe agregar el barco a la lista de barcos")
    void testPlaceShip_AddsToShipsList() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(1, 1), Orientation.VERTICAL);
        board.placeShip(ship);

        assertEquals(1, board.getShips().size());
        assertTrue(board.getShips().contains(ship));
    }

    /**
     * Tests that placeShip returns false when ship extends beyond boundaries.
     */
    @Test
    @DisplayName("placeShip con barco fuera de límites debe retornar false")
    void testPlaceShip_OutOfBounds() {
        Ship ship = new Ship(ShipType.CARRIER, new Coordinate(8, 0), Orientation.HORIZONTAL);
        assertFalse(board.placeShip(ship));
    }

    /**
     * Tests that placeShip returns false when ship overlaps with existing ship.
     */
    @Test
    @DisplayName("placeShip con superposición debe retornar false")
    void testPlaceShip_Overlap() {
        Ship ship1 = new Ship(ShipType.SUBMARINE, new Coordinate(3, 3), Orientation.HORIZONTAL);
        Ship ship2 = new Ship(ShipType.DESTROYER, new Coordinate(4, 3), Orientation.VERTICAL);

        board.placeShip(ship1);
        assertFalse(board.placeShip(ship2));
    }

    /**
     * Tests that removeShip returns false for non-existent ship.
     */
    @Test
    @DisplayName("removeShip con barco no existente debe retornar false")
    void testRemoveShip_NonExistentShip() {
        Ship ship = new Ship(ShipType.FRIGATE, new Coordinate(1, 1), Orientation.HORIZONTAL);
        assertFalse(board.removeShip(ship));
    }

    /**
     * Tests that removeShip returns true for existing ship.
     */
    @Test
    @DisplayName("removeShip con barco existente debe retornar true")
    void testRemoveShip_ExistingShip() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(2, 2), Orientation.HORIZONTAL);
        board.placeShip(ship);

        assertTrue(board.removeShip(ship));
    }

    /**
     * Tests that removeShip updates cell status to EMPTY.
     */
    @Test
    @DisplayName("removeShip debe cambiar el estado de las celdas a EMPTY")
    void testRemoveShip_UpdatesCellStatus() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(3, 3), Orientation.HORIZONTAL);
        board.placeShip(ship);
        board.removeShip(ship);

        assertEquals(CellStatus.EMPTY, board.getCell(3, 3).getStatus());
        assertEquals(CellStatus.EMPTY, board.getCell(4, 3).getStatus());
    }

    /**
     * Tests that removeShip removes the ship from the ships list.
     */
    @Test
    @DisplayName("removeShip debe eliminar el barco de la lista")
    void testRemoveShip_RemovesFromList() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(5, 5), Orientation.VERTICAL);
        board.placeShip(ship);
        board.removeShip(ship);

        assertEquals(0, board.getShips().size());
        assertFalse(board.getShips().contains(ship));
    }

    /**
     * Tests that getShips returns an empty list for an empty board.
     */
    @Test
    @DisplayName("getShips en tablero vacío debe retornar lista vacía")
    void testGetShips_EmptyBoard() {
        assertTrue(board.getShips().isEmpty());
    }

    /**
     * Tests that getShips returns a copy of the ships list, not the original.
     */
    @Test
    @DisplayName("getShips debe retornar copia de la lista")
    void testGetShips_ReturnsCopy() {
        Ship ship = new Ship(ShipType.FRIGATE, new Coordinate(0, 0), Orientation.HORIZONTAL);
        board.placeShip(ship);

        board.getShips().clear();
        assertEquals(1, board.getShips().size());
    }

    /**
     * Tests that getShipAt returns the correct ship at the coordinate.
     */
    @Test
    @DisplayName("getShipAt debe retornar el barco en la coordenada")
    void testGetShipAt_WithShip() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(4, 4), Orientation.HORIZONTAL);
        board.placeShip(ship);

        assertEquals(ship, board.getShipAt(new Coordinate(4, 4)));
        assertEquals(ship, board.getShipAt(new Coordinate(5, 4)));
    }

    /**
     * Tests that getShipAt returns null when no ship exists at the coordinate.
     */
    @Test
    @DisplayName("getShipAt sin barco debe retornar null")
    void testGetShipAt_NoShip() {
        assertNull(board.getShipAt(new Coordinate(7, 7)));
    }

    /**
     * Tests that hasShipAt returns true when a ship exists at the coordinate.
     */
    @Test
    @DisplayName("hasShipAt debe retornar true si hay barco")
    void testHasShipAt_WithShip() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(1, 1), Orientation.HORIZONTAL);
        board.placeShip(ship);

        assertTrue(board.hasShipAt(new Coordinate(1, 1)));
        assertTrue(board.hasShipAt(new Coordinate(2, 1)));
    }

    /**
     * Tests that hasShipAt returns false when no ship exists at the coordinate.
     */
    @Test
    @DisplayName("hasShipAt debe retornar false si no hay barco")
    void testHasShipAt_NoShip() {
        assertFalse(board.hasShipAt(new Coordinate(5, 5)));
    }

    /**
     * Tests that isValidCoordinate returns true for valid coordinates.
     */
    @Test
    @DisplayName("isValidCoordinate con coordenadas válidas debe retornar true")
    void testIsValidCoordinate_Valid() {
        assertTrue(board.isValidCoordinate(new Coordinate(0, 0)));
        assertTrue(board.isValidCoordinate(new Coordinate(9, 9)));
        assertTrue(board.isValidCoordinate(new Coordinate(5, 5)));
    }

    /**
     * Tests that isValidCoordinate returns false for invalid coordinates.
     */
    @Test
    @DisplayName("isValidCoordinate con coordenadas inválidas debe retornar false")
    void testIsValidCoordinate_Invalid() {
        assertFalse(board.isValidCoordinate(new Coordinate(-1, 0)));
        assertFalse(board.isValidCoordinate(new Coordinate(0, -1)));
        assertFalse(board.isValidCoordinate(new Coordinate(10, 0)));
        assertFalse(board.isValidCoordinate(new Coordinate(0, 10)));
    }

    /**
     * Tests that getSunkShipsCount returns 0 for a board with no ships.
     */
    @Test
    @DisplayName("getSunkShipsCount en tablero sin barcos debe retornar 0")
    void testGetSunkShipsCount_NoShips() {
        assertEquals(0, board.getSunkShipsCount());
    }

    /**
     * Tests that getSunkShipsCount returns 0 when no ships are sunk.
     */
    @Test
    @DisplayName("getSunkShipsCount sin barcos hundidos debe retornar 0")
    void testGetSunkShipsCount_NoSunkShips() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        board.placeShip(ship);

        assertEquals(0, board.getSunkShipsCount());
    }

    /**
     * Tests that getSunkShipsCount returns the correct count when ships are sunk.
     */
    @Test
    @DisplayName("getSunkShipsCount con barcos hundidos debe retornar cantidad correcta")
    void testGetSunkShipsCount_WithSunkShips() {
        Ship ship1 = new Ship(ShipType.FRIGATE, new Coordinate(0, 0), Orientation.HORIZONTAL);
        Ship ship2 = new Ship(ShipType.DESTROYER, new Coordinate(2, 2), Orientation.HORIZONTAL);

        board.placeShip(ship1);
        board.placeShip(ship2);

        ship1.hit(new Coordinate(0, 0));

        ship2.hit(new Coordinate(2, 2));
        ship2.hit(new Coordinate(3, 2));

        assertEquals(2, board.getSunkShipsCount());
    }

    /**
     * Tests that allShipsSunk returns false for an empty board.
     */
    @Test
    @DisplayName("allShipsSunk en tablero vacío debe retornar false")
    void testAllShipsSunk_EmptyBoard() {
        assertFalse(board.allShipsSunk());
    }

    /**
     * Tests that allShipsSunk returns false when not all ships are sunk.
     */
    @Test
    @DisplayName("allShipsSunk con barcos no hundidos debe retornar false")
    void testAllShipsSunk_NotAllSunk() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        board.placeShip(ship);

        assertFalse(board.allShipsSunk());
    }

    /**
     * Tests that allShipsSunk returns true when all ships are sunk.
     */
    @Test
    @DisplayName("allShipsSunk con todos los barcos hundidos debe retornar true")
    void testAllShipsSunk_AllSunk() {
        Ship ship1 = new Ship(ShipType.FRIGATE, new Coordinate(0, 0), Orientation.HORIZONTAL);
        Ship ship2 = new Ship(ShipType.FRIGATE, new Coordinate(2, 2), Orientation.HORIZONTAL);

        board.placeShip(ship1);
        board.placeShip(ship2);

        ship1.hit(new Coordinate(0, 0));
        ship2.hit(new Coordinate(2, 2));

        assertTrue(board.allShipsSunk());
    }

    /**
     * Tests that reset clears all ships from the board.
     */
    @Test
    @DisplayName("reset debe limpiar todos los barcos")
    void testReset_ClearsShips() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(3, 3), Orientation.HORIZONTAL);
        board.placeShip(ship);

        board.reset();

        assertEquals(0, board.getShips().size());
    }

    /**
     * Tests that reset resets all cells to EMPTY status.
     */
    @Test
    @DisplayName("reset debe resetear todas las celdas a EMPTY")
    void testReset_ResetsCells() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(5, 5), Orientation.HORIZONTAL);
        board.placeShip(ship);

        board.reset();

        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                assertEquals(CellStatus.EMPTY, board.getCell(x, y).getStatus());
            }
        }
    }

    /**
     * Tests that reset clears all ship positions.
     */
    @Test
    @DisplayName("reset debe limpiar posiciones de barcos")
    void testReset_ClearsShipPositions() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(1, 1), Orientation.VERTICAL);
        board.placeShip(ship);

        board.reset();

        assertFalse(board.hasShipAt(new Coordinate(1, 1)));
        assertFalse(board.hasShipAt(new Coordinate(1, 2)));
    }
}