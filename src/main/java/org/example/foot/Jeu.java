package org.example.foot;

import org.example.utils.Constante;
import org.example.utils.Geometrie;
import org.example.utils.Rectangle;
import org.example.utils.TraitementImage;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Jeu {
    private Terrain terrain;
    private Rectangle[] cages;
    private Point ballon;
    private List<Equipe> equipes;
    private Joueur pocesseurBallon;
    private Joueur dernierDefenseur = null;
    private int typeJeu;

    private Mat image = null;

    public Jeu() {

    }

    public Jeu(String imagePath) throws Exception {
        this.setEquipes(new ArrayList<>());
        Mat image = Imgcodecs.imread(imagePath);
        this.image = image;
        TraitementImage traitementImage = new TraitementImage(imagePath);
        List<TraitementImage.CircleInfo> listeCercles = traitementImage.getAllCercles();
        Terrain t = new Terrain();
        t.setHauteur((int)image.size().height);
        t.setLargeur((int) image.size().width);
        setTerrain(t);

        List<Rectangle> rectangles = traitementImage.getRectangles();
        if(rectangles.size() != 2) {
            throw new Exception("Nombre de cage doit etre 2, provenu: " + rectangles.size());
        }

        this.cages = rectangles.toArray(new Rectangle[0]);
//        this.cages[0] = rectangles.get(0);
//        this.cages[1] = rectangles.get(1);

        for(TraitementImage.CircleInfo circleInfo: listeCercles) {
            Joueur j = new Joueur();
            if(circleInfo.color.equalsIgnoreCase("noir")) {
                Point ballon = circleInfo.center;
                System.out.println("ballon: " + ballon);
                this.setBallon(ballon);

            } else if(circleInfo.color.equalsIgnoreCase("rouge")) {
                j.setPosition(circleInfo.center);
                j.setTaille(circleInfo.radius);
                this.getEquipeByNom("rouge").addJoueur(j);

            } else if(circleInfo.color.equalsIgnoreCase("bleu")) {
//                Joueur j = new Joueur();
                j.setPosition(circleInfo.center);
                j.setTaille(circleInfo.radius);
                this.getEquipeByNom("bleu").addJoueur(j);
            }

        }

        this.makeCampEquipes();
//        this.makeJoueursHorsJeu();
    }

    public Jeu(String imagePath, int typeJeu) throws Exception {
        this(imagePath);
        if(typeJeu == Constante.JEU_AVANT_TIR) {
            this.makeJoueursHorsJeu();

        }
    }


    public Joueur getPossesseurBallon() {
        if(ballon == null) {
//            System.out.println("Aucun ballon trouvee");
            throw new RuntimeException("Aucun ballon trouvee");
        }

        if(this.pocesseurBallon == null) {
            Joueur possesseur = null;
            Double minDistance = Double.MAX_VALUE;

            for(Joueur joueur: this.getJoueurs()) {
                double distance = Geometrie.distance(joueur.getPosition(), ballon);
//                double distance = new Point2D.Double(joueur.getPosition().x, joueur.getPosition().y).distance(new Point2D.Double(ballon.x, ballon.y));
                System.out.println(joueur + " distance: " + distance);

                if(minDistance > distance) {
                    minDistance = distance;
                    possesseur = joueur;
                }
            }
            this.setPocesseurBallon(possesseur);
        }


        return this.pocesseurBallon;
    }

    public void makeCampEquipes() {
        Equipe equipe1 = this.getEquipeByNom("bleu");
        Equipe equipe2 = this.getEquipeByNom("rouge");

        Joueur[] jExtreme1 = equipe1.getJoueursExtreme();
        Joueur[] jExtreme2 = equipe2.getJoueursExtreme();

        if(jExtreme1[0].getPosition().y < jExtreme2[0].getPosition().y) {
            jExtreme1[0].setEstGardien(true);
            jExtreme2[1].setEstGardien(true);
            equipe1.setCamp(Constante.CAMP_HAUT);
            equipe2.setCamp(Constante.CAMP_BAS);

        } else {
            jExtreme1[1].setEstGardien(true);
            jExtreme2[0].setEstGardien(true);
            equipe1.setCamp(Constante.CAMP_BAS);
            equipe2.setCamp(Constante.CAMP_HAUT);
        }
    }

    public void makeJoueursHorsJeu() {
        Joueur possesseur = this.getPossesseurBallon();

        if(possesseur == null) {
            System.out.println("Aucun joueur possede le ballon");
            return;
        }

        Equipe equipePossesseur = possesseur.getEquipe();
        Equipe autreEquipe = this.getAutreEquipe(equipePossesseur);
        Joueur dernierDefenseur = autreEquipe.getDernierDefenseur();
        this.dernierDefenseur = dernierDefenseur;
        System.out.println(dernierDefenseur.getPosition());

        if(equipePossesseur.getCamp().equalsIgnoreCase(Constante.CAMP_HAUT)) {
//            Joueur jExtreme = autreEquipe.getJoueursExtreme()[1];

            for(Joueur j: equipePossesseur.getJoueurs()) {
//                if(!j.equals(this.getPossesseurBallon()) && j.getPosition().y > dernierDefenseur.getPosition().y + dernierDefenseur.getTaille()) {
                if(true && j.getPosition().y > dernierDefenseur.getPosition().y + dernierDefenseur.getTaille()) {

                        System.out.println(true);
                    j.setEstHorsJeu(true);
                }
            }

        } else if(equipePossesseur.getCamp().equalsIgnoreCase(Constante.CAMP_BAS)) {
//            Joueur jExtreme = autreEquipe.getJoueursExtreme()[0];

            for(Joueur j: equipePossesseur.getJoueurs()) {
//                if(!j.equals(this.getPossesseurBallon()) && j.getPosition().y < dernierDefenseur.getPosition().y - dernierDefenseur.getTaille()) {
                if(true && j.getPosition().y < dernierDefenseur.getPosition().y - dernierDefenseur.getTaille()) {

                    System.out.println(true);
                    j.setEstHorsJeu(true);
                }
            }

        } else {
            System.out.println("Aucun Camp pour l'equipe Pocesseur: " + equipePossesseur.getNom());
        }
    }

    public List<Joueur> getJoueursHorsJeu() {
        List<Joueur> list = new ArrayList<>();

        for(Joueur j: this.getJoueurs()) {
            if(j.isEstHorsJeu()) {
                list.add(j);
            }
        }

        return list;
    }


    public void rewriteImage(String outputPath) {
        if(this.image == null) {
            System.out.println("No Image Found");
            return;
        }
//        for(Joueur j: this.getJoueursHorsJeu()) {
//            System.out.println("OKKKK");
//            Imgproc.putText(image, "HJ", j.getPosition(),
//                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 0), 1);
//        }

        double limite = this.dernierDefenseur.getEquipe().getLimite();
//        if(this.getTerrain() != null) {
            Imgproc.line(image, new Point(0, limite) , new Point(terrain.getLargeur(), limite) ,  new Scalar(0, 0, 0), 1);

//        }

        for(Joueur j: this.getJoueurs()) {
            if(j.isEstHorsJeu()) {
                Imgproc.putText(image, "HJ", j.getPosition(), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 0), 1);
                Imgproc.circle(image, j.getPosition(), j.getTaille() + 5 , new Scalar(255, 0, 0), 2);
            }
            else if(j.isEstGardien()) {
                Imgproc.putText(image, "Gardien", j.getPosition(), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 0), 1);

            } else {
//                Imgproc.putText(image, j.getEquipe().getNom(), j.getPosition(), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 0), 1);

            }

            Imgproc.putText(image, "Possesseur", this.getPossesseurBallon().getPosition(), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 0), 1);


        }

        for(Joueur j: this.getPossesseurBallon().getEquipe().getJoueurs()) {
            if(!j.isEstHorsJeu() && !j.isEstHorsJeu() && !j.equals(pocesseurBallon) && pocesseurBallon.isBehind(j)) {
                Imgproc.putText(image, "N", j.getPosition(), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 0), 1);
                Imgproc.arrowedLine(image, pocesseurBallon.getPosition(), j.getPosition(), new Scalar(0, 0, 0), 1);
            }
        }



        Imgcodecs.imwrite(outputPath, image);
    }

    public Joueur getGardien(String camp) {
        return this.getEquipes().stream().filter(e -> e.getCamp().equalsIgnoreCase(camp)).findFirst().get().getGardien();
    }

