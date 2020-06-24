package com.company.gui;

public class Point {
    public int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void addVector(Vector vector) {
        x += vector.x;
        y += vector.y;
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", x,y);
    }
}
