package org.example;

import org.example.utils.Constante;
import org.example.utils.Geometrie;
import org.example.utils.Ligne;
import org.example.utils.Rectangle;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class LineDetection {

    public static void main(String[] args) {
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Read the input image
        String imagePath = "/Users/macbook/Downloads/0.JPG";
        Mat image = Imgcodecs.imread(imagePath);

        if (image.empty()) {
            System.out.println("Could not open or find the image!");
            return;
        }

        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Threshold the image to isolate black regions
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 50, 255, Imgproc.THRESH_BINARY_INV);

        // Apply edge detection
        Mat edges = new Mat();
        Imgproc.Canny(binary, edges, 50, 150);

        // Perform morphological operations to enhance line detection
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Mat morph = new Mat();
        Imgproc.morphologyEx(edges, morph, Imgproc.MORPH_CLOSE, kernel);

        // Find contours to detect lines
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(morph, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Draw contours on the original image
        Mat output = image.clone();
        Imgproc.drawContours(output, contours, -1, new Scalar(0, 0, 255), 2);

        // Save the output image
        String outputPath = "output_image.jpg";
        Imgcodecs.imwrite(outputPath, output);

        System.out.println("Detected lines saved to: " + outputPath);
    }
}
