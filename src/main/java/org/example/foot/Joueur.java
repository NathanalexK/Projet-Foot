package org.example.foot;

import org.example.utils.Constante;
import org.opencv.core.Point;

public class Joueur {
    private Point position;
    private Equipe equipe;
    private boolean estGardien = false;
    private boolean estHorsJeu;
    private int taille;

//    public double getYLimite() {
//        if(this.getEquipe() == null) {
//
//        }
//    }

    public boolean isBehind(Joueur j) {
        String camp = this.getEquipe().getCamp();

        if(camp.equalsIgnoreCase(Constante.CAMP_HAUT)) {
            return this.getPosition().y < j.getPosition().y;
        }
        return this.getPosition().y > j.getPosition().y;
    }


    @Override
    public String toString() {
        return this.getPosition() + " - " + this.getEquipe().getNom();
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public boolean isEstGardien() {
        return estGardien;
    }

    public void setEstGardien(boolean estGardien) {
        this.estGardien = estGardien;
    }

    public int getTaille() {
        return taille;
    }

    public void setTaille(int taille) {
        this.taille = taille;
    }

    public boolean isEstHorsJeu() {
        return estHorsJeu;
    }

    public void setEstHorsJeu(boolean estHorsJeu) {
        this.estHorsJeu = estHorsJeu;
    }
}
