package com.example.miniproyecto4.model.Board;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.Ship;
import com.example.miniproyecto4.model.Ship.ShipType;
import com.example.miniproyecto4.model.Validation.Orientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BoardValidator Unit Tests")
class BoardValidatorTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(10);
    }

    // Tests para isValidPlacement()

    @Test
    @DisplayName("isValidPlacement con ship null debe retornar false")
    void testIsValidPlacement_ShipNull() {
        assertFalse(BoardValidator.isValidPlacement(board, null));
    }

    @Test
    @DisplayName("isValidPlacement con coordenada inicial null debe retornar false")
    void testIsValidPlacement_StartCoordinateNull() {
        Ship ship = new Ship(ShipType.DESTROYER);
        assertFalse(BoardValidator.isValidPlacement(board, ship));
    }

    @Test
    @DisplayName("isValidPlacement con coordenadas válidas debe retornar true")
    void testIsValidPlacement_Valid() {
        Ship ship = new Ship(ShipType.CARRIER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        assertTrue(BoardValidator.isValidPlacement(board, ship));
    }

    @Test
    @DisplayName("isValidPlacement con coordenadas fuera de límites debe retornar false")
    void testIsValidPlacement_OutOfBounds() {
        Ship ship = new Ship(ShipType.CARRIER, new Coordinate(8, 0), Orientation.HORIZONTAL);
        assertFalse(BoardValidator.isValidPlacement(board, ship));
    }

    @Test
    @DisplayName("isValidPlacement con superposición debe retornar false")
    void testIsValidPlacement_Overlap() {
        Ship ship1 = new Ship(ShipType.CARRIER, new Coordinate(2, 2), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.DESTROYER, new Coordinate(3, 2), Orientation.VERTICAL);
        assertFalse(BoardValidator.isValidPlacement(board, ship2));
    }

    // Tests para isWithinBounds()

    @Test
    @DisplayName("isWithinBounds con coordenada válida debe retornar true")
    void testIsWithinBounds_Valid() {
        assertTrue(BoardValidator.isWithinBounds(board, new Coordinate(5, 5)));
        assertTrue(BoardValidator.isWithinBounds(board, new Coordinate(0, 0)));
        assertTrue(BoardValidator.isWithinBounds(board, new Coordinate(9, 9)));
    }

    @Test
    @DisplayName("isWithinBounds con coordenada inválida debe retornar false")
    void testIsWithinBounds_Invalid() {
        assertFalse(BoardValidator.isWithinBounds(board, new Coordinate(-1, 5)));
        assertFalse(BoardValidator.isWithinBounds(board, new Coordinate(5, -1)));
        assertFalse(BoardValidator.isWithinBounds(board, new Coordinate(10, 5)));
        assertFalse(BoardValidator.isWithinBounds(board, new Coordinate(5, 10)));
    }

    // Tests para hasNoOverlap()

    @Test
    @DisplayName("hasNoOverlap sin barcos en el tablero debe retornar true")
    void testHasNoOverlap_NoShips() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(1, 1), Orientation.HORIZONTAL);
        assertTrue(BoardValidator.hasNoOverlap(board, ship));
    }

    @Test
    @DisplayName("hasNoOverlap con barco existente debe retornar false")
    void testHasNoOverlap_WithShip() {
        Ship ship1 = new Ship(ShipType.SUBMARINE, new Coordinate(3, 3), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.DESTROYER, new Coordinate(4, 3), Orientation.VERTICAL);
        assertFalse(BoardValidator.hasNoOverlap(board, ship2));
    }

    @Test
    @DisplayName("hasNoOverlap con barcos adyacentes sin superposición debe retornar true")
    void testHasNoOverlap_Adjacent() {
        Ship ship1 = new Ship(ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.SUBMARINE, new Coordinate(0, 1), Orientation.HORIZONTAL);
        assertTrue(BoardValidator.hasNoOverlap(board, ship2));
    }
}