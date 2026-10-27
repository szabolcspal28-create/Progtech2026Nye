package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MoveServiceTest {

    private MoveService underTest;
    private Board initialBoard;

    @BeforeEach
    void setUp() {
        underTest = new MoveService();
        // Csinálunk egy mini tesztpályát (6x6) két járművel
        Vehicle v1 = new Vehicle('A', new Position(2, 0), Orientation.HORIZONTAL, 2);
        Vehicle v2 = new Vehicle('X', new Position(0, 3), Orientation.VERTICAL, 3);

        initialBoard = new Board.Builder()
                .withRows(6)
                .withCols(6)
                .withVehicles(List.of(v1, v2))
                .build();
    }

    @Test
    void testProcessMoveShouldMoveVehicleRightWhenPathIsClear() {
        // Given - Adott egy érvényes parancs jobbra
        String command = "A jobb 1";

        // When - Amikor végrehajtjuk a lépést
        Board resultBoard = underTest.processMove(initialBoard, command);

        // Then - Akkor a tábla megváltozik, és a jármű új oszlopa 1 lesz (0-ról)
        assertNotEquals(initialBoard, resultBoard);
        assertEquals(1, resultBoard.getVehicles().get(0).getPosition().getCol());
    }

    @Test
    void testProcessMoveShouldReturnSameBoardWhenFormatIsInvalid() {
        // Given - Adott egy rossz formátumú parancs
        String command = "hibas_parancs";

        // When
        Board resultBoard = underTest.processMove(initialBoard, command);

        // Then - A tábla állapota nem változhat
        assertEquals(initialBoard, resultBoard);
    }

    @Test
    void testProcessMoveShouldReturnSameBoardWhenMovingOutOfBounds() {
        // Given - Az 'A' jármű a 0. oszlopban van, balra lemenne a pályáról
        String command = "A bal 1";

        // When
        Board resultBoard = underTest.processMove(initialBoard, command);

        // Then
        assertEquals(initialBoard, resultBoard);
    }

    @Test
    void testProcessMoveShouldReturnSameBoardWhenVehicleDoesNotExist() {
        // Given - Nincs 'C' jármű a pályán
        String command = "C jobb 1";

        // When
        Board resultBoard = underTest.processMove(initialBoard, command);

        // Then
        assertEquals(initialBoard, resultBoard);
    }

    @Test
    void testProcessMoveShouldReturnSameBoardWhenMovingInWrongDirection() {
        // Given - Az 'A' jármű vízszintes (HORIZONTAL), nem mehet fel
        String command = "A fel 1";

        // When
        Board resultBoard = underTest.processMove(initialBoard, command);

        // Then
        assertEquals(initialBoard, resultBoard);
    }
}