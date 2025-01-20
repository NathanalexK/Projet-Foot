package org.example.utils;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.sound.sampled.Line;
import java.util.*;

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
        if ((h >= 0 && h <= 10) || (h >= 160 && h <= 180) || ((h > 110 && h < 120 && s > 225 && v > 240))) {
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
        Imgproc.GaussianBlur(image, blurredImage, new Size(5, 5), 0);

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
        Imgproc.findContours(blueMask, blueContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.findContours(redMask, redContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint mop : blueContours) {
            MatOfPoint2f contour2f = new MatOfPoint2f(mop.toArray());

            Point center = new Point();
            float[] radius = new float[1];

            Imgproc.minEnclosingCircle(contour2f, center, radius);
            CircleInfo circleInfo = new CircleInfo(center, (int) radius[0], "Bleu");
            System.out.println("B");
            circles.add(circleInfo);
        }

        for (MatOfPoint mop : redContours) {
            MatOfPoint2f contour2f = new MatOfPoint2f(mop.toArray());

            Point center = new Point();
            float[] radius = new float[1];

            Imgproc.minEnclosingCircle(contour2f, center, radius);
            CircleInfo circleInfo = new CircleInfo(center, (int) radius[0], "Rouge");
            circles.add(circleInfo);
            System.out.println("R");

        }

        for (MatOfPoint mop : blackContours) {
            MatOfPoint2f contour2f = new MatOfPoint2f(mop.toArray());

            Point center = new Point();
            float[] radius = new float[1];

            Imgproc.minEnclosingCircle(contour2f, center, radius);
            CircleInfo circleInfo = new CircleInfo(center, (int) radius[0], "Noir");
            System.out.println("Centre: " + center);
            circles.add(circleInfo);
            System.out.println("N");

        }

        return circles;
    }


    public void detectBlackRectangle() {
        // Load the image
//        Mat image = Imgcodecs.imread(imagePath);

        // Convert the image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Apply a binary threshold to isolate the black rectangle
        Mat thresholded = new Mat();
        Imgproc.threshold(gray, thresholded, 50, 255, Imgproc.THRESH_BINARY_INV);

        // Find contours in the thresholded image
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresholded, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        // Filter contours to find the rectangle and the shapes inside it
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint contour = contours.get(i);
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            double epsilon = 0.02 * Imgproc.arcLength(contour2f, true);
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(contour2f, approxCurve, epsilon, true);

            // Check if the contour has 4 vertices (rectangle)
            if (approxCurve.toArray().length == 4) {
                Rect rect = Imgproc.boundingRect(contour);

                // Draw the rectangle on the original image
                Imgproc.rectangle(image, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);

                // Print the position and dimensions of the rectangle
                System.out.println("Rectangle detected at: (" + rect.x + ", " + rect.y + ")");
                System.out.println("Dimensions: " + rect.width + "x" + rect.height);

                // Now, find the shapes inside the rectangle
                Mat roi = new Mat(thresholded, rect);
                List<MatOfPoint> innerContours = new ArrayList<>();
                Mat innerHierarchy = new Mat();
                Imgproc.findContours(roi, innerContours, innerHierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

                for (int j = 0; j < innerContours.size(); j++) {
                    MatOfPoint innerContour = innerContours.get(j);
                    Rect innerRect = Imgproc.boundingRect(innerContour);

                    // Draw the inner shapes on the original image
                    Imgproc.rectangle(image, new Point(rect.x + innerRect.x, rect.y + innerRect.y),
                            new Point(rect.x + innerRect.x + innerRect.width, rect.y + innerRect.y + innerRect.height),
                            new Scalar(255, 0, 0), 2);

                    // Print the position and dimensions of the inner shapes
                    System.out.println("Shape detected at: (" + (rect.x + innerRect.x) + ", " + (rect.y + innerRect.y) + ")");
                    System.out.println("Dimensions: " + innerRect.width + "x" + innerRect.height);
                }
            }
        }
    }


    public  List<Rect> detectRectanglesWith3Sides() {

        Mat gray = new Mat();
        Mat blurred = new Mat();
        Mat binary = new Mat();

        // Prétraitement
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);       // Conversion en niveaux de gris
        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);    // Réduction du bruit
        Imgproc.threshold(blurred, binary, 50, 255, Imgproc.THRESH_BINARY_INV); // Seuillage

        // Détection des contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Rect> detectedRectangles = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());

            // Approximations des contours
            double epsilon = 0.02 * Imgproc.arcLength(contour2f, true);
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(contour2f, approx, epsilon, true);
            System.out.println(approx.total());


            // Vérification : Polygone avec 3 ou 4 sommets
            if (approx.total() == 3) {
                // Vérifier si les côtés visibles correspondent à 3 côtés
                // Créer un rectangle englobant (bounding box)
                Rect rect = Imgproc.boundingRect(new MatOfPoint(approx.toArray()));
                double aspectRatio = (double) rect.width / rect.height;

                // Filtrer par aspect ratio ou autre logique
                if (aspectRatio > 0.5 && aspectRatio < 2.0) {
                    detectedRectangles.add(rect);
                }
            }
        }
        return detectedRectangles;
    }

    public List<Line> detectBlackLines() {
        // Charger l'image


        // Convertir en niveaux de gris
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Appliquer un seuillage binaire inversé (les lignes noires deviennent blanches)
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 50, 255, Imgproc.THRESH_BINARY_INV);

        Imgproc.GaussianBlur(binary, binary, new Size(5, 5), 0);


        // Détection des lignes avec la transformation de Hough
        Mat lines = new Mat();
        Imgproc.HoughLinesP(binary, lines, 1, Math.PI / 90, 50, 50, 10);

        // Liste pour stocker les lignes détectées
        List<Line> detectedLines = new ArrayList<>();

        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
            Point start = new Point(line[0], line[1]);
            Point end = new Point(line[2], line[3]);
            detectedLines.add(new Line(start, end));
            System.out.println(new Line(start, end));
            Imgproc.arrowedLine(image, start, end, new Scalar(40, 20, 30), 2);

        }
        Imgcodecs.imwrite("test3.jpg", image);

        return detectedLines;
    }

    public List<Point[]> detectBlackLine2() {
        // Charger l'image
//        Mat image = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_COLOR);

        // Convertir l'image en niveaux de gris
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Appliquer un seuil pour détecter les pixels noirs
        Mat blackMask = new Mat();
        Core.inRange(grayImage, new Scalar(0), new Scalar(50), blackMask);

        // Trouver les contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(blackMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Liste pour stocker les points extrêmes des lignes détectées
        List<Point[]> linePoints = new ArrayList<>();

        // Parcourir les contours et extraire les points extrêmes
        for (MatOfPoint contour : contours) {
            Rect boundingBox = Imgproc.boundingRect(contour);

            // Extraire les deux points extrêmes (coins opposés du rectangle de délimitation)
            Point topLeft = new Point(boundingBox.x, boundingBox.y);
            Point bottomRight = new Point(boundingBox.x + boundingBox.width, boundingBox.y + boundingBox.height);

            // Ajouter les points à la liste
            System.out.println(topLeft + " - " + bottomRight);
            Imgproc.arrowedLine(image, topLeft, bottomRight, new Scalar(40, 20, 30), 2);
            linePoints.add(new Point[] {topLeft, bottomRight});
        }
        Imgcodecs.imwrite("test2.jpg", image);
        return linePoints;
    }

    static class Line {
        Point start;
        Point end;

        public Line(Point start, Point end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "Line{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    public void detectLines() {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Appliquer un seuillage pour détecter les lignes noires
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 50, 255, Imgproc.THRESH_BINARY_INV);

        // Détecter les bords avec Canny
        Mat edges = new Mat();
        Imgproc.Canny(binary, edges, 50, 150);

        // Détecter les lignes avec la transformée de Hough
        Mat lines = new Mat();
        Imgproc.HoughLinesP(edges, lines, 1, Math.PI/180, 50, 0, 0);

        // Stocker les lignes détectées
        List<Line> detectedLines = new ArrayList<>();

        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
            Point pt1 = new Point(line[0], line[1]);
            Point pt2 = new Point(line[2], line[3]);
            Imgproc.line(image, pt1, pt2, new Scalar(50, 50, 25), 2);
            System.out.println("AAAA");
            detectedLines.add(new Line(pt1, pt2));
        }

        // Afficher les lignes détectées

        // Sauvegarder l'image avec les lignes dessinées
        Imgcodecs.imwrite("output_with_lines.jpg", image);
    }

    public List<Rectangle> getRectangles() {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        Scalar lowerBlack = new Scalar(0, 0, 0);
        Scalar upperBlack = new Scalar(180, 255, 50);

        Mat mask = new Mat();
        Core. inRange(gray, lowerBlack, upperBlack, mask);


        // Threshold and invert if necessary
        Mat binary = new Mat();
        Imgproc.GaussianBlur(mask, binary, new Size(5, 5), 0);

//        Core.bitwise_not(gray, gray);
//        Imgproc.threshold(gray, binary, 50, 255, Imgproc.THRESH_BINARY);

        // Detect edges
        Mat edges = new Mat();
        Imgproc.Canny(binary, edges, 50, 150);

        // Detect lines
        Mat lines = new Mat();

        //TODO: Changer threshold en 50
        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 50, 200, 10);

        List<Ligne> lignes = new ArrayList<>();
        // Draw lines on the image
        for (int i = 0; i < lines.rows(); i++) {
//            lines.get
            double[] line = lines.get(i, 0);
            System.out.println("ligne: " + Arrays.toString(line));
            Point pt1 = new Point(line[0], line[1]);
            Point pt2 = new Point(line[2], line[3]);
            Ligne ligne = new Ligne(pt1, pt2);
            lignes.add(ligne);
            Imgproc.line(image, pt1, pt2, new Scalar(0, 0, 255), 2);
//            System.out.println(pt1 + "-" + pt2);
        }

        lignes = Geometrie.enleverDoublons(lignes);

        for(Ligne ligne: lignes) {
            System.out.println(ligne);
        }

        List<Ligne> horizontales = lignes.stream().filter(l -> l.getLigneType() == Constante.LIGNE_HORIZONTAL).toList();
        List<Ligne> verticales = lignes.stream().filter(l -> l.getLigneType() == Constante.LIGNE_VERTICAL).toList();

        List<Rectangle> rectangles = new ArrayList<>();
//        for(Ligne horizontal: horizontales) {
//
//            try {
//                Rectangle rectangle = new Rectangle(horizontal, verticales);
//                System.out.println(rectangle);
//                rectangles.add(rectangle);
//            } catch (Exception e) {
//                System.err.println(e.getMessage());
//            }
//        }

        rectangles.add(new Rectangle(horizontales.get(0), horizontales.get(0).getP1().y < horizontales.get(1).getP1().y ? 'h' : 'b'));
        rectangles.add(new Rectangle(horizontales.get(1), horizontales.get(0).getP1().y > horizontales.get(1).getP1().y ? 'h' : 'b'));


        Imgproc.circle(image, new Point(580.5, 1720.5),  25 , new Scalar(255, 0, 0), 2);

        Imgcodecs.imwrite("output_with_lines2.jpg", image);

        return rectangles.stream().sorted((r1, r2) -> Double.compare(r1.getpMin().y, r2.getpMin().y)).toList();
    }



}
