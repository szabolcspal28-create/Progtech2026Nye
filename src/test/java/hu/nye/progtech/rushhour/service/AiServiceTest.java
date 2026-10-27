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

        Vehicle vehicleB = new Vehicle('B', new Position(0, 0), Orientation.HORIZONTAL, 2);
        // A Builder mintát kell használnunk a Board létrehozásához, ahogy a kódunkban megírtuk!
        board = new Board.Builder()
                .withRows(6)
                .withCols(6)
                .withVehicles(List.of(vehicleB))
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