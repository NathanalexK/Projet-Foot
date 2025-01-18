package org.example.foot;

import org.example.utils.Constante;
import org.example.utils.Geometrie;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class Equipe {
    private String nom;
    private String camp;
    private List<Joueur> joueurs;
    Joueur gardien = null;

    public Equipe() {
        this.joueurs = new ArrayList<>();

    }

    public Joueur[] getJoueursExtreme() {
        double yMin = Double.MAX_VALUE;
        double yMax = Double.MIN_VALUE;

        Joueur joueurMin = null;
        Joueur joueurMax = null;

        for (Joueur joueur : this.getJoueurs()) {
            double yPos = joueur.getPosition().y;

            if (yMin > yPos) {
                yMin = yPos;
                joueurMin = joueur;
            }

            if (yMax < yPos) {
                yMax = yPos;
                joueurMax = joueur;
            }
        }

        return new Joueur[]{joueurMin, joueurMax};
    }

    public Joueur getDernierDefenseur() {
        if (this.getCamp() == null) {
            System.out.printf("Aucun camp pour l'équipe " + this.getNom());
        }

        Joueur gardien = this.getGardien();
        Joueur dernierDefenseur = null;
        double distanceMin = Double.MAX_VALUE;

        for (Joueur joueur : this.getJoueurs()) {
            System.out.println(joueur.getPosition());

            if (joueur.equals(gardien)) {
                continue;
            }

            double distance = Geometrie.distance(gardien.getPosition().y, joueur.getPosition().y) - joueur.getTaille();
            if (distanceMin > distance) {
                distanceMin = distance;
                dernierDefenseur = joueur;
            }
        }


//        Joueur[] jExtremes = this.getJoueursExtreme();
//        double yPosGardien;
//
//        if(this.getCamp().equalsIgnoreCase(Constante.CAMP_HAUT)){
//            yPosGardien = jExtremes[0].getPosition().y;
//            double yProcheGardien = Double.MAX_VALUE;
//
//            for(Joueur joueur: this.getJoueurs()) {
//                if(joueur.isEstGardien()) continue;
//
//                if(yProcheGardien > joueur.getPosition().y) {
//                    dernierDefenseur = joueur;
//                    yProcheGardien = joueur.getPosition().y;
//                }
//            }
//        }
//
//        else if(this.getCamp().equalsIgnoreCase(Constante.CAMP_BAS)) {
//            yPosGardien = jExtremes[1].getPosition().y;
//            double yProcheGardien = Double.MIN_VALUE;
//
//            for(Joueur joueur: this.getJoueurs()) {
//                if(joueur.isEstGardien()) continue;
//
//                if(yProcheGardien < joueur.getPosition().y) {
//                    dernierDefenseur = joueur;
//                    yProcheGardien = joueur.getPosition().y;
//                }
//            }
//        }

        return dernierDefenseur;
    }

    public Joueur getGardien() {
        if (this.gardien == null) {
            for (Joueur j : this.getJoueurs()) {
                if (j.isEstGardien()) {
                    this.setGardien(j);
                }
            }
        }

        return this.gardien;
    }

    public void setGardien(Joueur gardien) {
        this.gardien = gardien;
    }

    public Point getPositionDernierJoueur(Jeu j) {
        Joueur[] jExtremes = this.getJoueursExtreme();
        if (this.getCamp().equalsIgnoreCase(Constante.CAMP_HAUT)) {

        }


        //TODO

        return null;
    }

    public Double getLimite() {
        Joueur dernierDefenseur = this.getDernierDefenseur();
        if(this.getCamp().equalsIgnoreCase(Constante.CAMP_HAUT)) {
            return dernierDefenseur.getPosition().y - dernierDefenseur.getTaille();
        }
        return dernierDefenseur.getPosition().y + dernierDefenseur.getTaille();

    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    public void setJoueurs(List<Joueur> joueurs) {
        this.joueurs = joueurs;
    }

    public void addJoueur(Joueur j) {
        j.setEquipe(this);
        this.getJoueurs().add(j);
    }

    public String getCamp() {
        return camp;
    }

    public void setCamp(String camp) {
        this.camp = camp;
    }
}
