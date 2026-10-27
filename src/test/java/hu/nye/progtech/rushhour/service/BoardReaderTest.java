package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardReaderTest {

    @Test
    void testReadBoardShouldReturnValidBoardWhenFileExists() {
        BoardReader underTest = new BoardReader();
        Board board = underTest.readBoard("default-board.txt");

        assertNotNull(board);
        assertEquals(6, board.getRows());
        assertEquals(6, board.getCols());
        assertFalse(board.getVehicles().isEmpty());
    }

    @Test
    void testReadBoardShouldThrowExceptionWhenFileIsMissing() {
        BoardReader underTest = new BoardReader();

        assertThrows(IllegalArgumentException.class, () -> {
            underTest.readBoard("nem_letezo_fajl.txt");
        });
    }
}