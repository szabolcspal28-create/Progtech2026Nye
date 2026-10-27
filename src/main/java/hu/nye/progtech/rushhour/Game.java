package hu.nye.progtech.rushhour;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.service.AiService;
import hu.nye.progtech.rushhour.service.BoardReader;
import hu.nye.progtech.rushhour.service.DatabaseService;
import hu.nye.progtech.rushhour.service.MoveService;
import hu.nye.progtech.rushhour.service.XmlStateService;
import hu.nye.progtech.rushhour.ui.BoardPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);
    private static final String SAVE_FILE = "savegame.xml";

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
        XmlStateService xmlService = new XmlStateService(); // XML Szerviz beállítva

        logger.info("Játék elindítva: {}", playerName);

        boolean isRunning = true;
        while (isRunning) {
            printer.printBoard(board);
            System.out.println("Parancsok: '[jármű] [irány] [lépés]' (pl. 'A jobb 1') | 'mentes' | 'betoltes' | 'kilepes'");
            System.out.print("Kérem a következő parancsot: ");
            String command = scanner.nextLine();

            // Parancsok vizsgálata
            if (command.equalsIgnoreCase("kilepes")) {
                System.out.println("Játék befejezése... Viszlát, " + playerName + "!");
                dbService.printLeaderboard();
                break;
            } else if (command.equalsIgnoreCase("mentes")) {
                xmlService.saveGame(board, SAVE_FILE);
                System.out.println(">>> Játékállás sikeresen elmentve!");
                continue;
            } else if (command.equalsIgnoreCase("betoltes")) {
                Board loadedBoard = xmlService.loadGame(SAVE_FILE);
                if (loadedBoard != null) {
                    board = loadedBoard;
                    System.out.println(">>> Játékállás sikeresen betöltve!");
                } else {
                    System.out.println(">>> Hiba: Nem található mentett játékállás.");
                }
                continue;
            }

            // Ha nem a fenti parancsok egyike volt, akkor lépésként értelmezzük
            Board nextBoard = moveService.processMove(board, command);
            if (nextBoard == board) continue;
            board = nextBoard;

            if (checkWinCondition(board, 'A', board.getCols())) {
                printer.printBoard(board);
                System.out.println("Gratulálok, " + playerName + "! Kijuttattad a járművet, TE NYERTÉL!");
                dbService.saveWin(playerName, "Ember");
                dbService.printLeaderboard();
                break;
            }

            board = aiService.playAiTurn(board);

            if (checkWinCondition(board, 'B', 0)) {
                printer.printBoard(board);
                System.out.println("Sajnos a gép kivezette a járművét. A GÉP NYERT!");
                dbService.saveWin(playerName, "Gép");
                dbService.printLeaderboard();
                break;
            }
        }
        scanner.close();
    }

    private boolean checkWinCondition(Board board, char targetId, int exitCol) {
        return board.getVehicles().stream()
                .filter(v -> v.getId() == targetId)
                .anyMatch(v -> {
                    if (exitCol == 0 && v.getPosition().getCol() == 0) return true;
                    return exitCol > 0 && v.getPosition().getCol() + v.getLength() == exitCol;
                });
    }
}