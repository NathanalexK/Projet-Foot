package org.example.foot;

import org.example.utils.Constante;
import org.example.utils.Geometrie;
import org.example.utils.Rectangle;
import org.opencv.core.Point;

import java.sql.SQLOutput;

public class Set {
    private Jeu avantTir;
    private Jeu apresTir;
    private Joueur tireur;
    private Match match;

    public Set(Match match) {
        setMatch(match);
    }

    public Set(String imageAvantTir, String imageApresTir) throws Exception {
        setAvantTir(new Jeu(imageAvantTir, Constante.AVANT_TIR));
        setApresTir(new Jeu(imageApresTir, Constante.APRES_TIR));
    }

    public Jeu getAvantTir() {
        return avantTir;
    }

    public void setAvantTir(Jeu avantTir) {
        this.avantTir = avantTir;
    }

    /*
        Null si aucun equipe marque
     */
    public Equipe getEquipeMarquant()  {
        System.out.println("Ballon Apres Tir: " + this.getPositionBallonApresTir());

        Point ballonApresTir = this.getPositionBallonApresTir();
        Rectangle[] rectangles = this.getApresTir().getCages();
        boolean estButCampHaut = rectangles[0].enclose(this.getPositionBallonApresTir());
        boolean estButCampBas = rectangles[1].enclose(this.getPositionBallonApresTir());



        if(estButCampHaut) {
            System.out.println("But Haut");
            Joueur gardien = this.getApresTir().getGardien(Constante.CAMP_HAUT);
            int rayon = gardien.getTaille();
            double  xMin = gardien.getPosition().x - rayon,
                    xMax = gardien.getPosition().x + rayon,
                    yMin = gardien.getPosition().y + rayon - 1,
                    yMax = gardien.getPosition().y + (3 * rayon) + 1;

            if(xMin <= ballonApresTir.x && ballonApresTir.x <= xMax && yMin <= ballonApresTir.y && ballonApresTir.y <= yMax) {
                try {
                    this.getMatch().ajouterArret(gardien.getEquipe());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                throw new RuntimeException("Arret Ballon");
            }
        }

        if(estButCampBas) {
            System.out.println("But Bas");
            Joueur gardien = this.getApresTir().getGardien(Constante.CAMP_BAS);
            System.out.println("Gardien: " + gardien.getPosition() + " rayon: " + gardien.getTaille());
            System.out.println("Ballon: " + this.getApresTir());
            int rayon = gardien.getTaille();
            double  xMin = gardien.getPosition().x - rayon,
                    xMax = gardien.getPosition().x + rayon,
                    yMax = gardien.getPosition().y - rayon - 1,
                    yMin = gardien.getPosition().y - (3 * rayon) - 1;

            if(xMin <= ballonApresTir.x && ballonApresTir.x <= xMax && yMin <= ballonApresTir.y && ballonApresTir.y <= yMax) {
                System.out.println("Arret");
                try {
                    this.getMatch().ajouterArret(gardien.getEquipe());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                throw new RuntimeException("Arret Ballon");
            }
        }


//        Point[][] cages = this.getApresTir().getCages();
//        boolean estButCampHaut = Geometrie.estDansRectange(this.getPositionBallonApresTir(), cages[0][0], cages[0][1]);
//        boolean estButCampBas = Geometrie.estDansRectange(this.getPositionBallonApresTir(), cages[1][0], cages[1][1]);

        if(!estButCampHaut && !estButCampBas) return null;

        System.out.println("Ballon 1: " + this.getAvantTir().getBallon());
        System.out.println("Ballon 2: " + this.getApresTir().getBallon());

        Joueur tireur = this.getTireur();
        System.out.println("HJ: "+ tireur.isEstHorsJeu());
        if(tireur.isEstHorsJeu()) throw new RuntimeException("Le Joueur est Hors Jeu");
        System.out.println(tireur);
        System.out.println("Camp: " + tireur.getEquipe().getCamp());

        if(estButCampHaut && tireur.getEquipe().getCamp().equalsIgnoreCase(Constante.CAMP_BAS)) {
            return tireur.getEquipe();
        }

        if(estButCampBas && tireur.getEquipe().getCamp().equalsIgnoreCase(Constante.CAMP_HAUT)) {
            return tireur.getEquipe();
        }

        return null;
    }

    public Jeu getApresTir() {
        return apresTir;
    }

    public void setApresTir(Jeu apresTir) {
        this.apresTir = apresTir;
    }

    public Joueur getTireur() {
        if(tireur == null) {
            this.tireur = this.getAvantTir().getPossesseurBallon();
        }

        return tireur;
    }

    public Point getPositionBallonApresTir() {
        return this.getApresTir().getBallon();
    }

    public void setTireur(Joueur tireur) {
        this.tireur = tireur;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
