package org.example;


//import org.bytedeco.opencv.;
//import org.ope
import org.example.components.MainWindow;
import org.example.foot.Jeu;
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

        String imagePath = "/Users/macbook/Documents/ITU/S5/archiLog/foot/src/main/java/org/example/foot2.jpg";
        Mat image = Imgcodecs.imread(imagePath);

        if(!image.empty()){
            System.out.println("Success");

        } else {
            System.out.println("Error");
        }


        Mat blurredImage = new Mat();

        Mat blueMask = new Mat();
        Scalar lowerBlue = new Scalar(100, 150, 50);
        Scalar upperBlue = new Scalar(140, 255, 255);
        Scalar loweRed1 = new Scalar(0, 50, 50);
        Scalar upperRed1 = new Scalar(10, 255, 255);
        Scalar lowerRed2 = new Scalar(160, 50, 50);
        Scalar upperRed2 = new Scalar(180, 255, 255);
        Imgproc.GaussianBlur(image, blurredImage, new Size(5,5), 0);

        Mat hsvImage = new Mat();
        Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

        blueMask = new Mat();
        Core.inRange(hsvImage, lowerBlue, upperBlue, blueMask);

        Mat redMask1 = new Mat();
        Mat redMask2 = new Mat();
        Mat redMask = new Mat();

        Core.inRange(hsvImage, loweRed1, upperRed1, redMask1);
        Core.inRange(hsvImage, lowerRed2, upperRed2, redMask2);
        Core.bitwise_or(redMask1, redMask2, redMask);


        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(blueMask,contours,  new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for(MatOfPoint mop : contours) {
            System.out.println("Contours: " + mop);
        }


        System.out.println("Hello, World!");
//        Jeu jeu = new Jeu(imagePath);
//        jeu.rewriteImage();

        SwingUtilities.invokeLater(() -> {
            MainWindow w = new MainWindow();
            w.setVisible(true);
        });

//        TraitementImage traitementImage = new TraitementImage(imagePath);
//        traitementImage.convertirEnNiveauDeGris().getDetailsCercle();

//        for(TraitementImage.CircleInfo circle : TraitementImage.getCirclesWithColors(imagePath)) {
//            Imgproc.circle(image, new Point(circle.center.x, circle.center.y), circle.radius, new Scalar(0, 255, 0), 2);
//            Imgproc.putText(image, circle.color, new Point(circle.center.x - 10, circle.center.y - 10),
//                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 255), 1);
//            System.out.println(circle);
//        }


//        Mat hsvImage = new Mat();
//        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);
//
//        // Define the red color range in HSV
//        Scalar lowerRed1 = new Scalar(0, 120, 70);
//        Scalar upperRed1 = new Scalar(10, 255, 255);
//        Scalar lowerRed2 = new Scalar(170, 120, 70);
//        Scalar upperRed2 = new Scalar(180, 255, 255);
//
//        // Create masks for red regions
//        Mat mask1 = new Mat();
//        Mat mask2 = new Mat();
//        Core.inRange(hsvImage, lowerRed1, upperRed1, mask1);
//        Core.inRange(hsvImage, lowerRed2, upperRed2, mask2);
//
//        // Combine the masks
//        Mat redMask = new Mat();
//        Core.add(mask1, mask2, redMask);
//
//        // Remove noise with morphological operations
//        Imgproc.morphologyEx(redMask, redMask, Imgproc.MORPH_OPEN, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));
//        Imgproc.morphologyEx(redMask, redMask, Imgproc.MORPH_CLOSE, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5)));
//
//        // Find contours in the mask
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(redMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        System.out.println("Detected red shapes:");
//        for (MatOfPoint contour : contours) {
//            // Approximate the contour with an ellipse
//            if (contour.total() >= 5) { // An ellipse requires at least 5 points
//                RotatedRect ellipse = Imgproc.fitEllipse(new MatOfPoint2f(contour.toArray()));
//
//                // Print the ellipse details
//                Point center = ellipse.center;
//                Size axes = ellipse.size; // Major and minor axis
//                double angle = ellipse.angle;
//
//                System.out.printf("Center: (%.2f, %.2f), Axes: (%.2f, %.2f), Angle: %.2f°%n",
//                        center.x, center.y, axes.width / 2, axes.height / 2, angle);
//
//                // Draw the ellipse on the image
//                Imgproc.ellipse(image, ellipse, new Scalar(0, 255, 0), 2);
//            } else {
//                // For small contours, use minimum enclosing circle
//                Point center = new Point();
//                float[] radius = new float[1];
//                Imgproc.minEnclosingCircle(new MatOfPoint2f(contour.toArray()), center, radius);
//
//                System.out.printf("Circle center: (%.2f, %.2f), Radius: %.2f%n", center.x, center.y, radius[0]);
//
//                // Draw the circle on the image
//                Imgproc.circle(image, center, (int) radius[0], new Scalar(255, 0, 0), 2);
//            }
//        }

//
//        // Save the result
    }
}