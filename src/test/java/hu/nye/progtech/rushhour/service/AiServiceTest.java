package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AiServiceTest {

    private MoveService moveServiceMock;
    private AiService underTest;
    private Board board;

    @BeforeEach
    void setUp() {
        moveServiceMock = Mockito.mock(MoveService.class);
        underTest = new AiService(moveServiceMock);

        Vehicle vehicleA = new Vehicle('A', new Position(0, 0), Orientation.HORIZONTAL, 2);
        Vehicle vehicleB = new Vehicle('B', new Position(2, 2), Orientation.VERTICAL, 2);

        // Kibővítettük a tesztpályát, hogy legyen legalább két jármű (A és B),
        // így az AI nem lép ki azonnal a "size <= 1" védelem miatt.
        board = new Board.Builder()
                .withRows(6)
                .withCols(6)
                .withVehicles(List.of(vehicleA, vehicleB))
                .build();
    }

    @Test
    void testPlayAiTurnShouldReturnNewBoard() {
        Board newBoard = new Board.Builder()
                .withRows(6)
                .withCols(6)
                .withVehicles(List.of())
                .build();

        when(moveServiceMock.processMove(eq(board), anyString())).thenReturn(newBoard);

        Board result = underTest.playAiTurn(board);

        assertNotSame(board, result);
    }
}