//    public void makeGardienEquipes() {
//
//    }









//    public List<Joueur> getJoueursHorsJeu() {
//
//
//        //TODO: Implementer
//        return null;
//    }



    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public Point getBallon() {
        return ballon;
    }

    public void setBallon(Point ballon) {
        this.ballon = ballon;
    }

    public List<Equipe> getEquipes() {
        return equipes;
    }

    public void setEquipes(List<Equipe> equipes) {
        this.equipes = equipes;
    }

    public Equipe getEquipeByNom(String nom) {
        for(Equipe equipe: this.getEquipes()) {
            if(equipe.getNom().equalsIgnoreCase(nom)) {
                return equipe;
            }
        }

        Equipe equipe = new Equipe();
        equipe.setNom(nom);
        this.getEquipes().add(equipe);
        return equipe;
    }

    public Equipe getAutreEquipe(Equipe current) {
        for(Equipe e: this.getEquipes()) {
            if(!e.getNom().equalsIgnoreCase(current.getNom())) {
                return e;
            }
        }
        return null;
    }

    public List<Joueur> getJoueurs() {
        List<Joueur> joueurs = new ArrayList<>();

        for(Equipe e: this.getEquipes()) {
            joueurs.addAll(e.getJoueurs());
        }

        return joueurs;
    }

    public Joueur getPocesseurBallon() {
        return pocesseurBallon;
    }

    public void setPocesseurBallon(Joueur pocesseurBallon) {
        this.pocesseurBallon = pocesseurBallon;
    }

    public Mat getImage() {
        return image;
    }

    public void setImage(Mat image) {
        this.image = image;
    }

    public Rectangle[] getCages() {
        return cages;
    }

    public void setCages(Rectangle[] cages) {
        this.cages = cages;
    }

    //    public Point[][] getCages() {
//        return cages;
//    }
//
//    public void setCages(Point[][] cages) {
//        this.cages = cages;
//    }
}
