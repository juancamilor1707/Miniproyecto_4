package com.example.miniproyecto4.model.Board;

import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.Ship;
import com.example.miniproyecto4.model.Ship.ShipType;
import com.example.miniproyecto4.model.Validation.Orientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BoardValidator class.
 * Tests all validation methods for ship placement, boundary checking, and overlap detection.
 */
@DisplayName("BoardValidator Unit Tests")
class BoardValidatorTest {

    /**
     * The game board used for testing validation operations.
     */
    private Board board;

    /**
     * Sets up the test environment before each test.
     * Initializes a new 10x10 board.
     */
    @BeforeEach
    void setUp() {
        board = new Board(10);
    }

    /**
     * Tests that isValidPlacement returns false when the ship is null.
     */
    @Test
    @DisplayName("isValidPlacement con ship null debe retornar false")
    void testIsValidPlacement_ShipNull() {
        assertFalse(BoardValidator.isValidPlacement(board, null));
    }

    /**
     * Tests that isValidPlacement returns false when the ship has no start coordinate.
     */
    @Test
    @DisplayName("isValidPlacement con coordenada inicial null debe retornar false")
    void testIsValidPlacement_StartCoordinateNull() {
        Ship ship = new Ship(ShipType.DESTROYER);
        assertFalse(BoardValidator.isValidPlacement(board, ship));
    }

    /**
     * Tests that isValidPlacement returns true when the ship placement is valid.
     */
    @Test
    @DisplayName("isValidPlacement con coordenadas válidas debe retornar true")
    void testIsValidPlacement_Valid() {
        Ship ship = new Ship(ShipType.CARRIER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        assertTrue(BoardValidator.isValidPlacement(board, ship));
    }

    /**
     * Tests that isValidPlacement returns false when the ship extends beyond board boundaries.
     */
    @Test
    @DisplayName("isValidPlacement con coordenadas fuera de límites debe retornar false")
    void testIsValidPlacement_OutOfBounds() {
        Ship ship = new Ship(ShipType.CARRIER, new Coordinate(8, 0), Orientation.HORIZONTAL);
        assertFalse(BoardValidator.isValidPlacement(board, ship));
    }

    /**
     * Tests that isValidPlacement returns false when the ship overlaps with an existing ship.
     */
    @Test
    @DisplayName("isValidPlacement con superposición debe retornar false")
    void testIsValidPlacement_Overlap() {
        Ship ship1 = new Ship(ShipType.CARRIER, new Coordinate(2, 2), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.DESTROYER, new Coordinate(3, 2), Orientation.VERTICAL);
        assertFalse(BoardValidator.isValidPlacement(board, ship2));
    }

    /**
     * Tests that isWithinBounds returns true for valid coordinates.
     */
    @Test
    @DisplayName("isWithinBounds con coordenada válida debe retornar true")
    void testIsWithinBounds_Valid() {
        assertTrue(BoardValidator.isWithinBounds(board, new Coordinate(5, 5)));
        assertTrue(BoardValidator.isWithinBounds(board, new Coordinate(0, 0)));
        assertTrue(BoardValidator.isWithinBounds(board, new Coordinate(9, 9)));
    }

    /**
     * Tests that isWithinBounds returns false for invalid coordinates.
     */
    @Test
    @DisplayName("isWithinBounds con coordenada inválida debe retornar false")
    void testIsWithinBounds_Invalid() {
        assertFalse(BoardValidator.isWithinBounds(board, new Coordinate(-1, 5)));
        assertFalse(BoardValidator.isWithinBounds(board, new Coordinate(5, -1)));
        assertFalse(BoardValidator.isWithinBounds(board, new Coordinate(10, 5)));
        assertFalse(BoardValidator.isWithinBounds(board, new Coordinate(5, 10)));
    }

    /**
     * Tests that hasNoOverlap returns true when the board has no ships.
     */
    @Test
    @DisplayName("hasNoOverlap sin barcos en el tablero debe retornar true")
    void testHasNoOverlap_NoShips() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(1, 1), Orientation.HORIZONTAL);
        assertTrue(BoardValidator.hasNoOverlap(board, ship));
    }

    /**
     * Tests that hasNoOverlap returns false when there is an overlap with an existing ship.
     */
    @Test
    @DisplayName("hasNoOverlap con barco existente debe retornar false")
    void testHasNoOverlap_WithShip() {
        Ship ship1 = new Ship(ShipType.SUBMARINE, new Coordinate(3, 3), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.DESTROYER, new Coordinate(4, 3), Orientation.VERTICAL);
        assertFalse(BoardValidator.hasNoOverlap(board, ship2));
    }

    /**
     * Tests that hasNoOverlap returns true for adjacent ships without overlap.
     */
    @Test
    @DisplayName("hasNoOverlap con barcos adyacentes sin superposición debe retornar true")
    void testHasNoOverlap_Adjacent() {
        Ship ship1 = new Ship(ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.SUBMARINE, new Coordinate(0, 1), Orientation.HORIZONTAL);
        assertTrue(BoardValidator.hasNoOverlap(board, ship2));
    }
}