package org.example.utils;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class TraitementImage {
    private Mat image;

    public TraitementImage(String imagePath) throws Exception {
        Mat imageTemp = Imgcodecs.imread(imagePath);
        if (imageTemp.empty()) {
            throw new Exception("Impossible de charger l' image: " + imageTemp);
        }
        this.image = imageTemp;
    }

    public TraitementImage(Mat image) {
        this.image = image;
    }

    public TraitementImage convertirEnNiveauDeGris() {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Appliquer un flou pour réduire le bruit
        Imgproc.GaussianBlur(gray, gray, new Size(9, 9), 2);

        // Détecter les cercles
        Mat circles = new Mat();
        Imgproc.HoughCircles(
                gray,
                circles,
                Imgproc.HOUGH_GRADIENT,
                1,
                gray.rows() / 8,
                100,
                30,
                0,
                0
        );
//        this.image = gray;
        return new TraitementImage(image);
    }

    public List<CircleInfo> getDetailsCercle() {
        List<CircleInfo> list = new ArrayList<>();
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(9, 9), 2);

        // Détecter les cercles
        Mat circles = new Mat();
        Imgproc.HoughCircles(
                gray,
                circles,
                Imgproc.HOUGH_GRADIENT,
                1,
                gray.rows() / 64,
                100,
                20,
                0,
                0
        );

        List<CircleInfo> circleList = new ArrayList<>();

        // Convertir l'image en espace HSV pour analyser les couleurs
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        // Parcourir les cercles détectés
        for (int i = 0; i < circles.cols(); i++) {
            double[] circleData = circles.get(0, i);
            if (circleData == null) continue;

            Point center = new Point(circleData[0], circleData[1]);
            int radius = (int) Math.round(circleData[2]);

            // Extraire la couleur au centre du cercle
            double[] hsv = hsvImage.get((int) center.y, (int) center.x);
            if (hsv == null) continue;

            String color = classifierCouleur(hsv);
            System.out.println(String.format("Couleur: %s, hsv(%s, %s, %s)", color, hsv[0], hsv[1], hsv[2]));
            circleList.add(new CircleInfo(center, radius, color));
        }

        return circleList;
    }


    public static class CircleInfo {
        public Point center;
        public int radius;
        public String color;

        public CircleInfo(Point center, int radius, String color) {
            this.center = center;
            this.radius = radius;
            this.color = color;
        }

        @Override
        public String toString() {
            return "Center: " + center + ", Radius: " + radius + ", Color: " + color;
        }
    }

    public static List<CircleInfo> detectBlueCircles(Mat image) {
        List<CircleInfo> circlesInfo = new ArrayList<>();

        // Convertir l'image en espace colorimétrique HSV pour une meilleure détection des couleurs
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        // Définir la plage de couleurs pour le bleu dans l'espace HSV
        Scalar lowerBlue = new Scalar(100, 50, 50); // Valeurs minimales pour le bleu
        Scalar upperBlue = new Scalar(140, 255, 255); // Valeurs maximales pour le bleu

        // Créer un masque pour les pixels bleus
        Mat blueMask = new Mat();
        Core.inRange(hsvImage, lowerBlue, upperBlue, blueMask);

        // Appliquer un flou pour réduire le bruit
        Imgproc.GaussianBlur(blueMask, blueMask, new Size(9, 9), 2, 2);

        // Détecter les cercles dans l'image
        Mat circles = new Mat();
        Imgproc.HoughCircles(blueMask, circles, Imgproc.HOUGH_GRADIENT, 1, blueMask.rows() / 8, 200, 100, 0, 0);

        // Parcourir les cercles détectés
        for (int i = 0; i < circles.cols(); i++) {
            double[] circle = circles.get(0, i);
            Point center = new Point(Math.round(circle[0]), Math.round(circle[1]));
            int radius = (int) Math.round(circle[2]);

            // Extraire la couleur moyenne du cercle
            Mat mask = new Mat(image.size(), CvType.CV_8UC1, new Scalar(0));
            Imgproc.circle(mask, center, radius, new Scalar(255), -1);
            Scalar meanColor = Core.mean(image, mask);

            // Ajouter les informations du cercle à la liste
            circlesInfo.add(new CircleInfo(center, radius, meanColor.toString()));
        }

        return circlesInfo;
    }


    public static List<CircleInfo> getCirclesWithColors(String imagePath) {
        // Charger l'image
        Mat image = Imgcodecs.imread(imagePath);
        if (image.empty()) {
            throw new IllegalArgumentException("Erreur: Impossible de charger l'image.");
        }

        // Convertir en niveaux de gris pour la détection des cercles
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(9, 9), 0);

        // Détecter les cercles
        Mat circles = new Mat();
        Imgproc.HoughCircles(
                gray,
                circles,
                Imgproc.HOUGH_GRADIENT,
                1,
                gray.rows() / 64,
                100,
                20,
                0,
                0
        );

        List<CircleInfo> circleList = new ArrayList<>();

        // Convertir l'image en espace HSV pour analyser les couleurs
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        // Parcourir les cercles détectés
        for (int i = 0; i < circles.cols(); i++) {
            double[] circleData = circles.get(0, i);
            if (circleData == null) continue;

            Point center = new Point(circleData[0], circleData[1]);
            int radius = (int) Math.round(circleData[2]);

            // Extraire la couleur au centre du cercle
            double[] hsv = hsvImage.get((int) center.y, (int) center.x);
            if (hsv == null) continue;

            String color = classifierCouleur(hsv);
            System.out.println(String.format("H: %s  S: %s  V: %s", hsv[0], hsv[1], hsv[2]));
            System.out.println("Couleur: " + color);
            circleList.add(new CircleInfo(center, radius, color));
        }

        return circleList;
    }



    private static String classifierCouleur(double[] hsv) {
//        double h = hsv[0], s = hsv[1], v = hsv[2];
//
//        if (v < 50) return "Noir"; // Couleur très sombre
//        if (h >= 0 && h <= 10 || h >= 160 && h <= 180) return "Rouge"; // Teinte rouge
//        if (h >= 100 && h <= 140) return "Bleu"; // Teinte bleue
//
//
//        return "Autre";
        double h = hsv[0];
        double s = hsv[1];
        double v = hsv[2];


        // Plage pour le noir (faible luminosité)
        if (v < 10) {
            return "Noir";
        }

        // Plage pour le rouge (teinte autour de 0 et 180)
        if ((h >= 0 && h <= 10) || (h >= 160 && h <= 180) || ((h > 110 && h < 120 && s > 225 && v > 240)  )) {
            if (s > 100) {
                return "Rouge";
            }
        }

        // Plage pour le bleu (inclut le bleu clair)
        if (h >= 60 && h <= 300) {
//            if (s > 50 && v > 70) {
                return "Bleu";
//            }
        }

        return "Autre";

    }

    public static void main(String[] args) {
        String imagePath = "chemin/vers/image.jpg"; // Remplacez par le chemin de votre image
        List<CircleInfo> circles = getCirclesWithColors(imagePath);

        for (CircleInfo circle : circles) {
            System.out.println(circle);
        }


    }


    public Mat getImage() {
        return image;
    }

    public List<CircleInfo> getAllCercles() {
        List<CircleInfo> circles = new ArrayList<>();
        Mat blurredImage = new Mat();

        Mat blueMask = new Mat();
        Scalar lowerBlue = new Scalar(90, 50, 50);
        Scalar upperBlue = new Scalar(140, 255, 255);
        Scalar lowerBlue2 = new Scalar(130, 50, 50);
        Scalar upperBlue2 = new Scalar(150, 255, 255);

        Scalar loweRed1 = new Scalar(0, 50, 50);
        Scalar upperRed1 = new Scalar(10, 255, 255);
        Scalar lowerRed2 = new Scalar(160, 50, 50);
        Scalar upperRed2 = new Scalar(180, 255, 255);
        Imgproc.GaussianBlur(image, blurredImage, new Size(5,5), 0);

        Mat hsvImage = new Mat();
        Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

        blueMask = new Mat();
        Mat blueMask1 = new Mat();
        Mat blueMask2 = new Mat();
        Core.inRange(hsvImage, lowerBlue, upperBlue, blueMask1);
        Core.inRange(hsvImage, lowerBlue2, upperBlue2, blueMask2);
        Core.bitwise_or(blueMask1, blueMask2, blueMask);

        Mat redMask1 = new Mat();
        Mat redMask2 = new Mat();
        Mat redMask = new Mat();

        Core.inRange(hsvImage, loweRed1, upperRed1, redMask1);
        Core.inRange(hsvImage, lowerRed2, upperRed2, redMask2);
        Core.bitwise_or(redMask1, redMask2, redMask);

        Mat blackMack = new Mat();
        Scalar lowerBlack = new Scalar(0, 0, 0);
        Scalar upperBlack = new Scalar(180, 255, 50);
        Core.inRange(hsvImage, lowerBlack, upperBlack, blackMack);

        List<MatOfPoint> blackContours = new ArrayList<>();
        List<MatOfPoint> blueContours = new ArrayList<>();
        List<MatOfPoint> redContours = new ArrayList<>();

        Imgproc.findContours(blackMack, blackContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(blueMask, blueContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE) ;
        Imgproc.findContours(redMask, redContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE) ;

        for(MatOfPoint mop: blueContours) {
            MatOfPoint2f contour2f = new MatOfPoint2f(mop.toArray());

            Point center = new Point();
            float[] radius = new float[1];

            Imgproc.minEnclosingCircle(contour2f, center, radius);
            CircleInfo circleInfo = new CircleInfo(center, (int)radius[0], "Bleu");
            circles.add(circleInfo);
        }

        for(MatOfPoint mop: redContours) {
            MatOfPoint2f contour2f = new MatOfPoint2f(mop.toArray());

            Point center = new Point();
            float[] radius = new float[1];

            Imgproc.minEnclosingCircle(contour2f, center, radius);
            CircleInfo circleInfo = new CircleInfo(center, (int)radius[0], "Rouge");
            circles.add(circleInfo);

        }

        for(MatOfPoint mop: blackContours) {
            MatOfPoint2f contour2f = new MatOfPoint2f(mop.toArray());

            Point center = new Point();
            float[] radius = new float[1];

            Imgproc.minEnclosingCircle(contour2f, center, radius);
            CircleInfo circleInfo = new CircleInfo(center, (int)radius[0], "Noir");
            circles.add(circleInfo);

        }




        return circles;

    }
}
