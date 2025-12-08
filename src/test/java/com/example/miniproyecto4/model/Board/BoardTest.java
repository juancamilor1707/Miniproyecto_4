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

@DisplayName("Board Unit Tests")
class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    // Tests para constructores

    @Test
    @DisplayName("Constructor por defecto debe crear tablero de tamaño 10")
    void testDefaultConstructor() {
        assertEquals(10, board.getSize());
    }

    @Test
    @DisplayName("Constructor con tamaño debe crear tablero del tamaño especificado")
    void testConstructorWithSize() {
        Board customBoard = new Board(15);
        assertEquals(15, customBoard.getSize());
    }

    // Tests para getSize()

    @Test
    @DisplayName("getSize debe retornar el tamaño correcto")
    void testGetSize() {
        Board board5 = new Board(5);
        assertEquals(5, board5.getSize());
    }

    // Tests para getCell()

    @Test
    @DisplayName("getCell con coordenadas válidas debe retornar celda")
    void testGetCell_ValidCoordinates() {
        Cell cell = board.getCell(5, 5);
        assertNotNull(cell);
        assertEquals(5, cell.getCoordinate().getX());
        assertEquals(5, cell.getCoordinate().getY());
    }

    @Test
    @DisplayName("getCell con coordenadas inválidas debe retornar null")
    void testGetCell_InvalidCoordinates() {
        assertNull(board.getCell(-1, 5));
        assertNull(board.getCell(5, -1));
        assertNull(board.getCell(10, 5));
        assertNull(board.getCell(5, 10));
    }

    @Test
    @DisplayName("getCell con Coordinate válida debe retornar celda")
    void testGetCell_WithCoordinate() {
        Coordinate coord = new Coordinate(3, 3);
        Cell cell = board.getCell(coord);
        assertNotNull(cell);
        assertEquals(coord, cell.getCoordinate());
    }

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

    // Tests para placeShip()

    @Test
    @DisplayName("placeShip con ship null debe retornar false")
    void testPlaceShip_NullShip() {
        assertFalse(board.placeShip(null));
    }

    @Test
    @DisplayName("placeShip con barco sin coordenada inicial debe retornar false")
    void testPlaceShip_NoStartCoordinate() {
        Ship ship = new Ship(ShipType.DESTROYER);
        assertFalse(board.placeShip(ship));
    }

    @Test
    @DisplayName("placeShip con barco válido debe retornar true")
    void testPlaceShip_ValidShip() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        assertTrue(board.placeShip(ship));
    }

    @Test
    @DisplayName("placeShip debe cambiar el estado de las celdas a SHIP")
    void testPlaceShip_UpdatesCellStatus() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(2, 2), Orientation.HORIZONTAL);
        board.placeShip(ship);

        assertEquals(CellStatus.SHIP, board.getCell(2, 2).getStatus());
        assertEquals(CellStatus.SHIP, board.getCell(3, 2).getStatus());
    }

    @Test
    @DisplayName("placeShip debe agregar el barco a la lista de barcos")
    void testPlaceShip_AddsToShipsList() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(1, 1), Orientation.VERTICAL);
        board.placeShip(ship);

        assertEquals(1, board.getShips().size());
        assertTrue(board.getShips().contains(ship));
    }

    @Test
    @DisplayName("placeShip con barco fuera de límites debe retornar false")
    void testPlaceShip_OutOfBounds() {
        Ship ship = new Ship(ShipType.CARRIER, new Coordinate(8, 0), Orientation.HORIZONTAL);
        assertFalse(board.placeShip(ship));
    }

    @Test
    @DisplayName("placeShip con superposición debe retornar false")
    void testPlaceShip_Overlap() {
        Ship ship1 = new Ship(ShipType.SUBMARINE, new Coordinate(3, 3), Orientation.HORIZONTAL);
        Ship ship2 = new Ship(ShipType.DESTROYER, new Coordinate(4, 3), Orientation.VERTICAL);

        board.placeShip(ship1);
        assertFalse(board.placeShip(ship2));
    }

    // Tests para removeShip()

    @Test
    @DisplayName("removeShip con barco no existente debe retornar false")
    void testRemoveShip_NonExistentShip() {
        Ship ship = new Ship(ShipType.FRIGATE, new Coordinate(1, 1), Orientation.HORIZONTAL);
        assertFalse(board.removeShip(ship));
    }

    @Test
    @DisplayName("removeShip con barco existente debe retornar true")
    void testRemoveShip_ExistingShip() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(2, 2), Orientation.HORIZONTAL);
        board.placeShip(ship);

        assertTrue(board.removeShip(ship));
    }

    @Test
    @DisplayName("removeShip debe cambiar el estado de las celdas a EMPTY")
    void testRemoveShip_UpdatesCellStatus() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(3, 3), Orientation.HORIZONTAL);
        board.placeShip(ship);
        board.removeShip(ship);

        assertEquals(CellStatus.EMPTY, board.getCell(3, 3).getStatus());
        assertEquals(CellStatus.EMPTY, board.getCell(4, 3).getStatus());
    }

    @Test
    @DisplayName("removeShip debe eliminar el barco de la lista")
    void testRemoveShip_RemovesFromList() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(5, 5), Orientation.VERTICAL);
        board.placeShip(ship);
        board.removeShip(ship);

        assertEquals(0, board.getShips().size());
        assertFalse(board.getShips().contains(ship));
    }

    // Tests para getShips()

    @Test
    @DisplayName("getShips en tablero vacío debe retornar lista vacía")
    void testGetShips_EmptyBoard() {
        assertTrue(board.getShips().isEmpty());
    }

    @Test
    @DisplayName("getShips debe retornar copia de la lista")
    void testGetShips_ReturnsCopy() {
        Ship ship = new Ship(ShipType.FRIGATE, new Coordinate(0, 0), Orientation.HORIZONTAL);
        board.placeShip(ship);

        board.getShips().clear();
        assertEquals(1, board.getShips().size());
    }

    // Tests para getShipAt()

    @Test
    @DisplayName("getShipAt debe retornar el barco en la coordenada")
    void testGetShipAt_WithShip() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(4, 4), Orientation.HORIZONTAL);
        board.placeShip(ship);

        assertEquals(ship, board.getShipAt(new Coordinate(4, 4)));
        assertEquals(ship, board.getShipAt(new Coordinate(5, 4)));
    }

    @Test
    @DisplayName("getShipAt sin barco debe retornar null")
    void testGetShipAt_NoShip() {
        assertNull(board.getShipAt(new Coordinate(7, 7)));
    }

    // Tests para hasShipAt()

    @Test
    @DisplayName("hasShipAt debe retornar true si hay barco")
    void testHasShipAt_WithShip() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(1, 1), Orientation.HORIZONTAL);
        board.placeShip(ship);

        assertTrue(board.hasShipAt(new Coordinate(1, 1)));
        assertTrue(board.hasShipAt(new Coordinate(2, 1)));
    }

    @Test
    @DisplayName("hasShipAt debe retornar false si no hay barco")
    void testHasShipAt_NoShip() {
        assertFalse(board.hasShipAt(new Coordinate(5, 5)));
    }

    // Tests para isValidCoordinate()

    @Test
    @DisplayName("isValidCoordinate con coordenadas válidas debe retornar true")
    void testIsValidCoordinate_Valid() {
        assertTrue(board.isValidCoordinate(new Coordinate(0, 0)));
        assertTrue(board.isValidCoordinate(new Coordinate(9, 9)));
        assertTrue(board.isValidCoordinate(new Coordinate(5, 5)));
    }

    @Test
    @DisplayName("isValidCoordinate con coordenadas inválidas debe retornar false")
    void testIsValidCoordinate_Invalid() {
        assertFalse(board.isValidCoordinate(new Coordinate(-1, 0)));
        assertFalse(board.isValidCoordinate(new Coordinate(0, -1)));
        assertFalse(board.isValidCoordinate(new Coordinate(10, 0)));
        assertFalse(board.isValidCoordinate(new Coordinate(0, 10)));
    }

    // Tests para getSunkShipsCount()

    @Test
    @DisplayName("getSunkShipsCount en tablero sin barcos debe retornar 0")
    void testGetSunkShipsCount_NoShips() {
        assertEquals(0, board.getSunkShipsCount());
    }

    @Test
    @DisplayName("getSunkShipsCount sin barcos hundidos debe retornar 0")
    void testGetSunkShipsCount_NoSunkShips() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        board.placeShip(ship);

        assertEquals(0, board.getSunkShipsCount());
    }

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

    // Tests para allShipsSunk()

    @Test
    @DisplayName("allShipsSunk en tablero vacío debe retornar false")
    void testAllShipsSunk_EmptyBoard() {
        assertFalse(board.allShipsSunk());
    }

    @Test
    @DisplayName("allShipsSunk con barcos no hundidos debe retornar false")
    void testAllShipsSunk_NotAllSunk() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        board.placeShip(ship);

        assertFalse(board.allShipsSunk());
    }

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

    // Tests para reset()

    @Test
    @DisplayName("reset debe limpiar todos los barcos")
    void testReset_ClearsShips() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(3, 3), Orientation.HORIZONTAL);
        board.placeShip(ship);

        board.reset();

        assertEquals(0, board.getShips().size());
    }

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