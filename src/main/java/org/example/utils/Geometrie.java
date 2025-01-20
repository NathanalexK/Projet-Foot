package org.example.utils;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

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

    public static boolean estDansRectange(Point p, Point pMin, Point pMax) {
        // (xMin, yMin) < (x,y) < (xMax, yMax)
        int xMin = (int)pMin.x, xMax = (int)pMax.x, yMin = (int)pMin.y, yMax = (int)pMax.y;
        int x = (int)p.x, y = (int) p.y;
        System.out.println(String.format("xMin = %s, xMax = %s, yMin = %s, yMax = %s, x = %s, y = %s", xMin, xMax, yMin, yMax, x, y));
        return xMin < x && x < xMax && yMin < y && y < yMax;
    }

    public static double calculerAngle(Point p1, Point p2) {
        // Calculate differences
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;

        // Calculate angle in radians
        double angleRadians = Math.atan2(dy, dx);

        // Convert to degrees (optional)
        double angleDegrees = Math.toDegrees(angleRadians);

        return angleDegrees; // Returns angle in degrees
    }

    public static List<Ligne> enleverDoublons(List<Ligne> lignes) {
        List<Ligne> list = new ArrayList<>();

        for(Ligne ligne: lignes) {
            boolean estPareil = false;
            for(Ligne autreLigne: list) {
                if(ligne.egal(autreLigne, 20)) {
                    estPareil = true;
                    break;
                }
            }

            if(!estPareil) {
                list.add(ligne);
            }
        }

        return list;
    }
}
