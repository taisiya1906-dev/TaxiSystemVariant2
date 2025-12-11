package model;

import java.time.LocalDateTime;

public class Order {
    private final int id;
    private final int fromX;
    private final int fromY;
    private final int toX;
    private final int toY;
    private final LocalDateTime creationTime;

    public Order(int id, int fromX, int fromY, int toX, int toY) {
        this.id = id;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.creationTime = LocalDateTime.now();
    }

    public int getId() { return id; }
    public int getFromX() { return fromX; }
    public int getFromY() { return fromY; }
    public int getToX() { return toX; }
    public int getToY() { return toY; }
    public LocalDateTime getCreationTime() { return creationTime; }

    public int calculateDistance() {
        return Math.abs(toX - fromX) + Math.abs(toY - fromY);
    }

    @Override
    public String toString() {
        return "Order #" + id + " from (" + fromX + "," + fromY + ") to (" + toX + "," + toY + ")";
    }
}