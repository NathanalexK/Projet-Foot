package org.example.components;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainWindow extends JFrame {
    ImageIcon photo = new ImageIcon();
    private CenterPanel centerPanel = null;
    private BottomPanel bottomPanel = null;
    private String imagepath = null;

    public MainWindow() {
        setTitle("Foot Hors Jeu");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        TopBarPanel topPanel = new TopBarPanel();
        topPanel.setCaller(this);

        centerPanel = new CenterPanel();
        centerPanel.setCaller(this);

        bottomPanel = new BottomPanel(this);



        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        this.getContentPane().add(new JScrollPane(centerPanel), BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

    }

    public void setImagePath(String imagePath) throws IOException {
        this.imagepath = imagePath;

        if(centerPanel != null) {
            centerPanel.setImage(imagePath);
        }
    }

    public String getImagePath() {
        return this.imagepath;
    }

    public void update() {

    }
}
