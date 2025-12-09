package com.example.miniproyecto4.model.Validation;

import com.example.miniproyecto4.model.Board.Board;
import com.example.miniproyecto4.model.Cell.Coordinate;
import com.example.miniproyecto4.model.Ship.Ship;
import com.example.miniproyecto4.model.Ship.ShipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ShipPlacementValidator class.
 * Tests validation methods for ship placement including bounds checking,
 * overlap detection, and overall placement validation.
 */
@DisplayName("ShipPlacementValidator Unit Tests")
class ShipPlacementValidatorTest {

    /**
     * The validator instance used for testing ship placement rules.
     */
    private ShipPlacementValidator validator;

    /**
     * The game board used for testing placement validation.
     */
    private Board board;

    /**
     * Sets up the test environment before each test.
     * Initializes a new validator and a 10x10 board.
     */
    @BeforeEach
    void setUp() {
        validator = new ShipPlacementValidator();
        board = new Board(10);
    }

    /**
     * Tests that validate returns false when board is null.
     */
    @Test
    @DisplayName("validate con board null debe retornar false")
    void testValidate_NullBoard() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        assertFalse(validator.validate(null, ship));
    }

    /**
     * Tests that validate returns false when ship is null.
     */
    @Test
    @DisplayName("validate con ship null debe retornar false")
    void testValidate_NullShip() {
        assertFalse(validator.validate(board, null));
    }

    /**
     * Tests that validate returns false when both board and ship are null.
     */
    @Test
    @DisplayName("validate con ambos null debe retornar false")
    void testValidate_BothNull() {
        assertFalse(validator.validate(null, null));
    }

    /**
     * Tests that validate returns false when ship has no start coordinate.
     */
    @Test
    @DisplayName("validate con ship sin coordenada inicial debe retornar false")
    void testValidate_NoStartCoordinate() {
        Ship ship = new Ship(ShipType.DESTROYER);
        assertFalse(validator.validate(board, ship));
    }

    /**
     * Tests that validate returns true for valid ship placement.
     */
    @Test
    @DisplayName("validate con colocación válida debe retornar true")
    void testValidate_ValidPlacement() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(2, 2), Orientation.HORIZONTAL);
        assertTrue(validator.validate(board, ship));
    }

    /**
     * Tests that validate returns false when ship extends beyond boundaries.
     */
    @Test
    @DisplayName("validate con barco fuera de límites debe retornar false")
    void testValidate_OutOfBounds() {
        Ship ship = new Ship(ShipType.CARRIER, new Coordinate(8, 0), Orientation.HORIZONTAL);
        assertFalse(validator.validate(board, ship));
    }

    /**
     * Tests that validate returns false when ship overlaps with existing ship.
     */
    @Test
    @DisplayName("validate con superposición debe retornar false")
    void testValidate_Overlap() {
        Ship ship1 = new Ship(ShipType.SUBMARINE, new Coordinate(3, 3), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.DESTROYER, new Coordinate(4, 3), Orientation.VERTICAL);
        assertFalse(validator.validate(board, ship2));
    }

    /**
     * Tests that validate returns true for adjacent ships without overlap.
     */
    @Test
    @DisplayName("validate con barcos adyacentes sin superposición debe retornar true")
    void testValidate_AdjacentShips() {
        Ship ship1 = new Ship(ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.SUBMARINE, new Coordinate(0, 1), Orientation.HORIZONTAL);
        assertTrue(validator.validate(board, ship2));
    }

    /**
     * Tests that isWithinBounds returns true when all coordinates are within boundaries.
     */
    @Test
    @DisplayName("isWithinBounds con todas las coordenadas dentro de límites debe retornar true")
    void testIsWithinBounds_AllInside() {
        Ship ship = new Ship(ShipType.CARRIER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        assertTrue(validator.isWithinBounds(board, ship));
    }

    /**
     * Tests that isWithinBounds returns false when horizontal placement exceeds boundaries.
     */
    @Test
    @DisplayName("isWithinBounds con coordenada horizontal fuera de límites debe retornar false")
    void testIsWithinBounds_HorizontalOutOfBounds() {
        Ship ship = new Ship(ShipType.CARRIER, new Coordinate(8, 0), Orientation.HORIZONTAL);
        assertFalse(validator.isWithinBounds(board, ship));
    }

    /**
     * Tests that isWithinBounds returns false when vertical placement exceeds boundaries.
     */
    @Test
    @DisplayName("isWithinBounds con coordenada vertical fuera de límites debe retornar false")
    void testIsWithinBounds_VerticalOutOfBounds() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(0, 9), Orientation.VERTICAL);
        assertFalse(validator.isWithinBounds(board, ship));
    }

    /**
     * Tests that isWithinBounds returns false for negative coordinates.
     */
    @Test
    @DisplayName("isWithinBounds con coordenada negativa debe retornar false")
    void testIsWithinBounds_NegativeCoordinate() {
        Ship ship = new Ship(ShipType.DESTROYER, new Coordinate(-1, 0), Orientation.HORIZONTAL);
        assertFalse(validator.isWithinBounds(board, ship));
    }

    /**
     * Tests that isWithinBounds returns true for placement at top-left corner.
     */
    @Test
    @DisplayName("isWithinBounds en esquina superior izquierda debe retornar true")
    void testIsWithinBounds_TopLeftCorner() {
        Ship ship = new Ship(ShipType.FRIGATE, new Coordinate(0, 0), Orientation.HORIZONTAL);
        assertTrue(validator.isWithinBounds(board, ship));
    }

    /**
     * Tests that isWithinBounds returns true for placement at bottom-right corner.
     */
    @Test
    @DisplayName("isWithinBounds en esquina inferior derecha debe retornar true")
    void testIsWithinBounds_BottomRightCorner() {
        Ship ship = new Ship(ShipType.FRIGATE, new Coordinate(9, 9), Orientation.HORIZONTAL);
        assertTrue(validator.isWithinBounds(board, ship));
    }

    /**
     * Tests that hasNoOverlap returns true on an empty board.
     */
    @Test
    @DisplayName("hasNoOverlap sin barcos en el tablero debe retornar true")
    void testHasNoOverlap_EmptyBoard() {
        Ship ship = new Ship(ShipType.SUBMARINE, new Coordinate(5, 5), Orientation.HORIZONTAL);
        assertTrue(validator.hasNoOverlap(board, ship));
    }

    /**
     * Tests that hasNoOverlap returns false when overlapping at first coordinate.
     */
    @Test
    @DisplayName("hasNoOverlap con superposición en primera coordenada debe retornar false")
    void testHasNoOverlap_OverlapFirstCoordinate() {
        Ship ship1 = new Ship(ShipType.DESTROYER, new Coordinate(2, 2), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.SUBMARINE, new Coordinate(2, 2), Orientation.VERTICAL);
        assertFalse(validator.hasNoOverlap(board, ship2));
    }

    /**
     * Tests that hasNoOverlap returns false when overlapping at last coordinate.
     */
    @Test
    @DisplayName("hasNoOverlap con superposición en última coordenada debe retornar false")
    void testHasNoOverlap_OverlapLastCoordinate() {
        Ship ship1 = new Ship(ShipType.SUBMARINE, new Coordinate(4, 4), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.DESTROYER, new Coordinate(5, 3), Orientation.VERTICAL);
        assertFalse(validator.hasNoOverlap(board, ship2));
    }

    /**
     * Tests that hasNoOverlap returns false when ships partially overlap.
     */
    @Test
    @DisplayName("hasNoOverlap con superposición parcial debe retornar false")
    void testHasNoOverlap_PartialOverlap() {
        Ship ship1 = new Ship(ShipType.CARRIER, new Coordinate(1, 1), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.SUBMARINE, new Coordinate(3, 0), Orientation.VERTICAL);
        assertFalse(validator.hasNoOverlap(board, ship2));
    }

    /**
     * Tests that hasNoOverlap returns true for adjacent ships without overlap.
     */
    @Test
    @DisplayName("hasNoOverlap con barcos adyacentes debe retornar true")
    void testHasNoOverlap_AdjacentShips() {
        Ship ship1 = new Ship(ShipType.DESTROYER, new Coordinate(1, 1), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.SUBMARINE, new Coordinate(1, 2), Orientation.HORIZONTAL);
        assertTrue(validator.hasNoOverlap(board, ship2));
    }

    /**
     * Tests that hasNoOverlap returns true for diagonally placed ships.
     */
    @Test
    @DisplayName("hasNoOverlap con barcos en diagonal debe retornar true")
    void testHasNoOverlap_DiagonalShips() {
        Ship ship1 = new Ship(ShipType.FRIGATE, new Coordinate(0, 0), Orientation.HORIZONTAL);
        board.placeShip(ship1);

        Ship ship2 = new Ship(ShipType.FRIGATE, new Coordinate(1, 1), Orientation.HORIZONTAL);
        assertTrue(validator.hasNoOverlap(board, ship2));
    }

    /**
     * Tests that hasNoOverlap returns true for multiple ships without any overlap.
     */
    @Test
    @DisplayName("hasNoOverlap con múltiples barcos sin superposición debe retornar true")
    void testHasNoOverlap_MultipleShipsNoOverlap() {
        Ship ship1 = new Ship(ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL);
        Ship ship2 = new Ship(ShipType.SUBMARINE, new Coordinate(5, 5), Orientation.VERTICAL);

        board.placeShip(ship1);
        board.placeShip(ship2);

        Ship ship3 = new Ship(ShipType.FRIGATE, new Coordinate(9, 9), Orientation.HORIZONTAL);
        assertTrue(validator.hasNoOverlap(board, ship3));
    }
}