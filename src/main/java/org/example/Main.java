package org.example;


//import org.bytedeco.opencv.;
//import org.ope
import org.example.components.MainWindow;
import org.example.components.MainWindow_2;
import org.example.foot.Jeu;
import org.example.foot.Match;
import org.example.utils.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        System.out.println("OpenCV version: " + Core.VERSION);

        String imagePath = "/Users/macbook/Documents/foot/foot11.jpg";
//        String imagePath = "/Users/macbook/Downloads/0.JPG";

        Mat image = Imgcodecs.imread(imagePath);

        if(!image.empty()){
            System.out.println("Success");

        } else {
            System.out.println("Error");
        }


        SwingUtilities.invokeLater(() -> {
            MainWindow_2 mainWindow2 = null;
            try {
                mainWindow2 = new MainWindow_2(new Match());
                mainWindow2.setVisible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

        Mat gray = new Mat();

        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);


        Imgproc.GaussianBlur(gray, gray, new Size(9, 9), 0);

        // Threshold and invert if necessary
        Mat binary = new Mat();
        Core.bitwise_not(gray, gray);
        Imgproc.threshold(gray, binary, 50, 255, Imgproc.THRESH_BINARY);

        // Detect edges
        Mat edges = new Mat();
        Imgproc.Canny(binary, edges, 50, 150);

        // Detect lines
        Mat lines = new Mat();
        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 50);

        List<Ligne> lignes = new ArrayList<>();
        // Draw lines on the image
        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
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
        for(Ligne horizontal: horizontales) {
            try {
                Rectangle rectangle = new Rectangle(horizontal, verticales);
                System.out.println(rectangle);
                rectangles.add(rectangle);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        // Save the result
        Imgcodecs.imwrite("test4.jpg", image);

    }
}