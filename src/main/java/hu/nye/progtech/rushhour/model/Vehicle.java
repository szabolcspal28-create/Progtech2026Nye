package hu.nye.progtech.rushhour.model;

import java.util.Objects;

public final class Vehicle {
    private final char id;
    private final Position position;
    private final Orientation orientation;
    private final int length;

    public Vehicle(char id, Position position, Orientation orientation, int length) {
        this.id = id;
        this.position = position;
        this.orientation = orientation;
        this.length = length;
    }

    public char getId() { return id; }
    public Position getPosition() { return position; }
    public Orientation getOrientation() { return orientation; }
    public int getLength() { return length; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id && length == vehicle.length &&
                Objects.equals(position, vehicle.position) &&
                orientation == vehicle.orientation;
    }

    @Override
    public int hashCode() { return Objects.hash(id, position, orientation, length); }

    @Override
    public String toString() {
        return "Vehicle{id=" + id + ", position=" + position +
                ", orientation=" + orientation + ", length=" + length + '}';
    }
}