package org.example.components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CenterPanel extends JPanel {
    private JFrame caller = null;
    private ImageIcon photo = new ImageIcon();

    public CenterPanel() {
        setLayout(new FlowLayout());
        setBorder(BorderFactory.createLineBorder(Color.black));
        this.add(new JLabel(new ImageIcon("/Users/macbook/Downloads/4.jpg")));


    }

    public void setImage(String imagePath) throws IOException {
        this.removeAll();
        JLabel imageLabel = new JLabel();
        System.out.println(imagePath);
        File file = new File(imagePath);
        BufferedImage image = ImageIO.read(file);
        Image scaled = image.getScaledInstance(800, 800, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));
        this.add(imageLabel);
        this.repaint();
        if(caller != null) {
            caller.invalidate();
            caller.validate();
            caller.repaint();
        }
    }

    public JFrame getCaller() {
        return caller;
    }

    public void setCaller(JFrame caller) {
        this.caller = caller;
    }
}
