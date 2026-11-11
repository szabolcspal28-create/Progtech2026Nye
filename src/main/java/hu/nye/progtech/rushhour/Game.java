package hu.nye.progtech.rushhour;

import java.util.Scanner;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.service.AiService;
import hu.nye.progtech.rushhour.service.BoardReader;
import hu.nye.progtech.rushhour.service.DatabaseService;
import hu.nye.progtech.rushhour.service.MoveService;
import hu.nye.progtech.rushhour.service.XmlStateService;
import hu.nye.progtech.rushhour.ui.BoardPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A játék fő osztálya, amely a játékmenetet és a ciklust irányítja.
 */
public class Game {
    /**
     * A naplózó objektum a Game osztályhoz.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    /**
     * A mentési fájl neve.
     */
    private static final String SAVE_FILE = "savegame.xml";

    /**
     * Elindítja a játékot és kezeli a fő eseményhurkot.
     */
    public void start() {
        System.out.println("Üdvözöllek a Rush Hour játékban!");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Kérlek, add meg a neved: ");
        String playerName = scanner.nextLine();
        System.out.println("Sok sikert, " + playerName + "!");

        BoardReader reader = new BoardReader();
        Board board = reader.readBoard("default-board.txt");
        BoardPrinter printer = new BoardPrinter();

        MoveService moveService = new MoveService();
        AiService aiService = new AiService(moveService);
        DatabaseService dbService = DatabaseService.getInstance();
        XmlStateService xmlService = new XmlStateService();

        LOGGER.info("Játék elindítva: {}", playerName);

        boolean isRunning = true;
        while (isRunning) {
            printer.printBoard(board);
            System.out.println("Parancsok: '[jármű] [irány] [lépés]' "
                    + "(pl. 'A jobb 1') | 'mentes' | 'betoltes' | 'kilepes'");
            System.out.print("Kérem a következő parancsot: ");
            String command = scanner.nextLine();

            if ("kilepes".equalsIgnoreCase(command)) {
                System.out.println("Játék befejezése... Viszlát, "
                        + playerName + "!");
                dbService.printLeaderboard();
                break;
            } else if ("mentes".equalsIgnoreCase(command)) {
                xmlService.saveGame(board, SAVE_FILE);
                System.out.println(">>> Játékállás sikeresen elmentve!");
                continue;
            } else if ("betoltes".equalsIgnoreCase(command)) {
                Board loadedBoard = xmlService.loadGame(SAVE_FILE);
                if (loadedBoard != null) {
                    board = loadedBoard;
                    System.out.println(">>> Játékállás sikeresen betöltve!");
                } else {
                    System.out.println(">>> Hiba: Nem található mentett játék.");
                }
                continue;
            }

            Board nextBoard = moveService.processMove(board, command);
            if (nextBoard == board) {
                continue;
            }
            board = nextBoard;

            if (checkWinCondition(board, 'A', board.getCols())) {
                printer.printBoard(board);
                System.out.println("Gratulálok, " + playerName
                        + "! Kijuttattad a járművet, TE NYERTÉL!");
                dbService.saveWin(playerName, "Ember");
                dbService.printLeaderboard();
                break;
            }

            board = aiService.playAiTurn(board);

            if (checkWinCondition(board, 'B', 0)) {
                printer.printBoard(board);
                System.out.println("Sajnos a gép kivezette a járművét. "
                        + "A GÉP NYERT!");
                dbService.saveWin(playerName, "Gép");
                dbService.printLeaderboard();
                break;
            }
        }
        scanner.close();
    }

    private boolean checkWinCondition(final Board board, final char targetId,
                                      final int exitCol) {
        return board.getVehicles().stream()
                .filter(v -> v.getId() == targetId)
                .anyMatch(v -> {
                    if (exitCol == 0 && v.getPosition().getCol() == 0) {
                        return true;
                    }
                    return exitCol > 0
                            && v.getPosition().getCol() + v.getLength() == exitCol;
                });
    }
}
