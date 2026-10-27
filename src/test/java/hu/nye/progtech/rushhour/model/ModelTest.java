package hu.nye.progtech.rushhour.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelTest {

    @Test
    void testPosition() {
        Position p1 = new Position(1, 2);
        Position p2 = new Position(1, 2);
        Position p3 = new Position(2, 1);

        assertEquals(1, p1.getRow());
        assertEquals(2, p1.getCol());
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertEquals("Position{row=1, col=2}", p1.toString());
    }

    @Test
    void testVehicle() {
        Position pos = new Position(0, 0);
        Vehicle v1 = new Vehicle('A', pos, Orientation.HORIZONTAL, 2);
        Vehicle v2 = new Vehicle('A', pos, Orientation.HORIZONTAL, 2);
        Vehicle v3 = new Vehicle('B', pos, Orientation.VERTICAL, 3);

        assertEquals('A', v1.getId());
        assertEquals(pos, v1.getPosition());
        assertEquals(Orientation.HORIZONTAL, v1.getOrientation());
        assertEquals(2, v1.getLength());

        assertEquals(v1, v2);
        assertNotEquals(v1, v3);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertTrue(v1.toString().contains("Vehicle{id=A"));
    }

    @Test
    void testBoardAndBuilder() {
        Vehicle v = new Vehicle('A', new Position(0, 0), Orientation.HORIZONTAL, 2);
        Board board1 = new Board.Builder()
                .withRows(6)
                .withCols(6)
                .withVehicles(List.of(v))
                .build();

        Board board2 = new Board.Builder()
                .withRows(6)
                .withCols(6)
                .withVehicles(List.of(v))
                .build();

        Board board3 = new Board.Builder()
                .withRows(8)
                .withCols(8)
                .withVehicles(List.of())
                .build();

        assertEquals(6, board1.getRows());
        assertEquals(6, board1.getCols());
        assertEquals(1, board1.getVehicles().size());

        assertEquals(board1, board2);
        assertNotEquals(board1, board3);
        assertEquals(board1.hashCode(), board2.hashCode());
        assertTrue(board1.toString().contains("Board{rows=6"));
    }

    @Test
    void testOrientationEnum() {
        Orientation h = Orientation.valueOf("HORIZONTAL");
        Orientation v = Orientation.valueOf("VERTICAL");
        assertEquals(Orientation.HORIZONTAL, h);
        assertEquals(Orientation.VERTICAL, v);
    }
}