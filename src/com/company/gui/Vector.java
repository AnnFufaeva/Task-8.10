package com.company.gui;

public class Vector extends Point {
    public Vector(int x, int y) {
        super(x, y);
    }

    public void addToPoint(Point point) {
        point.x += super.x;
        point.y += super.y;
    }
}
