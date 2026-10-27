package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class MoveService {
    private static final Logger logger = LoggerFactory.getLogger(MoveService.class);

    public Board processMove(Board currentBoard, String command) {
        String[] parts = command.split(" ");
        if (parts.length != 3) {
            logger.warn("Érvénytelen formátum! Használat pl.: 'A jobb 1'");
            return currentBoard;
        }

        char id = parts[0].charAt(0);
        String direction = parts[1].toLowerCase();
        int steps;

        try {
            steps = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            logger.warn("A lépésszámnak számnak kell lennie!");
            return currentBoard;
        }

        Vehicle targetVehicle = currentBoard.getVehicles().stream()
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);

        if (targetVehicle == null) {
            logger.warn("Nincs ilyen jármű a pályán!");
            return currentBoard;
        }

        if (targetVehicle.getOrientation() == Orientation.HORIZONTAL && !(direction.equals("bal") || direction.equals("jobb"))) {
            logger.warn("Ez a jármű csak vízszintesen mozoghat!");
            return currentBoard;
        }
        if (targetVehicle.getOrientation() == Orientation.VERTICAL && !(direction.equals("fel") || direction.equals("le"))) {
            logger.warn("Ez a jármű csak függőlegesen mozoghat!");
            return currentBoard;
        }

        int newRow = targetVehicle.getPosition().getRow();
        int newCol = targetVehicle.getPosition().getCol();

        if (direction.equals("bal")) newCol -= steps;
        if (direction.equals("jobb")) newCol += steps;
        if (direction.equals("fel")) newRow -= steps;
        if (direction.equals("le")) newRow += steps;

        if (newRow < 0 || newCol < 0 ||
                (targetVehicle.getOrientation() == Orientation.HORIZONTAL && newCol + targetVehicle.getLength() > currentBoard.getCols()) ||
                (targetVehicle.getOrientation() == Orientation.VERTICAL && newRow + targetVehicle.getLength() > currentBoard.getRows())) {
            logger.warn("A jármű lemenne a pályáról!");
            return currentBoard;
        }

        int moveStartR = Math.min(targetVehicle.getPosition().getRow(), newRow);
        int moveEndR = Math.max(targetVehicle.getPosition().getRow() + (targetVehicle.getOrientation() == Orientation.VERTICAL ? targetVehicle.getLength() - 1 : 0),
                newRow + (targetVehicle.getOrientation() == Orientation.VERTICAL ? targetVehicle.getLength() - 1 : 0));

        int moveStartC = Math.min(targetVehicle.getPosition().getCol(), newCol);
        int moveEndC = Math.max(targetVehicle.getPosition().getCol() + (targetVehicle.getOrientation() == Orientation.HORIZONTAL ? targetVehicle.getLength() - 1 : 0),
                newCol + (targetVehicle.getOrientation() == Orientation.HORIZONTAL ? targetVehicle.getLength() - 1 : 0));

        boolean collision = currentBoard.getVehicles().stream()
                .filter(other -> other.getId() != id)
                .anyMatch(other -> {
                    int otherStartR = other.getPosition().getRow();
                    int otherEndR = otherStartR + (other.getOrientation() == Orientation.VERTICAL ? other.getLength() - 1 : 0);
                    int otherStartC = other.getPosition().getCol();
                    int otherEndC = otherStartC + (other.getOrientation() == Orientation.HORIZONTAL ? other.getLength() - 1 : 0);
                    return !(otherEndC < moveStartC || otherStartC > moveEndC || otherEndR < moveStartR || otherStartR > moveEndR);
                });

        if (collision) {
            logger.warn("A lépés érvénytelen, a jármű útja nem szabad!");
            return currentBoard;
        }

        Vehicle updatedVehicle = new Vehicle(id, new Position(newRow, newCol), targetVehicle.getOrientation(), targetVehicle.getLength());
        List<Vehicle> newVehicles = currentBoard.getVehicles().stream()
                .map(v -> v.getId() == id ? updatedVehicle : v)
                .collect(Collectors.toList());

        logger.info("Lépés sikeresen végrehajtva: {}", command);
        return new Board.Builder()
                .withRows(currentBoard.getRows())
                .withCols(currentBoard.getCols())
                .withVehicles(newVehicles)
                .build();
    }
}