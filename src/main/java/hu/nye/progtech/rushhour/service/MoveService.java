package hu.nye.progtech.rushhour.service;

import java.util.List;
import java.util.stream.Collectors;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Szolgáltatás a járművek mozgatásának és az ütközések kezelésére.
 */
public class MoveService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoveService.class);

    /**
     * Feldolgozza a megadott parancsot és végrehajtja a lépést a táblán.
     *
     * @param currentBoard az aktuális játéktábla
     * @param command      a lépést tartalmazó parancs (pl. "A jobb 1")
     * @return az új játéktábla a lépés után, vagy a régi, ha a lépés érvénytelen
     */
    public Board processMove(final Board currentBoard, final String command) {
        String[] parts = command.split(" ");
        if (parts.length != 3) {
            LOGGER.warn("Érvénytelen formátum! Használat pl.: 'A jobb 1'");
            return currentBoard;
        }

        char id = parts[0].charAt(0);
        String direction = parts[1].toLowerCase();
        int steps;

        try {
            steps = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            LOGGER.warn("A lépésszámnak számnak kell lennie!");
            return currentBoard;
        }

        Vehicle targetVehicle = currentBoard.getVehicles().stream()
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);

        if (targetVehicle == null) {
            LOGGER.warn("Nincs ilyen jármű a pályán!");
            return currentBoard;
        }

        boolean isHorizontal = targetVehicle.getOrientation() == Orientation.HORIZONTAL;
        boolean isVertical = targetVehicle.getOrientation() == Orientation.VERTICAL;

        if (isHorizontal && !("bal".equals(direction) || "jobb".equals(direction))) {
            LOGGER.warn("Ez a jármű csak vízszintesen mozoghat!");
            return currentBoard;
        }
        if (isVertical && !("fel".equals(direction) || "le".equals(direction))) {
            LOGGER.warn("Ez a jármű csak függőlegesen mozoghat!");
            return currentBoard;
        }

        int newRow = targetVehicle.getPosition().getRow();
        int newCol = targetVehicle.getPosition().getCol();

        if ("bal".equals(direction)) {
            newCol -= steps;
        }
        if ("jobb".equals(direction)) {
            newCol += steps;
        }
        if ("fel".equals(direction)) {
            newRow -= steps;
        }
        if ("le".equals(direction)) {
            newRow += steps;
        }

        if (isOutOfBounds(currentBoard, targetVehicle, newRow, newCol)) {
            LOGGER.warn("A jármű lemenne a pályáról!");
            return currentBoard;
        }

        if (hasCollision(currentBoard, targetVehicle, newRow, newCol)) {
            LOGGER.warn("A lépés érvénytelen, a jármű útja nem szabad!");
            return currentBoard;
        }

        Position newPos = new Position(newRow, newCol);
        Vehicle updatedVehicle = new Vehicle(id, newPos,
                targetVehicle.getOrientation(), targetVehicle.getLength());

        List<Vehicle> newVehicles = currentBoard.getVehicles().stream()
                .map(v -> v.getId() == id ? updatedVehicle : v)
                .collect(Collectors.toList());

        LOGGER.info("Lépés sikeresen végrehajtva: {}", command);
        return new Board.Builder()
                .withRows(currentBoard.getRows())
                .withCols(currentBoard.getCols())
                .withVehicles(newVehicles)
                .build();
    }

    private boolean isOutOfBounds(final Board board, final Vehicle vehicle,
                                  final int newRow, final int newCol) {
        if (newRow < 0 || newCol < 0) {
            return true;
        }
        if (vehicle.getOrientation() == Orientation.HORIZONTAL
                && newCol + vehicle.getLength() > board.getCols()) {
            return true;
        }
        return vehicle.getOrientation() == Orientation.VERTICAL
                && newRow + vehicle.getLength() > board.getRows();
    }

    private boolean hasCollision(final Board board, final Vehicle target,
                                 final int newRow, final int newCol) {
        boolean isVert = target.getOrientation() == Orientation.VERTICAL;
        boolean isHor = target.getOrientation() == Orientation.HORIZONTAL;

        int moveStartR = Math.min(target.getPosition().getRow(), newRow);
        int moveEndR = Math.max(
                target.getPosition().getRow() + (isVert ? target.getLength() - 1 : 0),
                newRow + (isVert ? target.getLength() - 1 : 0)
        );

        int moveStartC = Math.min(target.getPosition().getCol(), newCol);
        int moveEndC = Math.max(
                target.getPosition().getCol() + (isHor ? target.getLength() - 1 : 0),
                newCol + (isHor ? target.getLength() - 1 : 0)
        );

        return board.getVehicles().stream()
                .filter(other -> other.getId() != target.getId())
                .anyMatch(other -> {
                    boolean otherVert = other.getOrientation() == Orientation.VERTICAL;
                    boolean otherHor = other.getOrientation() == Orientation.HORIZONTAL;

                    int otherStartR = other.getPosition().getRow();
                    int otherEndR = otherStartR + (otherVert ? other.getLength() - 1 : 0);
                    int otherStartC = other.getPosition().getCol();
                    int otherEndC = otherStartC + (otherHor ? other.getLength() - 1 : 0);

                    return !(otherEndC < moveStartC || otherStartC > moveEndC
                            || otherEndR < moveStartR || otherStartR > moveEndR);
                });
    }
}