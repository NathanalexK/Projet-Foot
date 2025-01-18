package org.example.utils;

import org.opencv.core.Point;

public class Geometrie {
    public static Double distance(Point point1, Point point2) {
        return Math.sqrt(
                Math.pow(point2.x - point1.x, 2) +
                Math.pow(point2.y - point1.y, 2)
        );
    }

    public static Double distance(double x1, double x2) {
        return Math.abs(x1 - x2);
    }
}
