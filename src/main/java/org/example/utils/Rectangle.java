package org.example.utils;

import org.opencv.core.Point;

import java.util.List;

public class Rectangle {
    private Point pMin;
    private Point pMax;
    private Integer longueur;
    private Integer largeur;

    public Rectangle(Point pMin, Point pMax) {
        setpMin(pMin);
        setpMax(pMax);
    }

    public Rectangle(Ligne horizontal, List<Ligne> verticales) throws Exception {
        Ligne[] compatibles = new Ligne[]{null, null};
        double tolerance = 100;

        for(Ligne verticale: verticales) {
            if(Geometrie.distance(horizontal.getP1(), verticale.getP1()) <= tolerance) {
                compatibles[0] = verticale;
            }
            if(Geometrie.distance(horizontal.getP1(), verticale.getP2()) <= tolerance) {
                compatibles[0] = verticale;
            }
            if(Geometrie.distance(horizontal.getP2(), verticale.getP1()) <= tolerance) {
                compatibles[1] = verticale;
            }
            if(Geometrie.distance(horizontal.getP2(), verticale.getP2()) <= tolerance) {
                compatibles[1] = verticale;
            }
        }

        if(compatibles[0] == null || compatibles[1] == null) throw new Exception("Impossible de construire un rectangle a partir des lignes");

        setpMin(compatibles[0].getP1());
        setpMax(compatibles[1].getP2());
    }

    public Rectangle(Ligne horizontal, char emplacement) {
        if(emplacement == 'h') {
            setpMin(new Point(horizontal.getP1().x, 0));
            setpMax(horizontal.getP2());
        }

        else {
            setpMin(horizontal.getP1());
            setpMax(new Point(horizontal.getP2().x, horizontal.getP1().y + 500));
        }
    }

    public boolean enclose(Point p) {
//        System.out.println(Geometrie.estDansRectange(p, pMin, pMax));
        return Geometrie.estDansRectange(p, pMin, pMax);
    }

    public Point getpMin() {
        return pMin;
    }

    public void setpMin(Point pMin) {
        this.pMin = pMin;
    }

    public Point getpMax() {
        return pMax;
    }

    public void setpMax(Point pMax) {
        this.pMax = pMax;
    }

    public int getLongueur() {
        if(longueur == null) {
            setLongueur((int) (pMax.x - pMin.x));
        }
        return longueur;
    }

    public void setLongueur(int longueur) {
        this.longueur = longueur;
    }

    public int getLargeur() {
        if(largeur == null) {
            setLargeur((int)(pMax.y - pMin.y));
        }
        return largeur;
    }

    public void setLargeur(int largeur) {
        this.largeur = largeur;
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "pMin=" + pMin +
                ", pMax=" + pMax +
                ", longueur=" + this.getLongueur() +
                ", largeur=" + this.getLargeur() +
                '}';
    }
}
