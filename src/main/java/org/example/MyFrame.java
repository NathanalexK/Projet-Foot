package org.example;

import javax.swing.*;

public class MyFrame extends JFrame {
    public MyFrame() {
        this.setTitle("My First Window");
        this.setSize(600, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MyFrame().setVisible(true);
        });
    }
}
