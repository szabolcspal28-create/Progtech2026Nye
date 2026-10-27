package hu.nye.progtech.rushhour.ui;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Vehicle;

public class BoardPrinter {
    public void printBoard(Board board) {
        int rows = board.getRows();
        int cols = board.getCols();
        char[][] grid = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
            }
        }

        for (Vehicle v : board.getVehicles()) {
            int r = v.getPosition().getRow();
            int c = v.getPosition().getCol();
            for (int i = 0; i < v.getLength(); i++) {
                switch (v.getOrientation()) {
                    case HORIZONTAL -> grid[r][c + i] = v.getId();
                    case VERTICAL -> grid[r + i][c] = v.getId();
                }
            }
        }

        System.out.println("\nAktuális játéktér:");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}