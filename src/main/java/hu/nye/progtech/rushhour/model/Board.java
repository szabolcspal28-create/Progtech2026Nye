package hu.nye.progtech.rushhour.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Board {
    private final int rows;
    private final int cols;
    private final List<Vehicle> vehicles;

    private Board(Builder builder) {
        this.rows = builder.rows;
        this.cols = builder.cols;
        this.vehicles = List.copyOf(builder.vehicles);
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public List<Vehicle> getVehicles() { return vehicles; }

    public static class Builder {
        private int rows;
        private int cols;
        private List<Vehicle> vehicles = new ArrayList<>();

        public Builder withRows(int rows) { this.rows = rows; return this; }
        public Builder withCols(int cols) { this.cols = cols; return this; }
        public Builder withVehicles(List<Vehicle> vehicles) {
            this.vehicles = new ArrayList<>(vehicles);
            return this;
        }
        public Board build() { return new Board(this); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return rows == board.rows && cols == board.cols &&
                Objects.equals(vehicles, board.vehicles);
    }

    @Override
    public int hashCode() { return Objects.hash(rows, cols, vehicles); }

    @Override
    public String toString() {
        return "Board{rows=" + rows + ", cols=" + cols + ", vehicles=" + vehicles + '}';
    }
}