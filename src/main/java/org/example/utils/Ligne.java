package org.example.utils;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public class Ligne {
    private Point p1;
    private Point p2;
    private int ligneType = 0;

    public Ligne() {

    }

    public Ligne(Point p1, Point p2) {
//        this.p1 = p1;
//        this.p2 = p2;

        double angle = Math.abs(Geometrie.calculerAngle(p1, p2));
//        System.out.println(angle);
        double tolerance = 5;


        if(Math.abs(0 - angle) <= tolerance || Math.abs(180 - angle) <= tolerance) {
            this.setLigneType(Constante.LIGNE_HORIZONTAL);

            if(p1.x <= p2.x) {
                this.setP1(p1);
                this.setP2(p2);

            } else {
                this.setP1(p2);
                this.setP2(p1);
            }

//            System.out.println("Horizontal");

        } else if(Math.abs(90 - angle) <= tolerance || Math.abs(270 - angle) <= tolerance) {
            this.setLigneType(Constante.LIGNE_VERTICAL);

            if(p1.y <= p2.y) {
                this.setP1(p1);
                this.setP2(p2);

            } else {
                this.setP1(p2);
                this.setP2(p1);
            }

//            System.out.println("Vertical");

        } else {
            this.setLigneType(Constante.LIGNE_OBLIQUE);
        }
    }

    public boolean egal(Ligne autreLigne, double tolerance) {
        if(this.getLigneType() != autreLigne.getLigneType()) return false;

        double distance = Geometrie.distance(this.p1, autreLigne.p1) + Geometrie.distance(this.p2, autreLigne.p2);
        return  distance <= tolerance;
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public int getLigneType() {
        return ligneType;
    }

    public void setLigneType(int ligneType) {
        this.ligneType = ligneType;
    }

    @Override
    public String toString() {
        return "Ligne{" +
                "p1=" + p1 +
                ", p2=" + p2 +
                ", ligneType=" + ligneType +
                '}';
    }
}
