package com.example.pathmapper;

public class Coordinates {

    private double xLong;
    private double yLat;

    public Coordinates (double x, double y){
        this.xLong = x;
        this.yLat = y;
    }
    // X
    public double getLong() {
        return xLong;
    }
    // Y
    public double getLat() {
        return yLat;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + xLong +
                ", y=" + yLat +
                '}';
    }
}
