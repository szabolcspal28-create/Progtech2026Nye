package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class XmlStateServiceTest {

    @Test
    void testSaveAndLoadGameShouldWorkCorrectly() {
        XmlStateService underTest = new XmlStateService();
        Vehicle v1 = new Vehicle('A', new Position(2, 0), Orientation.HORIZONTAL, 2);
        Board originalBoard = new Board.Builder().withRows(6).withCols(6).withVehicles(List.of(v1)).build();

        String testFile = "test_savegame.xml";

        // Mentés
        underTest.saveGame(originalBoard, testFile);

        // Betöltés
        Board loadedBoard = underTest.loadGame(testFile);

        // Ellenőrzés
        assertNotNull(loadedBoard);
        assertEquals(originalBoard, loadedBoard);

        // Takarítás a teszt után
        new File(testFile).delete();
    }

    @Test
    void testLoadGameShouldReturnNullWhenFileDoesNotExist() {
        XmlStateService underTest = new XmlStateService();
        Board result = underTest.loadGame("non_existent_savegame.xml");
        assertNull(result);
    }
}