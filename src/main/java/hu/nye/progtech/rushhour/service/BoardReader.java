package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Vehicle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BoardReader {
    public Board readBoard(String fileName) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            throw new IllegalArgumentException("Nem található a fájl: " + fileName);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String firstLine = reader.readLine();
            String[] dimensions = firstLine.split(" ");
            int rows = Integer.parseInt(dimensions[0]);
            int cols = Integer.parseInt(dimensions[1]);

            List<Vehicle> vehicles = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" ");
                char id = parts[0].charAt(0);
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                Orientation orientation = Orientation.valueOf(parts[3]);
                int length = Integer.parseInt(parts[4]);

                vehicles.add(new Vehicle(id, new Position(row, col), orientation, length));
            }

            return new Board.Builder()
                    .withRows(rows)
                    .withCols(cols)
                    .withVehicles(vehicles)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Hiba a pálya beolvasásakor", e);
        }
    }
}