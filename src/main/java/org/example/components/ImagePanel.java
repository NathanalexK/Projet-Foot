package org.example.components;

import org.example.utils.ComponentUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class ImagePanel extends JPanel {
    private Consumer<String> onImageChange;

    private JPanel topPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();

    public ImagePanel() {
        setLayout(new BorderLayout());

        topPanel.setLayout(new FlowLayout());
        centerPanel.setLayout(new FlowLayout());
        bottomPanel.setLayout(new FlowLayout());

        JButton ajouterImageBtn = new JButton("Ajouter une image");

        ajouterImageBtn.addActionListener((e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setCurrentDirectory(new File("/Users/macbook/Documents/foot"));
            int result = fileChooser.showOpenDialog(null);

            if(result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    this.setImage(selectedFile.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        });

        bottomPanel.add(ajouterImageBtn);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setTitle(String title) {
        topPanel.removeAll();
        JLabel label = new JLabel(title);
        topPanel.add(label);
    }

    public void setImage(String imagePath) throws IOException {
        centerPanel.removeAll();
        JLabel imageLabel = new JLabel();
        System.out.println(imagePath);
        File file = new File(imagePath);
        BufferedImage image = ImageIO.read(file);
        Image scaled = image.getScaledInstance(800, 800, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));
        centerPanel.add(imageLabel);

        if(onImageChange != null) {
            onImageChange.accept(imagePath);
        }

        ComponentUtils.updateContainer(this);
    }

    public void removeImage() {
        centerPanel.removeAll();
        ComponentUtils.updateContainer(this);
    }

    public Consumer<String> getOnImageChange() {
        return onImageChange;
    }

    public void setOnImageChangeListener(Consumer<String> onImageChange) {
        this.onImageChange = onImageChange;
    }
}
