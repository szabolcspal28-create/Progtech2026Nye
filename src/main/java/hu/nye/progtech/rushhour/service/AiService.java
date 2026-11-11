package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Vehicle;

import java.util.List;
import java.util.Random;

public class AiService {
    private final MoveService moveService;
    private final Random random;

    public AiService(MoveService moveService) {
        this.moveService = moveService;
        this.random = new Random();
    }

    public Board playAiTurn(Board board) {
        List<Vehicle> vehicles = board.getVehicles();

        // Ha csak egy jármű van a pályán, a gép nem lé.
        if (vehicles.size() <= 1) {
            return board;
        }

        Board newBoard = board;
        int attempts = 0;
        int maxAttempts = 50;

        while (newBoard == board && attempts < maxAttempts) {
            Vehicle candidate = vehicles.get(random.nextInt(vehicles.size()));

            if (candidate.getId() == 'A') {
                attempts++;
                continue;
            }

            String dir = candidate.getOrientation() == Orientation.HORIZONTAL
                    ? (random.nextBoolean() ? "bal" : "jobb")
                    : (random.nextBoolean() ? "fel" : "le");

            int steps = random.nextInt(2) + 1;
            String command = candidate.getId() + " " + dir + " " + steps;

            newBoard = moveService.processMove(board, command);
            if (newBoard != board) {
                System.out.println("-> A gépi ellenfél lépése: " + command);
            }
            attempts++;
        }

        if (newBoard == board) {
            System.out.println("-> A gép passzol (nem talált érvényes lépést a limiten belül).");
        }

        return newBoard;
    }
}